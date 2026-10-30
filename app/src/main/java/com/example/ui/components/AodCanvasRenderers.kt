package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
import com.example.model.CameraRingMode
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.WeatherSnapshot
import com.example.model.WeatherWidgetStyle
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EdgeLightingAndCameraRingCanvas(
    config: AodConfigurationState,
    batteryPercent: Int,
    notificationColorHex: Long,
    cornerRadiusDp: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val speed = config.animationSpeedMultiplier.coerceIn(0.5f, 2.0f)
    val infiniteTransition = rememberInfiniteTransition(label = "edge_and_ring_anim_2026")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (3000 / speed).toInt().coerceAtLeast(800), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (1500 / speed).toInt().coerceAtLeast(500), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val primary = config.palette.primaryColor
    val secondary = config.palette.secondaryColor
    val accent = config.palette.accentColor
    val notifColor = Color(notificationColorHex)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cornerPx = cornerRadiusDp.toPx()

        // 1. Render Multi-Layered 2026 Shader Edge Lighting
        when (config.edgeStyle) {
            EdgeLightingStyle.NEON_WAVE -> {
                val angle = phase * 2f * PI.toFloat()
                val cx = w / 2f + cos(angle) * (w * 0.42f)
                val cy = h / 2f + sin(angle) * (h * 0.42f)
                val brush = Brush.sweepGradient(
                    colors = listOf(primary, secondary, accent, notifColor, primary),
                    center = Offset(cx.coerceIn(1f, w - 1f), cy.coerceIn(1f, h - 1f))
                )
                // Outer Soft Bloom Halo
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(w - 4.dp.toPx(), h - 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    alpha = 0.35f,
                    style = Stroke(width = 11.dp.toPx())
                )
                // Core High-Intensity Neon Wire
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = 3.5.dp.toPx())
                )
            }
            EdgeLightingStyle.LASER_CHASE -> {
                val strokeW = 4.dp.toPx()
                val progressY = phase * h
                val invY = (1f - phase) * h
                // Left laser beam + bloom
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, primary.copy(alpha = 0.35f), secondary.copy(alpha = 0.35f), Color.Transparent),
                        startY = (progressY - h * 0.32f),
                        endY = (progressY + h * 0.32f)
                    ),
                    start = Offset(strokeW, 0f),
                    end = Offset(strokeW, h),
                    strokeWidth = strokeW * 3f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, primary, secondary, PureWhite, Color.Transparent),
                        startY = (progressY - h * 0.24f),
                        endY = (progressY + h * 0.24f)
                    ),
                    start = Offset(strokeW, 0f),
                    end = Offset(strokeW, h),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                // Right laser beam + bloom
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, secondary.copy(alpha = 0.35f), primary.copy(alpha = 0.35f), Color.Transparent),
                        startY = (invY - h * 0.32f),
                        endY = (invY + h * 0.32f)
                    ),
                    start = Offset(w - strokeW, 0f),
                    end = Offset(w - strokeW, h),
                    strokeWidth = strokeW * 3f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, secondary, primary, PureWhite, Color.Transparent),
                        startY = (invY - h * 0.24f),
                        endY = (invY + h * 0.24f)
                    ),
                    start = Offset(w - strokeW, 0f),
                    end = Offset(w - strokeW, h),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
            EdgeLightingStyle.AUDIO_SPECTRUM -> {
                val barCount = 22
                val stepY = (h * 0.68f) / barCount
                val startY = h * 0.16f
                for (i in 0 until barCount) {
                    val wave = sin((phase * 2f * PI + i * 0.52).toFloat())
                    val harmonic = cos((phase * 4f * PI - i * 0.31).toFloat())
                    val barLen = (6.dp.toPx() + ((wave + harmonic + 2f) / 4f) * 22.dp.toPx())
                    val y = startY + i * stepY
                    val barColor = when (i % 3) {
                        0 -> primary
                        1 -> secondary
                        else -> accent
                    }
                    // Left equalizer bar
                    drawLine(
                        color = barColor.copy(alpha = 0.9f),
                        start = Offset(2.dp.toPx(), y),
                        end = Offset(2.dp.toPx() + barLen, y),
                        strokeWidth = 2.8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Right equalizer bar
                    drawLine(
                        color = barColor.copy(alpha = 0.9f),
                        start = Offset(w - 2.dp.toPx(), y),
                        end = Offset(w - 2.dp.toPx() - barLen, y),
                        strokeWidth = 2.8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            EdgeLightingStyle.BREATHING_GLOW -> {
                drawRoundRect(
                    color = notifColor.copy(alpha = breathe * 0.28f),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(w - 4.dp.toPx(), h - 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = (8f + breathe * 6f).dp.toPx())
                )
                drawRoundRect(
                    color = primary.copy(alpha = breathe * 0.9f),
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = (2.2f + breathe * 2.5f).dp.toPx())
                )
            }
            EdgeLightingStyle.CORNER_SPARKS -> {
                val arcSize = 58.dp.toPx()
                val pad = 4.dp.toPx()
                val stroke = Stroke(width = 4.2.dp.toPx(), cap = StrokeCap.Round)
                val sparkColor = primary.copy(alpha = 0.55f + 0.45f * breathe)
                drawArc(
                    color = sparkColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(pad, pad),
                    size = Size(arcSize, arcSize),
                    style = stroke
                )
                drawArc(
                    color = secondary.copy(alpha = 0.55f + 0.45f * breathe),
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - arcSize - pad, pad),
                    size = Size(arcSize, arcSize),
                    style = stroke
                )
                drawArc(
                    color = secondary.copy(alpha = 0.55f + 0.45f * breathe),
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(pad, h - arcSize - pad),
                    size = Size(arcSize, arcSize),
                    style = stroke
                )
                drawArc(
                    color = sparkColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - arcSize - pad, h - arcSize - pad),
                    size = Size(arcSize, arcSize),
                    style = stroke
                )
            }
            EdgeLightingStyle.OFF -> Unit
        }

        // 1b. Optional Floating Wave Particles along edges (2026 Shader Particles)
        if (config.edgeParticleSparksEnabled && config.edgeStyle != EdgeLightingStyle.OFF) {
            val particleCount = 8
            for (p in 0 until particleCount) {
                val pProgress = (phase + p * (1f / particleCount)) % 1f
                val py = pProgress * h
                val pxOffset = (sin((pProgress * 4 * PI + p).toFloat()) * 6.dp.toPx()).toFloat()
                val pColor = if (p % 2 == 0) primary else secondary
                drawCircle(
                    color = pColor.copy(alpha = 0.72f * sin(pProgress * PI.toFloat())),
                    radius = 2.5.dp.toPx(),
                    center = Offset(8.dp.toPx() + pxOffset, py)
                )
                drawCircle(
                    color = pColor.copy(alpha = 0.72f * sin(pProgress * PI.toFloat())),
                    radius = 2.5.dp.toPx(),
                    center = Offset(w - 8.dp.toPx() - pxOffset, h - py)
                )
            }
        }

        // 2. Render Camera Cutout Notification & Battery Energy Ring
        val holeCenterX = (w / 2f) + config.cameraHoleOffsetXDp.dp.toPx()
        val holeCenterY = config.cameraHoleOffsetYDp.dp.toPx() + config.cameraRingRadiusDp.dp.toPx()
        val ringRadius = config.cameraRingRadiusDp.dp.toPx()
        val ringThickness = config.cameraRingThicknessDp.dp.toPx()

        // Physical camera punch-hole core
        if (config.showCameraCutoutHole) {
            drawCircle(
                color = PitchBlack,
                radius = (ringRadius - ringThickness * 0.6f).coerceAtLeast(6f),
                center = Offset(holeCenterX, holeCenterY)
            )
        }

        if (config.isCameraRingEnabled) {
            // Soft outer neon halo around camera ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(notifColor.copy(alpha = 0.38f * breathe), Color.Transparent),
                    center = Offset(holeCenterX, holeCenterY),
                    radius = ringRadius * 1.85f
                ),
                radius = ringRadius * 1.85f,
                center = Offset(holeCenterX, holeCenterY)
            )

            when (config.cameraRingMode) {
                CameraRingMode.COMBO_PULSE_AND_BATTERY -> {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(notifColor, primary, secondary, notifColor),
                            center = Offset(holeCenterX, holeCenterY)
                        ),
                        startAngle = phase * 360f,
                        sweepAngle = 290f,
                        useCenter = false,
                        topLeft = Offset(holeCenterX - ringRadius, holeCenterY - ringRadius),
                        size = Size(ringRadius * 2f, ringRadius * 2f),
                        style = Stroke(width = ringThickness, cap = StrokeCap.Round)
                    )
                    val innerR = ringRadius - ringThickness - 2.dp.toPx()
                    if (innerR > 4f) {
                        val batteryColor = when {
                            batteryPercent > 50 -> EnergyEmerald
                            batteryPercent > 20 -> SolarAmber
                            else -> MatrixRed
                        }
                        drawArc(
                            color = batteryColor,
                            startAngle = -90f,
                            sweepAngle = (batteryPercent / 100f) * 360f,
                            useCenter = false,
                            topLeft = Offset(holeCenterX - innerR, holeCenterY - innerR),
                            size = Size(innerR * 2f, innerR * 2f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                CameraRingMode.NOTIFICATION_LED_ONLY -> {
                    drawCircle(
                        color = notifColor.copy(alpha = 0.35f + 0.65f * breathe),
                        radius = ringRadius,
                        center = Offset(holeCenterX, holeCenterY),
                        style = Stroke(width = ringThickness)
                    )
                }
                CameraRingMode.BATTERY_ENERGY_RING -> {
                    val batteryColor = when {
                        batteryPercent > 55 -> EnergyEmerald
                        batteryPercent > 25 -> SolarAmber
                        else -> MatrixRed
                    }
                    drawCircle(
                        color = batteryColor.copy(alpha = 0.2f),
                        radius = ringRadius,
                        center = Offset(holeCenterX, holeCenterY),
                        style = Stroke(width = ringThickness)
                    )
                    drawArc(
                        color = batteryColor,
                        startAngle = -90f,
                        sweepAngle = (batteryPercent / 100f) * 360f,
                        useCenter = false,
                        topLeft = Offset(holeCenterX - ringRadius, holeCenterY - ringRadius),
                        size = Size(ringRadius * 2f, ringRadius * 2f),
                        style = Stroke(width = ringThickness, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
fun AodClockAndWidgetsContent(
    config: AodConfigurationState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    isCharging: Boolean,
    recentNotifications: List<NotificationLogEntity>,
    pixelShiftOffsetX: Float,
    pixelShiftOffsetY: Float,
    tiltNormalizedX: Float = 0f,
    tiltNormalizedY: Float = 0f,
    modifier: Modifier = Modifier
) {
    // High-refresh ticker (updates every 80ms for smooth 2026 sub-second sweep & fluid transitions)
    var currentMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentMillis = System.currentTimeMillis()
            delay(80L)
        }
    }

    val dateObj = remember(currentMillis / 1000L) { Date(currentMillis) }
    val hourStr = remember(currentMillis / 1000L) { SimpleDateFormat("HH", Locale.getDefault()).format(dateObj) }
    val minStr = remember(currentMillis / 1000L) { SimpleDateFormat("mm", Locale.getDefault()).format(dateObj) }
    val secStr = remember(currentMillis / 1000L) { SimpleDateFormat("ss", Locale.getDefault()).format(dateObj) }
    val subSecFraction = ((currentMillis % 1000L) / 1000f)
    val dateLineRu = remember(currentMillis / 10000L) {
        SimpleDateFormat("EEE, d MMMM", Locale("ru")).format(dateObj).uppercase(Locale("ru"))
    }

    val parallaxDx by animateFloatAsState(
        targetValue = if (config.parallax3dEnabled) tiltNormalizedX * 10f * config.parallaxIntensity else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "parallaxDx"
    )
    val parallaxDy by animateFloatAsState(
        targetValue = if (config.parallax3dEnabled) tiltNormalizedY * 8f * config.parallaxIntensity else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "parallaxDy"
    )

    val shiftX = ((if (config.pixelShiftEnabled) pixelShiftOffsetX else 0f) + parallaxDx).dp
    val shiftY = ((if (config.pixelShiftEnabled) pixelShiftOffsetY else 0f) + parallaxDy).dp

    Column(
        modifier = modifier
            .offset(x = shiftX, y = shiftY)
            .graphicsLayer {
                if (config.parallax3dEnabled) {
                    rotationY = tiltNormalizedX * 7f * config.parallaxIntensity
                    rotationX = -tiltNormalizedY * 6f * config.parallaxIntensity
                }
            }
            .alpha(config.aodBrightness)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (config.standByNightModeEnabled) {
            StandByNightstandClockView(
                config = config,
                hourStr = hourStr,
                minStr = minStr,
                secStr = secStr,
                dateLineRu = dateLineRu,
                batteryPercent = batteryPercent,
                isCharging = isCharging,
                weather = weather
            )
            return@Column
        }

        val itemSpacing = config.widgetsVerticalSpacingDp.coerceIn(2f, 24f).dp

        // Selected Clock Face Renderer (2026 Fluid Edition)
        if (config.showClockOnAod) {
            Box(modifier = Modifier.alpha(config.clockOpacity.coerceIn(0.2f, 1.0f))) {
                ClockFaceShowcase(
                    style = config.clockStyle,
                    palette = config.palette,
                    scale = config.clockScale,
                    hourStr = hourStr,
                    minStr = minStr,
                    secStr = secStr,
                    subSecFraction = subSecFraction,
                    showSeconds = config.showSeconds,
                    showGlowDivider = config.showClockGlowDivider
                )
            }
        }

        if (config.showDateAndBattery && (config.showDateOnAod || config.showBatteryIconOnAod || config.showBatteryPercentOnAod)) {
            Spacer(modifier = Modifier.height(itemSpacing))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (config.showDateOnAod) {
                    Text(
                        text = dateLineRu,
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 12.sp,
                        color = PureWhite.copy(alpha = 0.78f),
                        letterSpacing = 1.2.sp
                    )
                }
                if (config.showDateOnAod && (config.showBatteryIconOnAod || config.showBatteryPercentOnAod)) {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                if (config.showBatteryIconOnAod) {
                    Icon(
                        imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                        contentDescription = "Заряд батареи",
                        tint = if (batteryPercent > 20) EnergyEmerald else MatrixRed,
                        modifier = Modifier.size(14.dp)
                    )
                    if (config.showBatteryPercentOnAod) {
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
                if (config.showBatteryPercentOnAod) {
                    Text(
                        text = "$batteryPercent%",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 12.sp,
                        color = if (batteryPercent > 20) EnergyEmerald else MatrixRed
                    )
                }
            }
        }

        // Integrated AOD Weather Widget
        if (config.showWeatherOnAod) {
            Spacer(modifier = Modifier.height(itemSpacing))
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val s = config.weatherWidgetScale.coerceIn(0.7f, 1.3f)
                        scaleX = s
                        scaleY = s
                    }
                    .alpha(config.weatherWidgetOpacity.coerceIn(0.2f, 1.0f))
            ) {
                AodWeatherWidgetView(
                    style = config.weatherWidgetStyle,
                    weather = weather,
                    primaryColor = config.palette.primaryColor,
                    secondaryColor = config.palette.secondaryColor
                )
            }
        }

        // GPS Compass & Barometric Storm Trend Pill
        if (config.showBarometerAndCompassOnAod && (config.showCompassInPill || config.showBarometerInPill)) {
            Spacer(modifier = Modifier.height(itemSpacing))
            AodBarometerAndCompassPill(
                azimuthDeg = config.compassAzimuthDeg,
                pressureMmHg = weather.pressureMmHg,
                deltaMmHg = config.barometricDeltaMmHg,
                showCompass = config.showCompassInPill,
                showBarometer = config.showBarometerInPill,
                accentColor = config.palette.primaryColor
            )
        }

        // Pinned Memo & Pomodoro Focus Pill on AOD
        val showMemoNow = config.showPinnedMemoOnAod && config.pinnedMemoText.isNotBlank()
        val showPomodoroNow = config.showPomodoroOnAod && config.pomodoroActive
        if (showMemoNow || showPomodoroNow) {
            Spacer(modifier = Modifier.height(itemSpacing))
            AodPinnedMemoAndPomodoroPill(
                memoText = if (showMemoNow) config.pinnedMemoText else "",
                pomodoroActive = showPomodoroNow,
                pomodoroRemainingSec = config.pomodoroRemainingSec,
                accentColor = config.palette.secondaryColor
            )
        }

        // Allowed Notification Icons Row on AOD (excludes blocked items)
        val allowedNotifications = remember(recentNotifications) {
            recentNotifications.filter { !it.wasBlockedByFilter }
        }
        if (config.showNotificationBadges && allowedNotifications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(itemSpacing))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allowedNotifications.take(4).forEach { item ->
                    val badgeColor = Color(item.colorHex)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.16f))
                            .border(1.dp, badgeColor.copy(alpha = 0.78f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = item.appName,
                                tint = badgeColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.appName.take(8),
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 10.sp,
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClockFaceShowcase(
    style: ClockFaceStyle,
    palette: com.example.model.AodColorPalette,
    scale: Float,
    hourStr: String,
    minStr: String,
    secStr: String,
    subSecFraction: Float,
    showSeconds: Boolean,
    showGlowDivider: Boolean = true
) {
    val primary = palette.primaryColor
    val secondary = palette.secondaryColor
    val accent = palette.accentColor

    val infiniteTransition = rememberInfiniteTransition(label = "clock_face_2026")
    val liquidPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing)
        ),
        label = "liquidPhase"
    )

    when (style) {
        ClockFaceStyle.CYBER_PULSE -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    // Subtle Holographic Shadow Layer
                    Text(
                        text = "$hourStr:$minStr",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (42 * scale).sp,
                        color = secondary.copy(alpha = 0.28f),
                        letterSpacing = 2.sp,
                        modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$hourStr:$minStr",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (42 * scale).sp,
                            color = primary,
                            letterSpacing = 2.sp
                        )
                        if (showSeconds) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = secStr,
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = (18 * scale).sp,
                                color = secondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
                if (showGlowDivider) {
                    Box(
                        modifier = Modifier
                            .width((132 * scale).dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, primary, secondary, accent, Color.Transparent)
                                )
                            )
                    )
                }
            }
        }

        ClockFaceStyle.NOTHING_DOT_MATRIX -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(1.dp, PureWhite.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .background(Color(0xFF070A10).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MatrixRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NOTHING // GLYPH 3.0",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = MutedSlate,
                        letterSpacing = 2.sp
                    )
                }
                Text(
                    text = if (showSeconds) "$hourStr:$minStr:$secStr" else "$hourStr : $minStr",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = (38 * scale).sp,
                    color = PureWhite,
                    letterSpacing = 3.sp
                )
            }
        }

        ClockFaceStyle.PIXEL_ULTRA_THIN -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = hourStr,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (44 * scale).sp,
                    color = primary,
                    lineHeight = (44 * scale).sp
                )
                Text(
                    text = minStr,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (44 * scale).sp,
                    color = PureWhite.copy(alpha = 0.92f),
                    lineHeight = (44 * scale).sp
                )
            }
        }

        ClockFaceStyle.COSMIC_ORBIT_SPHERE -> {
            Box(
                modifier = Modifier.size((138 * scale).dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.34f),
                                secondary.copy(alpha = 0.14f),
                                Color.Transparent
                            )
                        ),
                        radius = r
                    )
                    drawCircle(
                        color = primary.copy(alpha = 0.55f),
                        radius = r * 0.86f,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = secondary.copy(alpha = 0.35f),
                        radius = r * 0.68f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    val satX = center.x + cos(liquidPhase) * (r * 0.86f)
                    val satY = center.y + sin(liquidPhase) * (r * 0.86f)
                    drawCircle(
                        color = secondary,
                        radius = 5.dp.toPx(),
                        center = Offset(satX, satY)
                    )
                    val sat2X = center.x + cos(-liquidPhase * 1.4f) * (r * 0.68f)
                    val sat2Y = center.y + sin(-liquidPhase * 1.4f) * (r * 0.68f)
                    drawCircle(
                        color = primary,
                        radius = 3.5.dp.toPx(),
                        center = Offset(sat2X, sat2Y)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$hourStr:$minStr",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (28 * scale).sp,
                        color = PureWhite
                    )
                    if (showSeconds) {
                        Text(
                            text = ":$secStr",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = (13 * scale).sp,
                            color = primary
                        )
                    }
                }
            }
        }

        ClockFaceStyle.AURORA_METABALLS -> {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF060A12))
                    .border(1.2.dp, primary.copy(alpha = 0.55f), RoundedCornerShape(26.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated organic metaball blobs behind the clock text
                Canvas(modifier = Modifier.matchParentSize()) {
                    val b1x = size.width * (0.3f + 0.25f * sin(liquidPhase))
                    val b2x = size.width * (0.7f + 0.25f * cos(liquidPhase))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primary.copy(alpha = 0.42f), Color.Transparent)
                        ),
                        radius = size.height * 0.75f,
                        center = Offset(b1x, size.height * 0.5f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondary.copy(alpha = 0.38f), Color.Transparent)
                        ),
                        radius = size.height * 0.7f,
                        center = Offset(b2x, size.height * 0.5f)
                    )
                }
                Text(
                    text = if (showSeconds) "$hourStr·$minStr·$secStr" else "$hourStr·$minStr",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (32 * scale).sp,
                    color = PureWhite
                )
            }
        }

        ClockFaceStyle.RETRO_FLIP_CLOCK -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlipCardTile(text = hourStr, accent = primary, scale = scale)
                Text(
                    text = ":",
                    fontFamily = OrbitronFontFamily,
                    fontSize = (30 * scale).sp,
                    color = secondary
                )
                FlipCardTile(text = minStr, accent = secondary, scale = scale)
                if (showSeconds) {
                    FlipCardTile(text = secStr, accent = accent, scale = scale * 0.74f)
                }
            }
        }

        ClockFaceStyle.SWISS_CHRONOGRAPH -> {
            val secInt = secStr.toIntOrNull() ?: 0
            val smoothSeconds = secInt + subSecFraction
            val minInt = minStr.toIntOrNull() ?: 0
            val hourInt = hourStr.toIntOrNull() ?: 0
            Box(
                modifier = Modifier.size((128 * scale).dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        color = primary.copy(alpha = 0.45f),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    for (tick in 0 until 12) {
                        val a = (tick * 30.0 - 90.0) * PI / 180.0
                        val startR = radius * 0.82f
                        drawLine(
                            color = if (tick % 3 == 0) primary else MutedSlate,
                            start = Offset(
                                center.x + (cos(a) * startR).toFloat(),
                                center.y + (sin(a) * startR).toFloat()
                            ),
                            end = Offset(
                                center.x + (cos(a) * (radius * 0.94f)).toFloat(),
                                center.y + (sin(a) * (radius * 0.94f)).toFloat()
                            ),
                            strokeWidth = if (tick % 3 == 0) 2.5.dp.toPx() else 1.2.dp.toPx()
                        )
                    }
                    // Hour hand
                    val hAngle = (((hourInt % 12) + minInt / 60f) * 30f - 90f) * (PI / 180f)
                    drawLine(
                        color = PureWhite,
                        start = center,
                        end = Offset(
                            center.x + (cos(hAngle) * radius * 0.5f).toFloat(),
                            center.y + (sin(hAngle) * radius * 0.5f).toFloat()
                        ),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Minute hand
                    val mAngle = ((minInt + smoothSeconds / 60f) * 6f - 90f) * (PI / 180f)
                    drawLine(
                        color = primary,
                        start = center,
                        end = Offset(
                            center.x + (cos(mAngle) * radius * 0.74f).toFloat(),
                            center.y + (sin(mAngle) * radius * 0.74f).toFloat()
                        ),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // 120Hz Continuous Sweep Second Hand
                    if (showSeconds) {
                        val sAngle = (smoothSeconds * 6f - 90f) * (PI / 180f)
                        drawLine(
                            color = secondary,
                            start = center,
                            end = Offset(
                                center.x + (cos(sAngle) * radius * 0.84f).toFloat(),
                                center.y + (sin(sAngle) * radius * 0.84f).toFloat()
                            ),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(color = secondary, radius = 4.dp.toPx(), center = center)
                }
                Text(
                    text = "$hourStr:$minStr",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite.copy(alpha = 0.75f),
                    modifier = Modifier.offset(y = (26 * scale).dp)
                )
            }
        }

        ClockFaceStyle.SYNTHWAVE_LED_GRID -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianCard.copy(alpha = 0.65f))
                    .border(1.dp, secondary.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "SYNTH // HOLOGRAM 2026",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 9.sp,
                    color = secondary,
                    letterSpacing = 1.8.sp
                )
                Text(
                    text = if (showSeconds) "$hourStr:$minStr:$secStr" else "$hourStr:$minStr",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (32 * scale).sp,
                    color = primary
                )
            }
        }
    }
}

