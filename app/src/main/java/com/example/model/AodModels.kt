package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EnergyEmerald
import com.example.ui.theme.IceBlue
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SolarAmber

enum class ClockCategory(val titleRu: String, val subtitleRu: String) {
    CYBERPUNK("Киберпанк и Неон", "Светящиеся контуры и цифровой импульс"),
    MINIMAL_NOTHING("Nothing OS & Pixel", "Строгий минимализм и минимум пикселей"),
    COSMIC_LIQUID("Космос и Жидкий свет", "Орбитальные сферы и градиентные метаболы"),
    RETRO_ANALOG("Ретро и Хронограф", "Перекидные Flip-часы, матрица и стрелки")
}

enum class ClockFaceStyle(
    val id: String,
    val titleRu: String,
    val subtitleRu: String,
    val category: ClockCategory,
    val baseOprPercent: Float
) {
    CYBER_PULSE(
        id = "cyber_pulse",
        titleRu = "Cyber Neon Pulse",
        subtitleRu = "Неоновый глиф с двойным контуром и секундами",
        category = ClockCategory.CYBERPUNK,
        baseOprPercent = 2.1f
    ),
    NOTHING_DOT_MATRIX(
        id = "nothing_dot_matrix",
        titleRu = "Nothing OS Dot Matrix",
        subtitleRu = "Точечный матричный шрифт с красным акцентом",
        category = ClockCategory.MINIMAL_NOTHING,
        baseOprPercent = 1.2f
    ),
    PIXEL_ULTRA_THIN(
        id = "pixel_ultra_thin",
        titleRu = "Pixel Ambient Vertical",
        subtitleRu = "Двухстрочный минимализм с ультранизким OPR",
        category = ClockCategory.MINIMAL_NOTHING,
        baseOprPercent = 0.9f
    ),
    COSMIC_ORBIT_SPHERE(
        id = "cosmic_orbit_sphere",
        titleRu = "Cosmic Liquid Sphere",
        subtitleRu = "Плазменная сфера с гравитационными кольцами",
        category = ClockCategory.COSMIC_LIQUID,
        baseOprPercent = 2.6f
    ),
    AURORA_METABALLS(
        id = "aurora_metaballs",
        titleRu = "Aurora Liquid Flow",
        subtitleRu = "Жидкие органические капли северного сияния",
        category = ClockCategory.COSMIC_LIQUID,
        baseOprPercent = 2.4f
    ),
    RETRO_FLIP_CLOCK(
        id = "retro_flip_clock",
        titleRu = "Retro Flip Split",
        subtitleRu = "Механические перекидные пластины с тенью",
        category = ClockCategory.RETRO_ANALOG,
        baseOprPercent = 2.2f
    ),
    SWISS_CHRONOGRAPH(
        id = "swiss_chronograph",
        titleRu = "AMOLED Chronograph",
        subtitleRu = "Тонкие люминесцентные стрелки и циферблат",
        category = ClockCategory.RETRO_ANALOG,
        baseOprPercent = 1.6f
    ),
    SYNTHWAVE_LED_GRID(
        id = "synthwave_led_grid",
        titleRu = "Synth LED Grid 84",
        subtitleRu = "Ретро-матрица с бегущей строкой телеметрии",
        category = ClockCategory.CYBERPUNK,
        baseOprPercent = 2.3f
    );

    companion object {
        fun fromId(id: String): ClockFaceStyle =
            entries.find { it.id == id } ?: CYBER_PULSE
    }
}

enum class EdgeLightingStyle(
    val id: String,
    val titleRu: String,
    val descriptionRu: String,
    val oprAddition: Float
) {
    NEON_WAVE(
        id = "neon_wave",
        titleRu = "Неоновая волна",
        descriptionRu = "Плавный двухцветный градиент по контуру экрана",
        oprAddition = 0.65f
    ),
    LASER_CHASE(
        id = "laser_chase",
        titleRu = "Лазерный импульс",
        descriptionRu = "Два встречных световых кометных луча",
        oprAddition = 0.40f
    ),
    AUDIO_SPECTRUM(
        id = "audio_spectrum",
        titleRu = "Muviz Аудио-Спектр",
        descriptionRu = "Симметричный эквалайзер по боковым граням",
        oprAddition = 0.75f
    ),
    BREATHING_GLOW(
        id = "breathing_glow",
        titleRu = "Мягкое дыхание",
        descriptionRu = "Медленная пульсация при новых уведомлениях",
        oprAddition = 0.50f
    ),
    CORNER_SPARKS(
        id = "corner_sparks",
        titleRu = "Угловые разряды",
        descriptionRu = "Подсветка только скругленных углов дисплея",
        oprAddition = 0.25f
    ),
    OFF(
        id = "off",
        titleRu = "Отключено",
        descriptionRu = "Только часы и кружок вокруг камеры (0% краев)",
        oprAddition = 0.0f
    );

    companion object {
        fun fromId(id: String): EdgeLightingStyle =
            entries.find { it.id == id } ?: NEON_WAVE
    }
}

