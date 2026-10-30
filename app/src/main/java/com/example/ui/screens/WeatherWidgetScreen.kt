package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AodConfigurationState
import com.example.model.WeatherCityOption
import com.example.model.WeatherSnapshot
import com.example.model.WeatherWidgetStyle
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EnergyEmerald
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
fun WeatherWidgetCustomizationSection(
    config: AodConfigurationState,
    weather: WeatherSnapshot,
    isLoading: Boolean,
    favoriteCities: List<WeatherCityOption>,
    citySearchQuery: String,
    citySearchResults: List<WeatherCityOption>,
    isSearchingCities: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onExecuteSearchNow: () -> Unit,
    onSelectCity: (WeatherCityOption) -> Unit,
    onRemoveFavoriteCity: (WeatherCityOption) -> Unit,
    onSelectWidgetStyle: (WeatherWidgetStyle) -> Unit,
    onToggleShowWeatherOnAod: (Boolean) -> Unit,
    onRefreshWeather: () -> Unit,
    onPushWeatherToIslandTicker: () -> Unit = {},
    onToggleBarometerCompass: (Boolean) -> Unit = {},
    onRotateCompassTest: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Open-Meteo Telemetry Card
        SectionHeaderRow(
            icon = Icons.Default.Cloud,
            title = "Реальная Метеосводка (Open-Meteo API)",
            badgeText = if (weather.isLiveNetwork) "LIVE СЕТЬ" else "КЕШ"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = weather.cityName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                        Text(
                            text = "${weather.regionAndCountry} • ${String.format("%.2f°, %.2f°", weather.latitude, weather.longitude)}",
                            fontFamily = ShareTechMonoFontFamily,
                            fontSize = 11.sp,
                            color = MutedSlate
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${weather.conditionTextRu} • Ощущается как ${if (weather.apparentTempC > 0) "+${weather.apparentTempC}" else "${weather.apparentTempC}"}°C",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${if (weather.temperatureC > 0) "+${weather.temperatureC}" else "${weather.temperatureC}"}°C",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = SolarAmber
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onRefreshWeather,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF141C2B))
                                .testTag("refresh_weather_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CyberCyan
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Обновить прогноз погоды",
                                    tint = CyberCyan
                                )
                            }
                        }
                    }
                }

                // 6-Pill Real Atmospheric Telemetry Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherMetricMiniPill(
                        icon = Icons.Default.Air,
                        label = "Ветер / Порывы",
                        value = "${weather.windSpeedKmh}/${weather.windGustsKmh} км/ч",
                        tint = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniPill(
                        icon = Icons.Default.Compress,
                        label = "Давление",
                        value = "${weather.pressureMmHg} мм",
                        tint = NeonMagenta,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniPill(
                        icon = Icons.Default.WaterDrop,
                        label = "Влажность",
                        value = "${weather.humidityPercent}%",
                        tint = EnergyEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherMetricMiniPill(
                        icon = Icons.Default.Umbrella,
                        label = "Вероятн. осадков",
                        value = "${weather.precipitationProbPercent}%",
                        tint = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniPill(
                        icon = Icons.Default.WbSunny,
                        label = "УФ-Индекс",
                        value = "${weather.uvIndex}",
                        tint = SolarAmber,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherMetricMiniPill(
                        icon = Icons.Default.Thermostat,
                        label = "Ощущается",
                        value = "${weather.apparentTempC}°C",
                        tint = EnergyEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 24-Hour Temperature Curve Canvas
                Text(
                    text = "ПОЧАСОВОЙ ПРОГНОЗ НА 24 ЧАСА (ШАГ 3 ЧАСА)",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = MutedSlate
                )

                HourlyTemperatureCurveCanvas(
                    points = weather.hourlyPoints.map { it.tempC },
                    accentColor = config.palette.primaryColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    weather.hourlyPoints.forEach { pt ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0E1420))
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${if (pt.tempC > 0) "+${pt.tempC}" else "${pt.tempC}"}°",
                                fontFamily = ShareTechMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "${pt.precipProbPercent}%💧",
                                fontSize = 9.sp,
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

                Button(
                    onClick = onPushWeatherToIslandTicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("push_weather_to_island_ticker_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan.copy(alpha = 0.18f),
                        contentColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Вывести прогноз в бегущую строку Динамического Острова",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 1b. Barometric Storm Radar & GPS Compass Card (New v3.0 Innovation)
        SectionHeaderRow(
            icon = Icons.Default.Explore,
            title = "Барометрический Шторм-Радар и GPS Компас",
            badgeText = "${weather.pressureMmHg} мм • ${config.compassAzimuthDeg}°"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val compassDir = when (((config.compassAzimuthDeg % 360) + 360) % 360) {
                    in 23..67 -> "СВ (Северо-Восток)"
                    in 68..112 -> "В (Восток)"
                    in 113..157 -> "ЮВ (Юго-Восток)"
                    in 158..202 -> "Ю (Юг)"
                    in 203..247 -> "ЮЗ (Юго-Запад)"
                    in 248..292 -> "З (Запад)"
                    in 293..337 -> "СЗ (Северо-Запад)"
                    else -> "С (Север)"
                }
                val baroTrendRu = when {
                    config.barometricDeltaMmHg <= -2.0f -> "Резкое падение (Приближение циклона / осадков)"
                    config.barometricDeltaMmHg >= 2.0f -> "Рост давления (Антициклон / прояснение)"
                    else -> "Барометрический фон стабилен"
                }

                SettingToggleRow(
                    title = "Показывать барометр и компас на экране AOD",
                    subtitle = "Индикатор азимута и тренда атмосферного давления под часами",
                    checked = config.showBarometerAndCompassOnAod,
                    onCheckedChange = onToggleBarometerCompass,
                    testTag = "toggle_barometer_compass_switch"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0E1420))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Курс: $compassDir (${config.compassAzimuthDeg}°)",
                            fontFamily = ShareTechMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CyberCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Тренд 3ч: ${String.format("%+.1f", config.barometricDeltaMmHg)} мм рт. ст. • $baroTrendRu",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = if (config.barometricDeltaMmHg <= -2.0f) SolarAmber else EnergyEmerald
                        )
                    }

                    Button(
                        onClick = onRotateCompassTest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonMagenta.copy(alpha = 0.2f),
                            contentColor = NeonMagenta
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("rotate_compass_test_btn")
                    ) {
                        Text("Поворот +35°", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Global City Search (Open-Meteo Geocoding API) & Favorite Cities
        SectionHeaderRow(
            icon = Icons.Default.Public,
            title = "Онлайн-Поиск Любого Города Мира",
            badgeText = "${favoriteCities.size} в избранном"
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
                    text = "Введите название любого города или поселка на русском или английском языке (живой поиск Open-Meteo Geocoding):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = citySearchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("weather_city_search_input"),
                        label = { Text("Поиск города (например: Калининград, Токио, Дубай)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = CyberCyan
                            )
                        },
                        trailingIcon = {
                            if (citySearchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = MutedSlate
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        )
                    )

                    Button(
                        onClick = onExecuteSearchNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("weather_city_search_btn")
                    ) {
                        if (isSearchingCities) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = PitchBlack
                            )
                        } else {
                            Text("Найти", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Live Geocoding Search Results Dropdown List
                if (citySearchResults.isNotEmpty()) {
                    Text(
                        text = "НАЙДЕННЫЕ ГОРОДА (НАЖМИТЕ ДЛЯ ВЫБОРА И СОХРАНЕНИЯ):",
                        fontFamily = ShareTechMonoFontFamily,
                        fontSize = 10.sp,
                        color = EnergyEmerald
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        citySearchResults.forEach { foundCity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF101929))
                                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { onSelectCity(foundCity) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = foundCity.nameRu,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = foundCity.displaySubtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSlate,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "Выбрать →",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "ИЗБРАННЫЕ И БЫСТРЫЕ ГОРОДА",
                    fontFamily = ShareTechMonoFontFamily,
                    fontSize = 10.sp,
                    color = CyberCyan
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favoriteCities.forEach { city ->
                        val isSelected = config.selectedCity.nameRu.equals(city.nameRu, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCity(city) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = city.nameRu,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (favoriteCities.size > 2 && !isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Удалить из избранного",
                                            modifier = Modifier
                                                .size(13.dp)
                                                .clickable { onRemoveFavoriteCity(city) },
                                            tint = MutedSlate
                                        )
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.22f),
                                selectedLabelColor = CyberCyan,
                                containerColor = Color(0xFF0E131D),
                                labelColor = PureWhite
                            ),
                            modifier = Modifier.testTag("city_chip_${city.nameRu}")
                        )
                    }
                }
            }
        }

        // 3. 7-Day Real Forecast Card
        SectionHeaderRow(
            icon = Icons.Default.CalendarMonth,
            title = "Реальный Прогноз на 7 Дней (${weather.cityName})",
            badgeText = "7 суток"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weather.dailyPoints.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0E131D))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(105.dp)
                        ) {
                            Text(
                                text = day.dayOfWeekRu,
                                fontFamily = ShareTechMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CyberCyan,
                                modifier = Modifier.width(26.dp)
                            )
                            Text(
                                text = day.dateLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (day.weatherCode <= 1) Icons.Default.WbSunny else Icons.Default.Cloud,
                                contentDescription = null,
                                tint = if (day.weatherCode <= 1) SolarAmber else CyberCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${day.precipitationProbPercent}%💧",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "мин ${if (day.tempMinC > 0) "+${day.tempMinC}" else "${day.tempMinC}"}°",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                            Text(
                                text = "макс ${if (day.tempMaxC > 0) "+${day.tempMaxC}" else "${day.tempMaxC}"}°C",
                                fontFamily = ShareTechMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SolarAmber
                            )
                        }
                    }
                }
            }
        }

        // 4. AOD Weather Widget Layout Selector
        SectionHeaderRow(
            icon = Icons.Default.Thermostat,
            title = "Макет Виджета Погоды на Экране AOD",
            badgeText = config.weatherWidgetStyle.titleRu
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Отображать погоду на Always On Display",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Энергоэффективный рендеринг под часами (< 0.28% OPR)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                }
                Switch(
                    checked = config.showWeatherOnAod,
                    onCheckedChange = onToggleShowWeatherOnAod,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PitchBlack,
                        checkedTrackColor = CyberCyan
                    )
                )
            }
        }

        WeatherWidgetStyle.entries.forEach { style ->
            val selected = config.weatherWidgetStyle == style
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectWidgetStyle(style) }
                    .testTag("weather_style_${style.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) Color(0xFF141E30) else ObsidianCard
                ),
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.dp,
                    color = if (selected) CyberCyan else ObsidianBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = style.titleRu,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) CyberCyan else PureWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = style.descriptionRu,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CyberCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Активен",
                                fontFamily = ShareTechMonoFontFamily,
                                fontSize = 10.sp,
                                color = CyberCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricMiniPill(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0E131D))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                fontSize = 9.sp,
                color = MutedSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontFamily = ShareTechMonoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HourlyTemperatureCurveCanvas(
    points: List<Int>,
    accentColor: Color
) {
    if (points.size < 2) return
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val minT = (points.minOrNull() ?: 0) - 1
        val maxT = (points.maxOrNull() ?: 25) + 1
        val span = (maxT - minT).coerceAtLeast(1).toFloat()
        val stepX = size.width / (points.size - 1)

        val path = Path()
        val offsets = points.mapIndexed { idx, temp ->
            val x = idx * stepX
            val normalized = (temp - minT) / span
            val y = size.height - (normalized * size.height)
            Offset(x, y)
        }

        offsets.forEachIndexed { idx, pt ->
            if (idx == 0) path.moveTo(pt.x, pt.y)
            else path.lineTo(pt.x, pt.y)
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(listOf(accentColor, SolarAmber)),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        offsets.forEach { pt ->
            drawCircle(
                color = PureWhite,
                radius = 3.5.dp.toPx(),
                center = pt
            )
        }
    }
}
