package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class SystemQuickAction {
    data object ToggleAod : SystemQuickAction()
    data object ToggleDeepSleep : SystemQuickAction()
    data object CycleEdgeStyle : SystemQuickAction()
    data object TriggerTestPulse : SystemQuickAction()
    data class IncomingDeviceNotification(
        val packageName: String,
        val title: String,
        val text: String
    ) : SystemQuickAction()
}

object LuminaEventBus {
    private val _events = MutableSharedFlow<SystemQuickAction>(extraBufferCapacity = 16)
    val events: SharedFlow<SystemQuickAction> = _events.asSharedFlow()

    var isAodActiveGlobal: Boolean = true
    var isDeepSleepGlobal: Boolean = false

    fun emitAction(action: SystemQuickAction) {
        _events.tryEmit(action)
    }
}

class QuickActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
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
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null || sbn.packageName == packageName) return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "Уведомление"
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        if (text.isNotBlank()) {
            LuminaEventBus.emitAction(
                SystemQuickAction.IncomingDeviceNotification(
                    packageName = sbn.packageName,
                    title = title,
                    text = text
                )
            )
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
    private const val CHANNEL_ID = "lumina_aod_quick_panel"
    private const val NOTIFICATION_ID = 7071

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            isDeepSleep -> "Lumina AOD • Режим глубокого сна (0% OPR)"
            isAodActive -> "Lumina AOD активен • OPR $oprPercent%"
            else -> "Lumina AOD на паузе"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quick_tile)
            .setContentTitle(statusTitle)
            .setContentText("Стиль: $clockTitle • Быстрые действия в 1 тап")
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

        runCatching {
            manager.notify(NOTIFICATION_ID, notification)
        }
    }
}
