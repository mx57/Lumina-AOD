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
import com.example.model.PredefinedWeatherCities
import com.example.model.WeatherWidgetStyle
import kotlinx.coroutines.flow.Flow

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

    fun loadPersistedConfig(): AodConfigurationState {
        val sp = prefs ?: return AodConfigurationState()
        val cityName = sp.getString("selectedCityName", PredefinedWeatherCities.first().nameRu)
        val city = PredefinedWeatherCities.find { it.nameRu == cityName } ?: PredefinedWeatherCities.first()

        return AodConfigurationState(
            isAodEnabled = sp.getBoolean("isAodEnabled", true),
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
            weatherWidgetStyle = WeatherWidgetStyle.fromId(sp.getString("weatherWidgetStyle", WeatherWidgetStyle.COMPACT_PILL.id) ?: ""),
            selectedCity = city,
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
            .putString("weatherWidgetStyle", config.weatherWidgetStyle.id)
            .putString("selectedCityName", config.selectedCity.nameRu)
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
                    islandShapeId = IslandShapeStyle.ULTRA_THREAD.id
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
                    name = "Retro Flip & Energy Ring",
                    badgeTag = "Классика + Батарея",
                    clockStyleId = ClockFaceStyle.RETRO_FLIP_CLOCK.id,
                    edgeStyleId = EdgeLightingStyle.BREATHING_GLOW.id,
                    paletteId = AodColorPalette.SOLAR_FLARE.id,
                    ringModeId = CameraRingMode.BATTERY_ENERGY_RING.id,
                    weatherStyleId = WeatherWidgetStyle.HOURLY_CURVE.id,
                    islandShapeId = IslandShapeStyle.CAPSULE.id
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
                    messageBody = "Жидкий морфинг 120Hz и перетаскивание Острова активированы!",
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
