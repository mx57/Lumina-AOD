// File: app/src/main/java/com/example/ui/screens/StudioScreen.kt
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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AodPresetEntity
import com.example.model.AodColorPalette
import com.example.model.AodConfigurationState
import com.example.model.ClockCategory
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.SmartProfilePreset
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShareTechMonoFontFamily
import com.example.ui.theme.SolarAmber

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
    onToggleParallax3d: (Boolean) -> Unit,
    onParallaxIntensityChange: (Float) -> Unit,
    onToggleLiquidMetaball: (Boolean) -> Unit,
    onAnimationSpeedChange: (Float) -> Unit,
    onToggleEdgeParticles: (Boolean) -> Unit,
    onSimulateTiltPreview: (Float, Float) -> Unit,
    onApplyPreset: (AodPresetEntity) -> Unit,
    onSavePreset: (String) -> Unit,
    onDeletePreset: (Int) -> Unit,
    onToggleQuickDock: (Boolean) -> Unit = {},
    onToggleShowPinnedMemo: (Boolean) -> Unit = {},
    onUpdatePinnedMemoText: (String) -> Unit = {},
    onTogglePomodoroTimer: () -> Unit = {},
    onResetPomodoroTimer: () -> Unit = {},
    onToggleStandByMode: (Boolean) -> Unit = {},
    onToggleStandByAutoOnCharging: (Boolean) -> Unit = {},
    onToggleStandByRedTint: (Boolean) -> Unit = {},
    onToggleIceCardEnabled: (Boolean) -> Unit = {},
    onUpdateIceDetails: (String, String, String) -> Unit = { _, _, _ -> },
    onShowIceQrOnAod: () -> Unit = {},
    onApplySmartProfile: (SmartProfilePreset) -> Unit = {},
    onExportConfigJson: () -> String = { "{}" },
    onImportConfigJson: (String) -> Unit = {}
) {
    var activeSubTab by rememberSaveable { mutableIntStateOf(0) }
    var customPresetName by remember { mutableStateOf("") }
    var jsonInputBuffer by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val filteredClocks = remember(selectedCategory) {
        if (selectedCategory == null) ClockFaceStyle.entries
        else ClockFaceStyle.entries.filter { it.category == selectedCategory }
    }

    val subTabs = listOf(
        "Циферблаты",
        "Края и Цвета",
        "Док и StandBy",
        "Профили & JSON"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Segmented Sub-Tab Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ObsidianCard)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            subTabs.forEachIndexed { idx, label ->
                val selected = activeSubTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) CyberCyan else Color.Transparent)
                        .clickable { activeSubTab = idx }
                        .padding(vertical = 9.dp)
                        .testTag("studio_subtab_$idx"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) PitchBlack else PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        when (activeSubTab) {
            0 -> {
                // SUB-TAB 0: Clock Faces & Matrix Calibration
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

                // Compact 2-Column Clock Face Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredClocks.chunked(2).forEach { rowClocks ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowClocks.forEach { style ->
                                val isSelected = config.clockStyle == style
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectClockStyle(style) }
                                        .testTag("clock_style_${style.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF132235) else ObsidianCard
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CyberCyan else ObsidianBorder
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "OPR ${style.baseOprPercent}%",
                                                fontFamily = ShareTechMonoFontFamily,
                                                fontSize = 10.sp,
                                                color = EnergyEmerald
                                            )
                                            if (isSelected) {
                                                Text(
                                                    text = "АКТИВЕН",
                                                    fontFamily = ShareTechMonoFontFamily,
                                                    fontSize = 9.sp,
                                                    color = CyberCyan
                                                )
                                            }
                                        }
                                        Text(
                                            text = style.titleRu,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CyberCyan else PureWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = style.subtitleRu,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MutedSlate,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            if (rowClocks.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Fine-Tuning Sliders & Toggles
                SectionHeaderRow(
                    icon = Icons.Default.Tune,
                    title = "Калибровка циферблата и яркости",
                    badgeText = "Масштаб ${(config.clockScale * 100).toInt()}%"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Масштаб циферблата", color = PureWhite, fontSize = 13.sp)
                            Text(
                                text = "${(config.clockScale * 100).toInt()}%",
                                fontFamily = ShareTechMonoFontFamily,
                                color = CyberCyan
                            )
                        }
                        Slider(
                            value = config.clockScale,
                            onValueChange = onClockScaleChange,
                            valueRange = 0.75f..1.35f,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Светимость OLED пикселей", color = PureWhite, fontSize = 13.sp)
                            Text(
                                text = "${(config.aodBrightness * 100).toInt()}%",
                                fontFamily = ShareTechMonoFontFamily,
                                color = NeonMagenta
                            )
                        }
                        Slider(
                            value = config.aodBrightness,
                            onValueChange = onBrightnessChange,
                            valueRange = 0.25f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                        )

                        SettingToggleRow(
                            title = "Секунды в реальном времени (120Hz)",
                            subtitle = "Плавный отсчет секунд и ход секундной стрелки",
                            checked = config.showSeconds,
                            onCheckedChange = onToggleShowSeconds
                        )
                        SettingToggleRow(
                            title = "Погодный виджет под часами",
                            subtitle = "Отображать живую сводку погоды на AOD",
                            checked = config.showWeatherOnAod,
                            onCheckedChange = onToggleShowWeather
                        )
                        SettingToggleRow(
                            title = "Неоновые бейджи уведомлений",
                            subtitle = "Показывать цветные капсулы разрешенных приложений",
                            checked = config.showNotificationBadges,
                            onCheckedChange = onToggleShowBadges
                        )
                    }
                }
            }

            1 -> {
                // SUB-TAB 1: Edge Lighting, Color Palettes & 3D Parallax
                SectionHeaderRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Свечение краев 120Hz (Edge Lighting)",
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
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) NeonMagenta else PureWhite
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = edge.descriptionRu,
                                            style = MaterialTheme.typography.bodySmall,
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

                SectionHeaderRow(
                    icon = Icons.Default.Animation,
                    title = "Физика 2026: 3D-Параллакс и 120Hz",
                    badgeText = "Скорость ${String.format("%.1f", config.animationSpeedMultiplier)}x"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingToggleRow(
                            title = "3D-Параллакс от гироскопа",
                            subtitle = "Объемное смещение часов и Острова при наклоне",
                            checked = config.parallax3dEnabled,
                            onCheckedChange = onToggleParallax3d,
                            testTag = "toggle_parallax_3d"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Глубина 3D-параллакса", color = PureWhite, fontSize = 13.sp)
                            Text("${(config.parallaxIntensity * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
                        }
                        Slider(
                            value = config.parallaxIntensity,
                            onValueChange = onParallaxIntensityChange,
                            valueRange = 0.2f..1.4f,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onSimulateTiltPreview(-0.85f, 0.4f) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Влево", fontSize = 11.sp, color = CyberCyan)
                            }
                            OutlinedButton(
                                onClick = { onSimulateTiltPreview(0f, 0f) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Центр (0°)", fontSize = 11.sp, color = PureWhite)
                            }
                            OutlinedButton(
                                onClick = { onSimulateTiltPreview(0.85f, -0.4f) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Вправо", fontSize = 11.sp, color = CyberCyan)
                            }
                        }

                        SettingToggleRow(
                            title = "Жидкий морфинг метаболов (Liquid Morph)",
                            subtitle = "Вязкое слияние капли Острова и сфер",
                            checked = config.liquidMetaballMorphEnabled,
                            onCheckedChange = onToggleLiquidMetaball
                        )

                        SettingToggleRow(
                            title = "Волновые шейдерные частицы по краям",
                            subtitle = "Парящие неоновые искры вдоль граней экрана",
                            checked = config.edgeParticleSparksEnabled,
                            onCheckedChange = onToggleEdgeParticles
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Скорость анимаций и волн", color = PureWhite, fontSize = 13.sp)
                            Text("${String.format("%.1f", config.animationSpeedMultiplier)}x", fontFamily = ShareTechMonoFontFamily, color = SolarAmber)
                        }
                        Slider(
                            value = config.animationSpeedMultiplier,
                            onValueChange = onAnimationSpeedChange,
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = SolarAmber, activeTrackColor = SolarAmber)
                        )
                    }
                }
            }

            2 -> {
                // SUB-TAB 2: Quick Launch Dock, Pinned Memo + Pomodoro, StandBy Nightstand Mode, ICE Emergency QR Card
                SectionHeaderRow(
                    icon = Icons.Default.NoteAlt,
                    title = "Быстрый Док, Заметка и Фокус-Таймер Pomodoro",
                    badgeText = if (config.pomodoroActive) "ФОКУС ВКЛ" else "Док 4 кнопки"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingToggleRow(
                            title = "Быстрый док иконок внизу AOD (Quick Dock)",
                            subtitle = "4 кнопки быстрого запуска: Фонарик, Фокус, StandBy и ICE QR",
                            checked = config.isQuickDockEnabled,
                            onCheckedChange = onToggleQuickDock,
                            testTag = "toggle_quick_dock_switch"
                        )

                        SettingToggleRow(
                            title = "Закрепленная заметка на экране AOD",
                            subtitle = "Важное напоминание прямо под часами",
                            checked = config.showPinnedMemoOnAod,
                            onCheckedChange = onToggleShowPinnedMemo
                        )

                        OutlinedTextField(
                            value = config.pinnedMemoText,
                            onValueChange = onUpdatePinnedMemoText,
                            label = { Text("Текст заметки на AOD") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pinned_memo_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        // Pomodoro Focus Timer Strip
                        val mins = config.pomodoroRemainingSec / 60
                        val secs = config.pomodoroRemainingSec % 60
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0E1524))
                                .border(1.dp, SolarAmber.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = SolarAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Pomodoro Фокус-сессия",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = String.format("%02d:%02d", mins, secs),
                                        fontFamily = OrbitronFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = SolarAmber
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = onTogglePomodoroTimer,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (config.pomodoroActive) SolarAmber else CyberCyan,
                                        contentColor = PitchBlack
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("toggle_pomodoro_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (config.pomodoroActive) "Пауза" else "Старт",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = onResetPomodoroTimer,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF182235))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = "Сброс 25:00",
                                        tint = PureWhite
                                    )
                                }
                            }
                        }
                    }
                }

                // StandBy Nightstand Mode Card
                SectionHeaderRow(
                    icon = Icons.Default.Bedtime,
                    title = "Ночной прикроватный режим StandBy",
                    badgeText = if (config.standByNightModeEnabled) "АКТИВЕН" else "OPR < 0.9%"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, MatrixRed.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SettingToggleRow(
                            title = "Включить прикроватные часы StandBy",
                            subtitle = "Крупные неслепящие часы в стиле Nightstand Dock",
                            checked = config.standByNightModeEnabled,
                            onCheckedChange = onToggleStandByMode,
                            testTag = "toggle_standby_mode_switch"
                        )
                        SettingToggleRow(
                            title = "Авто-включение при подключении зарядки",
                            subtitle = "Автоматически переходить в StandBy на кабеле или MagSafe",
                            checked = config.standByAutoOnCharging,
                            onCheckedChange = onToggleStandByAutoOnCharging
                        )
                        SettingToggleRow(
                            title = "Красный спектр Night Red (#FF2A1F)",
                            subtitle = "Не разрушает мелатонин и ночное зрение в темноте",
                            checked = config.standByRedTintOnly,
                            onCheckedChange = onToggleStandByRedTint
                        )
                    }
                }

                // ICE Emergency Contact & Vector QR Card
                SectionHeaderRow(
                    icon = Icons.Default.QrCode2,
                    title = "Экстренная ICE-визитка и QR-код владельца",
                    badgeText = if (config.iceCardEnabled) "QR ВКЛ" else "На случай потери"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, EnergyEmerald.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingToggleRow(
                            title = "Разрешить кнопку ICE QR-визитки на AOD",
                            subtitle = "Отображает векторный QR-код для связи при потере телефона",
                            checked = config.iceCardEnabled,
                            onCheckedChange = onToggleIceCardEnabled
                        )

                        OutlinedTextField(
                            value = config.iceOwnerName,
                            onValueChange = { onUpdateIceDetails(it, config.iceEmergencyPhone, config.iceMedicalOrNote) },
                            label = { Text("Имя владельца / подпись") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnergyEmerald,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        OutlinedTextField(
                            value = config.iceEmergencyPhone,
                            onValueChange = { onUpdateIceDetails(config.iceOwnerName, it, config.iceMedicalOrNote) },
                            label = { Text("Экстренный телефон для связи (ICE)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnergyEmerald,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        OutlinedTextField(
                            value = config.iceMedicalOrNote,
                            onValueChange = { onUpdateIceDetails(config.iceOwnerName, config.iceEmergencyPhone, it) },
                            label = { Text("Медицинская памятка / вознаграждение") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnergyEmerald,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        Button(
                            onClick = onShowIceQrOnAod,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixRed,
                                contentColor = PureWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_ice_qr_preview_btn")
                        ) {
                            Icon(imageVector = Icons.Default.QrCode2, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Показать ICE QR-визитку на превью AOD", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            3 -> {
                // SUB-TAB 3: Smart Automation Profiles, Saved Presets & JSON Theme Export/Import
                SectionHeaderRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Умные авто-профили в 1 тап",
                    badgeText = config.activeSmartProfile.titleRu
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmartProfilePreset.entries.forEach { profile ->
                        val selected = config.activeSmartProfile == profile
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onApplySmartProfile(profile) }
                                .testTag("smart_profile_${profile.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) Color(0xFF132235) else ObsidianCard
                            ),
                            border = BorderStroke(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) CyberCyan else ObsidianBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = profile.titleRu,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) CyberCyan else PureWhite
                                    )
                                    Text(
                                        text = profile.subtitleRu,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSlate,
                                        fontSize = 11.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) CyberCyan else Color(0xFF172234)
                                ) {
                                    Text(
                                        text = if (selected) "АКТИВЕН" else "Включить",
                                        fontFamily = ShareTechMonoFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) PitchBlack else CyberCyan,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Saved Presets Carousel
                SectionHeaderRow(
                    icon = Icons.Default.BookmarkAdded,
                    title = "Коллекция сохраненных пресетов",
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
                                .width(200.dp)
                                .clickable { onApplyPreset(preset) }
                                .testTag("preset_card_${preset.id}"),
                            shape = RoundedCornerShape(16.dp),
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
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    maxLines = 1
                                )
                                Text(
                                    text = ClockFaceStyle.fromId(preset.clockStyleId).titleRu,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MutedSlate,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

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

                // JSON Theme Export & Import Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Экспорт и Импорт Конфигурации Темы (JSON)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Сохраните все настройки часов, Острова, погоды и палитры в JSON-строку или поделитесь с друзьями:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )

                        OutlinedTextField(
                            value = jsonInputBuffer,
                            onValueChange = { jsonInputBuffer = it },
                            placeholder = { Text("Нажмите «Экспорт в JSON» или вставьте JSON-код темы...") },
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("theme_json_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMagenta,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val exported = onExportConfigJson()
                                    jsonInputBuffer = exported
                                    clipboardManager.setText(AnnotatedString(exported))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberCyan,
                                    contentColor = PitchBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_json_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Экспорт в JSON", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val candidate = if (jsonInputBuffer.isNotBlank()) {
                                        jsonInputBuffer
                                    } else {
                                        clipboardManager.getText()?.text.orEmpty()
                                    }
                                    if (candidate.isNotBlank()) {
                                        jsonInputBuffer = candidate
                                        onImportConfigJson(candidate)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonMagenta,
                                    contentColor = PitchBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("import_json_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Импорт JSON", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
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
        Spacer(modifier = Modifier.width(8.dp))
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
