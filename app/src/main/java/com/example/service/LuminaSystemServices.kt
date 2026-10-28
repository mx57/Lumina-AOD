package com.example.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppColorRuleEntity
import com.example.data.LuminaDatabase
import com.example.data.LuminaRepository
import com.example.model.AppFilterMode
import com.example.model.InstalledAppInfo
import com.example.model.SystemPermissionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class SystemQuickAction {
    data object ToggleAod : SystemQuickAction()
    data object ToggleDeepSleep : SystemQuickAction()
    data object CycleEdgeStyle : SystemQuickAction()
    data object TriggerTestPulse : SystemQuickAction()
    data object ScreenTurnedOff : SystemQuickAction()
    data class DeviceChargingConnected(val batteryPercent: Int) : SystemQuickAction()
    data class IncomingDeviceNotification(
        val packageName: String,
        val title: String,
        val text: String
    ) : SystemQuickAction()
}

object LuminaEventBus {
    private val _events = MutableSharedFlow<SystemQuickAction>(extraBufferCapacity = 32)
    val events: SharedFlow<SystemQuickAction> = _events.asSharedFlow()

    var isAodActiveGlobal: Boolean = true
    var isDeepSleepGlobal: Boolean = false
    var isForegroundServiceRunning: Boolean = false

    fun emitAction(action: SystemQuickAction) {
        _events.tryEmit(action)
    }

    fun evaluateAppFilter(
        mode: AppFilterMode,
        rule: AppColorRuleEntity?
    ): Boolean {
        return when (mode) {
            AppFilterMode.HYBRID_SMART -> {
                if (rule?.isBlacklisted == true) false else true
            }
            AppFilterMode.WHITELIST_ONLY -> {
                rule?.isWhitelisted == true && !rule.isBlacklisted
            }
            AppFilterMode.BLACKLIST_ONLY -> {
                rule?.isBlacklisted != true
            }
        }
    }
}

object LuminaPermissionHelper {

    fun checkPermissions(context: Context): SystemPermissionStatus {
        val enabledListeners = runCatching {
            NotificationManagerCompat.getEnabledListenerPackages(context)
        }.getOrDefault(emptySet())
        val hasNotifListener = enabledListeners.contains(context.packageName)

        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

        val hasPostNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val ignoringBattery = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager?.isIgnoringBatteryOptimizations(context.packageName) == true
        } else {
            true
        }