enum class AodColorPalette(
    val id: String,
    val titleRu: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color
) {
    CYBER_CYAN_MAGENTA(
        id = "cyber_cyan_magenta",
        titleRu = "Киберпанк Неон",
        primaryColor = CyberCyan,
        secondaryColor = NeonMagenta,
        accentColor = ElectricViolet
    ),
    NOTHING_RED_MONO(
        id = "nothing_red_mono",
        titleRu = "Nothing OS Red",
        primaryColor = PureWhite,
        secondaryColor = MatrixRed,
        accentColor = CyberCyan
    ),
    AURORA_EMERALD(
        id = "aurora_emerald",
        titleRu = "Северное сияние",
        primaryColor = EnergyEmerald,
        secondaryColor = IceBlue,
        accentColor = CyberCyan
    ),
    SOLAR_FLARE(
        id = "solar_flare",
        titleRu = "Солнечная корона",
        primaryColor = SolarAmber,
        secondaryColor = NeonMagenta,
        accentColor = PureWhite
    ),
    COSMIC_VIOLET(
        id = "cosmic_violet",
        titleRu = "Глубокий космос",
        primaryColor = ElectricViolet,
        secondaryColor = CyberCyan,
        accentColor = NeonMagenta
    ),
    PURE_MONOCHROME(
        id = "pure_monochrome",
        titleRu = "Чистый Монохром",
        primaryColor = PureWhite,
        secondaryColor = IceBlue,
        accentColor = PureWhite
    );

    companion object {
        fun fromId(id: String): AodColorPalette =
            entries.find { it.id == id } ?: CYBER_CYAN_MAGENTA
    }
}

enum class CameraRingMode(val id: String, val titleRu: String, val subtitleRu: String) {
    COMBO_PULSE_AND_BATTERY(
        id = "combo",
        titleRu = "Уведомления + Заряд (aodNotify + Energy Ring)",
        subtitleRu = "Цветной импульс при пушах и дуга уровня батареи в покое"
    ),
    NOTIFICATION_LED_ONLY(
        id = "notification_only",
        titleRu = "Только LED-индикатор уведомлений",
        subtitleRu = "Загорается цветом приложения только при непрочитанных пушах"
    ),
    BATTERY_ENERGY_RING(
        id = "battery_only",
        titleRu = "Только Energy Ring (Уровень заряда)",
        subtitleRu = "Градиентное кольцо вокруг камеры от 100% до 0%"
    );

    companion object {
        fun fromId(id: String): CameraRingMode =
            entries.find { it.id == id } ?: COMBO_PULSE_AND_BATTERY
    }
}

enum class WeatherWidgetStyle(val id: String, val titleRu: String, val descriptionRu: String) {
    COMPACT_PILL("compact_pill", "Компактная капсула", "Температура, иконка неба и город в одной строке"),
    HOURLY_CURVE("hourly_curve", "Почасовой тренд", "Кривая температуры на 6 часов + ветер и влажность"),
    THERMO_RING("thermo_ring", "Термо-кольцо и УФ", "Круговой индикатор комфорта, УФ-индекс и осадки"),
    MINIMAL_TEXT("minimal_text", "Строка Nothing OS", "Матричная сводка погоды с расходом < 0.15% пикселей");

    companion object {
        fun fromId(id: String): WeatherWidgetStyle =
            entries.find { it.id == id } ?: COMPACT_PILL
    }
}

enum class IslandActivityType(val titleRu: String) {
    MEDIA_PLAYER("Музыкальный плеер"),
    MESSAGE_NOTIFICATION("Сообщение с быстрым ответом"),
    ACTIVE_TIMER("Таймер обратного отсчета"),
    INCOMING_CALL("Входящий вызов"),
    WEATHER_ALERT("Погодный радар"),
    BATTERY_TURBO("Быстрая зарядка AMOLED")
}

data class IslandMediaState(
    val trackTitle: String = "Midnight Cybernetics",
    val artistName: String = "Lumina Synth Collective",
    val isPlaying: Boolean = true,
    val progressFloat: Float = 0.42f,
    val durationSec: Int = 218,
    val accentHex: Long = 0xFF00F5D4
)

data class IslandTimerState(
    val label: String = "Фокус-сессия AMOLED",
    val remainingSeconds: Int = 445,
    val totalSeconds: Int = 900,
    val isRunning: Boolean = true
)

data class IslandMessageState(
    val appName: String = "Telegram",
    val sender: String = "Алексей • Дизайн-команда",
    val message: String = "Смотри новый пресет неонового острова с откликом 60fps!",
    val appColorHex: Long = 0xFF38BDF8,
    val lastReplySent: String? = null
)

data class IslandCallState(
    val callerName: String = "Марина (Волна Studio)",
    val callerNumber: String = "+7 (999) 412-88-04",
    val isCallAccepted: Boolean = false,
    val callDurationSeconds: Int = 0
)

