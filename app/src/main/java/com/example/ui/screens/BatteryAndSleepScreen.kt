package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AodConfigurationState
import com.example.service.HardwareTelemetryState
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
fun BatteryAndDeepSleepSection(
    config: AodConfigurationState,
    telemetry: HardwareTelemetryState,
    onToggleProximityPocket: (Boolean) -> Unit,
    onTogglePocketSimulation: () -> Unit,
    onTogglePixelShift: (Boolean) -> Unit,
    onTriggerManualPixelShift: () -> Unit,
    onToggleBurnInCheckerboard: (Boolean) -> Unit,
    onToggleDeepSleepSchedule: (Boolean) -> Unit,
    onUpdateDeepSleepHours: (Int, Int) -> Unit,
    onToggleManualDeepSleep: (Boolean) -> Unit,
    onLowBatteryCutoffChange: (Int) -> Unit,
    onToggleDoubleTapWake: (Boolean) -> Unit,
    onToggleLiftToWake: (Boolean) -> Unit,
    onTogglePersistentShadePanel: (Boolean) -> Unit,
    onCycleEdgeFromQuickPanel: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live AMOLED Power & Hardware Telemetry Dashboard
        SectionHeaderRow(
            icon = Icons.Default.BatterySaver,
            title = "Энергопотребление AMOLED и Телеметрия",
            badgeText = "${telemetry.batteryPercent}% • ${telemetry.batteryTempCelsius}°C"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, EnergyEmerald.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Коэффициент активных пикселей (OPR)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlate
                        )
                        Text(
                            text = "${config.calculateOprPercentage()}% ВКЛ / ${String.format("%.2f", 100f - config.calculateOprPercentage())}% ВЫКЛ",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EnergyEmerald
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PitchBlack)
                            .border(1.dp, EnergyEmerald.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "~${config.estimatedBatteryDrainPerHourPercent()}% / час",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 13.sp,
                            color = CyberCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryStatPill(
                        label = "Напряжение",
                        value = "${telemetry.batteryVoltageMv} мВ",
                        color = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryStatPill(
                        label = "Датчик кармана",
                        value = if (telemetry.isProximityCovered) "Закрыт (Сон)" else "Открыт",
                        color = if (telemetry.isProximityCovered) SolarAmber else EnergyEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryStatPill(
                        label = "Pixel Shift",
                        value = "Цикл #${telemetry.pixelShiftCycleCount}",
                        color = NeonMagenta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Deep Sleep Mode & Night Schedule
        SectionHeaderRow(
            icon = Icons.Default.Bedtime,
            title = "Режим Глубокого сна (Deep Sleep)",
            badgeText = if (config.isManualDeepSleepActive) "Спит (0% OPR)" else "${config.deepSleepStartHour}:00–0${config.deepSleepEndHour}:00"
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
                    title = "Мгновенный режим Глубокого сна",
                    subtitle = "Полностью выключает матрицу и анимации (0.00 мА•ч) до пробуждения",
                    checked = config.isManualDeepSleepActive,
                    onCheckedChange = onToggleManualDeepSleep,
                    testTag = "toggle_manual_deep_sleep"
                )

                SettingToggleRow(
                    title = "Ночное расписание Глубокого сна",
                    subtitle = "Автоматически переводить AOD в глубокий сон с ${config.deepSleepStartHour}:00 до 0${config.deepSleepEndHour}:00",
                    checked = config.deepSleepScheduled,
                    onCheckedChange = onToggleDeepSleepSchedule
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val schedules = listOf(
                        Triple("00:00 – 07:00", 0, 7),
                        Triple("23:00 – 06:00", 23, 6),
                        Triple("01:00 – 08:00", 1, 8)
                    )
                    schedules.forEach { (label, startH, endH) ->
                        val active = config.deepSleepStartHour == startH && config.deepSleepEndHour == endH
                        OutlinedButton(
                            onClick = { onUpdateDeepSleepHours(startH, endH) },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, if (active) CyberCyan else ObsidianBorder)
                        ) {
                            Text(
                                text = label,
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 10.sp,
                                color = if (active) CyberCyan else MutedSlate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Порог автоотключения при низком заряде",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PureWhite
                    )
                    Text(
                        text = "${config.lowBatteryAutoOffPercent}%",
                        fontFamily = ShareTechMonoFontFamily,
                        color = SolarAmber
                    )
                }
                Slider(
                    value = config.lowBatteryAutoOffPercent.toFloat(),
                    onValueChange = { onLowBatteryCutoffChange(it.toInt()) },
                    valueRange = 5f..45f,
                    colors = SliderDefaults.colors(
                        thumbColor = SolarAmber,
                        activeTrackColor = SolarAmber
                    )
                )
            }
        }

        // 3. Proximity Sensor, Wake Gestures & Burn-in Protection
        SectionHeaderRow(
            icon = Icons.Default.Security,
            title = "Защита AMOLED от выгорания и Датчики",
            badgeText = "Pixel Shift Вкл"
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
                    title = "Отключение по датчику приближения (Карманный режим)",
                    subtitle = "Гасит экран в кармане, сумке или при укладывании экраном вниз",
                    checked = config.proximityPocketMode,
                    onCheckedChange = onToggleProximityPocket
                )

                Button(
                    onClick = onTogglePocketSimulation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (telemetry.isProximityCovered) SolarAmber else Color(0xFF1A2436),
                        contentColor = if (telemetry.isProximityCovered) PitchBlack else PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_pocket_sensor_button")
                ) {
                    Icon(imageVector = Icons.Default.Sensors, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (telemetry.isProximityCovered) {
                            "Датчик закрыт (Экран погашен) — Нажмите, чтобы открыть"
                        } else {
                            "Проверить датчик приближения (Симуляция кармана)"
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                SettingToggleRow(
                    title = "Орбитальный сдвиг пикселей (Pixel Shift)",
                    subtitle = "Смещает часы по 8-точечной орбите каждую минуту для защиты OLED",
                    checked = config.pixelShiftEnabled,
                    onCheckedChange = onTogglePixelShift
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Текущее смещение: dX=${String.format("%.1f", telemetry.pixelShiftOffsetX)}dp, dY=${String.format("%.1f", telemetry.pixelShiftOffsetY)}dp",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                    OutlinedButton(
                        onClick = onTriggerManualPixelShift,
                        modifier = Modifier.testTag("manual_pixel_shift_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SyncAlt,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Сдвинуть сейчас", fontSize = 11.sp)
                    }
                }

                SettingToggleRow(
                    title = "Субпиксельная микро-сетка (Checkerboard Dimming)",
                    subtitle = "Снижает нагрузку на органические светодиоды на 28% без потери четкости",
                    checked = config.burnInCheckerboard,
                    onCheckedChange = onToggleBurnInCheckerboard
                )

                SettingToggleRow(
                    title = "Пробуждение по двойному тапу (Double-Tap)",
                    subtitle = "Быстрый выход из режима AOD двойным касанием экрана",
                    checked = config.doubleTapToWake,
                    onCheckedChange = onToggleDoubleTapWake
                )

                SettingToggleRow(
                    title = "Активация при поднятии телефона (Lift to Wake)",
                    subtitle = "Использует акселерометр для плавного включения часов при взятии в руку",
                    checked = config.liftToWake,
                    onCheckedChange = onToggleLiftToWake
                )
            }
        }

        // 4. Quick Settings Tile & Notification Shade Control Panel
        SectionHeaderRow(
            icon = Icons.Default.GridView,
            title = "Быстрая панель в шторке уведомлений",
            badgeText = if (config.persistentShadeNotification) "Закреплена" else "Скрыта"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingToggleRow(
                    title = "Пульт быстрой настройки в панели уведомлений",
                    subtitle = "Позволяет в 1 тап переключать AOD, Глубокий сон и стиль подсветки прямо из шторки",
                    checked = config.persistentShadeNotification,
                    onCheckedChange = onTogglePersistentShadePanel,
                    testTag = "toggle_shade_quick_panel"
                )

                // Interactive in-app mirror of the Notification Shade Quick Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PitchBlack)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ПРЕВЬЮ ПУЛЬТА В ШТОРКЕ ANDROID • OPR ${config.calculateOprPercentage()}%",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = CyberCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onToggleManualDeepSleep(!config.isManualDeepSleepActive) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (config.isManualDeepSleepActive) "Пробудить" else "Глубокий сон",
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = SolarAmber
                                )
                            }
                            OutlinedButton(
                                onClick = onCycleEdgeFromQuickPanel,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Стиль краев",
                                    fontFamily = ShareTechMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = NeonMagenta
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        runCatching {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Системный доступ к уведомлениям (Android Settings)",
                        color = PureWhite,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryStatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PitchBlack)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 10.sp,
            color = MutedSlate
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = ShareTechMonoFontFamily,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
