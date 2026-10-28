package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AodConfigurationState
import com.example.model.IslandActivityType
import com.example.model.IslandCallState
import com.example.model.IslandMediaState
import com.example.model.IslandMessageState
import com.example.model.IslandShapeStyle
import com.example.model.IslandTimerState
import com.example.model.IslandVisibilityMode
import com.example.model.WeatherSnapshot
import com.example.service.HardwareTelemetryState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DynamicIslandOverlay(
    config: AodConfigurationState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    isCharging: Boolean,
    unreadNotificationsCount: Int,
    hasRecentPushEvent: Boolean = true,
    telemetry: HardwareTelemetryState = HardwareTelemetryState(),
    onToggleExpand: () -> Unit,
    onPrimaryTapIsland: () -> Unit = onToggleExpand,
    onLongPressIsland: () -> Unit = {},
    onOpenParentApp: () -> Unit = {},
    onRestoreDismissedIsland: () -> Unit = {},
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddMinuteTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit,
    onToggleFlashlight: () -> Unit = {},
    onSwitchIslandActivity: (IslandActivityType) -> Unit,
    onDragIslandByDeltaDp: (Float, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!config.isDynamicIslandEnabled) return

    val shouldShowNow = config.shouldRenderIslandNow(
        unreadCount = unreadNotificationsCount,
        hasRecentPushEvent = hasRecentPushEvent
    )

    // When hidden in idle mode ("Появляться только при уведомлениях") or after long-press dismiss,
    // show a tiny unobtrusive restore badge only in studio preview if dismissed manually.
    if (!shouldShowNow) {
        if (config.islandTemporaryDismissed || config.islandVisibilityMode != IslandVisibilityMode.ALWAYS_VISIBLE) {
            Box(
                modifier = modifier
                    .offset(x = config.islandOffsetXDp.dp, y = config.islandOffsetYDp.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF0A101D).copy(alpha = 0.72f))
                    .border(0.8.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .clickable { onRestoreDismissedIsland() }
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .testTag("restore_hidden_island_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Показать Остров",
                        tint = CyberCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = if (config.islandTemporaryDismissed) "Остров скрыт (Тап)" else "Ожидание пуша (Тап — тест)",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 9.sp,
                        color = MutedSlate
                    )
                }
            }
        }
        return
    }

    val density = LocalDensity.current
    var dragVelX by remember { mutableFloatStateOf(0f) }
    var dragVelY by remember { mutableFloatStateOf(0f) }

    val primaryColor = config.palette.primaryColor
    val secondaryColor = config.palette.secondaryColor

    val damping = (0.95f - (config.islandSpringBounciness * 0.55f)).coerceIn(0.32f, 0.88f)
    val baseCompactWidth = when (config.islandShape) {
        IslandShapeStyle.LIQUID_SPHERE, IslandShapeStyle.STARLIGHT_ORB -> 136f
        IslandShapeStyle.ULTRA_THREAD -> 196f
        IslandShapeStyle.SPLIT_DROP -> 178f
        IslandShapeStyle.MATRIX_TICKER_BAR, IslandShapeStyle.AUDIO_SPECTRUM_WAVE -> 212f
        IslandShapeStyle.DUAL_CYBER_WINGS, IslandShapeStyle.FLOATING_NOTCH -> 194f
        else -> 172f
    }
    val baseCompactHeight = when (config.islandShape) {
        IslandShapeStyle.ULTRA_THREAD -> 24f
        IslandShapeStyle.LIQUID_SPHERE, IslandShapeStyle.STARLIGHT_ORB -> 38f
        IslandShapeStyle.MATRIX_TICKER_BAR -> 36f
        IslandShapeStyle.FLOATING_NOTCH -> 32f
        else -> 34f
    }

    val targetWidthDp = if (config.isIslandExpanded) {
        (286f * config.islandWidthScale).coerceIn(230f, 326f).dp
    } else {
        (baseCompactWidth * config.islandWidthScale).coerceIn(115f, 260f).dp
    }

    val targetHeightDp = if (config.isIslandExpanded) {
        (166f * config.islandHeightScale).coerceIn(138f, 220f).dp
    } else {
        (baseCompactHeight * config.islandHeightScale).coerceIn(22f, 54f).dp
    }

    val animatedWidth by animateDpAsState(
        targetValue = targetWidthDp,
        animationSpec = spring(dampingRatio = damping, stiffness = Spring.StiffnessMediumLow),
        label = "island_width_2026"
    )
    val animatedHeight by animateDpAsState(
        targetValue = targetHeightDp,
        animationSpec = spring(dampingRatio = damping, stiffness = Spring.StiffnessMediumLow),
        label = "island_height_2026"
    )

    val animOffsetX by animateDpAsState(
        targetValue = config.islandOffsetXDp.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "island_offset_x"
    )
    val animOffsetY by animateDpAsState(
        targetValue = config.islandOffsetYDp.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "island_offset_y"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "island_liquid_shader_2026")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 / config.animationSpeedMultiplier.coerceIn(0.5f, 2.0f)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val tiltRotationZ by animateFloatAsState(
        targetValue = (dragVelX * 0.15f).coerceIn(-6f, 6f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tiltRotationZ"
    )

    val shapeOutline = when (config.islandShape) {
        IslandShapeStyle.CYBER_HEXAGON, IslandShapeStyle.DUAL_CYBER_WINGS -> CutCornerShape(
            topStart = if (config.isIslandExpanded) 18.dp else 12.dp,
            topEnd = if (config.isIslandExpanded) 18.dp else 12.dp,
            bottomStart = if (config.isIslandExpanded) 18.dp else 12.dp,
            bottomEnd = if (config.isIslandExpanded) 18.dp else 12.dp
        )
        IslandShapeStyle.ULTRA_THREAD -> RoundedCornerShape(50)
        IslandShapeStyle.MATRIX_TICKER_BAR -> RoundedCornerShape(10.dp)
        IslandShapeStyle.FLOATING_NOTCH -> RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 20.dp,
            bottomEnd = 20.dp
        )
        else -> RoundedCornerShape(
            if (config.isIslandExpanded) 28.dp else config.islandCornerRadiusDp.dp
        )
    }

    val containerBgAlpha = when (config.islandShape) {
        IslandShapeStyle.HIDDEN_OUTLINE -> if (config.isIslandExpanded) 0.88f else 0.18f
        else -> config.islandGlassOpacity
    }

    // Build dynamic Marquee Ticker string
    val marqueeSummaryText = remember(
        config.activeIslandType,
        config.islandCustomTickerText,
        config.islandShowWeatherInTicker,
        messageState,
        mediaState,
        weather,
        telemetry.batteryPercent
    ) {
        val parts = mutableListOf<String>()
        if (config.islandCustomTickerText.isNotBlank()) {
            parts.add(config.islandCustomTickerText.trim())
        }
        when (config.activeIslandType) {
            IslandActivityType.MESSAGE_NOTIFICATION -> {
                parts.add("${messageState.appName} • ${messageState.sender}: ${messageState.message}")
            }
            IslandActivityType.MEDIA_PLAYER -> {
                parts.add("♫ ${mediaState.trackTitle} — ${mediaState.artistName}")
            }
            IslandActivityType.INCOMING_CALL -> {
                parts.add("Входящий вызов: ${callState.callerName} (${callState.callerNumber})")
            }
            IslandActivityType.ACTIVE_TIMER -> {
                parts.add("Таймер: ${timerState.label} (${timerState.remainingSeconds / 60}:${"%02d".format(timerState.remainingSeconds % 60)})")
            }
            IslandActivityType.WEATHER_ALERT, IslandActivityType.MARQUEE_TICKER -> {
                parts.add("${weather.cityName}: ${if (weather.temperatureC > 0) "+${weather.temperatureC}" else "${weather.temperatureC}"}°C (${weather.conditionTextRu}, ветер ${weather.windSpeedKmh} км/ч, ${weather.pressureMmHg} мм)")
            }
            IslandActivityType.SYSTEM_TELEMETRY -> {
                parts.add("CPU ${telemetry.cpuLoadPercent}% • RAM ${telemetry.ramUsedGb}/${telemetry.ramTotalGb} ГБ • Заряд $batteryPercent%")
            }
            IslandActivityType.BATTERY_TURBO -> {
                parts.add("AMOLED Turbo Charge: $batteryPercent% (${telemetry.batteryTempCelsius}°C)")
            }
        }
        if (config.islandShowWeatherInTicker && config.activeIslandType != IslandActivityType.WEATHER_ALERT) {
            parts.add("${weather.cityName} ${if (weather.temperatureC > 0) "+${weather.temperatureC}" else "${weather.temperatureC}"}°C • ${weather.conditionTextRu}")
        }
        parts.joinToString("   ✦   ")
    }

    Box(
        modifier = modifier
            .offset(x = animOffsetX, y = animOffsetY)
            .graphicsLayer {
                rotationZ = tiltRotationZ
            }
            .pointerInput(config.isIslandFreeDragEnabled) {
                if (config.isIslandFreeDragEnabled) {
                    detectDragGestures(
                        onDragEnd = {
                            dragVelX = 0f
                            dragVelY = 0f
                        },
                        onDragCancel = {
                            dragVelX = 0f
                            dragVelY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val dxDp = with(density) { dragAmount.x.toDp().value }
                            val dyDp = with(density) { dragAmount.y.toDp().value }
                            dragVelX = dxDp
                            dragVelY = dyDp
                            onDragIslandByDeltaDp(dxDp, dyDp)
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. Liquid Metaball Tendril Bridge & Special Shape Canvas Layer
        val distFromCameraY = abs(config.islandOffsetYDp - config.cameraHoleOffsetYDp)
        val distFromCameraX = abs(config.islandOffsetXDp - config.cameraHoleOffsetXDp)
        Canvas(
            modifier = Modifier
                .width(animatedWidth + 52.dp)
                .height(animatedHeight + 28.dp)
        ) {
            val w = size.width
            val h = size.height
            val glowAlpha = (config.islandNeonGlowIntensity * 0.44f * glowPulse).coerceIn(0f, 0.78f)

            // Outer Shader Bloom Halo
            if (config.islandNeonGlowIntensity > 0.05f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = glowAlpha),
                            secondaryColor.copy(alpha = glowAlpha * 0.85f),
                            primaryColor.copy(alpha = glowAlpha)
                        )
                    ),
                    topLeft = Offset(14.dp.toPx(), 6.dp.toPx()),
                    size = Size(w - 28.dp.toPx(), h - 12.dp.toPx()),
                    cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx())
                )
            }

            // Viscous Metaball Neck connecting to camera cutout when stretched nearby
            if (config.liquidMetaballMorphEnabled && distFromCameraY in 2f..36f && distFromCameraX < 40f && !config.isIslandExpanded) {
                val bridgeFactor = (1f - (distFromCameraY / 36f)).coerceIn(0f, 1f)
                val neckWidth = (18.dp.toPx() * bridgeFactor).coerceAtLeast(4.dp.toPx())
                val path = Path().apply {
                    moveTo(w / 2f - neckWidth, 0f)
                    quadraticTo(w / 2f, 10.dp.toPx() * bridgeFactor, w / 2f - neckWidth * 1.6f, 14.dp.toPx())
                    lineTo(w / 2f + neckWidth * 1.6f, 14.dp.toPx())
                    quadraticTo(w / 2f, 10.dp.toPx() * bridgeFactor, w / 2f + neckWidth, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.7f * bridgeFactor), PitchBlack)
                    )
                )
            }

            // Shape-specific 120Hz vector decorations
            when (config.islandShape) {
                IslandShapeStyle.DUAL_CYBER_WINGS -> {
                    val wingW = 12.dp.toPx()
                    val wingH = (h * 0.48f)
                    val wingY = (h - wingH) / 2f
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.85f),
                        topLeft = Offset(4.dp.toPx(), wingY),
                        size = Size(wingW, wingH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                    drawRoundRect(
                        color = secondaryColor.copy(alpha = 0.85f),
                        topLeft = Offset(w - 4.dp.toPx() - wingW, wingY),
                        size = Size(wingW, wingH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }
                IslandShapeStyle.STARLIGHT_ORB -> {
                    val rayCount = 8
                    val cx = w / 2f
                    val cy = h / 2f
                    for (rIdx in 0 until rayCount) {
                        val angle = (rIdx * (360f / rayCount) + wavePhase * 25f) * (PI / 180f)
                        val rInner = (w.coerceAtMost(h) * 0.36f)
                        val rOuter = rInner + 6.dp.toPx() * glowPulse
                        drawLine(
                            color = if (rIdx % 2 == 0) primaryColor.copy(alpha = 0.65f) else secondaryColor.copy(alpha = 0.65f),
                            start = Offset(
                                cx + (kotlin.math.cos(angle) * rInner).toFloat(),
                                cy + (sin(angle) * rInner).toFloat()
                            ),
                            end = Offset(
                                cx + (kotlin.math.cos(angle) * rOuter).toFloat(),
                                cy + (sin(angle) * rOuter).toFloat()
                            ),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                IslandShapeStyle.AUDIO_SPECTRUM_WAVE -> {
                    val bars = 16
                    val startX = 24.dp.toPx()
                    val usableW = w - 48.dp.toPx()
                    val stepX = usableW / bars
                    for (b in 0 until bars) {
                        val amp = (0.3f + 0.7f * abs(sin(wavePhase + b * 0.45f))) * 6.dp.toPx()
                        val bx = startX + b * stepX
                        drawLine(
                            color = if (b % 2 == 0) primaryColor.copy(alpha = 0.8f) else secondaryColor.copy(alpha = 0.8f),
                            start = Offset(bx, 6.dp.toPx() - amp),
                            end = Offset(bx, 6.dp.toPx()),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                else -> Unit
            }
        }

        // 2. Main Island Body + Optional Split-Drop Satellite Droplet
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(animatedHeight)
                    .clip(shapeOutline)
                    .background(Color(0xFF060911).copy(alpha = containerBgAlpha))
                    .border(
                        width = config.islandBorderWidthDp.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.92f),
                                secondaryColor.copy(alpha = 0.75f),
                                primaryColor.copy(alpha = 0.45f)
                            )
                        ),
                        shape = shapeOutline
                    )
                    .combinedClickable(
                        onClick = onPrimaryTapIsland,
                        onLongClick = onLongPressIsland
                    )
                    .padding(
                        horizontal = if (config.isIslandExpanded) 14.dp else 10.dp,
                        vertical = if (config.isIslandExpanded) 10.dp else 4.dp
                    )
                    .testTag("dynamic_island_overlay"),
                contentAlignment = Alignment.Center
            ) {
                // Subtle Matrix Grid background when MATRIX_TICKER_BAR is active
                if (config.islandShape == IslandShapeStyle.MATRIX_TICKER_BAR) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val dotStep = 6.dp.toPx()
                        var x = 4.dp.toPx()
                        while (x < size.width) {
                            var y = 4.dp.toPx()
                            while (y < size.height) {
                                drawCircle(
                                    color = primaryColor.copy(alpha = 0.08f),
                                    radius = 0.9.dp.toPx(),
                                    center = Offset(x, y)
                                )
                                y += dotStep
                            }
                            x += dotStep
                        }
                    }
                }

                AnimatedContent(
                    targetState = config.isIslandExpanded,
                    transitionSpec = {
                        (fadeIn(tween(220)) + scaleIn(initialScale = 0.92f))
                            .togetherWith(fadeOut(tween(150)) + scaleOut(targetScale = 0.92f))
                    },
                    label = "island_content_morph"
                ) { expanded ->
                    if (!expanded) {
                        CompactIslandPillContent(
                            config = config,
                            mediaState = mediaState,
                            timerState = timerState,
                            messageState = messageState,
                            callState = callState,
                            weather = weather,
                            batteryPercent = batteryPercent,
                            unreadCount = unreadNotificationsCount,
                            wavePhase = wavePhase,
                            marqueeText = marqueeSummaryText,
                            telemetry = telemetry,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor
                        )
                    } else {
                        ExpandedIslandCardContent(
                            activeType = config.activeIslandType,
                            config = config,
                            mediaState = mediaState,
                            timerState = timerState,
                            messageState = messageState,
                            callState = callState,
                            weather = weather,
                            batteryPercent = batteryPercent,
                            isCharging = isCharging,
                            wavePhase = wavePhase,
                            marqueeText = marqueeSummaryText,
                            telemetry = telemetry,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            onCollapse = onToggleExpand,
                            onOpenParentApp = onOpenParentApp,
                            onTogglePlayPause = onTogglePlayPause,
                            onSkipNext = onSkipNext,
                            onToggleTimer = onToggleTimer,
                            onAddMinuteTimer = onAddMinuteTimer,
                            onResetTimer = onResetTimer,
                            onSendQuickReply = onSendQuickReply,
                            onAcceptOrEndCall = onAcceptOrEndCall,
                            onToggleFlashlight = onToggleFlashlight,
                            onSwitchIslandActivity = onSwitchIslandActivity
                        )
                    }
                }
            }

            // Detached Liquid Satellite Droplet when SPLIT_DROP (iPhone Pro style) is selected & compact
            AnimatedVisibility(
                visible = config.islandShape == IslandShapeStyle.SPLIT_DROP && !config.isIslandExpanded,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(
                        modifier = Modifier
                            .width(9.dp)
                            .height(14.dp)
                    ) {
                        if (config.liquidMetaballMorphEnabled) {
                            val bridgeAlpha = (0.35f + 0.35f * sin(wavePhase)).coerceIn(0.15f, 0.75f)
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(primaryColor.copy(alpha = bridgeAlpha), secondaryColor.copy(alpha = bridgeAlpha))
                                ),
                                start = Offset(0f, size.height / 2f),
                                end = Offset(size.width, size.height / 2f),
                                strokeWidth = 3.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(targetHeightDp)
                            .clip(CircleShape)
                            .background(Color(0xFF060911).copy(alpha = containerBgAlpha))
                            .border(
                                width = config.islandBorderWidthDp.dp,
                                color = secondaryColor.copy(alpha = 0.88f),
                                shape = CircleShape
                            )
                            .clickable { onOpenParentApp() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (unreadNotificationsCount > 0) {
                            Text(
                                text = unreadNotificationsCount.coerceAtMost(9).toString(),
                                fontFamily = ShareTechMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = PureWhite
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactIslandPillContent(
    config: AodConfigurationState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    unreadCount: Int,
    wavePhase: Float,
    marqueeText: String,
    telemetry: HardwareTelemetryState,
    primaryColor: Color,
    secondaryColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Indicator / Activity Icon + Drag Grip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (config.isIslandFreeDragEnabled) {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Перетащить Остров",
                    tint = MutedSlate.copy(alpha = 0.65f),
                    modifier = Modifier.size(12.dp)
                )
            }

            val leadingIcon = when (config.activeIslandType) {
                IslandActivityType.MEDIA_PLAYER -> Icons.Default.MusicNote
                IslandActivityType.MESSAGE_NOTIFICATION -> Icons.Default.ChatBubble
                IslandActivityType.ACTIVE_TIMER -> Icons.Default.Timer
                IslandActivityType.INCOMING_CALL -> Icons.Default.Call
                IslandActivityType.WEATHER_ALERT -> Icons.Default.Cloud
                IslandActivityType.BATTERY_TURBO -> Icons.Default.Bolt
                IslandActivityType.SYSTEM_TELEMETRY -> Icons.Default.Memory
                IslandActivityType.MARQUEE_TICKER -> Icons.Default.Subtitles
            }
            val leadingTint = when (config.activeIslandType) {
                IslandActivityType.MESSAGE_NOTIFICATION -> Color(messageState.appColorHex)
                IslandActivityType.INCOMING_CALL, IslandActivityType.BATTERY_TURBO -> EnergyEmerald
                IslandActivityType.WEATHER_ALERT -> SolarAmber
                else -> primaryColor
            }

            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = leadingTint,
                modifier = Modifier.size(14.dp)
            )

            if (config.islandMarqueeEnabled ||
                config.islandShape == IslandShapeStyle.MATRIX_TICKER_BAR ||
                config.activeIslandType == IslandActivityType.MARQUEE_TICKER
            ) {
                Text(
                    text = marqueeText,
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = config.islandMarqueeFontSizeSp.sp,
                    color = PureWhite,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            velocity = (38f * config.islandMarqueeSpeed).dp
                        )
                )
            } else {
                val staticLabel = when (config.activeIslandType) {
                    IslandActivityType.MEDIA_PLAYER -> mediaState.trackTitle
                    IslandActivityType.MESSAGE_NOTIFICATION -> "${messageState.appName}: ${messageState.message}"
                    IslandActivityType.ACTIVE_TIMER -> {
                        val mins = timerState.remainingSeconds / 60
                        val secs = timerState.remainingSeconds % 60
                        "%02d:%02d".format(mins, secs)
                    }
                    IslandActivityType.INCOMING_CALL -> callState.callerName
                    IslandActivityType.WEATHER_ALERT -> "${weather.cityName} ${weather.temperatureC}°C"
                    IslandActivityType.BATTERY_TURBO -> "Turbo $batteryPercent%"
                    IslandActivityType.SYSTEM_TELEMETRY -> "RAM ${telemetry.ramUsagePercent}% • CPU ${telemetry.cpuLoadPercent}%"
                    IslandActivityType.MARQUEE_TICKER -> marqueeText
                }
                Text(
                    text = staticLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Right Status: Live 120Hz Equalizer + Privacy Dot + Unread Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (config.islandLiveEqualizerEnabled) {
                Canvas(
                    modifier = Modifier
                        .width(18.dp)
                        .height(12.dp)
                ) {
                    val bars = 4
                    val spacing = size.width / bars
                    for (i in 0 until bars) {
                        val factor = 0.3f + 0.7f * ((sin(wavePhase + i * 1.1f) + 1f) / 2f)
                        val barH = size.height * factor
                        drawLine(
                            color = if (i % 2 == 0) primaryColor else secondaryColor,
                            start = Offset(i * spacing + 2.dp.toPx(), (size.height - barH) / 2f),
                            end = Offset(i * spacing + 2.dp.toPx(), (size.height + barH) / 2f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            if (config.islandPrivacyDotEnabled) {
                val dotColor = when {
                    telemetry.isFlashlightOn || telemetry.isPrivacyCameraActive -> EnergyEmerald
                    telemetry.isPrivacyMicActive -> SolarAmber
                    else -> EnergyEmerald.copy(alpha = 0.65f)
                }
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }

            if (unreadCount > 0 && config.islandShape != IslandShapeStyle.SPLIT_DROP) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(secondaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.coerceAtMost(9).toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PitchBlack
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Раскрыть Остров",
                    tint = MutedSlate,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpandedIslandCardContent(
    activeType: IslandActivityType,
    config: AodConfigurationState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    isCharging: Boolean,
    wavePhase: Float,
    marqueeText: String,
    telemetry: HardwareTelemetryState,
    primaryColor: Color,
    secondaryColor: Color,
    onCollapse: () -> Unit,
    onOpenParentApp: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddMinuteTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onSwitchIslandActivity: (IslandActivityType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Mode Switcher Strip + Quick Torch + Open Parent App + Collapse Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IslandActivityType.entries.forEach { type ->
                    val selected = type == activeType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) primaryColor.copy(alpha = 0.22f)
                                else Color(0xFF121824)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected) primaryColor else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { onSwitchIslandActivity(type) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        val shortLabel = when (type) {
                            IslandActivityType.MEDIA_PLAYER -> "Плеер"
                            IslandActivityType.MESSAGE_NOTIFICATION -> "Пуш"
                            IslandActivityType.ACTIVE_TIMER -> "Таймер"
                            IslandActivityType.INCOMING_CALL -> "Вызов"
                            IslandActivityType.WEATHER_ALERT -> "Погода"
                            IslandActivityType.BATTERY_TURBO -> "Заряд"
                            IslandActivityType.SYSTEM_TELEMETRY -> "Система"
                            IslandActivityType.MARQUEE_TICKER -> "Строка"
                        }
                        Text(
                            text = shortLabel,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) primaryColor else MutedSlate
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (config.islandQuickTorchEnabled) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (telemetry.isFlashlightOn) SolarAmber else Color(0xFF151C28))
                            .clickable { onToggleFlashlight() }
                            .testTag("island_quick_torch_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (telemetry.isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Фонарик",
                            tint = if (telemetry.isFlashlightOn) PitchBlack else PureWhite,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Direct button to open the parent application of the active event
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(primaryColor.copy(alpha = 0.2f))
                        .border(0.8.dp, primaryColor, RoundedCornerShape(50))
                        .clickable { onOpenParentApp() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("island_open_parent_app_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Открыть родительское приложение",
                            tint = primaryColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Открыть",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.CloseFullscreen,
                    contentDescription = "Свернуть Остров",
                    tint = MutedSlate,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { onCollapse() }
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Main Body per Activity Type
        when (activeType) {
            IslandActivityType.MEDIA_PLAYER -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mediaState.trackTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            maxLines = 1,
                            modifier = if (config.islandMarqueeEnabled) {
                                Modifier.basicMarquee(velocity = (36f * config.islandMarqueeSpeed).dp)
                            } else Modifier
                        )
                        Text(
                            text = "${mediaState.artistName} • Тап открывает плеер",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (mediaState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Воспроизведение / Пауза",
                                tint = PureWhite
                            )
                        }
                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Следующий трек",
                                tint = primaryColor
                            )
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                ) {
                    val count = 24
                    val step = size.width / count
                    for (i in 0 until count) {
                        val amp = if (mediaState.isPlaying) {
                            0.25f + 0.75f * ((sin(wavePhase + i * 0.5f) + 1f) / 2f)
                        } else 0.2f
                        val h = size.height * amp
                        drawLine(
                            color = if (i / count.toFloat() <= mediaState.progressFloat) primaryColor else MutedSlate.copy(alpha = 0.35f),
                            start = Offset(i * step + 2.dp.toPx(), (size.height - h) / 2f),
                            end = Offset(i * step + 2.dp.toPx(), (size.height + h) / 2f),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            IslandActivityType.MESSAGE_NOTIFICATION -> {
                val appAccent = Color(messageState.appColorHex)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenParentApp() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${messageState.appName} • ${messageState.sender}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = appAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (messageState.lastReplySent != null) {
                            Text(
                                text = "Отправлено ✓",
                                fontSize = 9.sp,
                                color = EnergyEmerald
                            )
                        } else {
                            Text(
                                text = "Тап → в ${messageState.appName}",
                                fontSize = 9.sp,
                                color = PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Text(
                        text = messageState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite,
                        maxLines = 1,
                        modifier = if (config.islandMarqueeEnabled) {
                            Modifier.basicMarquee(velocity = (38f * config.islandMarqueeSpeed).dp)
                        } else Modifier
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Супер 👍", "Через 5 мин", "Смотрю").forEach { quickText ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF151C28))
                                    .border(1.dp, appAccent.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .clickable { onSendQuickReply(quickText) }
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = quickText,
                                        fontSize = 9.sp,
                                        color = PureWhite
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = appAccent,
                                        modifier = Modifier.size(9.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            IslandActivityType.ACTIVE_TIMER -> {
                val mins = timerState.remainingSeconds / 60
                val secs = timerState.remainingSeconds % 60
                val progress = (timerState.remainingSeconds.toFloat() / timerState.totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = timerState.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedSlate
                            )
                            Text(
                                text = "%02d:%02d".format(mins, secs),
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = SolarAmber
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SolarAmber.copy(alpha = 0.2f))
                                    .clickable { onToggleTimer() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (timerState.isRunning) "Пауза" else "Старт",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolarAmber
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF161D2A))
                                    .clickable { onAddMinuteTimer() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "+1 мин", fontSize = 10.sp, color = PureWhite)
                            }
                            IconButton(
                                onClick = onResetTimer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Сбросить таймер",
                                    tint = MutedSlate,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(50)),
                        color = SolarAmber,
                        trackColor = Color(0xFF1A2232)
                    )
                }
            }

            IslandActivityType.INCOMING_CALL -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = callState.callerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            maxLines = 1
                        )
                        Text(
                            text = if (callState.isCallAccepted) {
                                "Идет разговор • %02d:%02d".format(
                                    callState.callDurationSeconds / 60,
                                    callState.callDurationSeconds % 60
                                )
                            } else {
                                callState.callerNumber
                            },
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = if (callState.isCallAccepted) EnergyEmerald else MutedSlate
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (callState.isCallAccepted) MatrixRed else EnergyEmerald)
                                .clickable { onAcceptOrEndCall() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (callState.isCallAccepted) Icons.Default.CallEnd else Icons.Default.Call,
                                contentDescription = "Управление вызовом",
                                tint = PitchBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            IslandActivityType.WEATHER_ALERT -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${weather.cityName} • ${weather.conditionTextRu}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            maxLines = 1
                        )
                        Text(
                            text = "Ветер ${weather.windSpeedKmh} км/ч • Давл. ${weather.pressureMmHg} мм • Осадки ${weather.precipitationProbPercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite.copy(alpha = 0.85f),
                            maxLines = 1,
                            modifier = if (config.islandMarqueeEnabled) {
                                Modifier.basicMarquee(velocity = (34f * config.islandMarqueeSpeed).dp)
                            } else Modifier
                        )
                    }
                    Text(
                        text = "${if (weather.temperatureC > 0) "+${weather.temperatureC}" else "${weather.temperatureC}"}°C",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = SolarAmber
                    )
                }
            }

            IslandActivityType.BATTERY_TURBO -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = EnergyEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCharging) "AMOLED Turbo Charge" else "Энергомонитор",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = EnergyEmerald
                            )
                        }
                        Text(
                            text = "$batteryPercent%",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (batteryPercent / 100f).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = EnergyEmerald,
                        trackColor = Color(0xFF151C28)
                    )
                }
            }

            IslandActivityType.SYSTEM_TELEMETRY -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CPU ${telemetry.cpuLoadPercent}% • RAM ${telemetry.ramUsedGb}/${telemetry.ramTotalGb} ГБ",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Text(
                            text = "${telemetry.batteryTempCelsius}°C • ${telemetry.batteryVoltageMv} мВ",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 10.sp,
                            color = SolarAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { (telemetry.ramUsagePercent / 100f).coerceIn(0.05f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50)),
                            color = primaryColor,
                            trackColor = Color(0xFF151C28)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (telemetry.isFlashlightOn) SolarAmber else Color(0xFF161E2E))
                                .clickable { onToggleFlashlight() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (telemetry.isFlashlightOn) "Фонарик ВКЛ" else "Фонарик",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (telemetry.isFlashlightOn) PitchBlack else PureWhite
                            )
                        }
                    }
                }
            }

            IslandActivityType.MARQUEE_TICKER -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "МАТРИЧНОЕ ТАБЛО • БЕГУЩАЯ СТРОКА 120HZ",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 9.sp,
                        color = secondaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A0F1A))
                            .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = marqueeText,
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = (config.islandMarqueeFontSizeSp + 2f).sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = (44f * config.islandMarqueeSpeed).dp
                            )
                        )
                    }
                }
            }
        }
    }
}
