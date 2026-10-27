package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AodPresetEntity
import com.example.data.AppColorRuleEntity
import com.example.data.LuminaDatabase
import com.example.data.LuminaRepository
import com.example.data.NotificationLogEntity
import com.example.model.AodColorPalette
import com.example.model.AodConfigurationState
import com.example.model.CameraRingMode
import com.example.model.ClockCategory
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.IslandActivityType
import com.example.model.IslandCallState
import com.example.model.IslandMediaState
import com.example.model.IslandMessageState
import com.example.model.IslandTimerState
import com.example.model.WeatherCityOption
import com.example.model.WeatherSnapshot
import com.example.model.WeatherWidgetStyle
import com.example.network.WeatherNetworkClient
import com.example.service.HardwareTelemetryManager
import com.example.service.HardwareTelemetryState
import com.example.service.LuminaEventBus
import com.example.service.LuminaNotificationHelper
import com.example.service.SystemQuickAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LuminaAodViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LuminaDatabase.getInstance(application)
    private val repository = LuminaRepository(database.luminaDao())
    private val telemetryManager = HardwareTelemetryManager(application)

    private val _config = MutableStateFlow(AodConfigurationState())
    val config: StateFlow<AodConfigurationState> = _config.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ClockCategory?>(null)
    val selectedCategory: StateFlow<ClockCategory?> = _selectedCategory.asStateFlow()

    private val _isFullScreenAodActive = MutableStateFlow(false)
    val isFullScreenAodActive: StateFlow<Boolean> = _isFullScreenAodActive.asStateFlow()

    private val _weather = MutableStateFlow(WeatherSnapshot())
    val weather: StateFlow<WeatherSnapshot> = _weather.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    private val _mediaState = MutableStateFlow(IslandMediaState())
    val mediaState: StateFlow<IslandMediaState> = _mediaState.asStateFlow()

    private val _timerState = MutableStateFlow(IslandTimerState())
    val timerState: StateFlow<IslandTimerState> = _timerState.asStateFlow()

    private val _messageState = MutableStateFlow(IslandMessageState())
    val messageState: StateFlow<IslandMessageState> = _messageState.asStateFlow()

    private val _callState = MutableStateFlow(IslandCallState())
    val callState: StateFlow<IslandCallState> = _callState.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    val telemetry: StateFlow<HardwareTelemetryState> = telemetryManager.telemetry

    val presets: StateFlow<List<AodPresetEntity>> = repository.presetsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val appColorRules: StateFlow<List<AppColorRuleEntity>> = repository.appColorRulesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentNotifications: StateFlow<List<NotificationLogEntity>> = repository.recentNotificationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        telemetryManager.startMonitoring()

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        // Fetch initial live weather
        refreshWeatherForSelectedCity()

        // Listen for Quick Settings Tile & Shade Notification Actions
        viewModelScope.launch {
            LuminaEventBus.events.collect { action ->
                when (action) {
                    SystemQuickAction.ToggleAod -> {
                        val next = !_config.value.isAodEnabled
                        _config.update { it.copy(isAodEnabled = next) }
                        showTemporaryBanner(if (next) "AOD включен из быстрой панели" else "AOD приостановлен")
                        syncNotificationShade()
                    }
                    SystemQuickAction.ToggleDeepSleep -> {
                        val next = !_config.value.isManualDeepSleepActive
                        _config.update { it.copy(isManualDeepSleepActive = next) }
                        showTemporaryBanner(
                            if (next) "Активирован режим Глубокого сна (0% расхода)"
                            else "Выход из режима Глубокого сна"
                        )
                        syncNotificationShade()
                    }
                    SystemQuickAction.CycleEdgeStyle -> {
                        val styles = EdgeLightingStyle.entries
                        val nextIdx = (styles.indexOf(_config.value.edgeStyle) + 1) % styles.size
                        val nextStyle = styles[nextIdx]
                        _config.update { it.copy(edgeStyle = nextStyle) }
                        showTemporaryBanner("Подсветка краев: ${nextStyle.titleRu}")
                        syncNotificationShade()
                    }
                    SystemQuickAction.TriggerTestPulse -> {
                        _config.update {
                            it.copy(
                                activeIslandType = IslandActivityType.MESSAGE_NOTIFICATION,
                                isIslandExpanded = true
                            )
                        }
                    }
                    is SystemQuickAction.IncomingDeviceNotification -> {
                        val matchingRule = appColorRules.value.find {
                            it.packageName.equals(action.packageName, ignoreCase = true)
                        }
                        val colorHex = matchingRule?.colorHex ?: 0xFF00F5D4
                        val appLabel = matchingRule?.appDisplayName
                            ?: action.packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }

                        _messageState.update {
                            IslandMessageState(
                                appName = appLabel,
                                sender = action.title,
                                message = action.text,
                                appColorHex = colorHex,
                                lastReplySent = null
                            )
                        }
                        _config.update {
                            it.copy(
                                activeIslandType = IslandActivityType.MESSAGE_NOTIFICATION
                            )
                        }
                        repository.logNotification(appLabel, action.title, action.text, colorHex)
                    }
                }
            }
        }

        // 1-second ticker for Dynamic Island Timer, Media progress, Call timer & orbital Pixel Shift
        viewModelScope.launch {
            var tickSeconds = 0
            while (isActive) {
                delay(1000L)
                tickSeconds++

                // Active Timer countdown
                if (_timerState.value.isRunning && _timerState.value.remainingSeconds > 0) {
                    _timerState.update { state ->
                        state.copy(remainingSeconds = (state.remainingSeconds - 1).coerceAtLeast(0))
                    }
                }

                // Active Media progress
                if (_mediaState.value.isPlaying) {
                    _mediaState.update { state ->
                        val nextProgress = state.progressFloat + (1f / state.durationSec.coerceAtLeast(30))
                        state.copy(progressFloat = if (nextProgress > 1f) 0.05f else nextProgress)
                    }
                }

                // Active Call duration
                if (_callState.value.isCallAccepted) {
                    _callState.update { state ->
                        state.copy(callDurationSeconds = state.callDurationSeconds + 1)
                    }
                }

                // Orbital Pixel Shift every 60 seconds (or manual step)
                if (tickSeconds % 60 == 0 && _config.value.pixelShiftEnabled) {
                    telemetryManager.advanceOrbitalPixelShift(tickSeconds / 60)
                }
            }
        }

        syncNotificationShade()
    }

    fun selectClockCategory(category: ClockCategory?) {
        _selectedCategory.value = category
    }

    fun setClockStyle(style: ClockFaceStyle) {
        _config.update { it.copy(clockStyle = style) }
        syncNotificationShade()
    }

    fun setEdgeLightingStyle(style: EdgeLightingStyle) {
        _config.update { it.copy(edgeStyle = style) }
        syncNotificationShade()
    }

    fun setColorPalette(palette: AodColorPalette) {
        _config.update { it.copy(palette = palette) }
    }

    fun setClockScale(scale: Float) {
        _config.update { it.copy(clockScale = scale.coerceIn(0.75f, 1.30f)) }
    }

    fun setAodBrightness(brightness: Float) {
        _config.update { it.copy(aodBrightness = brightness.coerceIn(0.25f, 1.0f)) }
    }

    fun toggleAodEnabled(enabled: Boolean) {
        LuminaEventBus.isAodActiveGlobal = enabled
        _config.update { it.copy(isAodEnabled = enabled) }
        syncNotificationShade()
    }

    fun toggleShowSeconds(show: Boolean) {
        _config.update { it.copy(showSeconds = show) }
    }

    fun toggleShowWeatherOnAod(show: Boolean) {
        _config.update { it.copy(showWeatherOnAod = show) }
    }

    fun toggleShowNotificationBadges(show: Boolean) {
        _config.update { it.copy(showNotificationBadges = show) }
    }

    fun setFullScreenAodActive(active: Boolean) {
        _isFullScreenAodActive.value = active
    }

    // Dynamic Island & Camera Cutout Ring Controls
    fun toggleDynamicIsland(enabled: Boolean) {
        _config.update { it.copy(isDynamicIslandEnabled = enabled) }
    }

    fun toggleCameraRing(enabled: Boolean) {
        _config.update { it.copy(isCameraRingEnabled = enabled) }
    }

    fun setCameraRingMode(mode: CameraRingMode) {
        _config.update { it.copy(cameraRingMode = mode) }
    }

    fun updateCameraHolePosition(offsetX: Float, offsetY: Float, radius: Float, thickness: Float) {
        _config.update {
            it.copy(
                cameraHoleOffsetXDp = offsetX.coerceIn(-80f, 80f),
                cameraHoleOffsetYDp = offsetY.coerceIn(4f, 36f),
                cameraRingRadiusDp = radius.coerceIn(10f, 26f),
                cameraRingThicknessDp = thickness.coerceIn(1.5f, 6.5f)
            )
        }
    }

    fun setActiveIslandActivity(type: IslandActivityType, expandImmediately: Boolean = false) {
        _config.update {
            it.copy(
                activeIslandType = type,
                isIslandExpanded = expandImmediately || it.isIslandExpanded
            )
        }
    }

    fun toggleIslandExpanded() {
        _config.update { it.copy(isIslandExpanded = !it.isIslandExpanded) }
    }

    fun setIslandExpanded(expanded: Boolean) {
        _config.update { it.copy(isIslandExpanded = expanded) }
    }

    // Interactive Media Player Actions inside Dynamic Island
    fun toggleMediaPlayPause() {
        _mediaState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun skipToNextTrack() {
        val tracks = listOf(
            Triple("Midnight Cybernetics", "Lumina Synth Collective", 0xFF00F5D4),
            Triple("Nothing Glyph Echoes", "Teenage Minimalist", 0xFFFF3B30),
            Triple("Solar Flare Horizon", "Aurora Orbitals", 0xFFFFB800),
            Triple("Neon Tokyo Rain", "HyperOS Dreams", 0xFFFF2A85)
        )
        val currentIdx = tracks.indexOfFirst { it.first == _mediaState.value.trackTitle }
        val next = tracks[(currentIdx + 1) % tracks.size]
        _mediaState.update {
            it.copy(
                trackTitle = next.first,
                artistName = next.second,
                accentHex = next.third,
                progressFloat = 0.08f,
                isPlaying = true
            )
        }
    }

    // Interactive Timer Actions inside Dynamic Island
    fun toggleTimerRunning() {
        _timerState.update { it.copy(isRunning = !it.isRunning) }
    }

    fun addOneMinuteToTimer() {
        _timerState.update {
            it.copy(
                remainingSeconds = it.remainingSeconds + 60,
                totalSeconds = (it.totalSeconds + 60).coerceAtLeast(it.remainingSeconds + 60)
            )
        }
    }

    fun resetIslandTimer() {
        _timerState.update {
            it.copy(remainingSeconds = 900, totalSeconds = 900, isRunning = true)
        }
    }

    // Interactive Quick Reply inside Dynamic Island
    fun sendQuickReply(replyText: String) {
        if (replyText.isBlank()) return
        val currentMsg = _messageState.value
        _messageState.update {
            it.copy(lastReplySent = replyText)
        }
        viewModelScope.launch {
            repository.logNotification(
                appName = currentMsg.appName,
                sender = "Вы → ${currentMsg.sender}",
                body = "Быстрый ответ из Острова: «$replyText»",
                colorHex = currentMsg.appColorHex
            )
            showTemporaryBanner("Ответ отправлен в ${currentMsg.appName}: «$replyText»")
        }
    }

    fun triggerSimulatedAppNotification(rule: AppColorRuleEntity) {
        val sampleMessages = mapOf(
            "Telegram" to ("Дмитрий • Tech Lead" to "Сборка v1.0 для ARMv7 и ARMv8 готова к тесту!"),
            "WhatsApp" to ("Анна" to "Во сколько сегодня созвон по макетам экрана?"),
            "Spotify / Музыка" to ("Muviz Audio Engine" to "Включена синхронизация волн с битом трека"),
            "Gmail Почта" to ("GitHub Actions" to "Release workflow completed with 0 warnings"),
            "Телефон и Вызовы" to ("Пропущенный вызов" to "Перезвонить абоненту в 1 касание из Острова"),
            "VK Мессенджер" to ("Сообщество Android" to "Опубликован новый обзор AMOLED-кастомизации")
        )
        val pair = sampleMessages[rule.appDisplayName]
            ?: ("Уведомление ${rule.appDisplayName}" to "Проверка светового кольца вокруг камеры и краев экрана")

        _messageState.update {
            IslandMessageState(
                appName = rule.appDisplayName,
                sender = pair.first,
                message = pair.second,
                appColorHex = rule.colorHex,
                lastReplySent = null
            )
        }
        _config.update {
            it.copy(
                activeIslandType = IslandActivityType.MESSAGE_NOTIFICATION,
                isIslandExpanded = true
            )
        }
        viewModelScope.launch {
            repository.logNotification(rule.appDisplayName, pair.first, pair.second, rule.colorHex)
            showTemporaryBanner("Пульс уведомления: ${rule.appDisplayName}")
        }
    }

    // Interactive Call Actions inside Dynamic Island
    fun acceptOrEndIslandCall() {
        val accepted = _callState.value.isCallAccepted
        if (!accepted) {
            _callState.update { it.copy(isCallAccepted = true, callDurationSeconds = 1) }
            showTemporaryBanner("Вызов принят в режиме громкой связи")
        } else {
            _callState.update { it.copy(isCallAccepted = false, callDurationSeconds = 0) }
            _config.update { it.copy(isIslandExpanded = false, activeIslandType = IslandActivityType.MEDIA_PLAYER) }
            showTemporaryBanner("Вызов завершен")
        }
    }

    // Weather Controls
    fun selectWeatherCity(city: WeatherCityOption) {
        _config.update { it.copy(selectedCity = city) }
        refreshWeatherForSelectedCity()
    }

    fun setWeatherWidgetStyle(style: WeatherWidgetStyle) {
        _config.update { it.copy(weatherWidgetStyle = style) }
    }

    fun refreshWeatherForSelectedCity() {
        val city = _config.value.selectedCity
        viewModelScope.launch {
            _isWeatherLoading.value = true
            val result = WeatherNetworkClient.fetchCityWeather(city)
            result.onSuccess { snapshot ->
                _weather.value = snapshot
            }.onFailure {
                // Keep cached snapshot updated with selected city name
                _weather.update { prev ->
                    prev.copy(
                        cityName = city.nameRu,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                }
            }
            _isWeatherLoading.value = false
        }
    }

    // Deep Sleep & Power Saving Controls
    fun toggleProximityPocketMode(enabled: Boolean) {
        _config.update { it.copy(proximityPocketMode = enabled) }
    }

    fun togglePixelShift(enabled: Boolean) {
        _config.update { it.copy(pixelShiftEnabled = enabled) }
    }

    fun triggerManualPixelShiftStep() {
        val nextStep = telemetry.value.pixelShiftCycleCount
        telemetryManager.advanceOrbitalPixelShift(nextStep)
        showTemporaryBanner("Сдвиг пикселей выполнен (орбитальная точка #${nextStep + 1})")
    }

    fun toggleBurnInCheckerboard(enabled: Boolean) {
        _config.update { it.copy(burnInCheckerboard = enabled) }
    }

    fun toggleDeepSleepSchedule(enabled: Boolean) {
        _config.update { it.copy(deepSleepScheduled = enabled) }
    }

    fun updateDeepSleepHours(startHour: Int, endHour: Int) {
        _config.update {
            it.copy(
                deepSleepStartHour = startHour.coerceIn(0, 23),
                deepSleepEndHour = endHour.coerceIn(0, 23)
            )
        }
    }

    fun toggleManualDeepSleep(active: Boolean) {
        LuminaEventBus.isDeepSleepGlobal = active
        _config.update { it.copy(isManualDeepSleepActive = active) }
        syncNotificationShade()
    }

    fun setLowBatteryAutoOffPercent(percent: Int) {
        _config.update { it.copy(lowBatteryAutoOffPercent = percent.coerceIn(5, 50)) }
    }

    fun toggleDoubleTapToWake(enabled: Boolean) {
        _config.update { it.copy(doubleTapToWake = enabled) }
    }

    fun toggleLiftToWake(enabled: Boolean) {
        _config.update { it.copy(liftToWake = enabled) }
    }

    fun togglePersistentShadeNotification(enabled: Boolean) {
        _config.update { it.copy(persistentShadeNotification = enabled) }
        syncNotificationShade()
    }

    fun togglePocketSimulation() {
        telemetryManager.togglePocketSimulation()
    }

    // Presets & App Rules Persistence
    fun applyPreset(preset: AodPresetEntity) {
        _config.update {
            it.copy(
                clockStyle = ClockFaceStyle.fromId(preset.clockStyleId),
                edgeStyle = EdgeLightingStyle.fromId(preset.edgeStyleId),
                palette = AodColorPalette.fromId(preset.paletteId),
                cameraRingMode = CameraRingMode.fromId(preset.ringModeId),
                weatherWidgetStyle = WeatherWidgetStyle.fromId(preset.weatherStyleId),
                burnInCheckerboard = preset.burnInCheckerboard
            )
        }
        showTemporaryBanner("Применен пресет «${preset.name}»")
        syncNotificationShade()
    }

    fun saveCurrentAsPreset(customName: String) {
        val current = _config.value
        val cleanName = customName.trim().ifEmpty {
            "${current.clockStyle.titleRu} • ${current.palette.titleRu}"
        }
        viewModelScope.launch {
            repository.saveCustomPreset(
                AodPresetEntity(
                    name = cleanName,
                    badgeTag = "OPR ${current.calculateOprPercentage()}%",
                    clockStyleId = current.clockStyle.id,
                    edgeStyleId = current.edgeStyle.id,
                    paletteId = current.palette.id,
                    ringModeId = current.cameraRingMode.id,
                    weatherStyleId = current.weatherWidgetStyle.id,
                    burnInCheckerboard = current.burnInCheckerboard
                )
            )
            showTemporaryBanner("Сохранен пресет «$cleanName»")
        }
    }

    fun deletePreset(id: Int) {
        viewModelScope.launch {
            repository.deletePreset(id)
        }
    }

    fun updateAppRuleColor(rule: AppColorRuleEntity, newColorHex: Long) {
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(colorHex = newColorHex))
        }
    }

    fun toggleAppRuleRingPulse(rule: AppColorRuleEntity) {
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(ringPulseEnabled = !rule.ringPulseEnabled))
        }
    }

    fun clearNotificationLogs() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    private fun showTemporaryBanner(message: String) {
        viewModelScope.launch {
            _statusBannerMessage.value = message
            delay(3200L)
            if (_statusBannerMessage.value == message) {
                _statusBannerMessage.value = null
            }
        }
    }

    private fun syncNotificationShade() {
        val current = _config.value
        LuminaNotificationHelper.syncShadeControlPanel(
            context = getApplication(),
            enabled = current.persistentShadeNotification,
            isAodActive = current.isAodEnabled,
            isDeepSleep = current.isManualDeepSleepActive,
            clockTitle = current.clockStyle.titleRu,
            oprPercent = current.calculateOprPercentage()
        )
    }

    override fun onCleared() {
        super.onCleared()
        telemetryManager.stopMonitoring()
    }
}
