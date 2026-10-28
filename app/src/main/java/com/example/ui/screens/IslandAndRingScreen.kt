package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppColorRuleEntity
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
import com.example.model.CameraRingMode
import com.example.model.IslandActivityType
import com.example.model.IslandAnchorPreset
import com.example.model.IslandShapeStyle
import com.example.model.IslandTapAction
import com.example.model.IslandVisibilityMode
import com.example.service.HardwareTelemetryState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber

@Composable
fun IslandAndRingSection(
    config: AodConfigurationState,
    telemetry: HardwareTelemetryState = HardwareTelemetryState(),
    appRules: List<AppColorRuleEntity>,
    recentNotifications: List<NotificationLogEntity>,
    onToggleDynamicIsland: (Boolean) -> Unit,
    onSelectIslandShape: (IslandShapeStyle) -> Unit,
    onSelectVisibilityMode: (IslandVisibilityMode) -> Unit = {},
    onSelectTapAction: (IslandTapAction) -> Unit = {},
    onSetAutoCollapseSec: (Int) -> Unit = {},
    onToggleMarquee: (Boolean) -> Unit = {},
    onChangeMarqueeSpeed: (Float) -> Unit = {},
    onChangeMarqueeFontSize: (Float) -> Unit = {},
    onChangeCustomTickerText: (String) -> Unit = {},
    onToggleWeatherInTicker: (Boolean) -> Unit = {},
    onTogglePrivacyDot: (Boolean) -> Unit = {},
    onToggleLiveEqualizer: (Boolean) -> Unit = {},
    onToggleQuickTorch: (Boolean) -> Unit = {},
    onTriggerFlashlight: () -> Unit = {},
    onTriggerPrivacySim: () -> Unit = {},
    onOpenParentApp: () -> Unit = {},
    onSelectIslandAnchor: (IslandAnchorPreset) -> Unit,
    onToggleFreeDrag: (Boolean) -> Unit,
    onToggleMagneticSnap: (Boolean) -> Unit,
    onUpdateIslandExactOffset: (Float, Float) -> Unit,
    onResetIslandToCamera: () -> Unit,
    onUpdateIslandVisuals: (Float, Float, Float, Float, Float, Float, Float) -> Unit,
    onSelectIslandActivity: (IslandActivityType) -> Unit,
    onToggleIslandExpanded: () -> Unit,
    onToggleCameraRing: (Boolean) -> Unit,
    onSelectCameraRingMode: (CameraRingMode) -> Unit,
    onUpdateCameraHolePosition: (Float, Float, Float, Float) -> Unit,
    onTriggerSimulatedNotification: (AppColorRuleEntity) -> Unit,
    onClearNotifications: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dynamic Island Master Card + Quick Activity Mode Switcher
        SectionHeaderRow(
            icon = Icons.Default.DynamicFeed,
            title = "Динамический Остров 2026 (11 Форм & Мульти-Хаб)",
            badgeText = config.islandShape.titleRu
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Интерактивный Динамический Остров",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Свободное перемещение пальцем, 11 форм, бегущая строка и переход в приложение по тапу",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    Switch(
                        checked = config.isDynamicIslandEnabled,
                        onCheckedChange = onToggleDynamicIsland,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("toggle_dynamic_island_switch")
                    )
                }

                Text(
                    text = "АКТИВНЫЙ РЕЖИМ ОСТРОВА (8 ВИДЖЕТОВ)",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = CyberCyan
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IslandActivityType.entries.forEach { actType ->
                        val selected = config.activeIslandType == actType
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectIslandActivity(actType) },
                            label = {
                                Text(
                                    text = actType.titleRu,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan,
                                containerColor = Color(0xFF101622),
                                labelColor = PureWhite
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleIslandExpanded,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expand_collapse_island_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan.copy(alpha = 0.16f),
                            contentColor = CyberCyan
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (config.isIslandExpanded) "Свернуть в капсулу" else "Раскрыть виджет",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onOpenParentApp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_parent_app_test_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonMagenta,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Открыть родителя",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. Visibility Mode ("Появляться только при уведомлениях") & Tap Gestures ("Открытие родителя по тапу")
        SectionHeaderRow(
            icon = Icons.Default.Visibility,
            title = "Авто-появление при пушах и Жесты Тапа",
            badgeText = config.islandVisibilityMode.titleRu
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, EnergyEmerald.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "РЕЖИМ ПОЯВЛЕНИЯ ОСТРОВА НА ЭКРАНЕ",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = EnergyEmerald
                )

                IslandVisibilityMode.entries.forEach { mode ->
                    val selected = config.islandVisibilityMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) EnergyEmerald.copy(alpha = 0.14f) else Color(0xFF0E131D))
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) EnergyEmerald else ObsidianBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectVisibilityMode(mode) }
                            .padding(12.dp)
                            .testTag("island_visibility_${mode.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode.titleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) EnergyEmerald else PureWhite
                            )
                            Text(
                                text = mode.subtitleRu,
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Таймер авто-сворачивания после пуша",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite
                        )
                        Text(
                            text = "${config.islandAutoCollapseSec} сек",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = EnergyEmerald
                        )
                    }
                    Slider(
                        value = config.islandAutoCollapseSec.toFloat(),
                        onValueChange = { onSetAutoCollapseSec(it.toInt()) },
                        valueRange = 3f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = EnergyEmerald,
                            activeTrackColor = EnergyEmerald
                        )
                    )
                }

                Text(
                    text = "ДЕЙСТВИЕ ПРИ ТАПЕ И ОТКРЫТИЕ РОДИТЕЛЬСКОГО ПРИЛОЖЕНИЯ",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = CyberCyan
                )

                IslandTapAction.entries.forEach { tapAction ->
                    val selected = config.islandTapAction == tapAction
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) CyberCyan.copy(alpha = 0.14f) else Color(0xFF0E131D))
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) CyberCyan else ObsidianBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectTapAction(tapAction) }
                            .padding(12.dp)
                            .testTag("island_tap_action_${tapAction.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = if (selected) CyberCyan else MutedSlate,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tapAction.titleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) CyberCyan else PureWhite
                            )
                            Text(
                                text = tapAction.subtitleRu,
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Marquee Ticker ("Бегущая строка 120Hz") & Built-in Mini Functions
        SectionHeaderRow(
            icon = Icons.Default.Subtitles,
            title = "Бегущая Строка (Marquee) и Мини-Функции",
            badgeText = if (config.islandMarqueeEnabled) "120Hz ВКЛ" else "Статика"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Бегущая строка (Marquee Ticker)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Плавная прокрутка текста уведомлений, треков, погоды и ваших заметок внутри Острова",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    Switch(
                        checked = config.islandMarqueeEnabled,
                        onCheckedChange = onToggleMarquee,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = NeonMagenta
                        ),
                        modifier = Modifier.testTag("toggle_island_marquee_switch")
                    )
                }

                OutlinedTextField(
                    value = config.islandCustomTickerText,
                    onValueChange = onChangeCustomTickerText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("island_custom_ticker_input"),
                    label = { Text("Свой текст для бегущей строки (необязательно)") },
                    placeholder = { Text("Например: Не беспокоить • Встреча в 15:00") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Добавлять живую сводку погоды в бегущую строку",
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = config.islandShowWeatherInTicker,
                        onCheckedChange = onToggleWeatherInTicker,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = CyberCyan
                        )
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Скорость бегущей строки", style = MaterialTheme.typography.bodySmall, color = PureWhite)
                        Text(
                            text = "${String.format("%.1f", config.islandMarqueeSpeed)}x",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = NeonMagenta
                        )
                    }
                    Slider(
                        value = config.islandMarqueeSpeed,
                        onValueChange = onChangeMarqueeSpeed,
                        valueRange = 0.4f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonMagenta,
                            activeTrackColor = NeonMagenta
                        )
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Размер шрифта бегущей строки", style = MaterialTheme.typography.bodySmall, color = PureWhite)
                        Text(
                            text = "${config.islandMarqueeFontSizeSp.toInt()} sp",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                    Slider(
                        value = config.islandMarqueeFontSizeSp,
                        onValueChange = onChangeMarqueeFontSize,
                        valueRange = 10f..16f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan
                        )
                    )
                }

                Text(
                    text = "ВСТРОЕННЫЕ МИНИ-ФУНКЦИИ ОСТРОВА",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = SolarAmber
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Flashlight Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (telemetry.isFlashlightOn) SolarAmber.copy(alpha = 0.2f) else Color(0xFF0E131D))
                            .border(1.dp, if (telemetry.isFlashlightOn) SolarAmber else ObsidianBorder, RoundedCornerShape(14.dp))
                            .clickable { onTriggerFlashlight() }
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = "Фонарик",
                            tint = if (telemetry.isFlashlightOn) SolarAmber else PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (telemetry.isFlashlightOn) "Фонарик ВКЛ" else "Фонарик",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    // Privacy Indicator Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (telemetry.isPrivacyCameraActive) EnergyEmerald.copy(alpha = 0.2f) else Color(0xFF0E131D))
                            .border(1.dp, if (telemetry.isPrivacyCameraActive) EnergyEmerald else ObsidianBorder, RoundedCornerShape(14.dp))
                            .clickable { onTriggerPrivacySim() }
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Приватность",
                            tint = if (telemetry.isPrivacyCameraActive) EnergyEmerald else PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Точка приватности",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    // Live Equalizer Toggle Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (config.islandLiveEqualizerEnabled) CyberCyan.copy(alpha = 0.16f) else Color(0xFF0E131D))
                            .border(1.dp, if (config.islandLiveEqualizerEnabled) CyberCyan else ObsidianBorder, RoundedCornerShape(14.dp))
                            .clickable { onToggleLiveEqualizer(!config.islandLiveEqualizerEnabled) }
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Эквалайзер",
                            tint = if (config.islandLiveEqualizerEnabled) CyberCyan else MutedSlate,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (config.islandLiveEqualizerEnabled) "Спектр ВКЛ" else "Спектр ВЫКЛ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                // Live System Telemetry Summary inside Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0B101A))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectIslandActivity(IslandActivityType.SYSTEM_TELEMETRY) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Живая телеметрия: CPU ${telemetry.cpuLoadPercent}% • RAM ${telemetry.ramUsedGb}/${telemetry.ramTotalGb} ГБ",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }
                    Text(
                        text = "В Остров →",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // 4. 11 Geometric Shapes Selector
        SectionHeaderRow(
            icon = Icons.Default.AutoAwesome,
            title = "Геометрия и Формы Острова (11 Видов)",
            badgeText = "Liquid Morph"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IslandShapeStyle.entries.chunked(2).forEach { rowShapes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowShapes.forEach { shape ->
                        val isSelected = config.islandShape == shape
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectIslandShape(shape) }
                                .testTag("island_shape_${shape.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF141E30) else ObsidianCard
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.6.dp else 1.dp,
                                color = if (isSelected) CyberCyan else ObsidianBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ShapeMiniPreviewIcon(shape = shape, accent = if (isSelected) CyberCyan else MutedSlate)
                                    if (isSelected) {
                                        Text(
                                            text = "АКТИВНА",
                                            fontFamily = ShareTechMonoFontFamily,
                                            fontSize = 9.sp,
                                            color = CyberCyan
                                        )
                                    }
                                }
                                Text(
                                    text = shape.titleRu,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = shape.subtitleRu,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedSlate,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (rowShapes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 5. Free Drag-and-Drop, Anchor Presets & 0.5dp Precision X/Y Calibration
        SectionHeaderRow(
            icon = Icons.Default.OpenWith,
            title = "Перемещение Пальцем и Позиция на Экране",
            badgeText = "X:${config.islandOffsetXDp} Y:${config.islandOffsetYDp}dp"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Свободное перетаскивание пальцем (Drag-and-Drop)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Тяните Остров прямо на превью или экране Живого AOD в любую точку",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    Switch(
                        checked = config.isIslandFreeDragEnabled,
                        onCheckedChange = onToggleFreeDrag,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = CyberCyan
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Магнитная привязка к оси центра (X = 0 dp)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                        Text(
                            text = "Мягкое примагничивание к центру при перетаскивании",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    Switch(
                        checked = config.islandMagneticSnap,
                        onCheckedChange = onToggleMagneticSnap,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = EnergyEmerald
                        )
                    )
                }

                Text(
                    text = "БЫСТРЫЕ ЯКОРЯ ПОЛОЖЕНИЯ НА ЭКРАНЕ",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = CyberCyan
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IslandAnchorPreset.entries.forEach { anchor ->
                        val selected = config.islandAnchor == anchor
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectIslandAnchor(anchor) },
                            label = { Text(anchor.titleRu) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.22f),
                                selectedLabelColor = CyberCyan,
                                containerColor = Color(0xFF101622),
                                labelColor = PureWhite
                            )
                        )
                    }
                }

                PrecisionAxisRow(
                    label = "Горизонталь X (Лево / Право)",
                    valueDp = config.islandOffsetXDp,
                    minVal = -135f,
                    maxVal = 135f,
                    accentColor = CyberCyan,
                    onValueChange = { newX -> onUpdateIslandExactOffset(newX, config.islandOffsetYDp) }
                )

                PrecisionAxisRow(
                    label = "Вертикаль Y (Верх / Центр / Низ)",
                    valueDp = config.islandOffsetYDp,
                    minVal = 2f,
                    maxVal = 250f,
                    accentColor = NeonMagenta,
                    onValueChange = { newY -> onUpdateIslandExactOffset(config.islandOffsetXDp, newY) }
                )

                OutlinedButton(
                    onClick = onResetIslandToCamera,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Привязать Остров точно к вырезу селфи-камеры")
                }
            }
        }

        // 6. Deep Visual & Spring Physics Customization for Dynamic Island
        SectionHeaderRow(
            icon = Icons.Default.Tune,
            title = "Тонкие Настройки Размера, Свечения и Пружины",
            badgeText = "120Hz Physics"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IslandSliderControl(
                    label = "Масштаб ширины Острова",
                    valueText = "${(config.islandWidthScale * 100).toInt()}%",
                    value = config.islandWidthScale,
                    range = 0.70f..1.40f,
                    color = CyberCyan,
                    onValueChange = { w ->
                        onUpdateIslandVisuals(
                            w,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Масштаб высоты Острова",
                    valueText = "${(config.islandHeightScale * 100).toInt()}%",
                    value = config.islandHeightScale,
                    range = 0.75f..1.35f,
                    color = CyberCyan,
                    onValueChange = { h ->
                        onUpdateIslandVisuals(
                            config.islandWidthScale,
                            h,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Шейдерное неоновое свечение (Bloom Halo)",
                    valueText = "${(config.islandNeonGlowIntensity * 100).toInt()}%",
                    value = config.islandNeonGlowIntensity,
                    range = 0.0f..1.0f,
                    color = NeonMagenta,
                    onValueChange = { glow ->
                        onUpdateIslandVisuals(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            glow,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Плотность обсидианового стекла (Opacity)",
                    valueText = "${(config.islandGlassOpacity * 100).toInt()}%",
                    value = config.islandGlassOpacity,
                    range = 0.25f..1.0f,
                    color = EnergyEmerald,
                    onValueChange = { op ->
                        onUpdateIslandVisuals(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            op,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Упругость пружины морфинга (Spring Bounciness)",
                    valueText = "${(config.islandSpringBounciness * 100).toInt()}%",
                    value = config.islandSpringBounciness,
                    range = 0.30f..1.0f,
                    color = SolarAmber,
                    onValueChange = { bounce ->
                        onUpdateIslandVisuals(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            bounce,
                            config.islandBorderWidthDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Толщина неонового контура",
                    valueText = "${String.format("%.1f", config.islandBorderWidthDp)} dp",
                    value = config.islandBorderWidthDp,
                    range = 0.5f..3.5f,
                    color = CyberCyan,
                    onValueChange = { bw ->
                        onUpdateIslandVisuals(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            bw
                        )
                    }
                )
            }
        }

        // 7. Camera Cutout Ring (aodNotify + Energy Ring) Calibration
        SectionHeaderRow(
            icon = Icons.Default.CameraFront,
            title = "Кольцо Вокруг Камеры (aodNotify + Energy Ring)",
            badgeText = "R=${config.cameraRingRadiusDp.toInt()}dp"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Световое кольцо выреза камеры",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Неоновый индикатор уведомлений и уровня заряда вокруг фронтальной камеры",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    Switch(
                        checked = config.isCameraRingEnabled,
                        onCheckedChange = onToggleCameraRing,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = EnergyEmerald
                        )
                    )
                }

                CameraRingMode.entries.forEach { mode ->
                    val selected = config.cameraRingMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Color(0xFF141E30) else Color(0xFF0E131D))
                            .border(
                                width = 1.dp,
                                color = if (selected) EnergyEmerald else ObsidianBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectCameraRingMode(mode) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode.titleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) EnergyEmerald else PureWhite
                            )
                            Text(
                                text = mode.subtitleRu,
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                PrecisionAxisRow(
                    label = "Смещение выреза камеры X",
                    valueDp = config.cameraHoleOffsetXDp,
                    minVal = -100f,
                    maxVal = 100f,
                    accentColor = EnergyEmerald,
                    onValueChange = { x ->
                        onUpdateCameraHolePosition(
                            x,
                            config.cameraHoleOffsetYDp,
                            config.cameraRingRadiusDp,
                            config.cameraRingThicknessDp
                        )
                    }
                )

                PrecisionAxisRow(
                    label = "Смещение выреза камеры Y",
                    valueDp = config.cameraHoleOffsetYDp,
                    minVal = 2f,
                    maxVal = 50f,
                    accentColor = EnergyEmerald,
                    onValueChange = { y ->
                        onUpdateCameraHolePosition(
                            config.cameraHoleOffsetXDp,
                            y,
                            config.cameraRingRadiusDp,
                            config.cameraRingThicknessDp
                        )
                    }
                )

                IslandSliderControl(
                    label = "Радиус кольца камеры",
                    valueText = "${config.cameraRingRadiusDp.toInt()} dp",
                    value = config.cameraRingRadiusDp,
                    range = 8f..28f,
                    color = EnergyEmerald,
                    onValueChange = { r ->
                        onUpdateCameraHolePosition(
                            config.cameraHoleOffsetXDp,
                            config.cameraHoleOffsetYDp,
                            r,
                            config.cameraRingThicknessDp
                        )
                    }
                )
            }
        }

        // 8. Quick Notification Trigger & Live Log
        SectionHeaderRow(
            icon = Icons.Default.NotificationsActive,
            title = "Тест Событий и Журнал Уведомлений",
            badgeText = "${recentNotifications.size} событий"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Нажмите на приложение, чтобы протестировать живой пуш, бегущую строку и авто-пробуждение:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    appRules.forEach { rule ->
                        val c = Color(rule.colorHex)
                        SurfacePillButton(
                            label = if (rule.isBlacklisted) "🚫 ${rule.appDisplayName}" else "⚡ ${rule.appDisplayName}",
                            accent = if (rule.isBlacklisted) MatrixRed else c,
                            onClick = { onTriggerSimulatedNotification(rule) }
                        )
                    }
                }

                if (recentNotifications.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ПОСЛЕДНИЕ ПЕРЕХВАЧЕННЫЕ СОБЫТИЯ",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 10.sp,
                            color = MutedSlate
                        )
                        IconButton(
                            onClick = onClearNotifications,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Очистить журнал",
                                tint = MutedSlate
                            )
                        }
                    }

                    recentNotifications.take(4).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0E131D))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (item.wasBlockedByFilter) MatrixRed else Color(item.colorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.appName} • ${item.senderOrTitle}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.wasBlockedByFilter) MatrixRed else PureWhite
                                )
                                Text(
                                    text = item.messageBody,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedSlate,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapeMiniPreviewIcon(shape: IslandShapeStyle, accent: Color) {
    when (shape) {
        IslandShapeStyle.CAPSULE -> {
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.25f))
                    .border(1.5.dp, accent, RoundedCornerShape(50))
            )
        }
        IslandShapeStyle.SPLIT_DROP -> {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = 0.25f))
                        .border(1.5.dp, accent, RoundedCornerShape(50))
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.25f))
                        .border(1.5.dp, accent, CircleShape)
                )
            }
        }
        IslandShapeStyle.CYBER_HEXAGON -> {
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(13.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(accent.copy(alpha = 0.25f))
                    .border(1.5.dp, accent, CutCornerShape(4.dp))
            )
        }
        IslandShapeStyle.LIQUID_SPHERE, IslandShapeStyle.STARLIGHT_ORB -> {
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(15.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.35f))
                    .border(1.5.dp, accent, CircleShape)
            )
        }
        IslandShapeStyle.ULTRA_THREAD -> {
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )
        }
        IslandShapeStyle.HIDDEN_OUTLINE -> {
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(12.dp)
                    .border(1.5.dp, accent, RoundedCornerShape(50))
            )
        }
        IslandShapeStyle.MATRIX_TICKER_BAR -> {
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(13.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(accent.copy(alpha = 0.22f))
                    .border(1.5.dp, accent, RoundedCornerShape(3.dp))
            )
        }
        IslandShapeStyle.DUAL_CYBER_WINGS -> {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(10.dp)
                        .border(1.2.dp, accent, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(12.dp)
                        .clip(CutCornerShape(3.dp))
                        .background(accent.copy(alpha = 0.25f))
                        .border(1.5.dp, accent, CutCornerShape(3.dp))
                )
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(10.dp)
                        .border(1.2.dp, accent, RoundedCornerShape(2.dp))
                )
            }
        }
        IslandShapeStyle.FLOATING_NOTCH -> {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(13.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(accent.copy(alpha = 0.3f))
                    .border(1.5.dp, accent, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            )
        }
        IslandShapeStyle.AUDIO_SPECTRUM_WAVE -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(6.dp, 12.dp, 9.dp, 14.dp, 8.dp, 11.dp).forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h)
                            .clip(RoundedCornerShape(50))
                            .background(accent)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrecisionAxisRow(
    label: String,
    valueDp: Float,
    minVal: Float,
    maxVal: Float,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onValueChange((valueDp - 0.5f).coerceIn(minVal, maxVal)) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "-0.5dp",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "${String.format("%.1f", valueDp)} dp",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 12.sp,
                    color = accentColor
                )
                IconButton(
                    onClick = { onValueChange((valueDp + 0.5f).coerceIn(minVal, maxVal)) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "+0.5dp",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Slider(
            value = valueDp,
            onValueChange = onValueChange,
            valueRange = minVal..maxVal,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun IslandSliderControl(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = PureWhite)
            Text(
                text = valueText,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 12.sp,
                color = color
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
    }
}

@Composable
private fun SurfacePillButton(
    label: String,
    accent: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite
        )
    }
}
