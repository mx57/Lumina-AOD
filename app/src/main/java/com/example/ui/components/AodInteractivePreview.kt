package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
import com.example.model.IslandActivityType
import com.example.model.IslandCallState
import com.example.model.IslandMediaState
import com.example.model.IslandMessageState
import com.example.model.IslandTimerState
import com.example.model.WeatherSnapshot
import com.example.service.HardwareTelemetryState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber
import kotlin.math.abs

@Composable
fun InteractiveAmoledPreviewCard(
    config: AodConfigurationState,
    weather: WeatherSnapshot,
    telemetry: HardwareTelemetryState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    recentNotifications: List<NotificationLogEntity>,
    hasRecentPushEvent: Boolean = true,
    onToggleIslandExpand: () -> Unit,
    onPrimaryTapIsland: () -> Unit = onToggleIslandExpand,
    onLongPressIsland: () -> Unit = {},
    onOpenParentApp: () -> Unit = {},
    onRestoreDismissedIsland: () -> Unit = {},
    onToggleFlashlight: () -> Unit = {},
    onSwitchIslandActivity: (IslandActivityType) -> Unit = {},
    onDragIslandByDeltaDp: (Float, Float) -> Unit,
    onResetIslandPosition: () -> Unit,
    onPlayPauseMedia: () -> Unit,
    onNextTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddOneMinTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit,
    onLaunchFullScreenAod: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unreadCount = recentNotifications.count { !it.wasBlockedByFilter }
    val activeNotifColor = recentNotifications.firstOrNull { !it.wasBlockedByFilter }?.colorHex ?: messageState.appColorHex
    val isDeepSleeping = config.isManualDeepSleepActive ||
        (config.proximityPocketMode && telemetry.isProximityCovered) ||
        (telemetry.batteryPercent <= config.lowBatteryAutoOffPercent)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(356.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(PitchBlack)
            .border(1.5.dp, ObsidianBorder, RoundedCornerShape(32.dp))
            .testTag("amoled_preview_canvas")
    ) {
        if (isDeepSleeping) {
            DeepSleepZeroPowerOverlay(
                isPocketCovered = config.proximityPocketMode && telemetry.isProximityCovered,
                isLowBattery = telemetry.batteryPercent <= config.lowBatteryAutoOffPercent,
                onWakeUpTap = { }
            )
        } else {
            // 1. Edge Lighting & Camera Cutout Ring Canvas
            EdgeLightingAndCameraRingCanvas(
                config = config,
                batteryPercent = telemetry.batteryPercent,
                notificationColorHex = activeNotifColor,
                cornerRadiusDp = 32.dp
            )

            // 1b. Subtle Magnetic Center Guide Line when Island is near center X = 0
            if (config.isIslandFreeDragEnabled && config.islandMagneticSnap && abs(config.islandOffsetXDp) < 2f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = CyberCyan.copy(alpha = 0.18f),
                        start = Offset(size.width / 2f, 0f),
                        end = Offset(size.width / 2f, size.height * 0.45f),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }
            }

            // 2. Center Clock, Weather Widget & Notification Pills with 3D Parallax
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp, bottom = 46.dp),
                contentAlignment = Alignment.Center
            ) {
                AodClockAndWidgetsContent(
                    config = config,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    isCharging = telemetry.isCharging,
                    recentNotifications = recentNotifications,
                    pixelShiftOffsetX = telemetry.pixelShiftOffsetX,
                    pixelShiftOffsetY = telemetry.pixelShiftOffsetY,
                    tiltNormalizedX = telemetry.tiltNormalizedX,
                    tiltNormalizedY = telemetry.tiltNormalizedY
                )
            }

            // 3. Movable 11-Shape Interactive Dynamic Island v3.0
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DynamicIslandOverlay(
                    config = config,
                    mediaState = mediaState,
                    timerState = timerState,
                    messageState = messageState,
                    callState = callState,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    isCharging = telemetry.isCharging,
                    unreadNotificationsCount = unreadCount,
                    hasRecentPushEvent = hasRecentPushEvent,
                    telemetry = telemetry,
                    onToggleExpand = onToggleIslandExpand,
                    onPrimaryTapIsland = onPrimaryTapIsland,
                    onLongPressIsland = onLongPressIsland,
                    onOpenParentApp = onOpenParentApp,
                    onRestoreDismissedIsland = onRestoreDismissedIsland,
                    onTogglePlayPause = onPlayPauseMedia,
                    onSkipNext = onNextTrack,
                    onToggleTimer = onToggleTimer,
                    onAddMinuteTimer = onAddOneMinTimer,
                    onResetTimer = onResetTimer,
                    onSendQuickReply = onSendQuickReply,
                    onAcceptOrEndCall = onAcceptOrEndCall,
                    onToggleFlashlight = onToggleFlashlight,
                    onSwitchIslandActivity = onSwitchIslandActivity,
                    onDragIslandByDeltaDp = onDragIslandByDeltaDp
                )
            }

            // 4. Burn-in Checkerboard subpixel protection
            BurnInCheckerboardOverlay(enabled = config.burnInCheckerboard)
        }

        // Bottom Telemetry, Drag Indicator & Launch Fullscreen Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xCC0D131F))
                        .border(1.dp, EnergyEmerald.copy(alpha = 0.45f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EnergyEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OPR ${config.calculateOprPercentage()}% • 120Hz",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = PureWhite
                    )
                }

                // Island Coordinates & Reset Quick Pill
                Surface(
                    onClick = onResetIslandPosition,
                    shape = RoundedCornerShape(50),
                    color = Color(0xCC111927),
                    modifier = Modifier.testTag("reset_island_pos_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (config.isIslandFreeDragEnabled) Icons.Default.OpenWith else Icons.Default.CenterFocusStrong,
                            contentDescription = "Сбросить позицию Острова",
                            tint = CyberCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${config.islandOffsetXDp.toInt()},${config.islandOffsetYDp.toInt()}dp",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 10.sp,
                            color = CyberCyan
                        )
                    }
                }
            }

            Surface(
                onClick = onLaunchFullScreenAod,
                shape = RoundedCornerShape(50),
                color = CyberCyan,
                modifier = Modifier.testTag("launch_fullscreen_aod_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Экран AOD",
                        tint = PitchBlack,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Живой AOD",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PitchBlack,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FullScreenLiveAodOverlay(
    config: AodConfigurationState,
    weather: WeatherSnapshot,
    telemetry: HardwareTelemetryState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    recentNotifications: List<NotificationLogEntity>,
    hasRecentPushEvent: Boolean = true,
    onToggleIslandExpand: () -> Unit,
    onPrimaryTapIsland: () -> Unit = onToggleIslandExpand,
    onLongPressIsland: () -> Unit = {},
    onOpenParentApp: () -> Unit = {},
    onRestoreDismissedIsland: () -> Unit = {},
    onToggleFlashlight: () -> Unit = {},
    onSwitchIslandActivity: (IslandActivityType) -> Unit = {},
    onDragIslandByDeltaDp: (Float, Float) -> Unit,
    onPlayPauseMedia: () -> Unit,
    onNextTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddOneMinTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit,
    onTogglePocketModeTest: () -> Unit,
    onManualPixelShiftStep: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    BackHandler { onExitFullScreen() }

    val unreadCount = recentNotifications.count { !it.wasBlockedByFilter }
    val activeNotifColor = recentNotifications.firstOrNull { !it.wasBlockedByFilter }?.colorHex ?: messageState.appColorHex
    val isDeepSleeping = config.isManualDeepSleepActive ||
        (config.proximityPocketMode && telemetry.isProximityCovered) ||
        (telemetry.batteryPercent <= config.lowBatteryAutoOffPercent)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .pointerInput(config.doubleTapToWake) {
                detectTapGestures(
                    onDoubleTap = {
                        onExitFullScreen()
                    }
                )
            }
            .testTag("fullscreen_live_aod_overlay")
    ) {
        if (isDeepSleeping) {
            DeepSleepZeroPowerOverlay(
                isPocketCovered = config.proximityPocketMode && telemetry.isProximityCovered,
                isLowBattery = telemetry.batteryPercent <= config.lowBatteryAutoOffPercent,
                onWakeUpTap = {
                    if (telemetry.isProximityCovered) {
                        onTogglePocketModeTest()
                    } else {
                        onExitFullScreen()
                    }
                }
            )
        } else {
            EdgeLightingAndCameraRingCanvas(
                config = config,
                batteryPercent = telemetry.batteryPercent,
                notificationColorHex = activeNotifColor,
                cornerRadiusDp = 28.dp
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AodClockAndWidgetsContent(
                    config = config,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    isCharging = telemetry.isCharging,
                    recentNotifications = recentNotifications,
                    pixelShiftOffsetX = telemetry.pixelShiftOffsetX,
                    pixelShiftOffsetY = telemetry.pixelShiftOffsetY,
                    tiltNormalizedX = telemetry.tiltNormalizedX,
                    tiltNormalizedY = telemetry.tiltNormalizedY
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DynamicIslandOverlay(
                    config = config,
                    mediaState = mediaState,
                    timerState = timerState,
                    messageState = messageState,
                    callState = callState,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    isCharging = telemetry.isCharging,
                    unreadNotificationsCount = unreadCount,
                    hasRecentPushEvent = hasRecentPushEvent,
                    telemetry = telemetry,
                    onToggleExpand = onToggleIslandExpand,
                    onPrimaryTapIsland = onPrimaryTapIsland,
                    onLongPressIsland = onLongPressIsland,
                    onOpenParentApp = onOpenParentApp,
                    onRestoreDismissedIsland = onRestoreDismissedIsland,
                    onTogglePlayPause = onPlayPauseMedia,
                    onSkipNext = onNextTrack,
                    onToggleTimer = onToggleTimer,
                    onAddMinuteTimer = onAddOneMinTimer,
                    onResetTimer = onResetTimer,
                    onSendQuickReply = onSendQuickReply,
                    onAcceptOrEndCall = onAcceptOrEndCall,
                    onToggleFlashlight = onToggleFlashlight,
                    onSwitchIslandActivity = onSwitchIslandActivity,
                    onDragIslandByDeltaDp = onDragIslandByDeltaDp
                )
            }

            BurnInCheckerboardOverlay(enabled = config.burnInCheckerboard)
        }

        // Subtle bottom control dock on Live AOD Screen
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTogglePocketModeTest,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedSlate)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (telemetry.isProximityCovered) "Убрать из кармана" else "Тест: В кармане",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onManualPixelShiftStep,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedSlate)
                ) {
                    Text(
                        text = "Сдвиг #${telemetry.pixelShiftCycleCount}",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onExitFullScreen,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    modifier = Modifier.testTag("exit_fullscreen_aod_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Выход",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Выход",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MutedSlate.copy(alpha = 0.6f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Тап: раскрыть/приложение • Долгий тап: скрыть • Двойной тап по фону: выход",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = MutedSlate.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun DeepSleepZeroPowerOverlay(
    isPocketCovered: Boolean,
    isLowBattery: Boolean,
    onWakeUpTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onWakeUpTap() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isPocketCovered) Icons.Default.Sensors else Icons.Default.Bedtime,
            contentDescription = "Глубокий сон",
            tint = SolarAmber.copy(alpha = 0.55f),
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when {
                isPocketCovered -> "КАРМАННЫЙ РЕЖИМ АКТИВЕН (ДАТЧИК ПРИБЛИЖЕНИЯ)"
                isLowBattery -> "ЭКОНОМИЯ ПРИ НИЗКОМ ЗАРЯДЕ АККУМУЛЯТОРА"
                else -> "РЕЖИМ ГЛУБОКОГО СНА • 100% ПИКСЕЛЕЙ ВЫКЛЮЧЕНЫ"
            },
            fontFamily = ShareTechMonoFontFamily,
            fontSize = 12.sp,
            color = SolarAmber.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Потребление матрицы: 0.00 мА•ч (True Pitch-Black OLED)",
            fontFamily = ShareTechMonoFontFamily,
            fontSize = 10.sp,
            color = MutedSlate.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
