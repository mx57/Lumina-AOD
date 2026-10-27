package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.CameraFront
import androidx.compose.material.icons.outlined.Cloud
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EdgeLightingStyle
import com.example.ui.components.FullScreenLiveAodOverlay
import com.example.ui.components.InteractiveAmoledPreviewCard
import com.example.ui.screens.BatteryAndDeepSleepSection
import com.example.ui.screens.IslandAndCameraRingSection
import com.example.ui.screens.StudioCustomizationSection
import com.example.ui.screens.WeatherAndWidgetsSection
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            LuminaAODTheme {
                LuminaAodAppRoot()
            }
        }
    }
}

@Composable
fun LuminaAodAppRoot(
    viewModel: LuminaAodViewModel = viewModel()
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isFullScreenAod by viewModel.isFullScreenAodActive.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val messageState by viewModel.messageState.collectAsStateWithLifecycle()
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    val presets by viewModel.presets.collectAsStateWithLifecycle()
    val appColorRules by viewModel.appColorRules.collectAsStateWithLifecycle()
    val recentNotifications by viewModel.recentNotifications.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Handled gracefully */ }
    )

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
                                    fontSize = 11.sp,
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top Hero Banner & Master Controls
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

                    // Live Notification Toast Banner
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

                    // Interactive Real-Time AMOLED Canvas Preview (Always visible at top)
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
                            onToggleIslandExpand = viewModel::toggleIslandExpanded,
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

                    // Active Tab Content
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
                                onApplyPreset = viewModel::applyPreset,
                                onSavePreset = viewModel::saveCurrentAsPreset,
                                onDeletePreset = viewModel::deletePreset
                            )

                            1 -> IslandAndCameraRingSection(
                                config = config,
                                appColorRules = appColorRules,
                                recentNotifications = recentNotifications,
                                onToggleDynamicIsland = viewModel::toggleDynamicIsland,
                                onToggleCameraRing = viewModel::toggleCameraRing,
                                onSelectRingMode = viewModel::setCameraRingMode,
                                onUpdateCameraGeometry = viewModel::updateCameraHolePosition,
                                onSelectIslandActivity = { act ->
                                    viewModel.setActiveIslandActivity(act, expandImmediately = true)
                                },
                                onToggleIslandExpanded = viewModel::toggleIslandExpanded,
                                onSendCustomQuickReply = viewModel::sendQuickReply,
                                onTriggerSimulatedAppPush = viewModel::triggerSimulatedAppNotification,
                                onUpdateAppRuleColor = viewModel::updateAppRuleColor,
                                onClearNotifications = viewModel::clearNotificationLogs
                            )

                            2 -> WeatherAndWidgetsSection(
                                config = config,
                                weather = weather,
                                isLoading = isWeatherLoading,
                                onSelectCity = viewModel::selectWeatherCity,
                                onRefreshWeather = viewModel::refreshWeatherForSelectedCity,
                                onSelectWidgetStyle = viewModel::setWeatherWidgetStyle,
                                onToggleShowWeatherOnAod = viewModel::toggleShowWeatherOnAod
                            )

                            3 -> BatteryAndDeepSleepSection(
                                config = config,
                                telemetry = telemetry,
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
                onToggleIslandExpand = viewModel::toggleIslandExpanded,
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
                            text = "AMOLED PRO",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 10.sp,
                            color = EnergyEmerald
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Always On Display • Динамический Остров • Кольцо Камеры • OPR $oprPercent%",
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
