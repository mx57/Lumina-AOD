package com.example.network

import com.example.model.DailyWeatherPoint
import com.example.model.HourlyWeatherPoint
import com.example.model.WeatherCityOption
import com.example.model.WeatherSnapshot
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class OpenMeteoResponse(
    @Json(name = "current") val current: CurrentBlock?,
    @Json(name = "hourly") val hourly: HourlyBlock?,
    @Json(name = "daily") val daily: DailyBlock?
)

data class CurrentBlock(
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int?,
    @Json(name = "weather_code") val weatherCode: Int?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_gusts_10m") val windGusts10m: Double?,
    @Json(name = "surface_pressure") val surfacePressureHpa: Double?
)

data class HourlyBlock(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "temperature_2m") val temperature2m: List<Double>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "uv_index") val uvIndex: List<Double>?,
    @Json(name = "precipitation_probability") val precipitationProb: List<Int>?
)

data class DailyBlock(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "temperature_2m_max") val tempMax: List<Double>?,
    @Json(name = "temperature_2m_min") val tempMin: List<Double>?,
    @Json(name = "precipitation_probability_max") val precipProbMax: List<Int>?
)

data class GeocodingSearchResponse(
    @Json(name = "results") val results: List<GeocodingCityDto>?
)

data class GeocodingCityDto(
    @Json(name = "name") val name: String?,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "country") val country: String?,
    @Json(name = "admin1") val admin1: String?,
    @Json(name = "elevation") val elevation: Double?
)

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun fetchForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m,wind_gusts_10m,surface_pressure",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,uv_index,precipitation_probability",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") query: String,
        @Query("count") count: Int = 12,
        @Query("language") language: String = "ru",
        @Query("format") format: String = "json"
    ): GeocodingSearchResponse
}

object WeatherNetworkClient {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(9, TimeUnit.SECONDS)
        .readTimeout(9, TimeUnit.SECONDS)
        .build()