        return SystemPermissionStatus(
            hasNotificationListenerAccess = hasNotifListener,
            hasOverlayPermission = hasOverlay,
            hasPostNotificationsPermission = hasPostNotif,
            isIgnoringBatteryOptimizations = ignoringBattery,
            isForegroundServiceRunning = LuminaEventBus.isForegroundServiceRunning
        )
    }

    fun openNotificationListenerSettings(context: Context) {
        runCatching {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun openOverlayPermissionSettings(context: Context) {
        runCatching {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }.onFailure {
            runCatching {
                val fallback = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            }
        }
    }

    fun openAppDetailsSettings(context: Context) {
        runCatching {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun rebindNotificationListener(context: Context) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationListenerService.requestRebind(
                    ComponentName(context, LuminaNotificationListenerService::class.java)
                )
            }
        }
    }

    fun queryInstalledPackages(context: Context): List<InstalledAppInfo> {
        return runCatching {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            resolveInfos
                .mapNotNull { info ->
                    val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
                    if (pkg == context.packageName) return@mapNotNull null
                    val label = info.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: pkg
                    val isSys = (info.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    InstalledAppInfo(packageName = pkg, appName = label, isSystemApp = isSys)
                }
                .distinctBy { it.packageName }
                .sortedBy { it.appName.lowercase() }
        }.getOrDefault(emptyList())
    }
}

object LuminaSystemOverlayController {
    private var overlayView: View? = null
    private var titleTextView: TextView? = null
    private var bodyTextView: TextView? = null

    fun showOrUpdateSystemIslandBanner(
        context: Context,
        appName: String,
        title: String,
        message: String,
        accentHex: Long,
        offsetXDp: Float,
        offsetYDp: Float
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return
        }
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
        val density = context.resources.displayMetrics.density

        runCatching {
            if (overlayView == null) {
                val container = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(
                        (16 * density).toInt(),
                        (10 * density).toInt(),
                        (16 * density).toInt(),
                        (10 * density).toInt()
                    )
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 28f * density
                        setColor(Color.parseColor("#EE070A10"))
                        setStroke((1.5f * density).toInt(), (accentHex or 0xFF000000L).toInt())
                    }
                    setOnClickListener {
                        dismissSystemOverlay(context)
                        val openIntent = Intent(context, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra(MainActivity.EXTRA_EXPAND_ISLAND, true)
                        }
                        context.startActivity(openIntent)
                    }
                }

                val headerTv = TextView(context).apply {
                    setTextColor((accentHex or 0xFF000000L).toInt())
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    text = "$appName • $title"
                }
                val bodyTv = TextView(context).apply {
                    setTextColor(Color.WHITE)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    maxLines = 2
                    text = message
                }
                container.addView(headerTv)
                container.addView(bodyTv)
                titleTextView = headerTv
                bodyTextView = bodyTv
                overlayView = container

                val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }

                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    overlayType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    x = (offsetXDp * density).toInt()
                    y = ((offsetYDp + 8f) * density).toInt()
                }
                wm.addView(container, params)
            } else {
                titleTextView?.setTextColor((accentHex or 0xFF000000L).toInt())
                titleTextView?.text = "$appName • $title"
                bodyTextView?.text = message
                (overlayView?.background as? GradientDrawable)?.setStroke(
                    (1.5f * density).toInt(),
                    (accentHex or 0xFF000000L).toInt()
                )
            }
        }
    }

    fun dismissSystemOverlay(context: Context) {
        val view = overlayView ?: return
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
        runCatching {
            wm.removeView(view)
        }
        overlayView = null
        titleTextView = null
        bodyTextView = null
    }
}

class LuminaAodForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isScreenReceiverRegistered = false

    private val screenAndPowerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val ctx = context ?: return
            val repo = LuminaRepository(LuminaDatabase.getInstance(ctx).luminaDao(), ctx)
            val cfg = repo.loadPersistedConfig()
            if (!cfg.isAodEnabled || cfg.isManualDeepSleepActive) return

            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    LuminaEventBus.emitAction(SystemQuickAction.ScreenTurnedOff)
                    if (cfg.autoShowOnScreenOff) {
                        wakeAndLaunchLiveAodActivity(ctx, expandIsland = false)
                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    LuminaEventBus.emitAction(SystemQuickAction.DeviceChargingConnected(88))
                    if (cfg.autoWakeOnCharging) {
                        wakeAndLaunchLiveAodActivity(ctx, expandIsland = true)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        LuminaEventBus.isForegroundServiceRunning = true
        registerScreenReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LuminaEventBus.isForegroundServiceRunning = true
        val repo = LuminaRepository(LuminaDatabase.getInstance(applicationContext).luminaDao(), applicationContext)
        val cfg = repo.loadPersistedConfig()

        val notification = LuminaNotificationHelper.buildShadeNotification(
            context = applicationContext,
            isAodActive = cfg.isAodEnabled,
            isDeepSleep = cfg.isManualDeepSleepActive,
            clockTitle = cfg.clockStyle.titleRu,
            oprPercent = cfg.calculateOprPercentage()
        )

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    LuminaNotificationHelper.NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(LuminaNotificationHelper.NOTIFICATION_ID, notification)
            }
        }

        return START_STICKY
    }

    private fun registerScreenReceiver() {
        if (isScreenReceiverRegistered) return
        isScreenReceiverRegistered = true
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_POWER_CONNECTED)
        }
        runCatching {
            registerReceiver(screenAndPowerReceiver, filter)
        }
    }

    override fun onDestroy() {
        LuminaEventBus.isForegroundServiceRunning = false
        if (isScreenReceiverRegistered) {
            runCatching { unregisterReceiver(screenAndPowerReceiver) }
            isScreenReceiverRegistered = false
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun startOrSyncService(context: Context) {
            runCatching {
                val intent = Intent(context, LuminaAodForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        fun wakeAndLaunchLiveAodActivity(context: Context, expandIsland: Boolean) {
            runCatching {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                @Suppress("DEPRECATION")
                val wakeLock = pm?.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                    "luminaaod:EventWakeLock"
                )
                wakeLock?.acquire(5000L)

                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    putExtra(MainActivity.EXTRA_FORCE_LIVE_AOD, true)
                    putExtra(MainActivity.EXTRA_EXPAND_ISLAND, expandIsland)
                }
                context.startActivity(launchIntent)
            }
        }
    }
}

class QuickActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                LuminaAodForegroundService.startOrSyncService(context)
            }
            ACTION_TOGGLE_AOD -> {
                LuminaEventBus.isAodActiveGlobal = !LuminaEventBus.isAodActiveGlobal
                LuminaEventBus.emitAction(SystemQuickAction.ToggleAod)
            }
            ACTION_TOGGLE_DEEP_SLEEP -> {
                LuminaEventBus.isDeepSleepGlobal = !LuminaEventBus.isDeepSleepGlobal
                LuminaEventBus.emitAction(SystemQuickAction.ToggleDeepSleep)
            }
            ACTION_CYCLE_EDGE_STYLE -> {
                LuminaEventBus.emitAction(SystemQuickAction.CycleEdgeStyle)
            }
            ACTION_TRIGGER_TEST_PULSE -> {
                LuminaEventBus.emitAction(SystemQuickAction.TriggerTestPulse)
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_AOD = "com.example.ACTION_TOGGLE_AOD"
        const val ACTION_TOGGLE_DEEP_SLEEP = "com.example.ACTION_TOGGLE_DEEP_SLEEP"
        const val ACTION_CYCLE_EDGE_STYLE = "com.example.ACTION_CYCLE_EDGE_STYLE"
        const val ACTION_TRIGGER_TEST_PULSE = "com.example.ACTION_TRIGGER_TEST_PULSE"
    }
}

class LuminaNotificationListenerService : NotificationListenerService() {

    private val listenerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()
        LuminaAodForegroundService.startOrSyncService(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null || sbn.packageName == packageName) return
        if (sbn.isOngoing) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "Уведомление"
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: ""

        if (text.isBlank() && title.isBlank()) return

        // Emit to ViewModel when active
        LuminaEventBus.emitAction(
            SystemQuickAction.IncomingDeviceNotification(
                packageName = sbn.packageName,
                title = title,
                text = text.ifBlank { title }
            )
        )

        // Also handle background persistence, Whitelist/Blacklist filter & screen wake even if UI is closed
        listenerScope.launch {
            val db = LuminaDatabase.getInstance(applicationContext)
            val repo = LuminaRepository(db.luminaDao(), applicationContext)
            val cfg = repo.loadPersistedConfig()
            val rules = repo.getAllAppRulesOnce()
            val matchingRule = rules.find { it.packageName.equals(sbn.packageName, ignoreCase = true) }
            val isAllowed = LuminaEventBus.evaluateAppFilter(cfg.appFilterMode, matchingRule)
            val colorHex = matchingRule?.colorHex ?: 0xFF00F5D4L
            val appLabel = matchingRule?.appDisplayName
                ?: sbn.packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }

            if (isAllowed && cfg.isAodEnabled && !cfg.isManualDeepSleepActive) {
                val shouldWake = cfg.autoWakeOnNotification && (matchingRule?.wakeScreenOnPush ?: true)
                val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
                val isInteractive = pm?.isInteractive ?: true

                if (!isInteractive && shouldWake) {
                    LuminaAodForegroundService.wakeAndLaunchLiveAodActivity(
                        context = applicationContext,
                        expandIsland = matchingRule?.autoExpandIslandOnPush ?: true
                    )
                } else if (cfg.systemOverlayEnabled) {
                    launch(Dispatchers.Main) {
                        LuminaSystemOverlayController.showOrUpdateSystemIslandBanner(
                            context = applicationContext,
                            appName = appLabel,
                            title = title,
                            message = text.ifBlank { title },
                            accentHex = colorHex,
                            offsetXDp = cfg.islandOffsetXDp,
                            offsetYDp = cfg.islandOffsetYDp
                        )
                    }
                }
            }
        }
    }
}

class LuminaQuickTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        syncTileState()
    }

    override fun onClick() {
        super.onClick()
        LuminaEventBus.isAodActiveGlobal = !LuminaEventBus.isAodActiveGlobal
        LuminaEventBus.emitAction(SystemQuickAction.ToggleAod)
        syncTileState()
    }

    private fun syncTileState() {
        val tile = qsTile ?: return
        val active = LuminaEventBus.isAodActiveGlobal && !LuminaEventBus.isDeepSleepGlobal
        tile.state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.qs_tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (LuminaEventBus.isDeepSleepGlobal) {
                getString(R.string.qs_tile_subtitle_sleep)
            } else {
                getString(R.string.qs_tile_subtitle_active)
            }
        }
        tile.updateTile()
    }
}

object LuminaNotificationHelper {
    const val CHANNEL_ID = "lumina_aod_quick_panel"
    const val NOTIFICATION_ID = 7071

    fun buildShadeNotification(
        context: Context,
        isAodActive: Boolean,
        isDeepSleep: Boolean,
        clockTitle: String,
        oprPercent: Float
    ): Notification {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleAodPending = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, QuickActionReceiver::class.java).apply {
                action = QuickActionReceiver.ACTION_TOGGLE_AOD
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleSleepPending = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, QuickActionReceiver::class.java).apply {
                action = QuickActionReceiver.ACTION_TOGGLE_DEEP_SLEEP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cycleEdgePending = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, QuickActionReceiver::class.java).apply {
                action = QuickActionReceiver.ACTION_CYCLE_EDGE_STYLE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusTitle = when {
            isDeepSleep -> "Lumina AOD • Глубокий сон (0% OPR)"
            isAodActive -> "Lumina AOD 2026 Активен • OPR $oprPercent%"
            else -> "Lumina AOD на паузе"
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quick_tile)
            .setContentTitle(statusTitle)
            .setContentText("Стиль: $clockTitle • Перехват пушей и 120Hz Остров")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openAppIntent)
            .addAction(
                R.drawable.ic_quick_tile,
                if (isAodActive) "Выкл AOD" else "Вкл AOD",
                toggleAodPending
            )
            .addAction(
                R.drawable.ic_quick_tile,
                if (isDeepSleep) "Пробудить" else "Глубокий сон",
                toggleSleepPending
            )
            .addAction(
                R.drawable.ic_quick_tile,
                "Стиль краев",
                cycleEdgePending
            )
            .build()
    }

    fun syncShadeControlPanel(
        context: Context,
        enabled: Boolean,
        isAodActive: Boolean,
        isDeepSleep: Boolean,
        clockTitle: String,
        oprPercent: Float
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        if (!enabled) {
            manager.cancel(NOTIFICATION_ID)
            return
        }

        val notification = buildShadeNotification(
            context = context,
            isAodActive = isAodActive,
            isDeepSleep = isDeepSleep,
            clockTitle = clockTitle,
            oprPercent = oprPercent
        )

        runCatching {
            manager.notify(NOTIFICATION_ID, notification)
        }
    }
}
