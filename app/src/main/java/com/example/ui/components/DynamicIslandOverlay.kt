package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CloseFullscreen
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
    onToggleExpand: () -> Unit,
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

    val accentColor = when (config.activeIslandType) {
        IslandActivityType.MEDIA_PLAYER -> Color(mediaState.accentHex)
        IslandActivityType.MESSAGE_NOTIFICATION -> Color(messageState.appColorHex)
        IslandActivityType.ACTIVE_TIMER -> SolarAmber
        IslandActivityType.INCOMING_CALL -> EnergyEmerald
        IslandActivityType.WEATHER_ALERT -> IceBlue
        IslandActivityType.BATTERY_TURBO -> EnergyEmerald
    }

    val shape = RoundedCornerShape(if (config.isIslandExpanded) 26.dp else 50.dp)

    Box(
        modifier = modifier
            .offset(
                x = config.cameraHoleOffsetXDp.dp,
                y = (config.cameraHoleOffsetYDp - 4f).coerceAtLeast(2f).dp
            )
            .clip(shape)
            .background(Color(0xFF070A10))
            .border(1.2.dp, accentColor.copy(alpha = 0.72f), shape)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .clickable { onToggleExpand() }
            .testTag("dynamic_island_capsule")
            .padding(
                horizontal = if (config.isIslandExpanded) 14.dp else 12.dp,
                vertical = if (config.isIslandExpanded) 12.dp else 6.dp
            )
    ) {
        AnimatedContent(
            targetState = config.isIslandExpanded,
            label = "island_expand_state"
        ) { expanded ->
            if (!expanded) {
                CollapsedIslandPillContent(
                    activityType = config.activeIslandType,
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
}

@Composable
private fun CollapsedIslandPillContent(
    activityType: IslandActivityType,
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
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left side of camera punch-hole
        when (activityType) {
            IslandActivityType.MEDIA_PLAYER -> {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Музыка",
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = mediaState.trackTitle.take(12),
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
                    text = "${messageState.appName}: новое",
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

        // Camera Cutout Reserved Space in center
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(PitchBlack)
                .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape)
        )

        // Right side mini waveform
        MiniWaveformBars(color = accentColor, isAnimated = true)
    }
}

@Composable
private fun ExpandedIslandHubContent(
    activityType: IslandActivityType,
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
                    text = activityType.titleRu.uppercase(),
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = accentColor
                )
            }
            IconButton(
                onClick = onCollapse,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloseFullscreen,
                    contentDescription = "Свернуть остров",
                    tint = MutedSlate,
                    modifier = Modifier.size(15.dp)
                )
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
    val infiniteTransition = rememberInfiniteTransition(label = "mini_waveform")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(modifier = Modifier.size(width = 24.dp, height = 14.dp)) {
        val barCount = 4
        val spacing = size.width / barCount
        for (i in 0 until barCount) {
            val factor = if (isAnimated) {
                0.35f + 0.65f * ((sin(wavePhase + i * 1.1f) + 1f) / 2f)
            } else {
                0.3f
            }
            val barH = size.height * factor
            val x = i * spacing + spacing / 2f
            drawLine(
                color = color,
                start = Offset(x, (size.height - barH) / 2f),
                end = Offset(x, (size.height + barH) / 2f),
                strokeWidth = 2.4.dp.toPx(),
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
