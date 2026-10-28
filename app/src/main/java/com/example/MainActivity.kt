package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.CameraFront
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EdgeLightingStyle
import com.example.model.SystemPermissionStatus
import com.example.service.LuminaPermissionHelper
import com.example.ui.components.FullScreenLiveAodOverlay
import com.example.ui.components.InteractiveAmoledPreviewCard
import com.example.ui.screens.AppFilterRulesSection
import com.example.ui.screens.BatteryAndDeepSleepSection
import com.example.ui.screens.IslandAndRingSection
import com.example.ui.screens.StudioCustomizationSection
import com.example.ui.screens.WeatherWidgetCustomizationSection
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.LuminaAODTheme
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber
import com.example.viewmodel.LuminaAodViewModel

class MainActivity : ComponentActivity() {

    private val luminaViewModel: LuminaAodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        processWakeIntent(intent)
        enableEdgeToEdge()
        setContent {
            LuminaAODTheme {
                LuminaAodAppRoot(viewModel = luminaViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processWakeIntent(intent)
    }

    private fun processWakeIntent(intent: Intent?) {
        if (intent == null) return
        val forceLiveAod = intent.getBooleanExtra(EXTRA_FORCE_LIVE_AOD, false)
        val expandIsland = intent.getBooleanExtra(EXTRA_EXPAND_ISLAND, false)
        if (forceLiveAod || expandIsland) {
            luminaViewModel.handleLaunchIntentExtras(forceLiveAod, expandIsland)
        }
    }

    companion object {
        const val EXTRA_FORCE_LIVE_AOD = "com.example.EXTRA_FORCE_LIVE_AOD"
        const val EXTRA_EXPAND_ISLAND = "com.example.EXTRA_EXPAND_ISLAND"
    }
}

@Composable
fun LuminaAodAppRoot(
    viewModel: LuminaAodViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val config by viewModel.config.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isFullScreenAod by viewModel.isFullScreenAodActive.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsStateWithLifecycle()
    val favoriteCities by viewModel.favoriteCities.collectAsStateWithLifecycle()
    val citySearchQuery by viewModel.citySearchQuery.collectAsStateWithLifecycle()
    val citySearchResults by viewModel.citySearchResults.collectAsStateWithLifecycle()
    val isSearchingCities by viewModel.isSearchingCities.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val messageState by viewModel.messageState.collectAsStateWithLifecycle()
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    val presets by viewModel.presets.collectAsStateWithLifecycle()
    val appColorRules by viewModel.appColorRules.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val recentNotifications by viewModel.recentNotifications.collectAsStateWithLifecycle()
    val hasRecentPushEvent by viewModel.hasRecentPushEvent.collectAsStateWithLifecycle()
    val permissionStatus by viewModel.permissionStatus.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.refreshPermissionStatus() }
    )

