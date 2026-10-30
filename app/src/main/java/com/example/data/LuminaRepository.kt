package com.example.data

import android.content.Context
import com.example.model.AodColorPalette
import com.example.model.AodConfigurationState
import com.example.model.AppFilterMode
import com.example.model.CameraRingMode
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.IslandAnchorPreset
import com.example.model.IslandShapeStyle
import com.example.model.IslandTapAction
import com.example.model.IslandVisibilityMode
import com.example.model.PredefinedWeatherCities
import com.example.model.SmartProfilePreset
import com.example.model.WeatherCityOption
import com.example.model.WeatherWidgetStyle
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class LuminaRepository(
    private val dao: LuminaDao,
    private val appContext: Context? = null
) {

    private val prefs by lazy {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val presetsFlow: Flow<List<AodPresetEntity>> = dao.observeAllPresets()
    val appColorRulesFlow: Flow<List<AppColorRuleEntity>> = dao.observeAppColorRules()
    val recentNotificationsFlow: Flow<List<NotificationLogEntity>> = dao.observeRecentNotifications()

    fun loadFavoriteCities(): List<WeatherCityOption> {
        val sp = prefs ?: return PredefinedWeatherCities
        val raw = sp.getString("favoriteCitiesSerialized", null)
        if (raw.isNullOrBlank()) return PredefinedWeatherCities
        val parsed = raw.split(";;").mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size >= 5) {
                val name = parts[0]
                val lat = parts[1].toDoubleOrNull() ?: return@mapNotNull null
                val lon = parts[2].toDoubleOrNull() ?: return@mapNotNull null
                val country = parts[3]
                val admin1 = parts[4]
                val elev = parts.getOrNull(5)?.toIntOrNull() ?: 120
                WeatherCityOption(name, lat, lon, country, admin1, elev)
            } else null
        }
        return if (parsed.isNotEmpty()) parsed else PredefinedWeatherCities
    }

    fun saveFavoriteCities(cities: List<WeatherCityOption>) {
        val sp = prefs ?: return
        val serialized = cities.joinToString(";;") { c ->
            "${c.nameRu.replace("|", " ")}|${c.latitude}|${c.longitude}|${c.countryRu.replace("|", " ")}|${c.admin1Ru.replace("|", " ")}|${c.elevationM}"
        }
        sp.edit().putString("favoriteCitiesSerialized", serialized).apply()
    }

    fun loadPersistedConfig(): AodConfigurationState {
        val sp = prefs ?: return AodConfigurationState()
        val defaultCity = PredefinedWeatherCities.first()
        val cityName = sp.getString("selectedCityName", defaultCity.nameRu) ?: defaultCity.nameRu
        val cityLat = sp.getFloat("selectedCityLat", defaultCity.latitude.toFloat()).toDouble()
        val cityLon = sp.getFloat("selectedCityLon", defaultCity.longitude.toFloat()).toDouble()
        val cityCountry = sp.getString("selectedCityCountry", defaultCity.countryRu) ?: defaultCity.countryRu
        val cityAdmin = sp.getString("selectedCityAdmin", defaultCity.admin1Ru) ?: defaultCity.admin1Ru
        val cityElev = sp.getInt("selectedCityElev", defaultCity.elevationM)
        val city = WeatherCityOption(
            nameRu = cityName,
            latitude = cityLat,
            longitude = cityLon,
            countryRu = cityCountry,
            admin1Ru = cityAdmin,
            elevationM = cityElev
        )

        return AodConfigurationState(
            isAodEnabled = sp.getBoolean("isAodEnabled", true),
            isPreviewCollapsed = sp.getBoolean("isPreviewCollapsed", false),
            clockStyle = ClockFaceStyle.fromId(sp.getString("clockStyle", ClockFaceStyle.CYBER_PULSE.id) ?: ""),
            edgeStyle = EdgeLightingStyle.fromId(sp.getString("edgeStyle", EdgeLightingStyle.NEON_WAVE.id) ?: ""),
            palette = AodColorPalette.fromId(sp.getString("palette", AodColorPalette.CYBER_CYAN_MAGENTA.id) ?: ""),
            clockScale = sp.getFloat("clockScale", 1.0f),
            aodBrightness = sp.getFloat("aodBrightness", 0.88f),
            showSeconds = sp.getBoolean("showSeconds", true),
            showDateAndBattery = sp.getBoolean("showDateAndBattery", true),
            showWeatherOnAod = sp.getBoolean("showWeatherOnAod", true),
            showNotificationBadges = sp.getBoolean("showNotificationBadges", true),
            parallax3dEnabled = sp.getBoolean("parallax3dEnabled", true),
            parallaxIntensity = sp.getFloat("parallaxIntensity", 0.75f),
            liquidMetaballMorphEnabled = sp.getBoolean("liquidMetaballMorphEnabled", true),
            animationSpeedMultiplier = sp.getFloat("animationSpeedMultiplier", 1.0f),
            edgeParticleSparksEnabled = sp.getBoolean("edgeParticleSparksEnabled", true),
            autoWakeOnNotification = sp.getBoolean("autoWakeOnNotification", true),
            autoWakeOnCharging = sp.getBoolean("autoWakeOnCharging", true),
            autoShowOnScreenOff = sp.getBoolean("autoShowOnScreenOff", true),
            systemOverlayEnabled = sp.getBoolean("systemOverlayEnabled", true),
            appFilterMode = AppFilterMode.fromId(sp.getString("appFilterMode", AppFilterMode.HYBRID_SMART.id) ?: ""),
            isDynamicIslandEnabled = sp.getBoolean("isDynamicIslandEnabled", true),
            isCameraRingEnabled = sp.getBoolean("isCameraRingEnabled", true),
            cameraRingMode = CameraRingMode.fromId(sp.getString("cameraRingMode", CameraRingMode.COMBO_PULSE_AND_BATTERY.id) ?: ""),
            cameraHoleOffsetXDp = sp.getFloat("cameraHoleOffsetXDp", 0f),
            cameraHoleOffsetYDp = sp.getFloat("cameraHoleOffsetYDp", 12f),
            cameraRingRadiusDp = sp.getFloat("cameraRingRadiusDp", 15f),
            cameraRingThicknessDp = sp.getFloat("cameraRingThicknessDp", 3.2f),
            islandShape = IslandShapeStyle.fromId(sp.getString("islandShape", IslandShapeStyle.SPLIT_DROP.id) ?: ""),
            islandVisibilityMode = IslandVisibilityMode.fromId(sp.getString("islandVisibilityMode", IslandVisibilityMode.ALWAYS_VISIBLE.id) ?: ""),
            islandTapAction = IslandTapAction.fromId(sp.getString("islandTapAction", IslandTapAction.EXPAND_THEN_OPEN_APP.id) ?: ""),
            islandAnchor = IslandAnchorPreset.fromId(sp.getString("islandAnchor", IslandAnchorPreset.TOP_CENTER.id) ?: ""),
            islandOffsetXDp = sp.getFloat("islandOffsetXDp", 0f),
            islandOffsetYDp = sp.getFloat("islandOffsetYDp", 8f),
            isIslandFreeDragEnabled = sp.getBoolean("isIslandFreeDragEnabled", true),
            islandMagneticSnap = sp.getBoolean("islandMagneticSnap", true),
            islandWidthScale = sp.getFloat("islandWidthScale", 1.0f),
            islandHeightScale = sp.getFloat("islandHeightScale", 1.0f),
            islandCornerRadiusDp = sp.getFloat("islandCornerRadiusDp", 28f),
            islandGlassOpacity = sp.getFloat("islandGlassOpacity", 0.92f),
            islandNeonGlowIntensity = sp.getFloat("islandNeonGlowIntensity", 0.85f),
            islandSpringBounciness = sp.getFloat("islandSpringBounciness", 0.75f),
            islandBorderWidthDp = sp.getFloat("islandBorderWidthDp", 1.4f),
            islandAutoCollapseSec = sp.getInt("islandAutoCollapseSec", 8),
            islandMarqueeEnabled = sp.getBoolean("islandMarqueeEnabled", true),
            islandMarqueeSpeed = sp.getFloat("islandMarqueeSpeed", 1.0f),
            islandMarqueeFontSizeSp = sp.getFloat("islandMarqueeFontSizeSp", 12f),
            islandCustomTickerText = sp.getString("islandCustomTickerText", "") ?: "",
            islandShowWeatherInTicker = sp.getBoolean("islandShowWeatherInTicker", true),
            islandPrivacyDotEnabled = sp.getBoolean("islandPrivacyDotEnabled", true),
            islandLiveEqualizerEnabled = sp.getBoolean("islandLiveEqualizerEnabled", true),
            islandQuickTorchEnabled = sp.getBoolean("islandQuickTorchEnabled", true),
            weatherWidgetStyle = WeatherWidgetStyle.fromId(sp.getString("weatherWidgetStyle", WeatherWidgetStyle.COMPACT_PILL.id) ?: ""),
            selectedCity = city,
            showBarometerAndCompassOnAod = sp.getBoolean("showBarometerAndCompassOnAod", true),
            compassAzimuthDeg = sp.getInt("compassAzimuthDeg", 48),
            barometricDeltaMmHg = sp.getFloat("barometricDeltaMmHg", -1.6f),
            isQuickDockEnabled = sp.getBoolean("isQuickDockEnabled", true),
            showPinnedMemoOnAod = sp.getBoolean("showPinnedMemoOnAod", true),
            pinnedMemoText = sp.getString("pinnedMemoText", "Фокус: релиз v3.0 • Встреча в 16:00") ?: "",
            pomodoroActive = sp.getBoolean("pomodoroActive", false),
            pomodoroRemainingSec = sp.getInt("pomodoroRemainingSec", 1500),
            standByNightModeEnabled = sp.getBoolean("standByNightModeEnabled", false),
            standByAutoOnCharging = sp.getBoolean("standByAutoOnCharging", true),
            standByRedTintOnly = sp.getBoolean("standByRedTintOnly", false),
            iceCardEnabled = sp.getBoolean("iceCardEnabled", true),
            iceOwnerName = sp.getString("iceOwnerName", "Алексей • Владелец") ?: "Алексей • Владелец",
            iceEmergencyPhone = sp.getString("iceEmergencyPhone", "+7 (999) 412-88-04") ?: "+7 (999) 412-88-04",
            iceMedicalOrNote = sp.getString("iceMedicalOrNote", "При обнаружении телефона, пожалуйста, свяжитесь по номеру выше или отсканируйте QR") ?: "",
            activeSmartProfile = SmartProfilePreset.fromId(sp.getString("activeSmartProfile", SmartProfilePreset.WORK_FOCUS.id) ?: ""),
            proximityPocketMode = sp.getBoolean("proximityPocketMode", true),
            pixelShiftEnabled = sp.getBoolean("pixelShiftEnabled", true),
            burnInCheckerboard = sp.getBoolean("burnInCheckerboard", true),
            deepSleepScheduled = sp.getBoolean("deepSleepScheduled", true),
            deepSleepStartHour = sp.getInt("deepSleepStartHour", 0),
            deepSleepEndHour = sp.getInt("deepSleepEndHour", 7),
            isManualDeepSleepActive = sp.getBoolean("isManualDeepSleepActive", false),
            lowBatteryAutoOffPercent = sp.getInt("lowBatteryAutoOffPercent", 15),
            doubleTapToWake = sp.getBoolean("doubleTapToWake", true),
            liftToWake = sp.getBoolean("liftToWake", true),
            persistentShadeNotification = sp.getBoolean("persistentShadeNotification", true)
        )
    }

    fun savePersistedConfig(config: AodConfigurationState) {
        val sp = prefs ?: return
        sp.edit()
            .putBoolean("isAodEnabled", config.isAodEnabled)
            .putBoolean("isPreviewCollapsed", config.isPreviewCollapsed)
            .putString("clockStyle", config.clockStyle.id)
            .putString("edgeStyle", config.edgeStyle.id)
            .putString("palette", config.palette.id)
            .putFloat("clockScale", config.clockScale)
            .putFloat("aodBrightness", config.aodBrightness)
            .putBoolean("showSeconds", config.showSeconds)
            .putBoolean("showDateAndBattery", config.showDateAndBattery)
            .putBoolean("showWeatherOnAod", config.showWeatherOnAod)
            .putBoolean("showNotificationBadges", config.showNotificationBadges)
            .putBoolean("parallax3dEnabled", config.parallax3dEnabled)
            .putFloat("parallaxIntensity", config.parallaxIntensity)
            .putBoolean("liquidMetaballMorphEnabled", config.liquidMetaballMorphEnabled)
            .putFloat("animationSpeedMultiplier", config.animationSpeedMultiplier)
            .putBoolean("edgeParticleSparksEnabled", config.edgeParticleSparksEnabled)
            .putBoolean("autoWakeOnNotification", config.autoWakeOnNotification)
            .putBoolean("autoWakeOnCharging", config.autoWakeOnCharging)
            .putBoolean("autoShowOnScreenOff", config.autoShowOnScreenOff)
            .putBoolean("systemOverlayEnabled", config.systemOverlayEnabled)
            .putString("appFilterMode", config.appFilterMode.id)
            .putBoolean("isDynamicIslandEnabled", config.isDynamicIslandEnabled)
            .putBoolean("isCameraRingEnabled", config.isCameraRingEnabled)
            .putString("cameraRingMode", config.cameraRingMode.id)
            .putFloat("cameraHoleOffsetXDp", config.cameraHoleOffsetXDp)
            .putFloat("cameraHoleOffsetYDp", config.cameraHoleOffsetYDp)
            .putFloat("cameraRingRadiusDp", config.cameraRingRadiusDp)
            .putFloat("cameraRingThicknessDp", config.cameraRingThicknessDp)
            .putString("islandShape", config.islandShape.id)
            .putString("islandVisibilityMode", config.islandVisibilityMode.id)
            .putString("islandTapAction", config.islandTapAction.id)
            .putString("islandAnchor", config.islandAnchor.id)
            .putFloat("islandOffsetXDp", config.islandOffsetXDp)
            .putFloat("islandOffsetYDp", config.islandOffsetYDp)
            .putBoolean("isIslandFreeDragEnabled", config.isIslandFreeDragEnabled)
            .putBoolean("islandMagneticSnap", config.islandMagneticSnap)
            .putFloat("islandWidthScale", config.islandWidthScale)
            .putFloat("islandHeightScale", config.islandHeightScale)
            .putFloat("islandCornerRadiusDp", config.islandCornerRadiusDp)
            .putFloat("islandGlassOpacity", config.islandGlassOpacity)
            .putFloat("islandNeonGlowIntensity", config.islandNeonGlowIntensity)
            .putFloat("islandSpringBounciness", config.islandSpringBounciness)
            .putFloat("islandBorderWidthDp", config.islandBorderWidthDp)
            .putInt("islandAutoCollapseSec", config.islandAutoCollapseSec)
            .putBoolean("islandMarqueeEnabled", config.islandMarqueeEnabled)
            .putFloat("islandMarqueeSpeed", config.islandMarqueeSpeed)
            .putFloat("islandMarqueeFontSizeSp", config.islandMarqueeFontSizeSp)
            .putString("islandCustomTickerText", config.islandCustomTickerText)
            .putBoolean("islandShowWeatherInTicker", config.islandShowWeatherInTicker)
            .putBoolean("islandPrivacyDotEnabled", config.islandPrivacyDotEnabled)
            .putBoolean("islandLiveEqualizerEnabled", config.islandLiveEqualizerEnabled)
            .putBoolean("islandQuickTorchEnabled", config.islandQuickTorchEnabled)
            .putString("weatherWidgetStyle", config.weatherWidgetStyle.id)
            .putString("selectedCityName", config.selectedCity.nameRu)
            .putFloat("selectedCityLat", config.selectedCity.latitude.toFloat())
            .putFloat("selectedCityLon", config.selectedCity.longitude.toFloat())
            .putString("selectedCityCountry", config.selectedCity.countryRu)
            .putString("selectedCityAdmin", config.selectedCity.admin1Ru)
            .putInt("selectedCityElev", config.selectedCity.elevationM)
            .putBoolean("showBarometerAndCompassOnAod", config.showBarometerAndCompassOnAod)
            .putInt("compassAzimuthDeg", config.compassAzimuthDeg)
            .putFloat("barometricDeltaMmHg", config.barometricDeltaMmHg)
            .putBoolean("isQuickDockEnabled", config.isQuickDockEnabled)
            .putBoolean("showPinnedMemoOnAod", config.showPinnedMemoOnAod)
            .putString("pinnedMemoText", config.pinnedMemoText)
            .putBoolean("pomodoroActive", config.pomodoroActive)
            .putInt("pomodoroRemainingSec", config.pomodoroRemainingSec)
            .putBoolean("standByNightModeEnabled", config.standByNightModeEnabled)
            .putBoolean("standByAutoOnCharging", config.standByAutoOnCharging)
            .putBoolean("standByRedTintOnly", config.standByRedTintOnly)
            .putBoolean("iceCardEnabled", config.iceCardEnabled)
            .putString("iceOwnerName", config.iceOwnerName)
            .putString("iceEmergencyPhone", config.iceEmergencyPhone)
            .putString("iceMedicalOrNote", config.iceMedicalOrNote)
            .putString("activeSmartProfile", config.activeSmartProfile.id)
            .putBoolean("proximityPocketMode", config.proximityPocketMode)
            .putBoolean("pixelShiftEnabled", config.pixelShiftEnabled)
            .putBoolean("burnInCheckerboard", config.burnInCheckerboard)
            .putBoolean("deepSleepScheduled", config.deepSleepScheduled)
            .putInt("deepSleepStartHour", config.deepSleepStartHour)
            .putInt("deepSleepEndHour", config.deepSleepEndHour)
            .putBoolean("isManualDeepSleepActive", config.isManualDeepSleepActive)
            .putInt("lowBatteryAutoOffPercent", config.lowBatteryAutoOffPercent)
            .putBoolean("doubleTapToWake", config.doubleTapToWake)
            .putBoolean("liftToWake", config.liftToWake)
            .putBoolean("persistentShadeNotification", config.persistentShadeNotification)
            .apply()
    }

    fun exportConfigToJson(config: AodConfigurationState): String {
        val obj = JSONObject()
        obj.put("version", "3.0")
        obj.put("clockStyle", config.clockStyle.id)
        obj.put("edgeStyle", config.edgeStyle.id)
        obj.put("palette", config.palette.id)
        obj.put("clockScale", config.clockScale.toDouble())
        obj.put("aodBrightness", config.aodBrightness.toDouble())
        obj.put("islandShape", config.islandShape.id)
        obj.put("islandVisibilityMode", config.islandVisibilityMode.id)
        obj.put("islandTapAction", config.islandTapAction.id)
        obj.put("islandOffsetXDp", config.islandOffsetXDp.toDouble())
        obj.put("islandOffsetYDp", config.islandOffsetYDp.toDouble())
        obj.put("islandMarqueeEnabled", config.islandMarqueeEnabled)
        obj.put("weatherWidgetStyle", config.weatherWidgetStyle.id)
        obj.put("isQuickDockEnabled", config.isQuickDockEnabled)
        obj.put("showPinnedMemoOnAod", config.showPinnedMemoOnAod)
        obj.put("pinnedMemoText", config.pinnedMemoText)
        obj.put("standByNightModeEnabled", config.standByNightModeEnabled)
        obj.put("standByRedTintOnly", config.standByRedTintOnly)
        obj.put("iceCardEnabled", config.iceCardEnabled)
        obj.put("showBarometerAndCompassOnAod", config.showBarometerAndCompassOnAod)
        obj.put("activeSmartProfile", config.activeSmartProfile.id)
        return obj.toString()
    }

    fun importConfigFromJson(rawJson: String, current: AodConfigurationState): AodConfigurationState? {
        return runCatching {
            val obj = JSONObject(rawJson.trim())
            current.copy(
                clockStyle = ClockFaceStyle.fromId(obj.optString("clockStyle", current.clockStyle.id)),
                edgeStyle = EdgeLightingStyle.fromId(obj.optString("edgeStyle", current.edgeStyle.id)),
                palette = AodColorPalette.fromId(obj.optString("palette", current.palette.id)),
                clockScale = obj.optDouble("clockScale", current.clockScale.toDouble()).toFloat().coerceIn(0.75f, 1.35f),
                aodBrightness = obj.optDouble("aodBrightness", current.aodBrightness.toDouble()).toFloat().coerceIn(0.25f, 1.0f),
                islandShape = IslandShapeStyle.fromId(obj.optString("islandShape", current.islandShape.id)),
                islandVisibilityMode = IslandVisibilityMode.fromId(obj.optString("islandVisibilityMode", current.islandVisibilityMode.id)),
                islandTapAction = IslandTapAction.fromId(obj.optString("islandTapAction", current.islandTapAction.id)),
                islandOffsetXDp = obj.optDouble("islandOffsetXDp", current.islandOffsetXDp.toDouble()).toFloat().coerceIn(-135f, 135f),
                islandOffsetYDp = obj.optDouble("islandOffsetYDp", current.islandOffsetYDp.toDouble()).toFloat().coerceIn(2f, 260f),
                islandMarqueeEnabled = obj.optBoolean("islandMarqueeEnabled", current.islandMarqueeEnabled),
                weatherWidgetStyle = WeatherWidgetStyle.fromId(obj.optString("weatherWidgetStyle", current.weatherWidgetStyle.id)),
                isQuickDockEnabled = obj.optBoolean("isQuickDockEnabled", current.isQuickDockEnabled),
                showPinnedMemoOnAod = obj.optBoolean("showPinnedMemoOnAod", current.showPinnedMemoOnAod),
                pinnedMemoText = obj.optString("pinnedMemoText", current.pinnedMemoText),
                standByNightModeEnabled = obj.optBoolean("standByNightModeEnabled", current.standByNightModeEnabled),
                standByRedTintOnly = obj.optBoolean("standByRedTintOnly", current.standByRedTintOnly),
                iceCardEnabled = obj.optBoolean("iceCardEnabled", current.iceCardEnabled),
                showBarometerAndCompassOnAod = obj.optBoolean("showBarometerAndCompassOnAod", current.showBarometerAndCompassOnAod),
                activeSmartProfile = SmartProfilePreset.fromId(obj.optString("activeSmartProfile", current.activeSmartProfile.id))
            )
        }.getOrNull()
    }

    suspend fun getAllAppRulesOnce(): List<AppColorRuleEntity> {
        return dao.getAllAppColorRulesOnce()
    }

    suspend fun seedInitialDataIfEmpty() {
        if (dao.getPresetCount() == 0) {
            val defaultPresets = listOf(
                AodPresetEntity(
                    name = "Muviz Cyber Liquid 2026",
                    badgeTag = "120Hz • Параллакс",
                    clockStyleId = ClockFaceStyle.CYBER_PULSE.id,
                    edgeStyleId = EdgeLightingStyle.AUDIO_SPECTRUM.id,
                    paletteId = AodColorPalette.CYBER_CYAN_MAGENTA.id,
                    ringModeId = CameraRingMode.COMBO_PULSE_AND_BATTERY.id,
                    weatherStyleId = WeatherWidgetStyle.COMPACT_PILL.id,
                    islandShapeId = IslandShapeStyle.SPLIT_DROP.id
                ),
                AodPresetEntity(
                    name = "Nothing OS Glyph 3.0",
                    badgeTag = "Ультра Эко • 1.1% OPR",
                    clockStyleId = ClockFaceStyle.NOTHING_DOT_MATRIX.id,
                    edgeStyleId = EdgeLightingStyle.CORNER_SPARKS.id,
                    paletteId = AodColorPalette.NOTHING_RED_MONO.id,
                    ringModeId = CameraRingMode.NOTIFICATION_LED_ONLY.id,
                    weatherStyleId = WeatherWidgetStyle.MINIMAL_TEXT.id,
                    islandShapeId = IslandShapeStyle.MATRIX_TICKER_BAR.id
                ),
                AodPresetEntity(
                    name = "Cosmic Metaball Sphere",
                    badgeTag = "Жидкие метаболы",
                    clockStyleId = ClockFaceStyle.COSMIC_ORBIT_SPHERE.id,
                    edgeStyleId = EdgeLightingStyle.NEON_WAVE.id,
                    paletteId = AodColorPalette.COSMIC_VIOLET.id,
                    ringModeId = CameraRingMode.COMBO_PULSE_AND_BATTERY.id,
                    weatherStyleId = WeatherWidgetStyle.THERMO_RING.id,
                    islandShapeId = IslandShapeStyle.LIQUID_SPHERE.id
                ),
                AodPresetEntity(
                    name = "Dual Wings & Audio Wave",
                    badgeTag = "Крылья • Эквалайзер",
                    clockStyleId = ClockFaceStyle.RETRO_FLIP_CLOCK.id,
                    edgeStyleId = EdgeLightingStyle.BREATHING_GLOW.id,
                    paletteId = AodColorPalette.SOLAR_FLARE.id,
                    ringModeId = CameraRingMode.BATTERY_ENERGY_RING.id,
                    weatherStyleId = WeatherWidgetStyle.HOURLY_CURVE.id,
                    islandShapeId = IslandShapeStyle.DUAL_CYBER_WINGS.id
                ),
                AodPresetEntity(
                    name = "Cyber Hexagon Visor",
                    badgeTag = "Гексагон • 120Hz",
                    clockStyleId = ClockFaceStyle.SYNTHWAVE_LED_GRID.id,
                    edgeStyleId = EdgeLightingStyle.LASER_CHASE.id,
                    paletteId = AodColorPalette.AURORA_EMERALD.id,
                    ringModeId = CameraRingMode.COMBO_PULSE_AND_BATTERY.id,
                    weatherStyleId = WeatherWidgetStyle.COMPACT_PILL.id,
                    islandShapeId = IslandShapeStyle.CYBER_HEXAGON.id
                )
            )
            defaultPresets.forEach { dao.insertPreset(it) }
        }

        if (dao.getAppRuleCount() == 0) {
            val defaultRules = listOf(
                AppColorRuleEntity(
                    packageName = "org.telegram.messenger",
                    appDisplayName = "Telegram",
                    categoryRu = "VIP Мессенджер",
                    colorHex = 0xFF38BDF8,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = true,
                    autoExpandIslandOnPush = true,
                    priorityLevel = 3
                ),
                AppColorRuleEntity(
                    packageName = "com.whatsapp",
                    appDisplayName = "WhatsApp",
                    categoryRu = "VIP Мессенджер",
                    colorHex = 0xFF00E676,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = true,
                    autoExpandIslandOnPush = true,
                    priorityLevel = 3
                ),
                AppColorRuleEntity(
                    packageName = "com.spotify.music",
                    appDisplayName = "Spotify / Музыка",
                    categoryRu = "Аудио & Эквалайзер",
                    colorHex = 0xFF00F5D4,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = false,
                    autoExpandIslandOnPush = true,
                    priorityLevel = 2
                ),
                AppColorRuleEntity(
                    packageName = "com.google.android.gm",
                    appDisplayName = "Gmail Почта",
                    categoryRu = "Работа и Почта",
                    colorHex = 0xFFFF3B30,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = false,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = true,
                    autoExpandIslandOnPush = false,
                    priorityLevel = 2
                ),
                AppColorRuleEntity(
                    packageName = "com.android.dialer",
                    appDisplayName = "Телефон и Вызовы",
                    categoryRu = "Экстренный приоритет",
                    colorHex = 0xFFFF2A85,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = true,
                    autoExpandIslandOnPush = true,
                    priorityLevel = 3
                ),
                AppColorRuleEntity(
                    packageName = "com.vkontakte.android",
                    appDisplayName = "VK Мессенджер",
                    categoryRu = "Соцсети",
                    colorHex = 0xFF7B61FF,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true,
                    isWhitelisted = true,
                    isBlacklisted = false,
                    wakeScreenOnPush = true,
                    autoExpandIslandOnPush = true,
                    priorityLevel = 2
                ),
                AppColorRuleEntity(
                    packageName = "com.promo.spam.market",
                    appDisplayName = "Маркет Акции & Реклама",
                    categoryRu = "Спам / Промо",
                    colorHex = 0xFFFFB800,
                    ringPulseEnabled = false,
                    edgeWaveEnabled = false,
                    showInDynamicIsland = false,
                    isWhitelisted = false,
                    isBlacklisted = true,
                    wakeScreenOnPush = false,
                    autoExpandIslandOnPush = false,
                    priorityLevel = 1
                )
            )
            defaultRules.forEach { dao.upsertAppColorRule(it) }
        }

        if (dao.getNotificationLogCount() == 0) {
            val initialLogs = listOf(
                NotificationLogEntity(
                    appName = "Telegram",
                    packageName = "org.telegram.messenger",
                    senderOrTitle = "Алексей • Дизайн-команда",
                    messageBody = "Жидкий морфинг 120Hz, бегущая строка и переход в приложение по тапу готовы!",
                    colorHex = 0xFF38BDF8,
                    wasBlockedByFilter = false
                ),
                NotificationLogEntity(
                    appName = "Spotify",
                    packageName = "com.spotify.music",
                    senderOrTitle = "Midnight Cybernetics",
                    messageBody = "Воспроизводится • Активирован спектр Muviz Edge 120Hz",
                    colorHex = 0xFF00F5D4,
                    wasBlockedByFilter = false
                ),
                NotificationLogEntity(
                    appName = "Маркет Акции & Реклама",
                    packageName = "com.promo.spam.market",
                    senderOrTitle = "Скидки до 70% сегодня",
                    messageBody = "Заблокировано Черным списком (Экран не разбужен)",
                    colorHex = 0xFFFF3B30,
                    wasBlockedByFilter = true
                )
            )
            initialLogs.forEach { dao.insertNotificationLog(it) }
        }
    }

    suspend fun saveCustomPreset(preset: AodPresetEntity) {
        dao.insertPreset(preset)
    }

    suspend fun deletePreset(id: Int) {
        dao.deletePresetById(id)
    }

    suspend fun updateAppRule(rule: AppColorRuleEntity) {
        dao.upsertAppColorRule(rule)
    }

    suspend fun deleteAppRule(packageName: String) {
        dao.deleteAppRuleByPackage(packageName)
    }

    suspend fun logNotification(
        appName: String,
        packageName: String = "",
        sender: String,
        body: String,
        colorHex: Long,
        wasBlocked: Boolean = false
    ) {
        dao.insertNotificationLog(
            NotificationLogEntity(
                appName = appName,
                packageName = packageName,
                senderOrTitle = sender,
                messageBody = body,
                colorHex = colorHex,
                wasBlockedByFilter = wasBlocked
            )
        )
    }

    suspend fun clearNotifications() {
        dao.clearNotificationLogs()
    }

    companion object {
        const val PREFS_NAME = "lumina_aod_2026_prefs"
    }
}
