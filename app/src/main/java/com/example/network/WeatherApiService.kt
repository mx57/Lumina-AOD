package com.example.network

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
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class OpenMeteoResponse(
    @Json(name = "current") val current: CurrentBlock?,
    @Json(name = "hourly") val hourly: HourlyBlock?
)

data class CurrentBlock(
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int?,
    @Json(name = "weather_code") val weatherCode: Int?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?
)

data class HourlyBlock(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "temperature_2m") val temperature2m: List<Double>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "uv_index") val uvIndex: List<Double>?
)

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun fetchForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,uv_index",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

object WeatherNetworkClient {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoApi::class.java)
    }

    suspend fun fetchCityWeather(city: WeatherCityOption): Result<WeatherSnapshot> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.fetchForecast(
                    latitude = city.latitude,
                    longitude = city.longitude
                )
                val current = response.current
                val temp = current?.temperature2m?.roundToInt() ?: 19
                val feelsLike = current?.apparentTemperature?.roundToInt() ?: temp
                val humidity = current?.relativeHumidity2m ?: 58
                val code = current?.weatherCode ?: 0
                val wind = current?.windSpeed10m?.roundToInt() ?: 9

                val hourlyTimes = response.hourly?.time.orEmpty()
                val hourlyTemps = response.hourly?.temperature2m.orEmpty()
                val hourlyCodes = response.hourly?.weatherCode.orEmpty()
                val uvMax = response.hourly?.uvIndex?.maxOrNull()?.toFloat() ?: 3.2f

                val points = mutableListOf<HourlyWeatherPoint>()
                val stepIndices = listOf(8, 11, 14, 17, 20, 23)
                for (idx in stepIndices) {
                    if (idx < hourlyTimes.size && idx < hourlyTemps.size) {
                        val rawTime = hourlyTimes[idx].substringAfter("T", "12:00")
                        val cCode = hourlyCodes.getOrElse(idx) { code }
                        points.add(
                            HourlyWeatherPoint(
                                hourLabel = rawTime,
                                tempC = hourlyTemps[idx].roundToInt(),
                                weatherCode = cCode
                            )
                        )
                    }
                }

                WeatherSnapshot(
                    cityName = city.nameRu,
                    temperatureC = temp,
                    apparentTempC = feelsLike,
                    conditionTextRu = weatherCodeToRussian(code),
                    weatherCode = code,
                    windSpeedKmh = wind,
                    humidityPercent = humidity,
                    uvIndex = (uvMax * 10f).roundToInt() / 10f,
                    hourlyPoints = if (points.isNotEmpty()) points else defaultHourly(temp),
                    updatedAtMillis = System.currentTimeMillis(),
                    isLiveNetwork = true
                )
            }
        }

    fun weatherCodeToRussian(code: Int): String = when (code) {
        0 -> "Чистое небо"
        1, 2 -> "Малооблачно"
        3 -> "Облачно с прояснениями"
        45, 48 -> "Дымка и туман"
        51, 53, 55 -> "Легкая морось"
        61, 63, 65 -> "Дождь"
        71, 73, 75 -> "Снегопад"
        80, 81, 82 -> "Ливневый дождь"
        95, 96, 99 -> "Грозовой фронт"
        else -> "Комфортная погода"
    }

    private fun defaultHourly(baseTemp: Int): List<HourlyWeatherPoint> = listOf(
        HourlyWeatherPoint("09:00", baseTemp - 2, 1),
        HourlyWeatherPoint("12:00", baseTemp, 0),
        HourlyWeatherPoint("15:00", baseTemp + 2, 0),
        HourlyWeatherPoint("18:00", baseTemp + 1, 1),
        HourlyWeatherPoint("21:00", baseTemp - 1, 2),
        HourlyWeatherPoint("00:00", baseTemp - 3, 0)
    )
}
