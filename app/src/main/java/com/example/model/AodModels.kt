package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

enum class ClockCategory(val titleRu: String) {
    DIGITAL("Цифровые"),
    ANALOG("Аналоговые"),
    ORBITAL("Космос & Жидкие"),
    MINIMAL("Nothing & Эко")
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
        titleRu = "Кибер-Импульс 2026",
        subtitleRu = "Голографическая типографика Orbitron с двухслойным неоновым свечением",
        category = ClockCategory.DIGITAL,
        baseOprPercent = 2.4f
    ),
    NOTHING_DOT_MATRIX(
        id = "nothing_dot_matrix",
        titleRu = "Nothing Glyph 3.0",
        subtitleRu = "Индустриальный матричный шрифт с ультранизким расходом энергии",
        category = ClockCategory.MINIMAL,
        baseOprPercent = 1.1f
    ),
    PIXEL_ULTRA_THIN(
        id = "pixel_ultra_thin",
        titleRu = "Дзен Ультра-Тонкий",
        subtitleRu = "Крупные двухэтажные часы для ночного режима (< 1.2% OPR)",
        category = ClockCategory.MINIMAL,
        baseOprPercent = 1.2f
    ),
    COSMIC_ORBIT_SPHERE(
        id = "cosmic_orbit_sphere",
        titleRu = "Орбитальная Сфера",
        subtitleRu = "Вращающиеся спутники 120Hz вокруг градиентного ядра времени",
        category = ClockCategory.ORBITAL,
        baseOprPercent = 2.7f
    ),
    AURORA_METABALLS(
        id = "aurora_metaballs",
        titleRu = "Жидкие Метаболы",
        subtitleRu = "Органические плазменные капли, плавно перетекающие за цифрами",
        category = ClockCategory.ORBITAL,
        baseOprPercent = 2.9f
    ),
    RETRO_FLIP_CLOCK(
        id = "retro_flip_clock",
        titleRu = "Перекидные Карточки",
        subtitleRu = "Тактильные механические сегменты часов, минут и секунд",
        category = ClockCategory.DIGITAL,
        baseOprPercent = 2.2f
    ),
    SWISS_CHRONOGRAPH(
        id = "swiss_chronograph",
        titleRu = "Швейцарский Хронограф",
        subtitleRu = "Прецизионный стрелочный циферблат с плавным ходом 120Hz",
        category = ClockCategory.ANALOG,
        baseOprPercent = 1.8f
    ),
    SYNTHWAVE_LED_GRID(
        id = "synthwave_led_grid",
        titleRu = "Синтвейв Голограмма",
        subtitleRu = "Киберпанк-визор с неоновой рамкой и цифровой индикацией",
        category = ClockCategory.DIGITAL,
        baseOprPercent = 2.5f
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
    NEON_WAVE("neon_wave", "Бегущая Волна", "Плавный двухцветный градиент по всему периметру экрана", 0.75f),
    LASER_CHASE("laser_chase", "Лазерный Импульс", "Двойные встречные лучи с шейдерным ореолом по бокам", 0.65f),
    AUDIO_SPECTRUM("audio_spectrum", "Спектр Muviz 120Hz", "Симметричный частотный эквалайзер вдоль изогнутых граней", 0.90f),
    BREATHING_GLOW("breathing_glow", "Дыхание Авроры", "Мягкое пульсирующее свечение при новых уведомлениях", 0.60f),
    CORNER_SPARKS("corner_sparks", "Угловые Искры", "Акцентные неоновые дуги только по углам корпуса", 0.40f),
    OFF("off", "Выключено", "Только центральный циферблат, Остров и кольцо камеры", 0.0f);

    companion object {
        fun fromId(id: String): EdgeLightingStyle =
            entries.find { it.id == id } ?: NEON_WAVE
    }
}

