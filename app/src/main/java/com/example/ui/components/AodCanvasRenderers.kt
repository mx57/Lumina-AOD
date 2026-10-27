package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val infiniteTransition = rememberInfiniteTransition(label = "edge_and_ring_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
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

        // 1. Render Edge Lighting
        when (config.edgeStyle) {
            EdgeLightingStyle.NEON_WAVE -> {
                val angle = phase * 2f * PI.toFloat()
                val cx = w / 2f + cos(angle) * (w * 0.4f)
                val cy = h / 2f + sin(angle) * (h * 0.4f)
                val brush = Brush.sweepGradient(
                    colors = listOf(primary, secondary, accent, primary),
                    center = Offset(cx.coerceIn(1f, w - 1f), cy.coerceIn(1f, h - 1f))
                )
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
            EdgeLightingStyle.LASER_CHASE -> {
                val strokeW = 4.dp.toPx()
                val progressY = phase * h
                val invY = (1f - phase) * h
                // Left laser beam
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, primary, secondary, Color.Transparent),
                        startY = (progressY - h * 0.25f),
                        endY = (progressY + h * 0.25f)
                    ),
                    start = Offset(strokeW, 0f),
                    end = Offset(strokeW, h),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                // Right laser beam
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, secondary, primary, Color.Transparent),
                        startY = (invY - h * 0.25f),
                        endY = (invY + h * 0.25f)
                    ),
                    start = Offset(w - strokeW, 0f),
                    end = Offset(w - strokeW, h),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
            EdgeLightingStyle.AUDIO_SPECTRUM -> {
                val barCount = 18
                val stepY = (h * 0.65f) / barCount
                val startY = h * 0.18f
                for (i in 0 until barCount) {
                    val wave = sin((phase * 2f * PI + i * 0.55).toFloat())
                    val barLen = (8.dp.toPx() + (wave + 1f) * 10.dp.toPx())
                    val y = startY + i * stepY
                    val barColor = if (i % 2 == 0) primary else secondary
                    // Left equalizer bar
                    drawLine(
                        color = barColor.copy(alpha = 0.85f),
                        start = Offset(2.dp.toPx(), y),
                        end = Offset(2.dp.toPx() + barLen, y),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Right equalizer bar
                    drawLine(
                        color = barColor.copy(alpha = 0.85f),
                        start = Offset(w - 2.dp.toPx(), y),
                        end = Offset(w - 2.dp.toPx() - barLen, y),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            EdgeLightingStyle.BREATHING_GLOW -> {
                drawRoundRect(
                    color = primary.copy(alpha = breathe * 0.85f),
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = (2.5f + breathe * 3f).dp.toPx())
                )
            }
            EdgeLightingStyle.CORNER_SPARKS -> {
                val arcSize = 54.dp.toPx()
                val pad = 4.dp.toPx()
                val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                val sparkColor = primary.copy(alpha = 0.5f + 0.5f * breathe)
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
                    color = secondary.copy(alpha = 0.5f + 0.5f * breathe),
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - arcSize - pad, pad),
                    size = Size(arcSize, arcSize),
                    style = stroke
                )
                drawArc(
                    color = secondary.copy(alpha = 0.5f + 0.5f * breathe),
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

        // 2. Render Camera Cutout Notification & Battery Energy Ring
        val holeCenterX = (w / 2f) + config.cameraHoleOffsetXDp.dp.toPx()
        val holeCenterY = config.cameraHoleOffsetYDp.dp.toPx() + config.cameraRingRadiusDp.dp.toPx()
        val ringRadius = config.cameraRingRadiusDp.dp.toPx()
        val ringThickness = config.cameraRingThicknessDp.dp.toPx()

        // Always draw physical camera punch-hole core
        drawCircle(
            color = PitchBlack,
            radius = (ringRadius - ringThickness * 0.6f).coerceAtLeast(6f),
            center = Offset(holeCenterX, holeCenterY)
        )

        if (config.isCameraRingEnabled) {
            when (config.cameraRingMode) {
                CameraRingMode.COMBO_PULSE_AND_BATTERY -> {
                    // Outer subtle rotating notification ring
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(notifColor, primary, notifColor),
                            center = Offset(holeCenterX, holeCenterY)
                        ),
                        startAngle = phase * 360f,
                        sweepAngle = 290f,
                        useCenter = false,
                        topLeft = Offset(holeCenterX - ringRadius, holeCenterY - ringRadius),
                        size = Size(ringRadius * 2f, ringRadius * 2f),
                        style = Stroke(width = ringThickness, cap = StrokeCap.Round)
                    )
                    // Inner battery level arc
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
                    // Track background
                    drawCircle(
                        color = batteryColor.copy(alpha = 0.2f),
                        radius = ringRadius,
                        center = Offset(holeCenterX, holeCenterY),
                        style = Stroke(width = ringThickness)
                    )
                    // Battery progress arc
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
    modifier: Modifier = Modifier
) {
    var currentMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val dateObj = remember(currentMillis) { Date(currentMillis) }
    val hourStr = remember(currentMillis) { SimpleDateFormat("HH", Locale.getDefault()).format(dateObj) }
    val minStr = remember(currentMillis) { SimpleDateFormat("mm", Locale.getDefault()).format(dateObj) }
    val secStr = remember(currentMillis) { SimpleDateFormat("ss", Locale.getDefault()).format(dateObj) }
    val dateLineRu = remember(currentMillis) {
        SimpleDateFormat("EEE, d MMMM", Locale("ru")).format(dateObj).uppercase(Locale("ru"))
    }

    val shiftX = if (config.pixelShiftEnabled) pixelShiftOffsetX.dp else 0.dp
    val shiftY = if (config.pixelShiftEnabled) pixelShiftOffsetY.dp else 0.dp

    Column(
        modifier = modifier
            .offset(x = shiftX, y = shiftY)
            .alpha(config.aodBrightness)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Selected Clock Face Renderer
        ClockFaceShowcase(
            style = config.clockStyle,
            palette = config.palette,
            scale = config.clockScale,
            hourStr = hourStr,
            minStr = minStr,
            secStr = secStr,
            showSeconds = config.showSeconds
        )

        if (config.showDateAndBattery) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dateLineRu,
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 12.sp,
                    color = PureWhite.copy(alpha = 0.78f),
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                    contentDescription = "Заряд батареи",
                    tint = if (batteryPercent > 20) EnergyEmerald else MatrixRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$batteryPercent%",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 12.sp,
                    color = if (batteryPercent > 20) EnergyEmerald else MatrixRed
                )
            }
        }

        // Integrated AOD Weather Widget
        if (config.showWeatherOnAod) {
            Spacer(modifier = Modifier.height(10.dp))
            AodWeatherWidgetView(
                style = config.weatherWidgetStyle,
                weather = weather,
                primaryColor = config.palette.primaryColor,
                secondaryColor = config.palette.secondaryColor
            )
        }

        // Notification Icons Row on AOD
        if (config.showNotificationBadges && recentNotifications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                recentNotifications.take(4).forEach { item ->
                    val badgeColor = Color(item.colorHex)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.16f))
                            .border(1.dp, badgeColor.copy(alpha = 0.75f), CircleShape)
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
    showSeconds: Boolean
) {
    val primary = palette.primaryColor
    val secondary = palette.secondaryColor
    val accent = palette.accentColor

    when (style) {
        ClockFaceStyle.CYBER_PULSE -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                Box(
                    modifier = Modifier
                        .width((120 * scale).dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, primary, secondary, Color.Transparent)
                            )
                        )
                )
            }
        }

        ClockFaceStyle.NOTHING_DOT_MATRIX -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(1.dp, PureWhite.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
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
                        text = "NOTHING // GLYPH OS",
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
                    color = PureWhite.copy(alpha = 0.9f),
                    lineHeight = (44 * scale).sp
                )
            }
        }

        ClockFaceStyle.COSMIC_ORBIT_SPHERE -> {
            val infiniteTransition = rememberInfiniteTransition(label = "orbit")
            val orbitAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 6000, easing = LinearEasing)
                ),
                label = "orbitAngle"
            )
            Box(
                modifier = Modifier.size((134 * scale).dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primary.copy(alpha = 0.28f), secondary.copy(alpha = 0.1f), Color.Transparent)
                        ),
                        radius = r
                    )
                    drawCircle(
                        color = primary.copy(alpha = 0.55f),
                        radius = r * 0.85f,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    val rad = (orbitAngle * PI / 180.0).toFloat()
                    val satX = center.x + cos(rad) * (r * 0.85f)
                    val satY = center.y + sin(rad) * (r * 0.85f)
                    drawCircle(
                        color = secondary,
                        radius = 4.5.dp.toPx(),
                        center = Offset(satX, satY)
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
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.20f),
                                secondary.copy(alpha = 0.18f),
                                accent.copy(alpha = 0.20f)
                            )
                        )
                    )
                    .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
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
                    FlipCardTile(text = secStr, accent = accent, scale = scale * 0.72f)
                }
            }
        }

        ClockFaceStyle.SWISS_CHRONOGRAPH -> {
            val secInt = secStr.toIntOrNull() ?: 0
            val minInt = minStr.toIntOrNull() ?: 0
            val hourInt = hourStr.toIntOrNull() ?: 0
            Box(
                modifier = Modifier.size((124 * scale).dp),
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
                    val mAngle = (minInt * 6f - 90f) * (PI / 180f)
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
                    // Second hand
                    if (showSeconds) {
                        val sAngle = (secInt * 6f - 90f) * (PI / 180f)
                        drawLine(
                            color = secondary,
                            start = center,
                            end = Offset(
                                center.x + (cos(sAngle) * radius * 0.82f).toFloat(),
                                center.y + (sin(sAngle) * radius * 0.82f).toFloat()
                            ),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(color = secondary, radius = 4.dp.toPx(), center = center)
                }
                Text(
                    text = "$hourStr:$minStr",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite.copy(alpha = 0.7f),
                    modifier = Modifier.offset(y = (26 * scale).dp)
                )
            }
        }

        ClockFaceStyle.SYNTHWAVE_LED_GRID -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianCard.copy(alpha = 0.55f))
                    .border(1.dp, secondary.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "SYNTH // AMOLED 84",
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
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
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
                    .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(50))
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
                    .border(1.dp, primaryColor.copy(alpha = 0.32f), RoundedCornerShape(14.dp))
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
