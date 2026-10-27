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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AodPresetEntity
import com.example.model.AodColorPalette
import com.example.model.AodConfigurationState
import com.example.model.ClockCategory
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily

@Composable
fun StudioCustomizationSection(
    config: AodConfigurationState,
    selectedCategory: ClockCategory?,
    presets: List<AodPresetEntity>,
    onSelectCategory: (ClockCategory?) -> Unit,
    onSelectClockStyle: (ClockFaceStyle) -> Unit,
    onSelectEdgeStyle: (EdgeLightingStyle) -> Unit,
    onSelectPalette: (AodColorPalette) -> Unit,
    onClockScaleChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onToggleShowSeconds: (Boolean) -> Unit,
    onToggleShowWeather: (Boolean) -> Unit,
    onToggleShowBadges: (Boolean) -> Unit,
    onApplyPreset: (AodPresetEntity) -> Unit,
    onSavePreset: (String) -> Unit,
    onDeletePreset: (Int) -> Unit
) {
    var customPresetName by remember { mutableStateOf("") }

    val filteredClocks = remember(selectedCategory) {
        if (selectedCategory == null) ClockFaceStyle.entries
        else ClockFaceStyle.entries.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Saved Presets Carousel (Muviz / Nothing / DynamicSpot / Energy Ring)
        SectionHeaderRow(
            icon = Icons.Default.BookmarkAdded,
            title = "Готовые пресеты и коллекция",
            badgeText = "${presets.size} стилей"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            presets.forEach { preset ->
                val palette = AodColorPalette.fromId(preset.paletteId)
                Card(
                    modifier = Modifier
                        .width(205.dp)
                        .clickable { onApplyPreset(preset) }
                        .testTag("preset_card_${preset.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, palette.primaryColor.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(palette.primaryColor.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = preset.badgeTag,
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = palette.primaryColor
                                )
                            }
                            if (presets.size > 2) {
                                IconButton(
                                    onClick = { onDeletePreset(preset.id) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Удалить пресет",
                                        tint = MutedSlate,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            maxLines = 1
                        )
                        Text(
                            text = ClockFaceStyle.fromId(preset.clockStyleId).titleRu,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 11.sp,
                            color = MutedSlate,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Save Current Configuration as Preset Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customPresetName,
                onValueChange = { customPresetName = it },
                placeholder = {
                    Text(
                        text = "Название вашего пресета...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("custom_preset_name_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )
            Button(
                onClick = {
                    onSavePreset(customPresetName)
                    customPresetName = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = PitchBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("save_preset_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Сохранить", fontWeight = FontWeight.Bold)
            }
        }

        // 2. Clock Face Categories & Styles
        SectionHeaderRow(
            icon = Icons.Default.WatchLater,
            title = "Коллекции циферблатов AOD",
            badgeText = config.clockStyle.titleRu
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("Все 8 стилей") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan,
                    selectedLabelColor = PitchBlack
                )
            )
            ClockCategory.entries.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat.titleRu) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = PitchBlack
                    )
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredClocks.forEach { style ->
                val isSelected = config.clockStyle == style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectClockStyle(style) }
                        .testTag("clock_style_${style.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF132235) else ObsidianCard
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CyberCyan else ObsidianBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = style.titleRu,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) CyberCyan else PureWhite
                            )
                            Text(
                                text = style.subtitleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = MutedSlate
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(EnergyEmerald.copy(alpha = 0.14f))
                                .border(1.dp, EnergyEmerald.copy(alpha = 0.45f), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OPR ${style.baseOprPercent}%",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = EnergyEmerald
                            )
                        }
                    }
                }
            }
        }

        // 3. Edge Lighting Animations (Muviz Edge)
        SectionHeaderRow(
            icon = Icons.Default.AutoAwesome,
            title = "Свечение краев экрана (Edge Lighting)",
            badgeText = config.edgeStyle.titleRu
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EdgeLightingStyle.entries.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { edge ->
                        val selected = config.edgeStyle == edge
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectEdgeStyle(edge) }
                                .testTag("edge_style_${edge.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) Color(0xFF1D142B) else ObsidianCard
                            ),
                            border = BorderStroke(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) NeonMagenta else ObsidianBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = edge.titleRu,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 14.sp,
                                    color = if (selected) NeonMagenta else PureWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = edge.descriptionRu,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 11.sp,
                                    color = MutedSlate,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. AMOLED Color Palettes
        SectionHeaderRow(
            icon = Icons.Default.Palette,
            title = "Неоновые и AMOLED палитры",
            badgeText = config.palette.titleRu
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AodColorPalette.entries.forEach { palette ->
                val selected = config.palette == palette
                Card(
                    modifier = Modifier
                        .width(142.dp)
                        .clickable { onSelectPalette(palette) }
                        .testTag("palette_${palette.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(
                        width = if (selected) 1.8.dp else 1.dp,
                        color = if (selected) palette.primaryColor else ObsidianBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(palette.primaryColor, palette.secondaryColor, palette.accentColor)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = palette.titleRu,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // 5. Fine-Tuning Sliders & Toggles
        SectionHeaderRow(
            icon = Icons.Default.Tune,
            title = "Тонкая калибровка матрицы",
            badgeText = "Масштаб ${(config.clockScale * 100).toInt()}%"
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Масштаб циферблата",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PureWhite
                    )
                    Text(
                        text = "${(config.clockScale * 100).toInt()}%",
                        fontFamily = ShareTechMonoFontFamily,
                        color = CyberCyan
                    )
                }
                Slider(
                    value = config.clockScale,
                    onValueChange = onClockScaleChange,
                    valueRange = 0.75f..1.30f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Светимость OLED пикселей",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PureWhite
                    )
                    Text(
                        text = "${(config.aodBrightness * 100).toInt()}%",
                        fontFamily = ShareTechMonoFontFamily,
                        color = CyberCyan
                    )
                }
                Slider(
                    value = config.aodBrightness,
                    onValueChange = onBrightnessChange,
                    valueRange = 0.25f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonMagenta,
                        activeTrackColor = NeonMagenta
                    )
                )

                SettingToggleRow(
                    title = "Показывать секунды в реальном времени",
                    subtitle = "Плавный отсчет секунд и секундной стрелки",
                    checked = config.showSeconds,
                    onCheckedChange = onToggleShowSeconds
                )
                SettingToggleRow(
                    title = "Погодный виджет на экране AOD",
                    subtitle = "Отображать живую сводку погоды под часами",
                    checked = config.showWeatherOnAod,
                    onCheckedChange = onToggleShowWeather
                )
                SettingToggleRow(
                    title = "Неоновые бейджи уведомлений",
                    subtitle = "Показывать цветные капсулы непрочитанных приложений",
                    checked = config.showNotificationBadges,
                    onCheckedChange = onToggleShowBadges
                )
            }
        }
    }
}

@Composable
fun SectionHeaderRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    badgeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite
            )
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.14f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = badgeText,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 11.sp,
                color = CyberCyan
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = PureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                color = MutedSlate
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PitchBlack,
                checkedTrackColor = CyberCyan
            )
        )
    }
}
