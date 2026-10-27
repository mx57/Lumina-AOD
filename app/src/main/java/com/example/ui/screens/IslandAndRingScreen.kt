package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.AppColorRuleEntity
import com.example.data.NotificationLogEntity
import com.example.model.AodConfigurationState
import com.example.model.CameraRingMode
import com.example.model.IslandActivityType
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.IceBlue
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
    appColorRules: List<AppColorRuleEntity>,
    recentNotifications: List<NotificationLogEntity>,
    onToggleDynamicIsland: (Boolean) -> Unit,
    onToggleCameraRing: (Boolean) -> Unit,
    onSelectRingMode: (CameraRingMode) -> Unit,
    onUpdateCameraGeometry: (Float, Float, Float, Float) -> Unit,
    onSelectIslandActivity: (IslandActivityType) -> Unit,
    onToggleIslandExpanded: () -> Unit,
    onSendCustomQuickReply: (String) -> Unit,
    onTriggerSimulatedAppPush: (AppColorRuleEntity) -> Unit,
    onUpdateAppRuleColor: (AppColorRuleEntity, Long) -> Unit,
    onClearNotifications: () -> Unit
) {
    var customReplyText by remember { mutableStateOf("") }
    val selectableColorHexes = listOf(
        0xFF38BDF8L,
        0xFF00E676L,
        0xFF00F5D4L,
        0xFFFF2A85L,
        0xFFFFB800L,
        0xFF7B61FFL,
        0xFFFF3B30L
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dynamic Island Interactive Multitasking Hub
        SectionHeaderRow(
            icon = Icons.Default.SmartButton,
            title = "Интерактивный Динамический Остров",
            badgeText = if (config.isIslandExpanded) "Развернут" else "Компактный"
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
                    title = "Включить Динамический остров (dynamicSpot Pro)",
                    subtitle = "Интерактивная капсула вокруг камеры с мультизадачностью",
                    checked = config.isDynamicIslandEnabled,
                    onCheckedChange = onToggleDynamicIsland,
                    testTag = "toggle_dynamic_island"
                )

                Text(
                    text = "Активный сценарий Острова (нажмите для проверки в превью):",
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

        // 2. Camera Cutout Ring (aodNotify + Energy Ring)
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
                        text = "Калибровка координат под вырез камеры:",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                }

                // X Offset Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Смещение по горизонтали (X)", color = MutedSlate, fontSize = 12.sp)
                    Text("${config.cameraHoleOffsetXDp.toInt()} dp", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
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
                    valueRange = -70f..70f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                // Y Offset Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Отступ сверху (Y)", color = MutedSlate, fontSize = 12.sp)
                    Text("${config.cameraHoleOffsetYDp.toInt()} dp", fontFamily = ShareTechMonoFontFamily, color = CyberCyan)
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
                    valueRange = 4f..36f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                )

                // Radius & Thickness Sliders
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
                    valueRange = 10f..26f,
                    colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                )
            }
        }

        // 3. Per-App Notification Color Rules & Live Simulator
        SectionHeaderRow(
            icon = Icons.Default.NotificationsActive,
            title = "Индивидуальные цвета приложений",
            badgeText = "${appColorRules.size} правил"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            appColorRules.forEach { rule ->
                val ruleColor = Color(rule.colorHex)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ruleColor.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(ruleColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = rule.appDisplayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = rule.categoryRu,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 11.sp,
                                        color = MutedSlate
                                    )
                                }
                            }
                            Button(
                                onClick = { onTriggerSimulatedAppPush(rule) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ruleColor.copy(alpha = 0.22f),
                                    contentColor = ruleColor
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("simulate_push_${rule.packageName}")
                            ) {
                                Text(
                                    text = "Тест пуша",
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Цвет кольца:",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                            selectableColorHexes.forEach { hex ->
                                val c = Color(hex)
                                val isCurrent = rule.colorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(if (isCurrent) 22.dp else 18.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(
                                            width = if (isCurrent) 2.dp else 0.dp,
                                            color = if (isCurrent) PureWhite else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { onUpdateAppRuleColor(rule, hex) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Notification History Log
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
                            text = "Журнал событий Острова и AOD",
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
                    recentNotifications.take(5).forEach { log ->
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
                                    .background(Color(log.colorHex))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${log.appName} • ${log.senderOrTitle}",
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(log.colorHex)
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
