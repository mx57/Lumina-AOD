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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SmartButton
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
import com.example.model.CameraRingMode
import com.example.model.IslandActivityType
import com.example.model.IslandAnchorPreset
import com.example.model.IslandShapeStyle
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
fun IslandAndCameraRingSection(
    config: AodConfigurationState,
    recentNotifications: List<NotificationLogEntity>,
    onToggleDynamicIsland: (Boolean) -> Unit,
    onSelectIslandShape: (IslandShapeStyle) -> Unit,
    onSelectIslandAnchor: (IslandAnchorPreset) -> Unit,
    onToggleIslandFreeDrag: (Boolean) -> Unit,
    onToggleIslandMagneticSnap: (Boolean) -> Unit,
    onSetIslandExactOffset: (Float, Float) -> Unit,
    onResetIslandPositionToCamera: () -> Unit,
    onUpdateIslandVisualSettings: (Float, Float, Float, Float, Float, Float, Float) -> Unit,
    onToggleCameraRing: (Boolean) -> Unit,
    onSelectRingMode: (CameraRingMode) -> Unit,
    onUpdateCameraGeometry: (Float, Float, Float, Float) -> Unit,
    onSelectIslandActivity: (IslandActivityType) -> Unit,
    onToggleIslandExpanded: () -> Unit,
    onSendCustomQuickReply: (String) -> Unit,
    onClearNotifications: () -> Unit
) {
    var customReplyText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dynamic Island Interactive Multitasking Hub
        SectionHeaderRow(
            icon = Icons.Default.SmartButton,
            title = "Динамический Остров 2026 (120Hz)",
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingToggleRow(
                    title = "Включить Динамический Остров (dynamicSpot Pro)",
                    subtitle = "Жидкая капсула 120Hz с мультизадачностью и перемещением пальцем",
                    checked = config.isDynamicIslandEnabled,
                    onCheckedChange = onToggleDynamicIsland,
                    testTag = "toggle_dynamic_island"
                )

                Text(
                    text = "Сценарий Острова (нажмите для проверки в превью):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate
                )

                IslandActivityType.entries.chunked(2).forEach { rowTypes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTypes.forEach { actType ->
                            val selected = config.activeIslandType == actType
                            Surface(
                                onClick = { onSelectIslandActivity(actType) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) CyberCyan.copy(alpha = 0.2f) else Color(0xFF161E2E),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) CyberCyan else ObsidianBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("island_activity_${actType.name}")
                            ) {
                                Text(
                                    text = actType.titleRu,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) CyberCyan else PureWhite,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
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
                        value = customReplyText,
                        onValueChange = { customReplyText = it },
                        placeholder = {
                            Text(
                                text = "Быстрый ответ прямо из Острова...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedSlate
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
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
                            onSendCustomQuickReply(customReplyText)
                            customReplyText = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Отправить ответ"
                        )
                    }
                }

                OutlinedButton(
                    onClick = onToggleIslandExpanded,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (config.isIslandExpanded) "Свернуть капсулу Острова" else "Развернуть карточку Острова",
                        color = CyberCyan
                    )
                }
            }
        }

        // 2. 6 Geometric Shapes & Position / Drag-and-Drop Constructor
        SectionHeaderRow(
            icon = Icons.Default.OpenWith,
            title = "Форма и Положение Острова (Drag & Drop)",
            badgeText = "X:${config.islandOffsetXDp} Y:${config.islandOffsetYDp}dp"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Выберите гео-форму Динамического Острова (6 форм):",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    color = PureWhite
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    IslandShapeStyle.entries.chunked(2).forEach { rowShapes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowShapes.forEach { shapeItem ->
                                val selected = config.islandShape == shapeItem
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectIslandShape(shapeItem) }
                                        .testTag("island_shape_${shapeItem.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selected) Color(0xFF14283E) else Color(0xFF0E1420)
                                    ),
                                    border = BorderStroke(
                                        width = if (selected) 1.5.dp else 1.dp,
                                        color = if (selected) CyberCyan else ObsidianBorder
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = shapeItem.titleRu,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 13.sp,
                                            color = if (selected) CyberCyan else PureWhite
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = shapeItem.subtitleRu,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 10.sp,
                                            color = MutedSlate,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                SettingToggleRow(
                    title = "Свободное перетаскивание пальцем (Drag-and-Drop)",
                    subtitle = "Зажмите Остров на превью или экране AOD и двигайте в любую точку",
                    checked = config.isIslandFreeDragEnabled,
                    onCheckedChange = onToggleIslandFreeDrag,
                    testTag = "toggle_island_free_drag"
                )

                SettingToggleRow(
                    title = "Магнитная привязка к центру экрана (Snap Grid)",
                    subtitle = "Автоматически выравнивает Остров при приближении к оси X = 0 dp",
                    checked = config.islandMagneticSnap,
                    onCheckedChange = onToggleIslandMagneticSnap
                )

                Text(
                    text = "Быстрые точки привязки (Якорь на экране):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate
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
                            label = { Text(anchor.titleRu, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = PitchBlack
                            ),
                            modifier = Modifier.testTag("island_anchor_${anchor.id}")
                        )
                    }
                }

                // Precision X Offset Slider & 0.5dp Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Позиция Острова X (Горизонталь)", color = PureWhite, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SmallPrecisionStepButton(icon = Icons.Default.Remove) {
                            onSetIslandExactOffset(config.islandOffsetXDp - 0.5f, config.islandOffsetYDp)
                        }
                        Text(
                            text = "${config.islandOffsetXDp} dp",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                        SmallPrecisionStepButton(icon = Icons.Default.Add) {
                            onSetIslandExactOffset(config.islandOffsetXDp + 0.5f, config.islandOffsetYDp)
                        }
                    }
                }
                Slider(
                    value = config.islandOffsetXDp,
                    onValueChange = { onSetIslandExactOffset(it, config.islandOffsetYDp) },
                    valueRange = -130f..130f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                // Precision Y Offset Slider & 0.5dp Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Позиция Острова Y (Вертикаль)", color = PureWhite, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SmallPrecisionStepButton(icon = Icons.Default.Remove) {
                            onSetIslandExactOffset(config.islandOffsetXDp, config.islandOffsetYDp - 0.5f)
                        }
                        Text(
                            text = "${config.islandOffsetYDp} dp",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                        SmallPrecisionStepButton(icon = Icons.Default.Add) {
                            onSetIslandExactOffset(config.islandOffsetXDp, config.islandOffsetYDp + 0.5f)
                        }
                    }
                }
                Slider(
                    value = config.islandOffsetYDp,
                    onValueChange = { onSetIslandExactOffset(config.islandOffsetXDp, it) },
                    valueRange = 2f..250f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                OutlinedButton(
                    onClick = onResetIslandPositionToCamera,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Привязать Остров точно к координатам камеры", color = SolarAmber, fontSize = 12.sp)
                }
            }
        }

        // 3. Deep Visual Customization of Dynamic Island (Size, Opacity, Glow, Spring)
        SectionHeaderRow(
            icon = Icons.Default.AutoFixHigh,
            title = "Глубокая кастомизация и Физика Острова",
            badgeText = "Свечение ${(config.islandNeonGlowIntensity * 100).toInt()}%"
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
                // Width & Height Scale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ширина капсулы Острова", color = PureWhite, fontSize = 13.sp)
                    Text("${(config.islandWidthScale * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
                }
                Slider(
                    value = config.islandWidthScale,
                    onValueChange = {
                        onUpdateIslandVisualSettings(
                            it,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    },
                    valueRange = 0.72f..1.40f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Высота капсулы / Масштаб", color = PureWhite, fontSize = 13.sp)
                    Text("${(config.islandHeightScale * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
                }
                Slider(
                    value = config.islandHeightScale,
                    onValueChange = {
                        onUpdateIslandVisualSettings(
                            config.islandWidthScale,
                            it,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    },
                    valueRange = 0.75f..1.35f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                // Glass Opacity & Neon Bloom Glow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Непрозрачность темного стекла", color = PureWhite, fontSize = 13.sp)
                    Text("${(config.islandGlassOpacity * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = EnergyEmerald)
                }
                Slider(
                    value = config.islandGlassOpacity,
                    onValueChange = {
                        onUpdateIslandVisualSettings(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            it,
                            config.islandNeonGlowIntensity,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    },
                    valueRange = 0.25f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = EnergyEmerald, activeTrackColor = EnergyEmerald)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Сила неонового свечения (Shader Bloom)", color = PureWhite, fontSize = 13.sp)
                    Text("${(config.islandNeonGlowIntensity * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = NeonMagenta)
                }
                Slider(
                    value = config.islandNeonGlowIntensity,
                    onValueChange = {
                        onUpdateIslandVisualSettings(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            it,
                            config.islandSpringBounciness,
                            config.islandBorderWidthDp
                        )
                    },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Упругость пружины 120Hz (Bounciness)", color = PureWhite, fontSize = 13.sp)
                    Text("${(config.islandSpringBounciness * 100).toInt()}%", fontFamily = ShareTechMonoFontFamily, color = SolarAmber)
                }
                Slider(
                    value = config.islandSpringBounciness,
                    onValueChange = {
                        onUpdateIslandVisualSettings(
                            config.islandWidthScale,
                            config.islandHeightScale,
                            config.islandCornerRadiusDp,
                            config.islandGlassOpacity,
                            config.islandNeonGlowIntensity,
                            it,
                            config.islandBorderWidthDp
                        )
                    },
                    valueRange = 0.30f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = SolarAmber, activeTrackColor = SolarAmber)
                )
            }
        }

        // 4. Camera Cutout Ring (aodNotify + Energy Ring)
        SectionHeaderRow(
            icon = Icons.Default.CameraFront,
            title = "Кружок вокруг камеры (aodNotify & Energy Ring)",
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingToggleRow(
                    title = "Световое кольцо вокруг селфи-камеры",
                    subtitle = "Заменяет физический LED-индикатор и показывает уровень заряда",
                    checked = config.isCameraRingEnabled,
                    onCheckedChange = onToggleCameraRing
                )

                CameraRingMode.entries.forEach { mode ->
                    val selected = config.cameraRingMode == mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRingMode(mode) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFF152538) else Color(0xFF0E1420)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selected) CyberCyan else ObsidianBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = mode.titleRu,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 14.sp,
                                color = if (selected) CyberCyan else PureWhite
                            )
                            Text(
                                text = mode.subtitleRu,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = null,
                        tint = NeonMagenta,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Калибровка кольца под физический вырез камеры:",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Смещение камеры X", color = MutedSlate, fontSize = 12.sp)
                    Text("${config.cameraHoleOffsetXDp} dp", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
                }
                Slider(
                    value = config.cameraHoleOffsetXDp,
                    onValueChange = {
                        onUpdateCameraGeometry(
                            it,
                            config.cameraHoleOffsetYDp,
                            config.cameraRingRadiusDp,
                            config.cameraRingThicknessDp
                        )
                    },
                    valueRange = -100f..100f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Отступ камеры сверху Y", color = MutedSlate, fontSize = 12.sp)
                    Text("${config.cameraHoleOffsetYDp} dp", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
                }
                Slider(
                    value = config.cameraHoleOffsetYDp,
                    onValueChange = {
                        onUpdateCameraGeometry(
                            config.cameraHoleOffsetXDp,
                            it,
                            config.cameraRingRadiusDp,
                            config.cameraRingThicknessDp
                        )
                    },
                    valueRange = 2f..50f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Радиус кольца / Толщина линии", color = MutedSlate, fontSize = 12.sp)
                    Text(
                        "${config.cameraRingRadiusDp.toInt()}dp / ${config.cameraRingThicknessDp}dp",
                        fontFamily = ShareTechMonoFontFamily,
                        color = NeonMagenta
                    )
                }
                Slider(
                    value = config.cameraRingRadiusDp,
                    onValueChange = {
                        onUpdateCameraGeometry(
                            config.cameraHoleOffsetXDp,
                            config.cameraHoleOffsetYDp,
                            it,
                            config.cameraRingThicknessDp
                        )
                    },
                    valueRange = 8f..28f,
                    colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                )
            }
        }

        // 5. Recent Notification & Filter History Log
        if (recentNotifications.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Журнал событий Острова и Фильтрации",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        IconButton(onClick = onClearNotifications) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Очистить журнал",
                                tint = MutedSlate
                            )
                        }
                    }
                    recentNotifications.take(6).forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (log.wasBlockedByFilter) MatrixRed else Color(log.colorHex))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = buildString {
                                        append(log.appName)
                                        append(" • ")
                                        append(log.senderOrTitle)
                                        if (log.wasBlockedByFilter) append(" [ЗАБЛОКИРОВАНО]")
                                    },
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = if (log.wasBlockedByFilter) MatrixRed else Color(log.colorHex)
                                )
                                Text(
                                    text = log.messageBody,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = PureWhite
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
private fun SmallPrecisionStepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color(0xFF172234),
        border = BorderStroke(1.dp, ObsidianBorder)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Шаг 0.5dp",
            tint = CyberCyan,
            modifier = Modifier
                .padding(4.dp)
                .size(14.dp)
        )
    }
}