    // Auto-refresh permission checks whenever user returns from Android Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (currentTab != 0 && !isFullScreenAod) {
        BackHandler { currentTab = 0 }
    }

    Box(modifier = Modifier.fillMaxSize().background(PitchBlack)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PitchBlack,
            contentWindowInsets = WindowInsets.safeDrawing,
            floatingActionButton = {
                if (!isFullScreenAod) {
                    FloatingActionButton(
                        onClick = { viewModel.setFullScreenAodActive(true) },
                        containerColor = CyberCyan,
                        contentColor = PitchBlack,
                        modifier = Modifier.testTag("fab_launch_live_aod")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.launch_live_aod)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Экран AOD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = ObsidianSurface,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        Triple(stringResource(R.string.tab_studio), Icons.Default.WatchLater, Icons.Outlined.WatchLater),
                        Triple(stringResource(R.string.tab_island), Icons.Default.CameraFront, Icons.Outlined.CameraFront),
                        Triple(stringResource(R.string.tab_apps), Icons.Default.FilterList, Icons.Outlined.FilterList),
                        Triple(stringResource(R.string.tab_weather), Icons.Default.Cloud, Icons.Outlined.Cloud),
                        Triple(stringResource(R.string.tab_battery), Icons.Default.BatterySaver, Icons.Outlined.BatterySaver)
                    )
                    tabs.forEachIndexed { idx, (title, activeIcon, inactiveIcon) ->
                        val selected = currentTab == idx
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = idx },
                            icon = {
                                Icon(
                                    imageVector = if (selected) activeIcon else inactiveIcon,
                                    contentDescription = title
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PitchBlack,
                                selectedTextColor = CyberCyan,
                                indicatorColor = CyberCyan,
                                unselectedIconColor = MutedSlate,
                                unselectedTextColor = MutedSlate
                            ),
                            modifier = Modifier.testTag("nav_tab_$idx")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Top Hero Banner & Master Controls
                    item {
                        LuminaStudioHeaderCard(
                            isAodEnabled = config.isAodEnabled,
                            isDeepSleep = config.isManualDeepSleepActive,
                            oprPercent = config.calculateOprPercentage(),
                            onToggleAod = viewModel::toggleAodEnabled,
                            onToggleDeepSleep = {
                                viewModel.toggleManualDeepSleep(!config.isManualDeepSleepActive)
                            }
                        )
                    }

                    // 2. Quick Permissions & Event Diagnostics Strip (1-tap access to permissions & test push)
                    item {
                        PermissionsAndQuickEventStrip(
                            permissionStatus = permissionStatus,
                            onOpenNotificationListener = { LuminaPermissionHelper.openNotificationListenerSettings(context) },
                            onOpenOverlayPermission = { LuminaPermissionHelper.openOverlayPermissionSettings(context) },
                            onGoToSystemTab = { currentTab = 4 },
                            onTriggerLiveEventTest = {
                                appColorRules.firstOrNull { !it.isBlacklisted }?.let {
                                    viewModel.triggerSimulatedAppNotification(it)
                                }
                            }
                        )
                    }

                    // 3. Live Notification Toast Banner
                    item {
                        AnimatedVisibility(
                            visible = bannerMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF102436))
                                    .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = bannerMessage.orEmpty(),
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 12.sp,
                                    color = CyberCyan
                                )
                            }
                        }
                    }

                    // 4. Interactive Real-Time 120Hz AMOLED Canvas Preview (Supports Drag & Drop, Tap-to-Open App, Marquee)
                    item {
                        InteractiveAmoledPreviewCard(
                            config = config,
                            weather = weather,
                            telemetry = telemetry,
                            mediaState = mediaState,
                            timerState = timerState,
                            messageState = messageState,
                            callState = callState,
                            recentNotifications = recentNotifications,
                            hasRecentPushEvent = hasRecentPushEvent,
                            onToggleIslandExpand = viewModel::toggleIslandExpanded,
                            onPrimaryTapIsland = viewModel::handleIslandPrimaryTap,
                            onLongPressIsland = viewModel::handleIslandLongPress,
                            onOpenParentApp = viewModel::openActiveIslandParentApp,
                            onRestoreDismissedIsland = viewModel::restoreDismissedIsland,
                            onToggleFlashlight = viewModel::toggleFlashlightFromIsland,
                            onSwitchIslandActivity = { act ->
                                viewModel.setActiveIslandActivity(act, expandImmediately = true)
                            },
                            onDragIslandByDeltaDp = viewModel::onDragIslandByDeltaDp,
                            onResetIslandPosition = viewModel::resetIslandPositionToCamera,
                            onPlayPauseMedia = viewModel::toggleMediaPlayPause,
                            onNextTrack = viewModel::skipToNextTrack,
                            onToggleTimer = viewModel::toggleTimerRunning,
                            onAddOneMinTimer = viewModel::addOneMinuteToTimer,
                            onResetTimer = viewModel::resetIslandTimer,
                            onSendQuickReply = viewModel::sendQuickReply,
                            onAcceptOrEndCall = viewModel::acceptOrEndIslandCall,
                            onLaunchFullScreenAod = { viewModel.setFullScreenAodActive(true) }
                        )
                    }

                    // 5. Active Tab Content
                    item {
                        when (currentTab) {
                            0 -> StudioCustomizationSection(
                                config = config,
                                selectedCategory = selectedCategory,
                                presets = presets,
                                onSelectCategory = viewModel::selectClockCategory,
                                onSelectClockStyle = viewModel::setClockStyle,
                                onSelectEdgeStyle = viewModel::setEdgeLightingStyle,
                                onSelectPalette = viewModel::setColorPalette,
                                onClockScaleChange = viewModel::setClockScale,
                                onBrightnessChange = viewModel::setAodBrightness,
                                onToggleShowSeconds = viewModel::toggleShowSeconds,
                                onToggleShowWeather = viewModel::toggleShowWeatherOnAod,
                                onToggleShowBadges = viewModel::toggleShowNotificationBadges,
                                onToggleParallax3d = viewModel::toggleParallax3d,
                                onParallaxIntensityChange = viewModel::setParallaxIntensity,
                                onToggleLiquidMetaball = viewModel::toggleLiquidMetaballMorph,
                                onAnimationSpeedChange = viewModel::setAnimationSpeedMultiplier,
                                onToggleEdgeParticles = viewModel::toggleEdgeParticleSparks,
                                onSimulateTiltPreview = viewModel::setSimulatedTiltPreview,
                                onApplyPreset = viewModel::applyPreset,
                                onSavePreset = viewModel::saveCurrentAsPreset,
                                onDeletePreset = viewModel::deletePreset
                            )

                            1 -> IslandAndRingSection(
                                config = config,
                                telemetry = telemetry,
                                appRules = appColorRules,
                                recentNotifications = recentNotifications,
                                onToggleDynamicIsland = viewModel::toggleDynamicIsland,
                                onSelectIslandShape = viewModel::setIslandShape,
                                onSelectVisibilityMode = viewModel::setIslandVisibilityMode,
                                onSelectTapAction = viewModel::setIslandTapAction,
                                onSetAutoCollapseSec = viewModel::setIslandAutoCollapseSec,
                                onToggleMarquee = viewModel::setIslandMarqueeEnabled,
                                onChangeMarqueeSpeed = viewModel::setIslandMarqueeSpeed,
                                onChangeMarqueeFontSize = viewModel::setIslandMarqueeFontSize,
                                onChangeCustomTickerText = viewModel::setIslandCustomTickerText,
                                onToggleWeatherInTicker = viewModel::setIslandShowWeatherInTicker,
                                onTogglePrivacyDot = viewModel::setIslandPrivacyDotEnabled,
                                onToggleLiveEqualizer = viewModel::setIslandLiveEqualizerEnabled,
                                onToggleQuickTorch = viewModel::setIslandQuickTorchEnabled,
                                onTriggerFlashlight = viewModel::toggleFlashlightFromIsland,
                                onTriggerPrivacySim = viewModel::togglePrivacySimulation,
                                onOpenParentApp = viewModel::openActiveIslandParentApp,
                                onSelectIslandAnchor = viewModel::setIslandAnchorPreset,
                                onToggleFreeDrag = viewModel::toggleIslandFreeDrag,
                                onToggleMagneticSnap = viewModel::toggleIslandMagneticSnap,
                                onUpdateIslandExactOffset = viewModel::setIslandExactOffsetDp,
                                onResetIslandToCamera = viewModel::resetIslandPositionToCamera,
                                onUpdateIslandVisuals = viewModel::updateIslandVisualSettings,
                                onSelectIslandActivity = { act ->
                                    viewModel.setActiveIslandActivity(act, expandImmediately = true)
                                },
                                onToggleIslandExpanded = viewModel::toggleIslandExpanded,
                                onToggleCameraRing = viewModel::toggleCameraRing,
                                onSelectCameraRingMode = viewModel::setCameraRingMode,
                                onUpdateCameraHolePosition = viewModel::updateCameraHolePosition,
                                onTriggerSimulatedNotification = viewModel::triggerSimulatedAppNotification,
                                onClearNotifications = viewModel::clearNotificationLogs
                            )

                            2 -> AppFilterRulesSection(
                                config = config,
                                appColorRules = appColorRules,
                                installedApps = installedApps,
                                onSelectFilterMode = viewModel::setAppFilterMode,
                                onToggleWhitelist = viewModel::toggleAppWhitelist,
                                onToggleBlacklist = viewModel::toggleAppBlacklist,
                                onToggleWakeScreen = viewModel::toggleAppWakeScreen,
                                onToggleAutoExpand = viewModel::toggleAppAutoExpandIsland,
                                onUpdateAppRuleColor = viewModel::updateAppRuleColor,
                                onAddAppRule = viewModel::addCustomOrInstalledAppRule,
                                onDeleteAppRule = viewModel::deleteAppRule,
                                onTriggerSimulatedAppPush = viewModel::triggerSimulatedAppNotification
                            )

                            3 -> WeatherWidgetCustomizationSection(
                                config = config,
                                weather = weather,
                                isLoading = isWeatherLoading,
                                favoriteCities = favoriteCities,
                                citySearchQuery = citySearchQuery,
                                citySearchResults = citySearchResults,
                                isSearchingCities = isSearchingCities,
                                onSearchQueryChange = viewModel::onCitySearchQueryChanged,
                                onExecuteSearchNow = { viewModel.executeCitySearchNow() },
                                onSelectCity = viewModel::selectWeatherCity,
                                onRemoveFavoriteCity = viewModel::removeFavoriteCity,
                                onSelectWidgetStyle = viewModel::setWeatherWidgetStyle,
                                onToggleShowWeatherOnAod = viewModel::toggleShowWeatherOnAod,
                                onRefreshWeather = viewModel::refreshWeatherForSelectedCity,
                                onPushWeatherToIslandTicker = viewModel::pushLiveWeatherToIslandTicker
                            )

                            4 -> BatteryAndDeepSleepSection(
                                config = config,
                                telemetry = telemetry,
                                permissionStatus = permissionStatus,
                                onRefreshPermissionsAndRestart = viewModel::restartServicesAndVerify,
                                onRequestPostNotificationPermission = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.refreshPermissionStatus()
                                    }
                                },
                                onToggleAutoWakeOnNotification = viewModel::toggleAutoWakeOnNotification,
                                onToggleAutoWakeOnCharging = viewModel::toggleAutoWakeOnCharging,
                                onToggleAutoShowOnScreenOff = viewModel::toggleAutoShowOnScreenOff,
                                onToggleSystemOverlay = viewModel::toggleSystemOverlayEnabled,
                                onToggleProximityPocket = viewModel::toggleProximityPocketMode,
                                onTogglePocketSimulation = viewModel::togglePocketSimulation,
                                onTogglePixelShift = viewModel::togglePixelShift,
                                onTriggerManualPixelShift = viewModel::triggerManualPixelShiftStep,
                                onToggleBurnInCheckerboard = viewModel::toggleBurnInCheckerboard,
                                onToggleDeepSleepSchedule = viewModel::toggleDeepSleepSchedule,
                                onUpdateDeepSleepHours = viewModel::updateDeepSleepHours,
                                onToggleManualDeepSleep = viewModel::toggleManualDeepSleep,
                                onLowBatteryCutoffChange = viewModel::setLowBatteryAutoOffPercent,
                                onToggleDoubleTapWake = viewModel::toggleDoubleTapToWake,
                                onToggleLiftToWake = viewModel::toggleLiftToWake,
                                onTogglePersistentShadePanel = viewModel::togglePersistentShadeNotification,
                                onCycleEdgeFromQuickPanel = {
                                    val all = EdgeLightingStyle.entries
                                    val next = all[(all.indexOf(config.edgeStyle) + 1) % all.size]
                                    viewModel.setEdgeLightingStyle(next)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen True Pitch-Black Live AOD Overlay
        if (isFullScreenAod) {
            FullScreenLiveAodOverlay(
                config = config,
                weather = weather,
                telemetry = telemetry,
                mediaState = mediaState,
                timerState = timerState,
                messageState = messageState,
                callState = callState,
                recentNotifications = recentNotifications,
                hasRecentPushEvent = hasRecentPushEvent,
                onToggleIslandExpand = viewModel::toggleIslandExpanded,
                onPrimaryTapIsland = viewModel::handleIslandPrimaryTap,
                onLongPressIsland = viewModel::handleIslandLongPress,
                onOpenParentApp = viewModel::openActiveIslandParentApp,
                onRestoreDismissedIsland = viewModel::restoreDismissedIsland,
                onToggleFlashlight = viewModel::toggleFlashlightFromIsland,
                onSwitchIslandActivity = { act ->
                    viewModel.setActiveIslandActivity(act, expandImmediately = true)
                },
                onDragIslandByDeltaDp = viewModel::onDragIslandByDeltaDp,
                onPlayPauseMedia = viewModel::toggleMediaPlayPause,
                onNextTrack = viewModel::skipToNextTrack,
                onToggleTimer = viewModel::toggleTimerRunning,
                onAddOneMinTimer = viewModel::addOneMinuteToTimer,
                onResetTimer = viewModel::resetIslandTimer,
                onSendQuickReply = viewModel::sendQuickReply,
                onAcceptOrEndCall = viewModel::acceptOrEndIslandCall,
                onTogglePocketModeTest = viewModel::togglePocketSimulation,
                onManualPixelShiftStep = viewModel::triggerManualPixelShiftStep,
                onExitFullScreen = { viewModel.setFullScreenAodActive(false) }
            )
        }
    }
}

