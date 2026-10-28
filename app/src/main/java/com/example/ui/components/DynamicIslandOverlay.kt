package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import com.example.model.WeatherSnapshot
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.IceBlue
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun DynamicIslandInteractiveCapsule(
    config: AodConfigurationState,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    tiltNormalizedX: Float = 0f,
    tiltNormalizedY: Float = 0f,
    onToggleExpand: () -> Unit,
    onDragIslandByDeltaDp: (Float, Float) -> Unit = { _, _ -> },
    onPlayPauseMedia: () -> Unit,
    onNextTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddOneMinTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!config.isDynamicIslandEnabled) return

    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    var dragVelocityX by remember { mutableFloatStateOf(0f) }

    val accentColor = when (config.activeIslandType) {
        IslandActivityType.MEDIA_PLAYER -> Color(mediaState.accentHex)
        IslandActivityType.MESSAGE_NOTIFICATION -> Color(messageState.appColorHex)
        IslandActivityType.ACTIVE_TIMER -> SolarAmber
        IslandActivityType.INCOMING_CALL -> EnergyEmerald
        IslandActivityType.WEATHER_ALERT -> IceBlue
        IslandActivityType.BATTERY_TURBO -> EnergyEmerald
    }

    val infiniteTransition = rememberInfiniteTransition(label = "island_2026_shader")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    val metaballPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "metaballPhase"
    )

    val damping = (1.15f - config.islandSpringBounciness).coerceIn(0.35f, 0.88f)
    val animatedOffsetX by animateFloatAsState(
        targetValue = config.islandOffsetXDp + (if (config.parallax3dEnabled) tiltNormalizedX * 4f * config.parallaxIntensity else 0f),
        animationSpec = spring(dampingRatio = damping, stiffness = Spring.StiffnessMediumLow),
        label = "islandOffsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = config.islandOffsetYDp + (if (config.parallax3dEnabled) tiltNormalizedY * 3f * config.parallaxIntensity else 0f),
        animationSpec = spring(dampingRatio = damping, stiffness = Spring.StiffnessMediumLow),
        label = "islandOffsetY"
    )

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "dragScale"
    )

    val shape: Shape = when (config.islandShape) {
        IslandShapeStyle.CYBER_HEXAGON -> CutCornerShape(if (config.isIslandExpanded) 18.dp else 12.dp)
        IslandShapeStyle.ULTRA_THREAD -> RoundedCornerShape(if (config.isIslandExpanded) 22.dp else 8.dp)
        else -> RoundedCornerShape(
            if (config.isIslandExpanded) 28.dp else config.islandCornerRadiusDp.dp
        )
    }

    val containerBgColor = when (config.islandShape) {
        IslandShapeStyle.HIDDEN_OUTLINE -> Color(0xFF05080F).copy(alpha = (config.islandGlassOpacity * 0.42f).coerceIn(0.15f, 0.8f))
        else -> Color(0xFF070B12).copy(alpha = config.islandGlassOpacity)
    }

    Row(
        modifier = modifier
            .offset(
                x = animatedOffsetX.dp,
                y = animatedOffsetY.dp
            )
            .graphicsLayer {
                scaleX = dragScale * (if (config.isIslandExpanded) 1f else config.islandWidthScale)
                scaleY = dragScale * (if (config.isIslandExpanded) 1f else config.islandHeightScale)
                rotationZ = (dragVelocityX * 0.08f).coerceIn(-4f, 4f)
            }
            .pointerInput(config.isIslandFreeDragEnabled) {
                if (config.isIslandFreeDragEnabled) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            dragVelocityX = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            dragVelocityX = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val dxDp = with(density) { dragAmount.x.toDp().value }
                            val dyDp = with(density) { dragAmount.y.toDp().value }
                            dragVelocityX = dxDp
                            onDragIslandByDeltaDp(dxDp, dyDp)
                        }
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    // 2026 Multi-Layer Shader Neon Bloom & Viscous Metaball Bridge
                    val glowStrength = config.islandNeonGlowIntensity * glowPulse
                    if (glowStrength > 0.05f) {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.42f * glowStrength),
                                    config.palette.secondaryColor.copy(alpha = 0.18f * glowStrength),
                                    Color.Transparent
                                ),
                                radius = size.maxDimension * 0.85f
                            ),
                            topLeft = Offset(-10.dp.toPx(), -8.dp.toPx()),
                            size = Size(size.width + 20.dp.toPx(), size.height + 16.dp.toPx()),
                            cornerRadius = CornerRadius(36.dp.toPx(), 36.dp.toPx())
                        )
                    }

                    // Liquid Metaball tendrils for LIQUID_SPHERE mode
                    if (config.islandShape == IslandShapeStyle.LIQUID_SPHERE && config.liquidMetaballMorphEnabled) {
                        val dropOffset = sin(metaballPhase) * 6.dp.toPx()
                        drawCircle(
                            color = accentColor.copy(alpha = 0.35f),
                            radius = (size.height * 0.45f),
                            center = Offset(14.dp.toPx() + dropOffset, size.height / 2f)
                        )
                        drawCircle(
                            color = config.palette.secondaryColor.copy(alpha = 0.32f),
                            radius = (size.height * 0.42f),
                            center = Offset(size.width - 14.dp.toPx() - dropOffset, size.height / 2f)
                        )
                    }
                }
                .clip(shape)
                .background(containerBgColor)
                .border(
                    width = (if (isDragging) config.islandBorderWidthDp + 0.8f else config.islandBorderWidthDp).dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.9f),
                            config.palette.secondaryColor.copy(alpha = 0.75f),
                            accentColor.copy(alpha = 0.9f)
                        )
                    ),
                    shape = shape
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = damping,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
                .clickable { onToggleExpand() }
                .testTag("dynamic_island_capsule")
                .padding(
                    horizontal = when {
                        config.isIslandExpanded -> 14.dp
                        config.islandShape == IslandShapeStyle.ULTRA_THREAD -> 16.dp
                        else -> 12.dp
                    },
                    vertical = when {
                        config.isIslandExpanded -> 12.dp
                        config.islandShape == IslandShapeStyle.ULTRA_THREAD -> 3.dp
                        else -> 6.dp
                    }
                )
        ) {
            AnimatedContent(
                targetState = config.isIslandExpanded,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + scaleIn(
                        initialScale = 0.86f,
                        animationSpec = spring(dampingRatio = damping, stiffness = Spring.StiffnessMedium)
                    )).togetherWith(
                        fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.9f)
                    ).using(SizeTransform(clip = false))
                },
                label = "island_expand_state_2026"
            ) { expanded ->
                if (!expanded) {
                    CollapsedIslandPillContent(
                        activityType = config.activeIslandType,
                        shapeStyle = config.islandShape,
                        isDragging = isDragging,
                        mediaState = mediaState,
                        timerState = timerState,
                        messageState = messageState,
                        callState = callState,
                        weather = weather,
                        batteryPercent = batteryPercent,
                        accentColor = accentColor
                    )
                } else {
                    ExpandedIslandHubContent(
                        activityType = config.activeIslandType,
                        isFreeDragEnabled = config.isIslandFreeDragEnabled,
                        offsetXDp = config.islandOffsetXDp,
                        offsetYDp = config.islandOffsetYDp,
                        mediaState = mediaState,
                        timerState = timerState,
                        messageState = messageState,
                        callState = callState,
                        weather = weather,
                        batteryPercent = batteryPercent,
                        accentColor = accentColor,
                        onCollapse = onToggleExpand,
                        onPlayPauseMedia = onPlayPauseMedia,
                        onNextTrack = onNextTrack,
                        onToggleTimer = onToggleTimer,
                        onAddOneMinTimer = onAddOneMinTimer,
                        onResetTimer = onResetTimer,
                        onSendQuickReply = onSendQuickReply,
                        onAcceptOrEndCall = onAcceptOrEndCall
                    )
                }
            }
        }

        // Detached Liquid Satellite Drop for iPhone Pro SPLIT_DROP style when collapsed
        if (!config.isIslandExpanded && config.islandShape == IslandShapeStyle.SPLIT_DROP) {
            val bridgeGap by animateFloatAsState(
                targetValue = if (isDragging) 3f else 6f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                label = "splitBridgeGap"
            )
            // Viscous metaball thread connecting main pill and satellite circle
            Canvas(modifier = Modifier.size(width = bridgeGap.dp, height = 14.dp)) {
                drawLine(
                    color = accentColor.copy(alpha = 0.45f),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(containerBgColor)
                    .border(config.islandBorderWidthDp.dp, accentColor.copy(alpha = 0.85f), CircleShape)
                    .clickable { onToggleExpand() },
                contentAlignment = Alignment.Center
            ) {
                MiniWaveformBars(color = accentColor, isAnimated = true)
            }
        }
    }
}