enum class AodColorPalette(
    val id: String,
    val titleRu: String,
    val primaryHex: Long,
    val secondaryHex: Long,
    val accentHex: Long
) {
    CYBER_CYAN_MAGENTA(
        id = "cyber_cyan",
        titleRu = "Киберпанк (Циан + Пурпур)",
        primaryHex = 0xFF00F5D4,
        secondaryHex = 0xFFFF2A85,
        accentHex = 0xFF7B61FF
    ),
    NOTHING_RED_MONO(
        id = "nothing_red_mono",
        titleRu = "Nothing OS (Белый + Алый)",
        primaryHex = 0xFFF8FAFC,
        secondaryHex = 0xFFFF3B30,
        accentHex = 0xFF94A3B8
    ),
    AURORA_EMERALD(
        id = "aurora_emerald",
        titleRu = "Изумрудная Аврора",
        primaryHex = 0xFF00E676,
        secondaryHex = 0xFF00F5D4,
        accentHex = 0xFF38BDF8
    ),
    SOLAR_FLARE(
        id = "solar_flare",
        titleRu = "Солнечная Плазма",
        primaryHex = 0xFFFFB800,
        secondaryHex = 0xFFFF3B30,
        accentHex = 0xFFFF2A85
    ),
    COSMIC_VIOLET(
        id = "cosmic_violet",
        titleRu = "Космический Ультрафиолет",
        primaryHex = 0xFF7B61FF,
        secondaryHex = 0xFFFF2A85,
        accentHex = 0xFF00F5D4
    ),
    ICE_CRYSTAL(
        id = "ice_crystal",
        titleRu = "Кристальный Лед",
        primaryHex = 0xFF38BDF8,
        secondaryHex = 0xFFE2E8F0,
        accentHex = 0xFF7B61FF
    );

    val primaryColor: Color get() = Color(primaryHex)
    val secondaryColor: Color get() = Color(secondaryHex)
    val accentColor: Color get() = Color(accentHex)

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
    ),
    MATRIX_TICKER_BAR(
        id = "matrix_ticker_bar",
        titleRu = "Матричное Табло (LED Ticker)",
        subtitleRu = "Широкая светодиодная панель с точечной матрицей для бегущей строки"
    ),
    DUAL_CYBER_WINGS(
        id = "dual_cyber_wings",
        titleRu = "Двойные Крылья (Dual Wings)",
        subtitleRu = "Центральное ядро камеры с двумя симметричными крыльями по бокам"
    ),
    STARLIGHT_ORB(
        id = "starlight_orb",
        titleRu = "Звездная Капля (Starlight Orb)",
        subtitleRu = "Компактное пульсирующее ядро с радиальными световыми лучами"
    ),
    FLOATING_NOTCH(
        id = "floating_notch",
        titleRu = "Вырез-Монобровь (Top Notch)",
        subtitleRu = "Монолитный верхний козырек, плавно сливающийся с гранью экрана"
    ),
    AUDIO_SPECTRUM_WAVE(
        id = "audio_spectrum_wave",
        titleRu = "Спектр-Волна (Audio Wave)",
        subtitleRu = "Живой эквалайзер с частотными волнами вдоль контура Острова"
    );

    companion object {
        fun fromId(id: String): IslandShapeStyle =
            entries.find { it.id == id } ?: SPLIT_DROP
    }
}

enum class IslandVisibilityMode(
    val id: String,
    val titleRu: String,
    val subtitleRu: String
) {
    ALWAYS_VISIBLE(
        id = "always_visible",
        titleRu = "Всегда виден на экране",
        subtitleRu = "Остров постоянно отображается в компактном или раскрытом виде"
    ),
    ONLY_ON_EVENTS(
        id = "only_on_events",
        titleRu = "Только при уведомлениях и событиях",
        subtitleRu = "Скрыт в покое; плавно появляется при пушах, музыке, звонках и зарядке"
    ),
    ONLY_ON_PUSH(
        id = "only_on_push",
        titleRu = "Только при новых уведомлениях (Авто-скрытие)",
        subtitleRu = "Появляется только при входящем уведомлении на время таймера"
    );

    companion object {
        fun fromId(id: String): IslandVisibilityMode =
            entries.find { it.id == id } ?: ALWAYS_VISIBLE
    }
}

