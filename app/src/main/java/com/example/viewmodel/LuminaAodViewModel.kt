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
import com.example.model.AppFilterMode
import com.example.model.CameraRingMode
import com.example.model.ClockCategory
import com.example.model.ClockFaceStyle
import com.example.model.EdgeLightingStyle
import com.example.model.InstalledAppInfo
import com.example.model.IslandActivityType
import com.example.model.IslandAnchorPreset
import com.example.model.IslandCallState
import com.example.model.IslandMediaState
import com.example.model.IslandMessageState
import com.example.model.IslandShapeStyle
import com.example.model.IslandTimerState
import com.example.model.SystemPermissionStatus
import com.example.model.WeatherCityOption
import com.example.model.WeatherSnapshot
import com.example.model.WeatherWidgetStyle
import com.example.network.WeatherNetworkClient
import com.example.service.HardwareTelemetryManager
import com.example.service.HardwareTelemetryState
import com.example.service.LuminaAodForegroundService
import com.example.service.LuminaEventBus
import com.example.service.LuminaNotificationHelper
import com.example.service.LuminaPermissionHelper
import com.example.service.LuminaSystemOverlayController
import com.example.service.SystemQuickAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class LuminaAodViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LuminaDatabase.getInstance(application)
    private val repository = LuminaRepository(database.luminaDao(), application)
    private val telemetryManager = HardwareTelemetryManager(application)

    private val _config = MutableStateFlow(repository.loadPersistedConfig())
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

    private val _permissionStatus = MutableStateFlow(LuminaPermissionHelper.checkPermissions(application))
    val permissionStatus: StateFlow<SystemPermissionStatus> = _permissionStatus.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _lastNotificationPulseTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastNotificationPulseTimestamp: StateFlow<Long> = _lastNotificationPulseTimestamp.asStateFlow()

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
        LuminaAodForegroundService.startOrSyncService(application)
        refreshPermissionStatus()
        loadInstalledApps()

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        refreshWeatherForSelectedCity()

        viewModelScope.launch {
            LuminaEventBus.events.collect { action ->
                when (action) {
                    SystemQuickAction.ToggleAod -> {
                        val next = !_config.value.isAodEnabled
                        updateConfigAndPersist { it.copy(isAodEnabled = next) }
                        showTemporaryBanner(if (next) "AOD включен из быстрой панели" else "AOD приостановлен")
                        syncNotificationShade()
                    }
                    SystemQuickAction.ToggleDeepSleep -> {
                        val next = !_config.value.isManualDeepSleepActive
                        updateConfigAndPersist { it.copy(isManualDeepSleepActive = next) }
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
                        updateConfigAndPersist { it.copy(edgeStyle = nextStyle) }
                        showTemporaryBanner("Подсветка краев: ${nextStyle.titleRu}")
                        syncNotificationShade()
                    }
                    SystemQuickAction.TriggerTestPulse -> {
                        _lastNotificationPulseTimestamp.value = System.currentTimeMillis()
                        updateConfigAndPersist {
                            it.copy(
                                activeIslandType = IslandActivityType.MESSAGE_NOTIFICATION,
                                isIslandExpanded = true
                            )
                        }
                    }
                    SystemQuickAction.ScreenTurnedOff -> {
                        if (_config.value.isAodEnabled && _config.value.autoShowOnScreenOff) {
                            _isFullScreenAodActive.value = true
                        }
                    }
                    is SystemQuickAction.DeviceChargingConnected -> {
                        if (_config.value.isAodEnabled && _config.value.autoWakeOnCharging) {
                            updateConfigAndPersist {
                                it.copy(
                                    activeIslandType = IslandActivityType.BATTERY_TURBO,
                                    isIslandExpanded = true
                                )
                            }
                            showTemporaryBanner("Подключена зарядка AMOLED Turbo (${action.batteryPercent}%)")
                        }
                    }
                    is SystemQuickAction.IncomingDeviceNotification -> {
                        handleIncomingNotification(
                            packageName = action.packageName,
                            title = action.title,
                            text = action.text
                        )
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

                if (_timerState.value.isRunning && _timerState.value.remainingSeconds > 0) {
                    _timerState.update { state ->
                        state.copy(remainingSeconds = (state.remainingSeconds - 1).coerceAtLeast(0))
                    }
                }

                if (_mediaState.value.isPlaying) {
                    _mediaState.update { state ->
                        val nextProgress = state.progressFloat + (1f / state.durationSec.coerceAtLeast(30))
                        state.copy(progressFloat = if (nextProgress > 1f) 0.05f else nextProgress)
                    }
                }

                if (_callState.value.isCallAccepted) {
                    _callState.update { state ->
                        state.copy(callDurationSeconds = state.callDurationSeconds + 1)
                    }
                }

                if (tickSeconds % 60 == 0 && _config.value.pixelShiftEnabled) {
                    telemetryManager.advanceOrbitalPixelShift(tickSeconds / 60)
                }
            }
        }

        syncNotificationShade()
    }

    private fun updateConfigAndPersist(transform: (AodConfigurationState) -> AodConfigurationState) {
        _config.update { current ->
            val updated = transform(current)
            repository.savePersistedConfig(updated)
            updated
        }
    }

    fun refreshPermissionStatus() {
        val status = LuminaPermissionHelper.checkPermissions(getApplication())
        _permissionStatus.value = status
    }

    fun restartServicesAndVerify() {
        val app = getApplication<Application>()
        LuminaPermissionHelper.rebindNotificationListener(app)
        LuminaAodForegroundService.startOrSyncService(app)
        syncNotificationShade()
        refreshPermissionStatus()
        showTemporaryBanner("Службы AOD, слушатель пушей и оверлей синхронизированы!")
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = LuminaPermissionHelper.queryInstalledPackages(getApplication())
            _installedApps.value = apps
        }
    }

    private suspend fun handleIncomingNotification(
        packageName: String,
        title: String,
        text: String
    ) {
        val rules = appColorRules.value.ifEmpty { repository.getAllAppRulesOnce() }
        val matchingRule = rules.find { it.packageName.equals(packageName, ignoreCase = true) }
        val currentCfg = _config.value
        val isAllowed = LuminaEventBus.evaluateAppFilter(currentCfg.appFilterMode, matchingRule)

        val colorHex = matchingRule?.colorHex ?: 0xFF00F5D4L
        val appLabel = matchingRule?.appDisplayName
            ?: packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }

        if (!isAllowed) {
            repository.logNotification(
                appName = appLabel,
                packageName = packageName,
                sender = title,
                body = "Заблокировано фильтром (${currentCfg.appFilterMode.titleRu}): $text",
                colorHex = 0xFFFF3B30L,
                wasBlocked = true
            )
            showTemporaryBanner("Пуш от «$appLabel» отфильтрован списком правил")
            return
        }

        _lastNotificationPulseTimestamp.value = System.currentTimeMillis()
        _messageState.update {
            IslandMessageState(
                appName = appLabel,
                sender = title,
                message = text,
                appColorHex = colorHex,
                lastReplySent = null
            )
        }

        val shouldExpand = matchingRule?.autoExpandIslandOnPush ?: true
        val shouldWake = currentCfg.autoWakeOnNotification && (matchingRule?.wakeScreenOnPush ?: true)

        updateConfigAndPersist {
            it.copy(
                activeIslandType = IslandActivityType.MESSAGE_NOTIFICATION,
                isIslandExpanded = shouldExpand
            )
        }

        if (shouldWake && currentCfg.isAodEnabled && !currentCfg.isManualDeepSleepActive) {
            _isFullScreenAodActive.value = true
        }

        repository.logNotification(
            appName = appLabel,
            packageName = packageName,
            sender = title,
            body = text,
            colorHex = colorHex,
            wasBlocked = false
        )
    }

    fun handleLaunchIntentExtras(forceLiveAod: Boolean, expandIsland: Boolean) {
        if (forceLiveAod) {
            _isFullScreenAodActive.value = true
        }
        if (expandIsland) {
            updateConfigAndPersist { it.copy(isIslandExpanded = true) }
        }
        LuminaSystemOverlayController.dismissSystemOverlay(getApplication())
    }

    fun selectClockCategory(category: ClockCategory?) {
        _selectedCategory.value = category
    }

    fun setClockStyle(style: ClockFaceStyle) {
        updateConfigAndPersist { it.copy(clockStyle = style) }
        syncNotificationShade()
    }

    fun setEdgeLightingStyle(style: EdgeLightingStyle) {
        updateConfigAndPersist { it.copy(edgeStyle = style) }
        syncNotificationShade()
    }

    fun setColorPalette(palette: AodColorPalette) {
        updateConfigAndPersist { it.copy(palette = palette) }
    }

    fun setClockScale(scale: Float) {
        updateConfigAndPersist { it.copy(clockScale = scale.coerceIn(0.75f, 1.35f)) }
    }

    fun setAodBrightness(brightness: Float) {
        updateConfigAndPersist { it.copy(aodBrightness = brightness.coerceIn(0.25f, 1.0f)) }
    }

    fun toggleAodEnabled(enabled: Boolean) {
        LuminaEventBus.isAodActiveGlobal = enabled
        updateConfigAndPersist { it.copy(isAodEnabled = enabled) }
        syncNotificationShade()
    }

    fun toggleShowSeconds(show: Boolean) {
        updateConfigAndPersist { it.copy(showSeconds = show) }
    }

    fun toggleShowWeatherOnAod(show: Boolean) {
        updateConfigAndPersist { it.copy(showWeatherOnAod = show) }
    }

    fun toggleShowNotificationBadges(show: Boolean) {
        updateConfigAndPersist { it.copy(showNotificationBadges = show) }
    }

    // 2026 Motion & Parallax Controls
    fun toggleParallax3d(enabled: Boolean) {
        updateConfigAndPersist { it.copy(parallax3dEnabled = enabled) }
    }

    fun setParallaxIntensity(intensity: Float) {
        updateConfigAndPersist { it.copy(parallaxIntensity = intensity.coerceIn(0.1f, 1.5f)) }
    }

    fun toggleLiquidMetaballMorph(enabled: Boolean) {
        updateConfigAndPersist { it.copy(liquidMetaballMorphEnabled = enabled) }
    }

    fun setAnimationSpeedMultiplier(speed: Float) {
        updateConfigAndPersist { it.copy(animationSpeedMultiplier = speed.coerceIn(0.5f, 2.0f)) }
    }

    fun toggleEdgeParticleSparks(enabled: Boolean) {
        updateConfigAndPersist { it.copy(edgeParticleSparksEnabled = enabled) }
    }

    fun setSimulatedTiltPreview(nx: Float, ny: Float) {
        telemetryManager.setSimulatedTilt(nx, ny)
    }

    // System Event Triggers
    fun toggleAutoWakeOnNotification(enabled: Boolean) {
        updateConfigAndPersist { it.copy(autoWakeOnNotification = enabled) }
    }

    fun toggleAutoWakeOnCharging(enabled: Boolean) {
        updateConfigAndPersist { it.copy(autoWakeOnCharging = enabled) }
    }

    fun toggleAutoShowOnScreenOff(enabled: Boolean) {
        updateConfigAndPersist { it.copy(autoShowOnScreenOff = enabled) }
    }

    fun toggleSystemOverlayEnabled(enabled: Boolean) {
        updateConfigAndPersist { it.copy(systemOverlayEnabled = enabled) }
        if (!enabled) {
            LuminaSystemOverlayController.dismissSystemOverlay(getApplication())
        }
    }

    fun setFullScreenAodActive(active: Boolean) {
        _isFullScreenAodActive.value = active
    }

    // Dynamic Island & Movable Geometry Controls
    fun toggleDynamicIsland(enabled: Boolean) {
        updateConfigAndPersist { it.copy(isDynamicIslandEnabled = enabled) }
    }

    fun setIslandShape(shape: IslandShapeStyle) {
        updateConfigAndPersist { it.copy(islandShape = shape) }
    }

    fun setIslandAnchorPreset(anchor: IslandAnchorPreset) {
        updateConfigAndPersist {
            it.copy(
                islandAnchor = anchor,
                islandOffsetXDp = anchor.defaultXDp,
                islandOffsetYDp = anchor.defaultYDp
            )
        }
        showTemporaryBanner("Позиция Острова: ${anchor.titleRu}")
    }

    fun toggleIslandFreeDrag(enabled: Boolean) {
        updateConfigAndPersist { it.copy(isIslandFreeDragEnabled = enabled) }
    }

    fun toggleIslandMagneticSnap(enabled: Boolean) {
        updateConfigAndPersist { it.copy(islandMagneticSnap = enabled) }
    }

    fun onDragIslandByDeltaDp(deltaXDp: Float, deltaYDp: Float) {
        if (!_config.value.isIslandFreeDragEnabled) return
        updateConfigAndPersist { current ->
            var nextX = (current.islandOffsetXDp + deltaXDp).coerceIn(-135f, 135f)
            var nextY = (current.islandOffsetYDp + deltaYDp).coerceIn(2f, 260f)

            // Magnetic snap to horizontal center (0 dp) if within 4.5dp
            if (current.islandMagneticSnap && abs(nextX) < 4.5f) {
                nextX = 0f
            }
            // Round to 0.5dp precision
            val roundedX = (nextX * 2f).roundToInt() / 2f
            val roundedY = (nextY * 2f).roundToInt() / 2f

            current.copy(
                islandAnchor = IslandAnchorPreset.FREE_DRAG,
                islandOffsetXDp = roundedX,
                islandOffsetYDp = roundedY
            )
        }
    }

    fun setIslandExactOffsetDp(xDp: Float, yDp: Float) {
        val roundedX = ((xDp.coerceIn(-135f, 135f)) * 2f).roundToInt() / 2f
        val roundedY = ((yDp.coerceIn(2f, 260f)) * 2f).roundToInt() / 2f
        updateConfigAndPersist {
            it.copy(
                islandAnchor = IslandAnchorPreset.FREE_DRAG,
                islandOffsetXDp = roundedX,
                islandOffsetYDp = roundedY
            )
        }
    }

    fun resetIslandPositionToCamera() {
        updateConfigAndPersist {
            it.copy(
                islandAnchor = IslandAnchorPreset.TOP_CENTER,
                islandOffsetXDp = it.cameraHoleOffsetXDp,
                islandOffsetYDp = (it.cameraHoleOffsetYDp - 4f).coerceAtLeast(4f)
            )
        }
        showTemporaryBanner("Остров синхронизирован с центром селфи-камеры")
    }

    fun updateIslandVisualSettings(
        widthScale: Float = _config.value.islandWidthScale,
        heightScale: Float = _config.value.islandHeightScale,
        cornerRadiusDp: Float = _config.value.islandCornerRadiusDp,
        glassOpacity: Float = _config.value.islandGlassOpacity,
        neonGlow: Float = _config.value.islandNeonGlowIntensity,
        springBounciness: Float = _config.value.islandSpringBounciness,
        borderWidthDp: Float = _config.value.islandBorderWidthDp
    ) {
        updateConfigAndPersist {
            it.copy(
                islandWidthScale = widthScale.coerceIn(0.70f, 1.45f),
                islandHeightScale = heightScale.coerceIn(0.75f, 1.40f),
                islandCornerRadiusDp = cornerRadiusDp.coerceIn(6f, 50f),
                islandGlassOpacity = glassOpacity.coerceIn(0.25f, 1.0f),
                islandNeonGlowIntensity = neonGlow.coerceIn(0.0f, 1.0f),
                islandSpringBounciness = springBounciness.coerceIn(0.3f, 1.0f),
                islandBorderWidthDp = borderWidthDp.coerceIn(0.5f, 3.5f)
            )
        }
    }

    fun toggleCameraRing(enabled: Boolean) {
        updateConfigAndPersist { it.copy(isCameraRingEnabled = enabled) }
    }

    fun setCameraRingMode(mode: CameraRingMode) {
        updateConfigAndPersist { it.copy(cameraRingMode = mode) }
    }

    fun updateCameraHolePosition(offsetX: Float, offsetY: Float, radius: Float, thickness: Float) {
        val cleanX = ((offsetX.coerceIn(-110f, 110f)) * 2f).roundToInt() / 2f
        val cleanY = ((offsetY.coerceIn(2f, 60f)) * 2f).roundToInt() / 2f
        updateConfigAndPersist {
            it.copy(
                cameraHoleOffsetXDp = cleanX,
                cameraHoleOffsetYDp = cleanY,
                cameraRingRadiusDp = radius.coerceIn(8f, 30f),
                cameraRingThicknessDp = thickness.coerceIn(1.2f, 7.0f)
            )
        }
    }

    fun setActiveIslandActivity(type: IslandActivityType, expandImmediately: Boolean = false) {
        updateConfigAndPersist {
            it.copy(
                activeIslandType = type,
                isIslandExpanded = expandImmediately || it.isIslandExpanded
            )
        }
    }

    fun toggleIslandExpanded() {
        updateConfigAndPersist { it.copy(isIslandExpanded = !it.isIslandExpanded) }
    }

    // Media Player Actions inside Dynamic Island
    fun toggleMediaPlayPause() {
        _mediaState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun skipToNextTrack() {
        val tracks = listOf(
            Triple("Midnight Cybernetics", "Lumina Synth Collective", 0xFF00F5D4L),
            Triple("Nothing Glyph Echoes", "Teenage Minimalist", 0xFFFF3B30L),
            Triple("Solar Flare Horizon", "Aurora Orbitals", 0xFFFFB800L),
            Triple("Neon Tokyo Rain 2026", "HyperOS Liquid Dreams", 0xFFFF2A85L)
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

    // Timer Actions inside Dynamic Island
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

    // Quick Reply inside Dynamic Island
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

    // Whitelist & Blacklist Management
    fun setAppFilterMode(mode: AppFilterMode) {
        updateConfigAndPersist { it.copy(appFilterMode = mode) }
        showTemporaryBanner("Режим фильтрации: ${mode.titleRu}")
    }

    fun toggleAppWhitelist(rule: AppColorRuleEntity) {
        viewModelScope.launch {
            val nextWhite = !rule.isWhitelisted
            val updated = rule.copy(
                isWhitelisted = nextWhite,
                isBlacklisted = if (nextWhite) false else rule.isBlacklisted
            )
            repository.updateAppRule(updated)
        }
    }

    fun toggleAppBlacklist(rule: AppColorRuleEntity) {
        viewModelScope.launch {
            val nextBlack = !rule.isBlacklisted
            val updated = rule.copy(
                isBlacklisted = nextBlack,
                isWhitelisted = if (nextBlack) false else rule.isWhitelisted
            )
            repository.updateAppRule(updated)
        }
    }

    fun toggleAppWakeScreen(rule: AppColorRuleEntity) {
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(wakeScreenOnPush = !rule.wakeScreenOnPush))
        }
    }

    fun toggleAppAutoExpandIsland(rule: AppColorRuleEntity) {
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(autoExpandIslandOnPush = !rule.autoExpandIslandOnPush))
        }
    }

    fun updateAppPriority(rule: AppColorRuleEntity, priority: Int) {
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(priorityLevel = priority.coerceIn(1, 3)))
        }
    }

    fun addCustomOrInstalledAppRule(
        packageName: String,
        displayName: String,
        categoryRu: String = "Пользовательское",
        colorHex: Long = 0xFF00F5D4L,
        addToWhitelist: Boolean = true,
        addToBlacklist: Boolean = false
    ) {
        val cleanPkg = packageName.trim().ifEmpty {
            "com.custom.${displayName.lowercase().replace(" ", "_")}"
        }
        val cleanName = displayName.trim().ifEmpty { cleanPkg.substringAfterLast('.') }
        viewModelScope.launch {
            repository.updateAppRule(
                AppColorRuleEntity(
                    packageName = cleanPkg,
                    appDisplayName = cleanName,
                    categoryRu = categoryRu,
                    colorHex = colorHex,
                    isWhitelisted = addToWhitelist && !addToBlacklist,
                    isBlacklisted = addToBlacklist,
                    wakeScreenOnPush = !addToBlacklist,
                    autoExpandIslandOnPush = !addToBlacklist,
                    priorityLevel = if (addToBlacklist) 1 else 2
                )
            )
            showTemporaryBanner("Приложение «$cleanName» добавлено в правила фильтрации")
        }
    }

    fun deleteAppRule(packageName: String) {
        viewModelScope.launch {
            repository.deleteAppRule(packageName)
        }
    }

    fun triggerSimulatedAppNotification(rule: AppColorRuleEntity) {
        val sampleMessages = mapOf(
            "Telegram" to ("Дмитрий • Tech Lead" to "Остров 2026 с жидким морфингом 120Hz готов к проверке!"),
            "WhatsApp" to ("Анна" to "Во сколько сегодня созвон по макетам экрана?"),
            "Spotify / Музыка" to ("Muviz Audio Engine" to "Включена синхронизация волн с битом трека"),
            "Gmail Почта" to ("GitHub Actions" to "Release workflow completed with 0 warnings"),
            "Телефон и Вызовы" to ("Пропущенный вызов" to "Перезвонить абоненту в 1 касание из Острова"),
            "VK Мессенджер" to ("Сообщество Android" to "Опубликован новый обзор AMOLED-кастомизации"),
            "Маркет Акции & Реклама" to ("Супер-распродажа" to "Проверка блокировки спама Черным списком")
        )
        val pair = sampleMessages[rule.appDisplayName]
            ?: ("Тест уведомления ${rule.appDisplayName}" to "Проверка фильтрации, пробуждения AOD и жидкого Острова")

        viewModelScope.launch {
            handleIncomingNotification(
                packageName = rule.packageName,
                title = pair.first,
                text = pair.second
            )
            val isAllowed = LuminaEventBus.evaluateAppFilter(_config.value.appFilterMode, rule)
            if (isAllowed && _config.value.systemOverlayEnabled) {
                LuminaSystemOverlayController.showOrUpdateSystemIslandBanner(
                    context = getApplication(),
                    appName = rule.appDisplayName,
                    title = pair.first,
                    message = pair.second,
                    accentHex = rule.colorHex,
                    offsetXDp = _config.value.islandOffsetXDp,
                    offsetYDp = _config.value.islandOffsetYDp
                )
            }
            if (isAllowed) {
                showTemporaryBanner("Пуш принят: ${rule.appDisplayName} (Анимация 120Hz активна)")
            }
        }
    }

    // Call Actions inside Dynamic Island
    fun acceptOrEndIslandCall() {
        val accepted = _callState.value.isCallAccepted
        if (!accepted) {
            _callState.update { it.copy(isCallAccepted = true, callDurationSeconds = 1) }
            showTemporaryBanner("Вызов принят в режиме громкой связи")
        } else {
            _callState.update { it.copy(isCallAccepted = false, callDurationSeconds = 0) }
            updateConfigAndPersist { it.copy(isIslandExpanded = false, activeIslandType = IslandActivityType.MEDIA_PLAYER) }
            showTemporaryBanner("Вызов завершен")
        }
    }

    // Weather Controls
    fun selectWeatherCity(city: WeatherCityOption) {
        updateConfigAndPersist { it.copy(selectedCity = city) }
        refreshWeatherForSelectedCity()
    }

    fun setWeatherWidgetStyle(style: WeatherWidgetStyle) {
        updateConfigAndPersist { it.copy(weatherWidgetStyle = style) }
    }

    fun refreshWeatherForSelectedCity() {
        val city = _config.value.selectedCity
        viewModelScope.launch {
            _isWeatherLoading.value = true
            val result = WeatherNetworkClient.fetchCityWeather(city)
            result.onSuccess { snapshot ->
                _weather.value = snapshot
            }.onFailure {
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
        updateConfigAndPersist { it.copy(proximityPocketMode = enabled) }
    }

    fun togglePixelShift(enabled: Boolean) {
        updateConfigAndPersist { it.copy(pixelShiftEnabled = enabled) }
    }

    fun triggerManualPixelShiftStep() {
        val nextStep = telemetry.value.pixelShiftCycleCount
        telemetryManager.advanceOrbitalPixelShift(nextStep)
        showTemporaryBanner("Сдвиг пикселей выполнен (орбитальная точка #${nextStep + 1})")
    }

    fun toggleBurnInCheckerboard(enabled: Boolean) {
        updateConfigAndPersist { it.copy(burnInCheckerboard = enabled) }
    }

    fun toggleDeepSleepSchedule(enabled: Boolean) {
        updateConfigAndPersist { it.copy(deepSleepScheduled = enabled) }
    }

    fun updateDeepSleepHours(startHour: Int, endHour: Int) {
        updateConfigAndPersist {
            it.copy(
                deepSleepStartHour = startHour.coerceIn(0, 23),
                deepSleepEndHour = endHour.coerceIn(0, 23)
            )
        }
    }

    fun toggleManualDeepSleep(active: Boolean) {
        LuminaEventBus.isDeepSleepGlobal = active
        updateConfigAndPersist { it.copy(isManualDeepSleepActive = active) }
        syncNotificationShade()
    }

    fun setLowBatteryAutoOffPercent(percent: Int) {
        updateConfigAndPersist { it.copy(lowBatteryAutoOffPercent = percent.coerceIn(5, 50)) }
    }

    fun toggleDoubleTapToWake(enabled: Boolean) {
        updateConfigAndPersist { it.copy(doubleTapToWake = enabled) }
    }

    fun toggleLiftToWake(enabled: Boolean) {
        updateConfigAndPersist { it.copy(liftToWake = enabled) }
    }

    fun togglePersistentShadeNotification(enabled: Boolean) {
        updateConfigAndPersist { it.copy(persistentShadeNotification = enabled) }
        syncNotificationShade()
    }

    fun togglePocketSimulation() {
        telemetryManager.togglePocketSimulation()
    }

    // Presets & App Rules Persistence
    fun applyPreset(preset: AodPresetEntity) {
        updateConfigAndPersist {
            it.copy(
                clockStyle = ClockFaceStyle.fromId(preset.clockStyleId),
                edgeStyle = EdgeLightingStyle.fromId(preset.edgeStyleId),
                palette = AodColorPalette.fromId(preset.paletteId),
                cameraRingMode = CameraRingMode.fromId(preset.ringModeId),
                weatherWidgetStyle = WeatherWidgetStyle.fromId(preset.weatherStyleId),
                islandShape = IslandShapeStyle.fromId(preset.islandShapeId),
                burnInCheckerboard = preset.burnInCheckerboard
            )
        }
        showTemporaryBanner("Применен пресет «${preset.name}»")
        syncNotificationShade()
    }

    fun saveCurrentAsPreset(customName: String) {
        val current = _config.value
        val cleanName = customName.trim().ifEmpty {
            "${current.clockStyle.titleRu} • ${current.islandShape.titleRu}"
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
                    islandShapeId = current.islandShape.id,
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

    fun clearNotificationLogs() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    private fun showTemporaryBanner(message: String) {
        viewModelScope.launch {
            _statusBannerMessage.value = message
            delay(3400L)
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