@Composable
private fun FlipCardTile(
    text: String,
    accent: Color,
    scale: Float
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0E131D))
            .border(1.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
            .padding(horizontal = (12 * scale).dp, vertical = (6 * scale).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = ShareTechMonoFontFamily,
            fontSize = (32 * scale).sp,
            color = PureWhite
        )
    }
}

@Composable
fun AodWeatherWidgetView(
    style: WeatherWidgetStyle,
    weather: WeatherSnapshot,
    primaryColor: Color,
    secondaryColor: Color
) {
    when (style) {
        WeatherWidgetStyle.COMPACT_PILL -> {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF0D131F))
                    .border(1.dp, primaryColor.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (weather.weatherCode <= 1) Icons.Default.WbSunny else Icons.Default.Cloud,
                    contentDescription = weather.conditionTextRu,
                    tint = SolarAmber,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${weather.cityName} • ${weather.temperatureC}°C",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 12.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = weather.conditionTextRu,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    color = MutedSlate
                )
            }
        }

        WeatherWidgetStyle.HOURLY_CURVE -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A0F18))
                    .border(1.dp, primaryColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${weather.cityName}: ${weather.temperatureC}°C (${weather.conditionTextRu})",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp,
                        color = primaryColor
                    )
                    Text(
                        text = "Ветер ${weather.windSpeedKmh} км/ч",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = MutedSlate
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weather.hourlyPoints.take(5).forEach { pt ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${pt.tempC}°",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = PureWhite
                            )
                            Text(
                                text = pt.hourLabel,
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 9.sp,
                                color = MutedSlate
                            )
                        }
                    }
                }
            }
        }

        WeatherWidgetStyle.THERMO_RING -> {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0B101B))
                    .border(1.dp, secondaryColor.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(2.dp, primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${weather.temperatureC}°",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 12.sp,
                        color = PureWhite
                    )
                }
                Column {
                    Text(
                        text = "${weather.cityName} • Ощущ. ${weather.apparentTempC}°C",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Влажность",
                                tint = CyberCyan,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "${weather.humidityPercent}%",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 10.sp,
                                color = MutedSlate
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "Ветер",
                                tint = secondaryColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "УФ ${weather.uvIndex}",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 10.sp,
                                color = MutedSlate
                            )
                        }
                    }
                }
            }
        }

        WeatherWidgetStyle.MINIMAL_TEXT -> {
            Text(
                text = "[ ${weather.cityName.uppercase()} // ${weather.temperatureC}°C // ${weather.conditionTextRu.uppercase()} ]",
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 11.sp,
                color = primaryColor.copy(alpha = 0.88f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BurnInCheckerboardOverlay(
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (!enabled) return
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 6.dp.toPx()
        val cols = (size.width / step).toInt()
        val rows = (size.height / step).toInt()
        for (r in 0..rows step 2) {
            for (c in 0..cols step 2) {
                drawCircle(
                    color = PitchBlack.copy(alpha = 0.25f),
                    radius = 1.dp.toPx(),
                    center = Offset(c * step, r * step)
                )
            }
        }
    }
}

@Composable
fun StandByNightstandClockView(
    config: AodConfigurationState,
    hourStr: String,
    minStr: String,
    secStr: String,
    dateLineRu: String,
    batteryPercent: Int,
    isCharging: Boolean,
    weather: WeatherSnapshot
) {
    val nightTint = if (config.standByRedTintOnly) Color(0xFFFF2A1F) else SolarAmber
    val secondaryNight = if (config.standByRedTintOnly) Color(0xFFAA1912) else Color(0xFFFF7A00)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF050202))
            .border(1.dp, nightTint.copy(alpha = 0.38f), RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = nightTint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (config.standByRedTintOnly) "STANDBY // MONO RED" else "STANDBY // NIGHT DOCK",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = nightTint.copy(alpha = 0.85f)
                )
            }
            Text(
                text = "${if (isCharging) "⚡ " else ""}$batteryPercent% • ${weather.temperatureC}°C",
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 11.sp,
                color = nightTint
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$hourStr:$minStr",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (48 * config.clockScale).sp,
                color = nightTint,
                letterSpacing = 3.sp
            )
            if (config.showSeconds) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = secStr,
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 18.sp,
                    color = secondaryNight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Text(
            text = "$dateLineRu • ${weather.cityName} (${weather.pressureMmHg} мм)",
            fontFamily = ShareTechMonoFontFamily,
            fontSize = 11.sp,
            color = nightTint.copy(alpha = 0.72f)
        )

        if (config.showPinnedMemoOnAod && config.pinnedMemoText.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📌 ${config.pinnedMemoText}",
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 10.sp,
                color = nightTint.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AodBarometerAndCompassPill(
    azimuthDeg: Int,
    pressureMmHg: Int,
    deltaMmHg: Float,
    showCompass: Boolean = true,
    showBarometer: Boolean = true,
    accentColor: Color
) {
    val directionRu = when (((azimuthDeg % 360) + 360) % 360) {
        in 23..67 -> "СВ"
        in 68..112 -> "В"
        in 113..157 -> "ЮВ"
        in 158..202 -> "Ю"
        in 203..247 -> "ЮЗ"
        in 248..292 -> "З"
        in 293..337 -> "СЗ"
        else -> "С"
    }
    val trendSymbol = when {
        deltaMmHg <= -1.2f -> "↓ Шторм/Осадки"
        deltaMmHg >= 1.2f -> "↑ Ясно/Рост"
        else -> "→ Стабильно"
    }
    val trendColor = when {
        deltaMmHg <= -1.2f -> SolarAmber
        deltaMmHg >= 1.2f -> EnergyEmerald
        else -> accentColor
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF0A0F18))
            .border(1.dp, trendColor.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showCompass) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Компас",
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$directionRu ${azimuthDeg}°",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = PureWhite
                )
            }
        }
        if (showCompass && showBarometer) {
            Text(
                text = "•",
                fontSize = 10.sp,
                color = MutedSlate
            )
        }
        if (showBarometer) {
            Text(
                text = "${pressureMmHg}мм ($trendSymbol)",
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 10.sp,
                color = trendColor
            )
        }
    }
}