enum class IslandTapAction(
    val id: String,
    val titleRu: String,
    val subtitleRu: String
) {
    EXPAND_THEN_OPEN_APP(
        id = "expand_then_open",
        titleRu = "Тап раскрывает → Повторный тап открывает приложение",
        subtitleRu = "Первый тап раскрывает карточку, тап по раскрытому Острову запускает приложение-источник"
    ),
    OPEN_PARENT_APP_IMMEDIATELY(
        id = "open_immediately",
        titleRu = "Одиночный тап сразу открывает родительское приложение",
        subtitleRu = "Мгновенный переход в приложение уведомления; долгий тап раскрывает Остров"
    ),
    TOGGLE_EXPAND_ONLY(
        id = "toggle_expand_only",
        titleRu = "Только раскрытие/сворачивание (переход по кнопке)",
        subtitleRu = "Тап переключает размер Острова, запуск приложения — по кнопке внутри карточки"
    );

    companion object {
        fun fromId(id: String): IslandTapAction =
            entries.find { it.id == id } ?: EXPAND_THEN_OPEN_APP
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
    HOURLY_CURVE("hourly_curve", "Почасовой тренд", "Кривая температуры + ветер, давление и влажность"),
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
    BATTERY_TURBO("Быстрая зарядка AMOLED"),
    SYSTEM_TELEMETRY("Телеметрия + Фонарик"),
    MARQUEE_TICKER("Бегущая строка (Табло)")
}

data class IslandMediaState(
    val trackTitle: String = "Midnight Cybernetics",
    val artistName: String = "Lumina Synth Collective",
    val isPlaying: Boolean = true,
    val progressFloat: Float = 0.42f,
    val durationSec: Int = 218,
    val accentHex: Long = 0xFF00F5D4,
    val packageName: String = "com.spotify.music"
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
    val message: String = "Жидкий морфинг 120Hz, бегущая строка и переход в приложение по тапу готовы!",
    val appColorHex: Long = 0xFF38BDF8,
    val lastReplySent: String? = null,
    val packageName: String = "org.telegram.messenger",
    val receivedAtMillis: Long = System.currentTimeMillis()
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
    val longitude: Double,
    val countryRu: String = "Россия",
    val admin1Ru: String = "",
    val elevationM: Int = 150
) {
    val displaySubtitle: String
        get() = buildString {
            if (admin1Ru.isNotBlank() && admin1Ru != nameRu) {
                append(admin1Ru)
                append(", ")
            }
            append(countryRu)
            append(" • ")
            append(String.format("%.2f°, %.2f°", latitude, longitude))
        }
}

val PredefinedWeatherCities = listOf(
    WeatherCityOption("Москва", 55.7558, 37.6173, "Россия", "Москва", 156),
    WeatherCityOption("Санкт-Петербург", 59.9343, 30.3351, "Россия", "Санкт-Петербург", 12),
    WeatherCityOption("Екатеринбург", 56.8389, 60.6057, "Россия", "Свердловская область", 237),
    WeatherCityOption("Новосибирск", 55.0084, 82.9357, "Россия", "Новосибирская область", 150),
    WeatherCityOption("Казань", 55.7961, 49.1064, "Россия", "Татарстан", 60),
    WeatherCityOption("Сочи", 43.5855, 39.7231, "Россия", "Краснодарский край", 34),
    WeatherCityOption("Краснодар", 45.0355, 38.9753, "Россия", "Краснодарский край", 29),
    WeatherCityOption("Нижний Новгород", 56.2965, 43.9361, "Россия", "Нижегородская область", 138),
    WeatherCityOption("Самара", 53.1959, 50.1002, "Россия", "Самарская область", 87),
    WeatherCityOption("Владивосток", 43.1155, 131.8855, "Россия", "Приморский край", 58),
    WeatherCityOption("Алматы", 43.2220, 76.8512, "Казахстан", "Алматы", 770),
    WeatherCityOption("Минск", 53.9006, 27.5590, "Беларусь", "Минск", 220)
)

data class HourlyWeatherPoint(
    val hourLabel: String,
    val tempC: Int,
    val weatherCode: Int,
    val precipProbPercent: Int = 0
)

data class DailyWeatherPoint(
    val dateLabel: String,
    val dayOfWeekRu: String,
    val tempMaxC: Int,
    val tempMinC: Int,
    val weatherCode: Int,
    val precipitationProbPercent: Int = 10
)

data class WeatherSnapshot(
    val cityName: String = "Москва",
    val regionAndCountry: String = "Россия",
    val latitude: Double = 55.7558,
    val longitude: Double = 37.6173,
    val temperatureC: Int = 18,
    val apparentTempC: Int = 17,
    val conditionTextRu: String = "Ясное ночное небо",
    val weatherCode: Int = 0,
    val windSpeedKmh: Int = 11,
    val windGustsKmh: Int = 18,
    val humidityPercent: Int = 62,
    val pressureMmHg: Int = 748,
    val precipitationProbPercent: Int = 12,
    val uvIndex: Float = 2.4f,
    val hourlyPoints: List<HourlyWeatherPoint> = listOf(
        HourlyWeatherPoint("Сейчас", 18, 0, 5),
        HourlyWeatherPoint("+3ч", 17, 1, 10),
        HourlyWeatherPoint("+6ч", 15, 1, 15),
        HourlyWeatherPoint("+9ч", 14, 2, 20),
        HourlyWeatherPoint("+12ч", 17, 1, 10),
        HourlyWeatherPoint("+15ч", 20, 0, 5),
        HourlyWeatherPoint("+18ч", 19, 0, 5),
        HourlyWeatherPoint("+21ч", 16, 1, 10)
    ),
    val dailyPoints: List<DailyWeatherPoint> = listOf(
        DailyWeatherPoint("Сегодня", "Ср", 19, 11, 0, 10),
        DailyWeatherPoint("Завтра", "Чт", 21, 12, 1, 15),
        DailyWeatherPoint("+2 дн", "Пт", 18, 10, 2, 25),
        DailyWeatherPoint("+3 дн", "Сб", 16, 9, 61, 65),
        DailyWeatherPoint("+4 дн", "Вс", 17, 10, 1, 20),
        DailyWeatherPoint("+5 дн", "Пн", 20, 11, 0, 10),
        DailyWeatherPoint("+6 дн", "Вт", 22, 13, 0, 5)
    ),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val isLiveNetwork: Boolean = false
)

enum class SmartProfilePreset(
    val id: String,
    val titleRu: String,
    val subtitleRu: String,
    val badgeRu: String,
    val clockStyle: ClockFaceStyle,
    val edgeStyle: EdgeLightingStyle,
    val palette: AodColorPalette,
    val islandShape: IslandShapeStyle,
    val standByMode: Boolean = false
) {
    WORK_FOCUS(
        id = "work_focus",
        titleRu = "Работа и Фокус (Pomodoro)",
        subtitleRu = "Четкий цифровой циферблат, заметка и таймер концентрации на AOD",
        badgeRu = "ФОКУС",
        clockStyle = ClockFaceStyle.CYBER_PULSE,
        edgeStyle = EdgeLightingStyle.CORNER_SPARKS,
        palette = AodColorPalette.CYBER_CYAN_MAGENTA,
        islandShape = IslandShapeStyle.CAPSULE,
        standByMode = false
    ),
    NIGHT_STANDBY(
        id = "night_standby",
        titleRu = "Ночной Прикроватный (StandBy)",
        subtitleRu = "Крупные неслепящие часы в янтарно-красном спектре (OPR < 0.9%)",
        badgeRu = "НОЧЬ 0.8%",
        clockStyle = ClockFaceStyle.PIXEL_ULTRA_THIN,
        edgeStyle = EdgeLightingStyle.OFF,
        palette = AodColorPalette.NOTHING_RED_MONO,
        islandShape = IslandShapeStyle.HIDDEN_OUTLINE,
        standByMode = true
    ),
    CHARGING_TURBO(
        id = "charging_turbo",
        titleRu = "Зарядка AMOLED Turbo",
        subtitleRu = "Орбитальная сфера, бегущая волна по краям и индикатор заряда",
        badgeRu = "TURBO 120HZ",
        clockStyle = ClockFaceStyle.COSMIC_ORBIT_SPHERE,
        edgeStyle = EdgeLightingStyle.NEON_WAVE,
        palette = AodColorPalette.AURORA_EMERALD,
        islandShape = IslandShapeStyle.SPLIT_DROP,
        standByMode = false
    ),
    MUSIC_PARTY(
        id = "music_party",
        titleRu = "Музыка и Эквалайзер Muviz",
        subtitleRu = "Живые метаболы, частотный спектр по граням и остров-волна",
        badgeRu = "АУДИО СПЕКТР",
        clockStyle = ClockFaceStyle.AURORA_METABALLS,
        edgeStyle = EdgeLightingStyle.AUDIO_SPECTRUM,
        palette = AodColorPalette.COSMIC_VIOLET,
        islandShape = IslandShapeStyle.AUDIO_SPECTRUM_WAVE,
        standByMode = false
    ),
    ULTRA_ECO(
        id = "ultra_eco",
        titleRu = "Ультра-Эко Nothing Glyph",
        subtitleRu = "Точечная матрица и появление Острова только при новых пушах",
        badgeRu = "ЭКО 0.9%",
        clockStyle = ClockFaceStyle.NOTHING_DOT_MATRIX,
        edgeStyle = EdgeLightingStyle.OFF,
        palette = AodColorPalette.NOTHING_RED_MONO,
        islandShape = IslandShapeStyle.ULTRA_THREAD,
        standByMode = false
    );

    companion object {
        fun fromId(id: String): SmartProfilePreset =
            entries.find { it.id == id } ?: WORK_FOCUS
    }
}

data class AodConfigurationState(
    val isAodEnabled: Boolean = true,
    val isPreviewCollapsed: Boolean = false,
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
    // Dynamic Island Custom Position, Shape, Visibility, Gestures & Ticker
    val islandShape: IslandShapeStyle = IslandShapeStyle.SPLIT_DROP,
    val islandVisibilityMode: IslandVisibilityMode = IslandVisibilityMode.ALWAYS_VISIBLE,
    val islandTapAction: IslandTapAction = IslandTapAction.EXPAND_THEN_OPEN_APP,
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
    // Marquee Ticker & Extra Island Features
    val islandMarqueeEnabled: Boolean = true,
    val islandMarqueeSpeed: Float = 1.0f,
    val islandMarqueeFontSizeSp: Float = 12f,
    val islandCustomTickerText: String = "",
    val islandShowWeatherInTicker: Boolean = true,
    val islandPrivacyDotEnabled: Boolean = true,
    val islandLiveEqualizerEnabled: Boolean = true,
    val islandQuickTorchEnabled: Boolean = true,
    val islandTemporaryDismissed: Boolean = false,
    val activeIslandType: IslandActivityType = IslandActivityType.MEDIA_PLAYER,
    val isIslandExpanded: Boolean = false,
    // Weather Widget & Barometric/Compass Radar
    val weatherWidgetStyle: WeatherWidgetStyle = WeatherWidgetStyle.COMPACT_PILL,
    val selectedCity: WeatherCityOption = PredefinedWeatherCities.first(),
    val showBarometerAndCompassOnAod: Boolean = true,
    val compassAzimuthDeg: Int = 48,
    val barometricDeltaMmHg: Float = -1.6f,
    // 8 New Innovations: Quick Dock, Pinned Memo & Pomodoro, StandBy Night Mode, ICE QR Card, Smart Profiles
    val isQuickDockEnabled: Boolean = true,
    val showPinnedMemoOnAod: Boolean = true,
    val pinnedMemoText: String = "Фокус: релиз v3.0 • Встреча в 16:00",
    val pomodoroActive: Boolean = false,
    val pomodoroRemainingSec: Int = 1500,
    val standByNightModeEnabled: Boolean = false,
    val standByAutoOnCharging: Boolean = true,
    val standByRedTintOnly: Boolean = false,
    val iceCardEnabled: Boolean = true,
    val iceOwnerName: String = "Алексей • Владелец",
    val iceEmergencyPhone: String = "+7 (999) 412-88-04",
    val iceMedicalOrNote: String = "При обнаружении телефона, пожалуйста, свяжитесь по номеру выше или отсканируйте QR",
    val isIceQrModalVisible: Boolean = false,
    val activeSmartProfile: SmartProfilePreset = SmartProfilePreset.WORK_FOCUS,
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
    fun shouldRenderIslandNow(unreadCount: Int, hasRecentPushEvent: Boolean): Boolean {
        if (!isDynamicIslandEnabled) return false
        if (islandTemporaryDismissed && !isIslandExpanded && !hasRecentPushEvent) return false
        return when (islandVisibilityMode) {
            IslandVisibilityMode.ALWAYS_VISIBLE -> true
            IslandVisibilityMode.ONLY_ON_EVENTS -> {
                isIslandExpanded || unreadCount > 0 || hasRecentPushEvent ||
                    activeIslandType == IslandActivityType.INCOMING_CALL ||
                    activeIslandType == IslandActivityType.BATTERY_TURBO
            }
            IslandVisibilityMode.ONLY_ON_PUSH -> {
                isIslandExpanded || unreadCount > 0 || hasRecentPushEvent
            }
        }
    }

    fun calculateOprPercentage(): Float {
        if (!isAodEnabled || isManualDeepSleepActive) return 0.0f
        if (standByNightModeEnabled) {
            val baseStandBy = if (standByRedTintOnly) 0.68f else 0.88f
            return ((baseStandBy * (0.5f + aodBrightness * 0.5f) * 100f).roundToInt() / 100f).coerceIn(0.3f, 2.5f)
        }
        var opr = clockStyle.baseOprPercent * clockScale
        opr += edgeStyle.oprAddition
        if (isCameraRingEnabled) opr += 0.18f
        if (isDynamicIslandEnabled) {
            val shapeFactor = when (islandShape) {
                IslandShapeStyle.ULTRA_THREAD, IslandShapeStyle.HIDDEN_OUTLINE -> 0.55f
                IslandShapeStyle.SPLIT_DROP, IslandShapeStyle.DUAL_CYBER_WINGS -> 1.15f
                IslandShapeStyle.MATRIX_TICKER_BAR, IslandShapeStyle.AUDIO_SPECTRUM_WAVE -> 1.2f
                else -> 1.0f
            }
            val visFactor = if (islandVisibilityMode == IslandVisibilityMode.ALWAYS_VISIBLE) 1.0f else 0.45f
            opr += (if (isIslandExpanded) 1.45f else 0.35f) * islandWidthScale * shapeFactor * visFactor
        }
        if (showWeatherOnAod) opr += 0.28f
        if (isQuickDockEnabled) opr += 0.14f
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