    private val forecastApi: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoApi::class.java)
    }

    private val geocodingApi: OpenMeteoGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoGeocodingApi::class.java)
    }

    suspend fun searchCitiesOnline(query: String): Result<List<WeatherCityOption>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val trimmed = query.trim()
                if (trimmed.length < 2) return@runCatching emptyList()
                val response = geocodingApi.searchCities(query = trimmed)
                response.results.orEmpty().mapNotNull { dto ->
                    val name = dto.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val lat = dto.latitude ?: return@mapNotNull null
                    val lon = dto.longitude ?: return@mapNotNull null
                    WeatherCityOption(
                        nameRu = name,
                        latitude = lat,
                        longitude = lon,
                        countryRu = dto.country ?: "Мир",
                        admin1Ru = dto.admin1 ?: "",
                        elevationM = dto.elevation?.roundToInt() ?: 100
                    )
                }.distinctBy { "${it.nameRu}_${(it.latitude * 10).roundToInt()}_${(it.longitude * 10).roundToInt()}" }
            }
        }

    suspend fun fetchCityWeather(city: WeatherCityOption): Result<WeatherSnapshot> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = forecastApi.fetchForecast(
                    latitude = city.latitude,
                    longitude = city.longitude
                )
                val current = response.current
                val temp = current?.temperature2m?.roundToInt() ?: 19
                val feelsLike = current?.apparentTemperature?.roundToInt() ?: temp
                val humidity = current?.relativeHumidity2m ?: 58
                val code = current?.weatherCode ?: 0
                val wind = current?.windSpeed10m?.roundToInt() ?: 9
                val windGusts = current?.windGusts10m?.roundToInt() ?: (wind + 6)
                val pressureHpa = current?.surfacePressureHpa ?: 1005.0
                val pressureMmHg = (pressureHpa * 0.750062).roundToInt()

                val hourlyTimes = response.hourly?.time.orEmpty()
                val hourlyTemps = response.hourly?.temperature2m.orEmpty()
                val hourlyCodes = response.hourly?.weatherCode.orEmpty()
                val hourlyPrecip = response.hourly?.precipitationProb.orEmpty()
                val uvMax = response.hourly?.uvIndex?.take(24)?.maxOrNull()?.toFloat() ?: 3.2f

                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).coerceIn(0, 23)
                val points = mutableListOf<HourlyWeatherPoint>()
                for (step in 0 until 8) {
                    val idx = currentHour + step * 3
                    if (idx < hourlyTimes.size && idx < hourlyTemps.size) {
                        val rawTime = hourlyTimes[idx].substringAfter("T", "12:00")
                        val cCode = hourlyCodes.getOrElse(idx) { code }
                        val pProb = hourlyPrecip.getOrElse(idx) { 10 }
                        points.add(
                            HourlyWeatherPoint(
                                hourLabel = if (step == 0) "Сейчас" else rawTime,
                                tempC = hourlyTemps[idx].roundToInt(),
                                weatherCode = cCode,
                                precipProbPercent = pProb
                            )
                        )
                    }
                }

                val dailyDates = response.daily?.time.orEmpty()
                val dailyCodes = response.daily?.weatherCode.orEmpty()
                val dailyMax = response.daily?.tempMax.orEmpty()
                val dailyMin = response.daily?.tempMin.orEmpty()
                val dailyPrecip = response.daily?.precipProbMax.orEmpty()

                val dailyList = mutableListOf<DailyWeatherPoint>()
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val dayFmt = SimpleDateFormat("dd.MM", Locale.getDefault())
                for (dIdx in 0 until minOf(7, dailyDates.size)) {
                    val rawDate = dailyDates[dIdx]
                    val parsedDate = runCatching { parser.parse(rawDate) }.getOrNull()
                    val dateLabel = when (dIdx) {
                        0 -> "Сегодня"
                        1 -> "Завтра"
                        else -> if (parsedDate != null) dayFmt.format(parsedDate) else rawDate
                    }
                    val dowRu = if (parsedDate != null) {
                        val cal = Calendar.getInstance().apply { time = parsedDate }
                        when (cal.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> "Пн"
                            Calendar.TUESDAY -> "Вт"
                            Calendar.WEDNESDAY -> "Ср"
                            Calendar.THURSDAY -> "Чт"
                            Calendar.FRIDAY -> "Пт"
                            Calendar.SATURDAY -> "Сб"
                            else -> "Вс"
                        }
                    } else "Дн"

                    dailyList.add(
                        DailyWeatherPoint(
                            dateLabel = dateLabel,
                            dayOfWeekRu = dowRu,
                            tempMaxC = dailyMax.getOrElse(dIdx) { temp + 2.0 }.roundToInt(),
                            tempMinC = dailyMin.getOrElse(dIdx) { temp - 5.0 }.roundToInt(),
                            weatherCode = dailyCodes.getOrElse(dIdx) { code },
                            precipitationProbPercent = dailyPrecip.getOrElse(dIdx) { 15 }
                        )
                    )
                }

                val currentPrecipProb = points.firstOrNull()?.precipProbPercent
                    ?: dailyList.firstOrNull()?.precipitationProbPercent
                    ?: 10

                WeatherSnapshot(
                    cityName = city.nameRu,
                    regionAndCountry = if (city.admin1Ru.isNotBlank() && city.admin1Ru != city.nameRu) {
                        "${city.admin1Ru}, ${city.countryRu}"
                    } else {
                        city.countryRu
                    },
                    latitude = city.latitude,
                    longitude = city.longitude,
                    temperatureC = temp,
                    apparentTempC = feelsLike,
                    conditionTextRu = weatherCodeToRussian(code),
                    weatherCode = code,
                    windSpeedKmh = wind,
                    windGustsKmh = windGusts,
                    humidityPercent = humidity,
                    pressureMmHg = pressureMmHg,
                    precipitationProbPercent = currentPrecipProb,
                    uvIndex = (uvMax * 10f).roundToInt() / 10f,
                    hourlyPoints = if (points.isNotEmpty()) points else defaultHourly(temp),
                    dailyPoints = if (dailyList.isNotEmpty()) dailyList else defaultDaily(temp),
                    updatedAtMillis = System.currentTimeMillis(),
                    isLiveNetwork = true
                )
            }
        }

    fun weatherCodeToRussian(code: Int): String = when (code) {
        0 -> "Чистое небо"
        1, 2 -> "Малооблачно"
        3 -> "Пасмурно"
        45, 48 -> "Дымка и туман"
        51, 53, 55 -> "Морось"
        56, 57 -> "Ледяная морось"
        61, 63, 65 -> "Дождь"
        66, 67 -> "Ледяной дождь"
        71, 73, 75, 77 -> "Снегопад"
        80, 81, 82 -> "Ливневый дождь"
        85, 86 -> "Снежный шквал"
        95, 96, 99 -> "Грозовой фронт"
        else -> "Комфортная погода"
    }

    private fun defaultHourly(baseTemp: Int): List<HourlyWeatherPoint> = listOf(
        HourlyWeatherPoint("Сейчас", baseTemp, 0, 5),
        HourlyWeatherPoint("09:00", baseTemp - 2, 1, 10),
        HourlyWeatherPoint("12:00", baseTemp + 1, 0, 5),
        HourlyWeatherPoint("15:00", baseTemp + 3, 0, 5),
        HourlyWeatherPoint("18:00", baseTemp + 1, 1, 15),
        HourlyWeatherPoint("21:00", baseTemp - 1, 2, 20),
        HourlyWeatherPoint("00:00", baseTemp - 3, 0, 10),
        HourlyWeatherPoint("03:00", baseTemp - 4, 0, 5)
    )

    private fun defaultDaily(baseTemp: Int): List<DailyWeatherPoint> = listOf(
        DailyWeatherPoint("Сегодня", "Ср", baseTemp + 2, baseTemp - 5, 0, 10),
        DailyWeatherPoint("Завтра", "Чт", baseTemp + 3, baseTemp - 4, 1, 15),
        DailyWeatherPoint("+2 дн", "Пт", baseTemp + 1, baseTemp - 6, 2, 25),
        DailyWeatherPoint("+3 дн", "Сб", baseTemp - 1, baseTemp - 7, 61, 60),
        DailyWeatherPoint("+4 дн", "Вс", baseTemp, baseTemp - 6, 1, 20),
        DailyWeatherPoint("+5 дн", "Пн", baseTemp + 2, baseTemp - 4, 0, 10),
        DailyWeatherPoint("+6 дн", "Вт", baseTemp + 4, baseTemp - 3, 0, 5)
    )
}