@Composable
private fun PermissionsAndQuickEventStrip(
    permissionStatus: SystemPermissionStatus,
    onOpenNotificationListener: () -> Unit,
    onOpenOverlayPermission: () -> Unit,
    onGoToSystemTab: () -> Unit,
    onTriggerLiveEventTest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianCard)
            .border(
                1.dp,
                if (permissionStatus.allCriticalGranted) EnergyEmerald.copy(alpha = 0.45f)
                else SolarAmber.copy(alpha = 0.65f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onGoToSystemTab() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (permissionStatus.allCriticalGranted) Icons.Default.CheckCircle else Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = if (permissionStatus.allCriticalGranted) EnergyEmerald else SolarAmber,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (permissionStatus.allCriticalGranted) {
                        "Все критические разрешения выданы (${permissionStatus.grantedCount}/4)"
                    } else {
                        "Разрешения: ${permissionStatus.grantedCount}/4 • Нажмите для настройки"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = PureWhite
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (permissionStatus.hasNotificationListenerAccess) "✓ Пуши: ВКЛ" else "⚠ Доступ к пушам",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = if (permissionStatus.hasNotificationListenerAccess) EnergyEmerald else SolarAmber,
                        modifier = Modifier.clickable { onOpenNotificationListener() }
                    )
                    Text(
                        text = if (permissionStatus.hasOverlayPermission) "✓ Оверлей: ВКЛ" else "⚠ Оверлей окон",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = if (permissionStatus.hasOverlayPermission) EnergyEmerald else SolarAmber,
                        modifier = Modifier.clickable { onOpenOverlayPermission() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            onClick = onTriggerLiveEventTest,
            shape = RoundedCornerShape(50),
            color = CyberCyan.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
            modifier = Modifier.testTag("quick_test_event_pulse_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Тест события",
                    tint = CyberCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Тест Пуша",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
private fun LuminaStudioHeaderCard(
    isAodEnabled: Boolean,
    isDeepSleep: Boolean,
    oprPercent: Float,
    onToggleAod: (Boolean) -> Unit,
    onToggleDeepSleep: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(22.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_amoled_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LUMINA AOD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EnergyEmerald.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "v3.0 • 120HZ",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 10.sp,
                            color = EnergyEmerald
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "11 Форм Острова • Бегущая Строка • Погода Мира • OPR $oprPercent%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    color = MutedSlate
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onToggleDeepSleep,
                    shape = CircleShape,
                    color = if (isDeepSleep) SolarAmber else Color(0xFF172133),
                    modifier = Modifier.testTag("header_deep_sleep_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Глубокий сон",
                            tint = if (isDeepSleep) PitchBlack else SolarAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Сон",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = if (isDeepSleep) PitchBlack else PureWhite
                        )
                    }
                }

                Switch(
                    checked = isAodEnabled,
                    onCheckedChange = onToggleAod,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PitchBlack,
                        checkedTrackColor = CyberCyan
                    ),
                    modifier = Modifier.testTag("master_aod_switch")
                )
            }
        }
    }
}
