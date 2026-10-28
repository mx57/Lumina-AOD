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
import kotlin.math.roundToInt

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
        subtitleRu = "Неоновый глиф с двойным контуром и 3D-свечением",
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
        subtitleRu = "Плазменная сфера с гравитационными кольцами 120Hz",
        category = ClockCategory.COSMIC_LIQUID,
        baseOprPercent = 2.6f
    ),
    AURORA_METABALLS(
        id = "aurora_metaballs",
        titleRu = "Aurora Liquid Flow",
        subtitleRu = "Жидкие органические метаболы северного сияния",
        category = ClockCategory.COSMIC_LIQUID,
        baseOprPercent = 2.4f
    ),
    RETRO_FLIP_CLOCK(
        id = "retro_flip_clock",
        titleRu = "Retro Flip Split",
        subtitleRu = "Механические перекидные пластины с 3D-фаской",
        category = ClockCategory.RETRO_ANALOG,
        baseOprPercent = 2.2f
    ),
    SWISS_CHRONOGRAPH(
        id = "swiss_chronograph",
        titleRu = "AMOLED Chronograph",
        subtitleRu = "Плавная секундная развертка 120Hz и люминофор",
        category = ClockCategory.RETRO_ANALOG,
        baseOprPercent = 1.6f
    ),
    SYNTHWAVE_LED_GRID(
        id = "synthwave_led_grid",
        titleRu = "Synth LED Grid 84",
        subtitleRu = "Голографическая матрица с бегущей строкой телеметрии",
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
        titleRu = "Жидкая Неоновая Волна",
        descriptionRu = "Многослойный шейдерный градиент 120Hz с ореолом",
        oprAddition = 0.65f
    ),
    LASER_CHASE(
        id = "laser_chase",
        titleRu = "Лазерные Кометы",
        descriptionRu = "Встречные плазменные лучи с затухающим шлейфом",
        oprAddition = 0.40f
    ),
    AUDIO_SPECTRUM(
        id = "audio_spectrum",
        titleRu = "Muviz Аудио-Спектр",
        descriptionRu = "Симметричный волновой эквалайзер по боковым граням",
        oprAddition = 0.75f
    ),
    BREATHING_GLOW(
        id = "breathing_glow",
        titleRu = "Глубокое Дыхание Bloom",
        descriptionRu = "Мягкая органическая пульсация с рассеянным светом",
        oprAddition = 0.50f
    ),
    CORNER_SPARKS(
        id = "corner_sparks",
        titleRu = "Угловые Плазма-Дуги",
        descriptionRu = "Энергетическая подсветка скругленных углов дисплея",
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
        subtitleRu = "Жидкий неоновый импульс при пушах и дуга батареи в покое"
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

enum class IslandShapeStyle(
    val id: String,
    val titleRu: String,
    val subtitleRu: String
) {
    CAPSULE(
        id = "capsule",
        titleRu = "Классическая Капсула",
        subtitleRu = "Гладкая обтекаемая таблетка с мягким неоновым ореолом"
    ),
    SPLIT_DROP(
        id = "split_drop",
        titleRu = "Разделенная Капля (iPhone Pro)",
        subtitleRu = "Основная капсула + отдельный жидкий спутник-метабол справа"
    ),
    CYBER_HEXAGON(
        id = "cyber_hexagon",
        titleRu = "Cyber-Гексагон",
        subtitleRu = "Скошенные технологичные грани в стиле киберпанк-визора"
    ),
    LIQUID_SPHERE(
        id = "liquid_sphere",
        titleRu = "Жидкая Сфера (Metaball)",
        subtitleRu = "Органическая капля с волнообразным живым контуром 120Hz"
    ),
    ULTRA_THREAD(
        id = "ultra_thread",
        titleRu = "Ультра-Нить (Minimal Bar)",
        subtitleRu = "Тонкая парящая световая полоса с минимальным перекрытием"
    ),
    HIDDEN_OUTLINE(
        id = "hidden_outline",
        titleRu = "Скрытый Неоновый Контур",
        subtitleRu = "Прозрачное тело с яркой лазерной обводкой только при событиях"
    );

    companion object {
        fun fromId(id: String): IslandShapeStyle =
            entries.find { it.id == id } ?: CAPSULE
    }
}

enum class IslandAnchorPreset(
    val id: String,
    val titleRu: String,
    val defaultXDp: Float,
    val defaultYDp: Float
) {
    TOP_CENTER("top_center", "Сверху по центру", 0f, 8f),
    TOP_LEFT("top_left", "Верхний левый угол", -64f, 10f),
    TOP_RIGHT("top_right", "Верхний правый угол", 64f, 10f),
    UPPER_FLOATING("upper_floating", "Над часами (Парящий)", 0f, 44f),
    CENTER_HUD("center_hud", "По центру экрана (HUD)", 0f, 118f),
    BOTTOM_DOCK("bottom_dock", "Нижний док-остров", 0f, 224f),
    FREE_DRAG("free_drag", "Свободно (Пальцем / X-Y)", 0f, 12f);

    companion object {
        fun fromId(id: String): IslandAnchorPreset =
            entries.find { it.id == id } ?: TOP_CENTER
    }
}

enum class AppFilterMode(
    val id: String,
    val titleRu: String,
    val subtitleRu: String
) {
    HYBRID_SMART(
        id = "hybrid_smart",
        titleRu = "Гибридный фильтр (Приоритет Белого + Блок Черного)",
        subtitleRu = "Блокирует приложения из Черного списка; приложения из Белого списка всегда будят экран и раскрывают Остров"
    ),
    WHITELIST_ONLY(
        id = "whitelist_only",
        titleRu = "Только Белый список (Строгий режим VIP)",
        subtitleRu = "AOD, Остров и подсветка срабатывают ТОЛЬКО для приложений, отмеченных в Белом списке"
    ),
    BLACKLIST_ONLY(
        id = "blacklist_only",
        titleRu = "Черный список (Разрешены все, кроме заблокированных)",
        subtitleRu = "Принимаются уведомления от всех приложений, кроме добавленных в Черный список"
    );

    companion object {
        fun fromId(id: String): AppFilterMode =
            entries.find { it.id == id } ?: HYBRID_SMART
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
    val message: String = "Жидкий морфинг 120Hz и перетаскивание Острова активированы!",
    val appColorHex: Long = 0xFF38BDF8,
    val lastReplySent: String? = null
)

data class IslandCallState(
    val callerName: String = "Марина (Волна Studio)",
    val callerNumber: String = "+7 (999) 412-88-04",
    val isCallAccepted: Boolean = false,
    val callDurationSeconds: Int = 0
)

data class SystemPermissionStatus(
    val hasNotificationListenerAccess: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val hasPostNotificationsPermission: Boolean = true,
    val isIgnoringBatteryOptimizations: Boolean = false,
    val isForegroundServiceRunning: Boolean = true
) {
    val grantedCount: Int
        get() = listOf(
            hasNotificationListenerAccess,
            hasOverlayPermission,
            hasPostNotificationsPermission,
            isIgnoringBatteryOptimizations
        ).count { it }

    val allCriticalGranted: Boolean
        get() = hasNotificationListenerAccess && hasOverlayPermission && hasPostNotificationsPermission
}

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean
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
    val aodBrightness: Float = 0.88f,
    val showSeconds: Boolean = true,
    val showDateAndBattery: Boolean = true,
    val showWeatherOnAod: Boolean = true,
    val showNotificationBadges: Boolean = true,
    // 2026 Physics & Motion Settings
    val parallax3dEnabled: Boolean = true,
    val parallaxIntensity: Float = 0.75f,
    val liquidMetaballMorphEnabled: Boolean = true,
    val animationSpeedMultiplier: Float = 1.0f,
    val edgeParticleSparksEnabled: Boolean = true,
    // System Events & Overlay Triggers
    val autoWakeOnNotification: Boolean = true,
    val autoWakeOnCharging: Boolean = true,
    val autoShowOnScreenOff: Boolean = true,
    val systemOverlayEnabled: Boolean = true,
    val appFilterMode: AppFilterMode = AppFilterMode.HYBRID_SMART,
    // Camera Cutout & Movable Multi-Shape Dynamic Island
    val isDynamicIslandEnabled: Boolean = true,
    val isCameraRingEnabled: Boolean = true,
    val cameraRingMode: CameraRingMode = CameraRingMode.COMBO_PULSE_AND_BATTERY,
    val cameraHoleOffsetXDp: Float = 0f,
    val cameraHoleOffsetYDp: Float = 12f,
    val cameraRingRadiusDp: Float = 15f,
    val cameraRingThicknessDp: Float = 3.2f,
    // Dynamic Island Custom Position, Shape & Styling
    val islandShape: IslandShapeStyle = IslandShapeStyle.SPLIT_DROP,
    val islandAnchor: IslandAnchorPreset = IslandAnchorPreset.TOP_CENTER,
    val islandOffsetXDp: Float = 0f,
    val islandOffsetYDp: Float = 8f,
    val isIslandFreeDragEnabled: Boolean = true,
    val islandMagneticSnap: Boolean = true,
    val islandWidthScale: Float = 1.0f,
    val islandHeightScale: Float = 1.0f,
    val islandCornerRadiusDp: Float = 28f,
    val islandGlassOpacity: Float = 0.92f,
    val islandNeonGlowIntensity: Float = 0.85f,
    val islandSpringBounciness: Float = 0.75f,
    val islandBorderWidthDp: Float = 1.4f,
    val islandAutoCollapseSec: Int = 8,
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
        if (isDynamicIslandEnabled) {
            val shapeFactor = when (islandShape) {
                IslandShapeStyle.ULTRA_THREAD, IslandShapeStyle.HIDDEN_OUTLINE -> 0.55f
                IslandShapeStyle.SPLIT_DROP -> 1.1f
                else -> 1.0f
            }
            opr += (if (isIslandExpanded) 1.45f else 0.35f) * islandWidthScale * shapeFactor
        }
        if (showWeatherOnAod) opr += 0.28f
        if (burnInCheckerboard) opr *= 0.72f
        opr *= (0.5f + aodBrightness * 0.5f)
        return ((opr * 100f).roundToInt() / 100f).coerceIn(0.1f, 8.5f)
    }

    fun estimatedBatteryDrainPerHourPercent(): Float {
        val opr = calculateOprPercentage()
        if (opr == 0f) return 0.05f
        val baseDrain = 0.22f + (opr * 0.16f)
        val sleepSaving = if (proximityPocketMode && deepSleepScheduled) 0.78f else 1.0f
        return ((baseDrain * sleepSaving * 100f).roundToInt() / 100f)
    }
}
