package com.example.ui.components

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
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
    onToggleIslandExpand: () -> Unit,
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
    val activeNotifColor = recentNotifications.firstOrNull()?.colorHex ?: messageState.appColorHex
    val isDeepSleeping = config.isManualDeepSleepActive ||
        (config.proximityPocketMode && telemetry.isProximityCovered) ||
        (telemetry.batteryPercent <= config.lowBatteryAutoOffPercent)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
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

            // 2. Center Clock, Weather Widget & Notification Pills
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp, bottom = 44.dp),
                contentAlignment = Alignment.Center
            ) {
                AodClockAndWidgetsContent(
                    config = config,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    isCharging = telemetry.isCharging,
                    recentNotifications = recentNotifications,
                    pixelShiftOffsetX = telemetry.pixelShiftOffsetX,
                    pixelShiftOffsetY = telemetry.pixelShiftOffsetY
                )
            }

            // 3. Interactive Dynamic Island at top around camera hole
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DynamicIslandInteractiveCapsule(
                    config = config,
                    mediaState = mediaState,
                    timerState = timerState,
                    messageState = messageState,
                    callState = callState,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    onToggleExpand = onToggleIslandExpand,
                    onPlayPauseMedia = onPlayPauseMedia,
                    onNextTrack = onNextTrack,
                    onToggleTimer = onToggleTimer,
                    onAddOneMinTimer = onAddOneMinTimer,
                    onResetTimer = onResetTimer,
                    onSendQuickReply = onSendQuickReply,
                    onAcceptOrEndCall = onAcceptOrEndCall
                )
            }

            // 4. Burn-in Checkerboard subpixel protection
            BurnInCheckerboardOverlay(enabled = config.burnInCheckerboard)
        }

        // Bottom Telemetry & Launch Fullscreen Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                    text = "OPR ${config.calculateOprPercentage()}% • ~${config.estimatedBatteryDrainPerHourPercent()}%/ч",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite
                )
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
    onToggleIslandExpand: () -> Unit,
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

    val activeNotifColor = recentNotifications.firstOrNull()?.colorHex ?: messageState.appColorHex
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
                    pixelShiftOffsetY = telemetry.pixelShiftOffsetY
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DynamicIslandInteractiveCapsule(
                    config = config,
                    mediaState = mediaState,
                    timerState = timerState,
                    messageState = messageState,
                    callState = callState,
                    weather = weather,
                    batteryPercent = telemetry.batteryPercent,
                    onToggleExpand = onToggleIslandExpand,
                    onPlayPauseMedia = onPlayPauseMedia,
                    onNextTrack = onNextTrack,
                    onToggleTimer = onToggleTimer,
                    onAddOneMinTimer = onAddOneMinTimer,
                    onResetTimer = onResetTimer,
                    onSendQuickReply = onSendQuickReply,
                    onAcceptOrEndCall = onAcceptOrEndCall
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
                    text = "Двойной тап в любом месте для разблокировки • OPR ${config.calculateOprPercentage()}%",
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