data class WeatherCityOption(
    val nameRu: String,
    val latitude: Double,
    val longitude: Double
)

val PredefinedWeatherCities = listOf(
    WeatherCityOption("Москва", 55.7558, 37.6173),
    WeatherCityOption("Санкт-Петербург", 59.9343, 30.3351),
    WeatherCityOption("Екатеринбург", 56.8389, 60.6057),
    WeatherCityOption("Новосибирск", 55.0084, 82.9357),
    WeatherCityOption("Казань", 55.7961, 49.1064),
    WeatherCityOption("Сочи", 43.5855, 39.7231),
    WeatherCityOption("Алматы", 43.2220, 76.8512),
    WeatherCityOption("Минск", 53.9006, 27.5590)
)

data class HourlyWeatherPoint(
    val hourLabel: String,
    val tempC: Int,
    val weatherCode: Int
)

data class WeatherSnapshot(
    val cityName: String = "Москва",
    val temperatureC: Int = 18,
    val apparentTempC: Int = 17,
    val conditionTextRu: String = "Ясное ночное небо",
    val weatherCode: Int = 0,
    val windSpeedKmh: Int = 11,
    val humidityPercent: Int = 62,
    val uvIndex: Float = 2.4f,
    val hourlyPoints: List<HourlyWeatherPoint> = listOf(
        HourlyWeatherPoint("Сейчас", 18, 0),
        HourlyWeatherPoint("+1ч", 17, 1),
        HourlyWeatherPoint("+2ч", 16, 1),
        HourlyWeatherPoint("+3ч", 15, 2),
        HourlyWeatherPoint("+4ч", 15, 0),
        HourlyWeatherPoint("+5ч", 16, 0)
    ),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val isLiveNetwork: Boolean = false
)

data class AodConfigurationState(
    val isAodEnabled: Boolean = true,
    val clockStyle: ClockFaceStyle = ClockFaceStyle.CYBER_PULSE,
    val edgeStyle: EdgeLightingStyle = EdgeLightingStyle.NEON_WAVE,
    val palette: AodColorPalette = AodColorPalette.CYBER_CYAN_MAGENTA,
    val clockScale: Float = 1.0f,
    val aodBrightness: Float = 0.85f,
    val showSeconds: Boolean = true,
    val showDateAndBattery: Boolean = true,
    val showWeatherOnAod: Boolean = true,
    val showNotificationBadges: Boolean = true,
    // Camera Cutout & Dynamic Island
    val isDynamicIslandEnabled: Boolean = true,
    val isCameraRingEnabled: Boolean = true,
    val cameraRingMode: CameraRingMode = CameraRingMode.COMBO_PULSE_AND_BATTERY,
    val cameraHoleOffsetXDp: Float = 0f,
    val cameraHoleOffsetYDp: Float = 12f,
    val cameraRingRadiusDp: Float = 15f,
    val cameraRingThicknessDp: Float = 3.2f,
    val activeIslandType: IslandActivityType = IslandActivityType.MEDIA_PLAYER,
    val isIslandExpanded: Boolean = false,
    // Weather Widget
    val weatherWidgetStyle: WeatherWidgetStyle = WeatherWidgetStyle.COMPACT_PILL,
    val selectedCity: WeatherCityOption = PredefinedWeatherCities.first(),
    // Power & Deep Sleep
    val proximityPocketMode: Boolean = true,
    val pixelShiftEnabled: Boolean = true,
    val burnInCheckerboard: Boolean = true,
    val deepSleepScheduled: Boolean = true,
    val deepSleepStartHour: Int = 0,
    val deepSleepEndHour: Int = 7,
    val isManualDeepSleepActive: Boolean = false,
    val lowBatteryAutoOffPercent: Int = 15,
    val doubleTapToWake: Boolean = true,
    val liftToWake: Boolean = true,
    val persistentShadeNotification: Boolean = true
) {
    fun calculateOprPercentage(): Float {
        if (!isAodEnabled || isManualDeepSleepActive) return 0.0f
        var opr = clockStyle.baseOprPercent * clockScale
        opr += edgeStyle.oprAddition
        if (isCameraRingEnabled) opr += 0.18f
        if (isDynamicIslandEnabled) opr += if (isIslandExpanded) 1.45f else 0.35f
        if (showWeatherOnAod) opr += 0.28f
        if (burnInCheckerboard) opr *= 0.72f
        opr *= (0.5f + aodBrightness * 0.5f)
        return (Math.round(opr * 100f) / 100f).coerceIn(0.1f, 8.5f)
    }

    fun estimatedBatteryDrainPerHourPercent(): Float {
        val opr = calculateOprPercentage()
        if (opr == 0f) return 0.05f
        val baseDrain = 0.22f + (opr * 0.16f)
        val sleepSaving = if (proximityPocketMode && deepSleepScheduled) 0.78f else 1.0f
        return Math.round(baseDrain * sleepSaving * 100f) / 100f
    }
}
