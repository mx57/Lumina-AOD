package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AodConfigurationState
import com.example.model.PredefinedWeatherCities
import com.example.model.WeatherCityOption
import com.example.model.WeatherSnapshot
import com.example.model.WeatherWidgetStyle
import com.example.ui.components.AodWeatherWidgetView
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.IceBlue
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
fun WeatherAndWidgetsSection(
    config: AodConfigurationState,
    weather: WeatherSnapshot,
    isLoading: Boolean,
    onSelectCity: (WeatherCityOption) -> Unit,
    onRefreshWeather: () -> Unit,
    onSelectWidgetStyle: (WeatherWidgetStyle) -> Unit,
    onToggleShowWeatherOnAod: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Open-Meteo Telemetry Card
        SectionHeaderRow(
            icon = Icons.Default.CloudSync,
            title = "Живая метеосводка Open-Meteo",
            badgeText = if (weather.isLiveNetwork) "Онлайн API" else "Автономный кэш"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weather.cityName,
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite
                            )
                        }
                        Text(
                            text = "${weather.conditionTextRu} • Ощущается как ${weather.apparentTempC}°C",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlate
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${weather.temperatureC}°C",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = SolarAmber
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = onRefreshWeather,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan.copy(alpha = 0.2f),
                                contentColor = CyberCyan
                            ),
                            shape = CircleShape,
                            modifier = Modifier.testTag("refresh_weather_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = CyberCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Обновить погоду",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Weather Metric Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherMetricMiniCard(
                        icon = Icons.Default.Air,
                        label = "Ветер",
                        value = "${weather.windSpeedKmh} км/ч",
                        tint = IceBlue,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniCard(
                        icon = Icons.Default.WaterDrop,
                        label = "Влажность",
                        value = "${weather.humidityPercent}%",
                        tint = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniCard(
                        icon = Icons.Default.WbSunny,
                        label = "Индекс УФ",
                        value = "${weather.uvIndex}",
                        tint = SolarAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hourly Temperature Curve Canvas
                Text(
                    text = "Почасовой температурный профиль для AOD:",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 11.sp,
                    color = MutedSlate
                )
                Spacer(modifier = Modifier.height(8.dp))
                HourlyTemperatureGraph(weather = weather)
            }
        }

        // 2. City Selector
        SectionHeaderRow(
            icon = Icons.Default.LocationOn,
            title = "Город для погодного виджета",
            badgeText = config.selectedCity.nameRu
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PredefinedWeatherCities.forEach { city ->
                val selected = config.selectedCity.nameRu == city.nameRu
                FilterChip(
                    selected = selected,
                    onClick = { onSelectCity(city) },
                    label = { Text(city.nameRu) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = PitchBlack
                    ),
                    modifier = Modifier.testTag("weather_city_${city.nameRu}")
                )
            }
        }

        // 3. Selectable AOD Weather Widget Styles
        SectionHeaderRow(
            icon = Icons.Default.Widgets,
            title = "Дизайн виджета погоды на AOD",
            badgeText = config.weatherWidgetStyle.titleRu
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingToggleRow(
                    title = "Интегрировать погоду в Always On Display",
                    subtitle = "Автоматическое энергосберегающее обновление прогноза",
                    checked = config.showWeatherOnAod,
                    onCheckedChange = onToggleShowWeatherOnAod
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            WeatherWidgetStyle.entries.forEach { style ->
                val selected = config.weatherWidgetStyle == style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectWidgetStyle(style) }
                        .testTag("weather_widget_style_${style.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) Color(0xFF101F30) else ObsidianCard
                    ),
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) CyberCyan else ObsidianBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = style.titleRu,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) CyberCyan else PureWhite
                                )
                                Text(
                                    text = style.descriptionRu,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 11.sp,
                                    color = MutedSlate
                                )
                            }
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(EnergyEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "АКТИВЕН",
                                        fontFamily = ShareTechMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = EnergyEmerald
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PitchBlack)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AodWeatherWidgetView(
                                style = style,
                                weather = weather,
                                primaryColor = config.palette.primaryColor,
                                secondaryColor = config.palette.secondaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricMiniCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0B101A))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    color = MutedSlate
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 15.sp,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HourlyTemperatureGraph(weather: WeatherSnapshot) {
    val points = weather.hourlyPoints
    if (points.isEmpty()) return

    val minT = (points.minOfOrNull { it.tempC } ?: 10) - 2
    val maxT = (points.maxOfOrNull { it.tempC } ?: 25) + 2
    val range = (maxT - minT).coerceAtLeast(4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PitchBlack)
            .padding(12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
            val path = Path()
            points.forEachIndexed { idx, pt ->
                val x = idx * stepX
                val normY = 1f - ((pt.tempC - minT).toFloat() / range.toFloat())
                val y = 10.dp.toPx() + normY * (size.height - 20.dp.toPx())
                if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(CyberCyan, SolarAmber, NeonMagenta)),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            points.forEachIndexed { idx, pt ->
                val x = idx * stepX
                val normY = 1f - ((pt.tempC - minT).toFloat() / range.toFloat())
                val y = 10.dp.toPx() + normY * (size.height - 20.dp.toPx())
                drawCircle(
                    color = PureWhite,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { pt ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${pt.tempC}°C",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                    Text(
                        text = pt.hourLabel,
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = MutedSlate
                    )
                }
            }
        }
    }
}
