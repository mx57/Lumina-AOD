package com.example.data

import com.example.model.AodColorPalette
import com.example.model.CameraRingMode
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.WeatherWidgetStyle
import kotlinx.coroutines.flow.Flow

class LuminaRepository(private val dao: LuminaDao) {

    val presetsFlow: Flow<List<AodPresetEntity>> = dao.observeAllPresets()
    val appColorRulesFlow: Flow<List<AppColorRuleEntity>> = dao.observeAppColorRules()
    val recentNotificationsFlow: Flow<List<NotificationLogEntity>> = dao.observeRecentNotifications()

    suspend fun seedInitialDataIfEmpty() {
        if (dao.getPresetCount() == 0) {
            val defaultPresets = listOf(
                AodPresetEntity(
                    name = "Muviz Cyber Wave",
                    badgeTag = "Киберпанк • 60fps",
                    clockStyleId = ClockFaceStyle.CYBER_PULSE.id,
                    edgeStyleId = EdgeLightingStyle.AUDIO_SPECTRUM.id,
                    paletteId = AodColorPalette.CYBER_CYAN_MAGENTA.id,
                    ringModeId = CameraRingMode.COMBO_PULSE_AND_BATTERY.id,
                    weatherStyleId = WeatherWidgetStyle.COMPACT_PILL.id
                ),
                AodPresetEntity(
                    name = "Nothing OS Glyph",
                    badgeTag = "Ультра Эко • 1.1% OPR",
                    clockStyleId = ClockFaceStyle.NOTHING_DOT_MATRIX.id,
                    edgeStyleId = EdgeLightingStyle.CORNER_SPARKS.id,
                    paletteId = AodColorPalette.NOTHING_RED_MONO.id,
                    ringModeId = CameraRingMode.NOTIFICATION_LED_ONLY.id,
                    weatherStyleId = WeatherWidgetStyle.MINIMAL_TEXT.id
                ),
                AodPresetEntity(
                    name = "Cosmic Liquid Island",
                    badgeTag = "Жидкий неон",
                    clockStyleId = ClockFaceStyle.COSMIC_ORBIT_SPHERE.id,
                    edgeStyleId = EdgeLightingStyle.NEON_WAVE.id,
                    paletteId = AodColorPalette.COSMIC_VIOLET.id,
                    ringModeId = CameraRingMode.COMBO_PULSE_AND_BATTERY.id,
                    weatherStyleId = WeatherWidgetStyle.THERMO_RING.id
                ),
                AodPresetEntity(
                    name = "Retro Flip & Energy Ring",
                    badgeTag = "Классика + Батарея",
                    clockStyleId = ClockFaceStyle.RETRO_FLIP_CLOCK.id,
                    edgeStyleId = EdgeLightingStyle.BREATHING_GLOW.id,
                    paletteId = AodColorPalette.SOLAR_FLARE.id,
                    ringModeId = CameraRingMode.BATTERY_ENERGY_RING.id,
                    weatherStyleId = WeatherWidgetStyle.HOURLY_CURVE.id
                ),
                AodPresetEntity(
                    name = "Pixel Deep Sleep Minimal",
                    badgeTag = "Минимум заряда",
                    clockStyleId = ClockFaceStyle.PIXEL_ULTRA_THIN.id,
                    edgeStyleId = EdgeLightingStyle.OFF.id,
                    paletteId = AodColorPalette.PURE_MONOCHROME.id,
                    ringModeId = CameraRingMode.BATTERY_ENERGY_RING.id,
                    weatherStyleId = WeatherWidgetStyle.MINIMAL_TEXT.id
                )
            )
            defaultPresets.forEach { dao.insertPreset(it) }
        }

        if (dao.getAppRuleCount() == 0) {
            val defaultRules = listOf(
                AppColorRuleEntity(
                    packageName = "org.telegram.messenger",
                    appDisplayName = "Telegram",
                    categoryRu = "Мессенджер",
                    colorHex = 0xFF38BDF8,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true
                ),
                AppColorRuleEntity(
                    packageName = "com.whatsapp",
                    appDisplayName = "WhatsApp",
                    categoryRu = "Мессенджер",
                    colorHex = 0xFF00E676,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true
                ),
                AppColorRuleEntity(
                    packageName = "com.spotify.music",
                    appDisplayName = "Spotify / Музыка",
                    categoryRu = "Аудио & Эквалайзер",
                    colorHex = 0xFF00F5D4,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true
                ),
                AppColorRuleEntity(
                    packageName = "com.google.android.gm",
                    appDisplayName = "Gmail Почта",
                    categoryRu = "Работа и Почта",
                    colorHex = 0xFFFF3B30,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = false,
                    showInDynamicIsland = true
                ),
                AppColorRuleEntity(
                    packageName = "com.android.dialer",
                    appDisplayName = "Телефон и Вызовы",
                    categoryRu = "Экстренный приоритет",
                    colorHex = 0xFFFF2A85,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true
                ),
                AppColorRuleEntity(
                    packageName = "com.vkontakte.android",
                    appDisplayName = "VK Мессенджер",
                    categoryRu = "Соцсети",
                    colorHex = 0xFF7B61FF,
                    ringPulseEnabled = true,
                    edgeWaveEnabled = true,
                    showInDynamicIsland = true
                )
            )
            defaultRules.forEach { dao.upsertAppColorRule(it) }
        }

        if (dao.getNotificationLogCount() == 0) {
            val initialLogs = listOf(
                NotificationLogEntity(
                    appName = "Telegram",
                    senderOrTitle = "Алексей • Дизайн-команда",
                    messageBody = "Смотри новый пресет неонового острова с откликом 60fps!",
                    colorHex = 0xFF38BDF8
                ),
                NotificationLogEntity(
                    appName = "Spotify",
                    senderOrTitle = "Midnight Cybernetics",
                    messageBody = "Воспроизводится • Активирован спектр Muviz Edge",
                    colorHex = 0xFF00F5D4
                ),
                NotificationLogEntity(
                    appName = "Система",
                    senderOrTitle = "Защита AMOLED активна",
                    messageBody = "Сдвиг пикселей каждую минуту • Карманный датчик включен",
                    colorHex = 0xFF00E676
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

    suspend fun logNotification(appName: String, sender: String, body: String, colorHex: Long) {
        dao.insertNotificationLog(
            NotificationLogEntity(
                appName = appName,
                senderOrTitle = sender,
                messageBody = body,
                colorHex = colorHex
            )
        )
    }

    suspend fun clearNotifications() {
        dao.clearNotificationLogs()
    }
}