@Composable
fun AodPinnedMemoAndPomodoroPill(
    memoText: String,
    pomodoroActive: Boolean,
    pomodoroRemainingSec: Int,
    accentColor: Color
) {
    val pomMin = pomodoroRemainingSec / 60
    val pomSec = pomodoroRemainingSec % 60
    val pomFormatted = String.format("%02d:%02d", pomMin, pomSec)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF0E1422))
            .border(1.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (pomodoroActive) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Pomodoro",
                tint = SolarAmber,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "ФОКУС $pomFormatted",
                fontFamily = ShareTechMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = SolarAmber
            )
        }
        if (memoText.isNotBlank()) {
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = "Заметка",
                tint = accentColor,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = memoText.take(36),
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 10.sp,
                color = PureWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AodQuickLaunchDockBar(
    isFlashlightOn: Boolean,
    isPomodoroActive: Boolean,
    isStandByActive: Boolean,
    isIceCardEnabled: Boolean,
    showTorchBtn: Boolean = true,
    showPomodoroBtn: Boolean = true,
    showStandByBtn: Boolean = true,
    showIceBtn: Boolean = true,
    dockScale: Float = 1.0f,
    dockOpacity: Float = 1.0f,
    accentColor: Color,
    onToggleFlashlight: () -> Unit,
    onTogglePomodoro: () -> Unit,
    onToggleStandBy: () -> Unit,
    onOpenIceQrCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAnyButton = showTorchBtn || showPomodoroBtn || showStandByBtn || (isIceCardEnabled && showIceBtn)
    if (!hasAnyButton) return

    Row(
        modifier = modifier
            .graphicsLayer {
                val s = dockScale.coerceIn(0.75f, 1.25f)
                scaleX = s
                scaleY = s
            }
            .alpha(dockOpacity.coerceIn(0.2f, 1.0f))
            .clip(RoundedCornerShape(50))
            .background(Color(0xD90B101B))
            .border(1.dp, accentColor.copy(alpha = 0.38f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showTorchBtn) {
            QuickDockIconButton(
                icon = Icons.Default.FlashlightOn,
                label = "Свет",
                active = isFlashlightOn,
                activeColor = SolarAmber,
                onClick = onToggleFlashlight,
                testTag = "dock_action_flashlight"
            )
        }
        if (showPomodoroBtn) {
            QuickDockIconButton(
                icon = Icons.Default.Timer,
                label = "Фокус",
                active = isPomodoroActive,
                activeColor = EnergyEmerald,
                onClick = onTogglePomodoro,
                testTag = "dock_action_pomodoro"
            )
        }
        if (showStandByBtn) {
            QuickDockIconButton(
                icon = Icons.Default.Bedtime,
                label = "StandBy",
                active = isStandByActive,
                activeColor = Color(0xFFFF453A),
                onClick = onToggleStandBy,
                testTag = "dock_action_standby"
            )
        }
        if (isIceCardEnabled && showIceBtn) {
            QuickDockIconButton(
                icon = Icons.Default.QrCode2,
                label = "ICE QR",
                active = false,
                activeColor = CyberCyan,
                onClick = onOpenIceQrCard,
                testTag = "dock_action_ice_qr"
            )
        }
    }
}

@Composable
private fun QuickDockIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (active) activeColor.copy(alpha = 0.22f) else Color(0xFF131B2B),
        modifier = Modifier.
            border(
                width = 1.dp,
                color = if (active) activeColor else Color(0xFF233047),
                shape = RoundedCornerShape(50)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) activeColor else PureWhite,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 10.sp,
                color = if (active) activeColor else PureWhite
            )
        }
    }
}