@Composable
private fun CollapsedIslandPillContent(
    activityType: IslandActivityType,
    shapeStyle: IslandShapeStyle,
    isDragging: Boolean,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isDragging) {
            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = "Перетаскивание",
                tint = CyberCyan,
                modifier = Modifier.size(14.dp)
            )
        }

        when (activityType) {
            IslandActivityType.MEDIA_PLAYER -> {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Музыка",
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = mediaState.trackTitle.take(13),
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite,
                    maxLines = 1
                )
            }
            IslandActivityType.MESSAGE_NOTIFICATION -> {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = "${messageState.appName}: пуш",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite
                )
            }
            IslandActivityType.ACTIVE_TIMER -> {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Таймер",
                    tint = SolarAmber,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = formatSecondsMinSec(timerState.remainingSeconds),
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = SolarAmber
                )
            }
            IslandActivityType.INCOMING_CALL -> {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Звонок",
                    tint = EnergyEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = callState.callerName.take(10),
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = EnergyEmerald
                )
            }
            IslandActivityType.WEATHER_ALERT -> {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Погода",
                    tint = SolarAmber,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${weather.temperatureC}°C ${weather.cityName.take(6)}",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite
                )
            }
            IslandActivityType.BATTERY_TURBO -> {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Зарядка",
                    tint = EnergyEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Turbo $batteryPercent%",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = EnergyEmerald
                )
            }
        }

        // Camera Cutout Reserved Lens in center
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(PitchBlack)
                .border(1.dp, accentColor.copy(alpha = 0.55f), CircleShape)
        )

        if (shapeStyle != IslandShapeStyle.SPLIT_DROP) {
            MiniWaveformBars(color = accentColor, isAnimated = true)
        }
    }
}