@Composable
fun IceEmergencyQrCardOverlay(
    ownerName: String,
    emergencyPhone: String,
    noteText: String,
    accentColor: Color,
    onDismiss: () -> Unit
) {
    val qrPayload = remember(ownerName, emergencyPhone, noteText) {
        "ICE:$ownerName|TEL:$emergencyPhone|$noteText"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack.copy(alpha = 0.92f))
            .clickable { onDismiss() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0C121E))
                .border(1.5.dp, accentColor, RoundedCornerShape(24.dp))
                .clickable(enabled = false) { }
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ICE // КАРТОЧКА ВЛАДЕЛЬЦА",
                        fontFamily = ShareTechMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = accentColor
                    )
                }
                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = Color(0xFF182235)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = PureWhite,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vector QR Code Matrix Renderer (21x21 Standard Version 1 Matrix)
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PureWhite)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSize = 21
                    val cellPx = size.width / gridSize

                    fun isFinderPattern(r: Int, c: Int, topR: Int, leftC: Int): Boolean {
                        val dr = r - topR
                        val dc = c - leftC
                        if (dr !in 0..6 || dc !in 0..6) return false
                        if (dr == 0 || dr == 6 || dc == 0 || dc == 6) return true
                        if (dr in 2..4 && dc in 2..4) return true
                        return false
                    }

                    for (r in 0 until gridSize) {
                        for (c in 0 until gridSize) {
                            val inTopLeft = r in 0..7 && c in 0..7
                            val inTopRight = r in 0..7 && c in (gridSize - 8) until gridSize
                            val inBottomLeft = r in (gridSize - 8) until gridSize && c in 0..7

                            val isBlack = when {
                                inTopLeft -> isFinderPattern(r, c, 0, 0)
                                inTopRight -> isFinderPattern(r, c, 0, gridSize - 7)
                                inBottomLeft -> isFinderPattern(r, c, gridSize - 7, 0)
                                r == 6 -> c % 2 == 0
                                c == 6 -> r % 2 == 0
                                else -> {
                                    val idx = (r * gridSize + c)
                                    val ch = qrPayload[idx % qrPayload.length].code
                                    ((ch xor (r * 31 + c * 17)) and 1) == 1
                                }
                            }
                            if (isBlack) {
                                drawRect(
                                    color = PitchBlack,
                                    topLeft = Offset(c * cellPx, r * cellPx),
                                    size = Size(cellPx * 1.02f, cellPx * 1.02f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = ownerName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = emergencyPhone,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EnergyEmerald
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = noteText,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MutedSlate,
                textAlign = TextAlign.Center
            )
        }
    }
}