@Composable
private fun ExpandedIslandHubContent(
    activityType: IslandActivityType,
    isFreeDragEnabled: Boolean,
    offsetXDp: Float,
    offsetYDp: Float,
    mediaState: IslandMediaState,
    timerState: IslandTimerState,
    messageState: IslandMessageState,
    callState: IslandCallState,
    weather: WeatherSnapshot,
    batteryPercent: Int,
    accentColor: Color,
    onCollapse: () -> Unit,
    onPlayPauseMedia: () -> Unit,
    onNextTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onAddOneMinTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSendQuickReply: (String) -> Unit,
    onAcceptOrEndCall: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.92f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${activityType.titleRu.uppercase()} • 120HZ",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = accentColor
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isFreeDragEnabled) {
                    Text(
                        text = "X:${offsetXDp} Y:${offsetYDp}",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 9.sp,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloseFullscreen,
                        contentDescription = "Свернуть остров",
                        tint = MutedSlate,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        when (activityType) {
            IslandActivityType.MEDIA_PLAYER -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mediaState.trackTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mediaState.artistName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = MutedSlate,
                            maxLines = 1
                        )
                    }
                    MiniWaveformBars(color = accentColor, isAnimated = mediaState.isPlaying)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { mediaState.progressFloat.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onPlayPauseMedia,
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.2f),
                        modifier = Modifier.testTag("island_media_play_pause")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (mediaState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Воспроизведение / Пауза",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (mediaState.isPlaying) "Пауза" else "Играть",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 12.sp,
                                color = PureWhite
                            )
                        }
                    }
                    Surface(
                        onClick = onNextTrack,
                        shape = CircleShape,
                        color = Color(0xFF172030),
                        modifier = Modifier.testTag("island_media_next")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Следующий трек",
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "След. трек",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 12.sp,
                                color = PureWhite
                            )
                        }
                    }
                }
            }

            IslandActivityType.MESSAGE_NOTIFICATION -> {
                Text(
                    text = "${messageState.appName} • ${messageState.sender}",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = messageState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = PureWhite
                )
                if (messageState.lastReplySent != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Отправлено: «${messageState.lastReplySent}»",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp,
                        color = EnergyEmerald
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickReplies = listOf("👍 Окей", "Скоро буду", "На созвоне")
                    quickReplies.forEach { replyChip ->
                        Surface(
                            onClick = { onSendQuickReply(replyChip) },
                            shape = RoundedCornerShape(50),
                            color = accentColor.copy(alpha = 0.16f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Reply,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = replyChip,
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = PureWhite,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            IslandActivityType.ACTIVE_TIMER -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = timerState.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatSecondsMinSec(timerState.remainingSeconds),
                            fontFamily = ShareTechMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = SolarAmber
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            onClick = onToggleTimer,
                            shape = CircleShape,
                            color = SolarAmber.copy(alpha = 0.22f)
                        ) {
                            Icon(
                                imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Старт/Пауза",
                                tint = SolarAmber,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(18.dp)
                            )
                        }
                        Surface(
                            onClick = onAddOneMinTimer,
                            shape = CircleShape,
                            color = Color(0xFF1A2234)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "+1 мин",
                                    tint = PureWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "1м",
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = PureWhite
                                )
                            }
                        }
                        Surface(
                            onClick = onResetTimer,
                            shape = CircleShape,
                            color = Color(0xFF1A2234)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Сброс",
                                tint = PureWhite,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(18.dp)
                            )
                        }
                    }
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
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Text(
                            text = if (callState.isCallAccepted) {
                                "Идет разговор • ${formatSecondsMinSec(callState.callDurationSeconds)}"
                            } else {
                                callState.callerNumber
                            },
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = if (callState.isCallAccepted) EnergyEmerald else MutedSlate
                        )
                    }
                    Surface(
                        onClick = onAcceptOrEndCall,
                        shape = RoundedCornerShape(50),
                        color = if (callState.isCallAccepted) MatrixRed else EnergyEmerald
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (callState.isCallAccepted) Icons.Default.CallEnd else Icons.Default.Call,
                                contentDescription = "Вызов",
                                tint = PitchBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (callState.isCallAccepted) "Завершить" else "Принять",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = PitchBlack,
                                fontWeight = FontWeight.Bold
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
                    Column {
                        Text(
                            text = "${weather.cityName} • ${weather.temperatureC}°C",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Text(
                            text = "${weather.conditionTextRu} • Ветер ${weather.windSpeedKmh} км/ч • Влажность ${weather.humidityPercent}%",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = IceBlue
                        )
                    }
                }
            }

            IslandActivityType.BATTERY_TURBO -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AMOLED Энергомонитор • $batteryPercent%",
                            style = MaterialTheme.typography.titleMedium,
                            color = EnergyEmerald
                        )
                        Text(
                            text = "Черные пиксели отключены • Режим Ultra Low OPR",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = EnergyEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniWaveformBars(
    color: Color,
    isAnimated: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_waveform_2026")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 720, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(modifier = Modifier.size(width = 24.dp, height = 14.dp)) {
        val barCount = 5
        val spacing = size.width / barCount
        for (i in 0 until barCount) {
            val factor = if (isAnimated) {
                0.28f + 0.72f * ((sin(wavePhase + i * 1.15f) + 1f) / 2f)
            } else {
                0.25f
            }
            val barH = size.height * factor
            val x = i * spacing + spacing / 2f
            drawLine(
                color = color,
                start = Offset(x, (size.height - barH) / 2f),
                end = Offset(x, (size.height + barH) / 2f),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun formatSecondsMinSec(totalSec: Int): String {
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
