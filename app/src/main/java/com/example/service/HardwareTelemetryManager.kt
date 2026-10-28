package com.example.service

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class HardwareTelemetryState(
    val batteryPercent: Int = 86,
    val isCharging: Boolean = false,
    val batteryTempCelsius: Float = 29.4f,
    val batteryVoltageMv: Int = 4180,
    val isProximityCovered: Boolean = false,
    val hasHardwareProximitySensor: Boolean = false,
    val hasHardwareAccelerometer: Boolean = false,
    val lastLiftGestureTimestamp: Long = 0L,
    val pixelShiftOffsetX: Float = 0f,
    val pixelShiftOffsetY: Float = 0f,
    val pixelShiftCycleCount: Int = 1,
    // 2026 3D Gyro / Tilt Parallax Coordinates (-1f..+1f)
    val tiltNormalizedX: Float = 0f,
    val tiltNormalizedY: Float = 0f,
    // System Memory, CPU & Torch Telemetry
    val ramUsedGb: Float = 4.2f,
    val ramTotalGb: Float = 8.0f,
    val ramUsagePercent: Int = 52,
    val cpuLoadPercent: Int = 18,
    val isFlashlightOn: Boolean = false,
    val isPrivacyCameraActive: Boolean = false,
    val isPrivacyMicActive: Boolean = false
)

class HardwareTelemetryManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private val proximitySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private val accelerometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _telemetry = MutableStateFlow(
        HardwareTelemetryState(
            hasHardwareProximitySensor = proximitySensor != null,
            hasHardwareAccelerometer = accelerometerSensor != null
        )
    )
    val telemetry: StateFlow<HardwareTelemetryState> = _telemetry.asStateFlow()

    private var isRegistered = false
    private var smoothedTiltX = 0f
    private var smoothedTiltY = 0f

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent == null) return
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 86)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
            val pct = ((level * 100f) / scale).toInt().coerceIn(1, 100)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
            val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 294)
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4180)

            val prevCharging = _telemetry.value.isCharging
            _telemetry.value = _telemetry.value.copy(
                batteryPercent = pct,
                isCharging = charging,
                batteryTempCelsius = tempTenths / 10f,
                batteryVoltageMv = voltage
            )
            refreshSystemMemoryTelemetry()
            if (charging && !prevCharging) {
                LuminaEventBus.emitAction(SystemQuickAction.DeviceChargingConnected(pct))
            }
        }
    }

    fun refreshSystemMemoryTelemetry() {
        runCatching {
            val memInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memInfo)
            if (memInfo.totalMem > 0L) {
                val totalGb = (memInfo.totalMem / (1024f * 1024f * 1024f))
                val availGb = (memInfo.availMem / (1024f * 1024f * 1024f))
                val usedGb = (totalGb - availGb).coerceAtLeast(0.4f)
                val pct = ((usedGb / totalGb) * 100f).roundToInt().coerceIn(5, 98)
                val cpuEst = (14 + (pct / 5) + ((System.currentTimeMillis() / 1000L) % 9).toInt()).coerceIn(8, 95)
                _telemetry.value = _telemetry.value.copy(
                    ramUsedGb = (usedGb * 10f).roundToInt() / 10f,
                    ramTotalGb = (totalGb * 10f).roundToInt() / 10f,
                    ramUsagePercent = pct,
                    cpuLoadPercent = cpuEst
                )
            }
        }
    }

    fun toggleFlashlight(enabled: Boolean? = null): Boolean {
        val targetState = enabled ?: !_telemetry.value.isFlashlightOn
        runCatching {
            val cm = cameraManager
            if (cm != null) {
                val flashCamId = cm.cameraIdList.firstOrNull { id ->
                    cm.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
                if (flashCamId != null) {
                    cm.setTorchMode(flashCamId, targetState)
                }
            }
        }
        _telemetry.value = _telemetry.value.copy(
            isFlashlightOn = targetState,
            isPrivacyCameraActive = targetState || _telemetry.value.isPrivacyCameraActive
        )
        return targetState
    }

    fun togglePrivacyIndicators(cameraActive: Boolean? = null, micActive: Boolean? = null) {
        _telemetry.value = _telemetry.value.copy(
            isPrivacyCameraActive = cameraActive ?: !_telemetry.value.isPrivacyCameraActive,
            isPrivacyMicActive = micActive ?: !_telemetry.value.isPrivacyMicActive
        )
    }

    fun startMonitoring() {
        if (isRegistered) return
        isRegistered = true
        refreshSystemMemoryTelemetry()
        runCatching {
            context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }
        proximitySensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopMonitoring() {
        if (!isRegistered) return
        isRegistered = false
        runCatching {
            context.unregisterReceiver(batteryReceiver)
        }
        sensorManager?.unregisterListener(this)
    }

    fun togglePocketSimulation(covered: Boolean? = null) {
        val current = _telemetry.value.isProximityCovered
        _telemetry.value = _telemetry.value.copy(
            isProximityCovered = covered ?: !current
        )
    }

    fun setSimulatedTilt(nx: Float, ny: Float) {
        _telemetry.value = _telemetry.value.copy(
            tiltNormalizedX = nx.coerceIn(-1f, 1f),
            tiltNormalizedY = ny.coerceIn(-1f, 1f)
        )
    }

    fun triggerLiftToWakeEvent() {
        _telemetry.value = _telemetry.value.copy(
            lastLiftGestureTimestamp = System.currentTimeMillis(),
            isProximityCovered = false
        )
    }

    fun advanceOrbitalPixelShift(stepIndex: Int) {
        val angles = listOf(0.0, 0.78, 1.57, 2.35, 3.14, 3.92, 4.71, 5.49)
        val angle = angles[stepIndex % angles.size]
        val radius = 5.0f
        val dx = (cos(angle) * radius).toFloat()
        val dy = (sin(angle) * radius).toFloat()
        refreshSystemMemoryTelemetry()
        _telemetry.value = _telemetry.value.copy(
            pixelShiftOffsetX = dx,
            pixelShiftOffsetY = dy,
            pixelShiftCycleCount = stepIndex + 1
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values.firstOrNull() ?: 5f
                val maxRange = event.sensor.maximumRange.coerceAtLeast(1f)
                val isNear = distance < maxRange && distance < 3.0f
                _telemetry.value = _telemetry.value.copy(isProximityCovered = isNear)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values.getOrElse(0) { 0f }
                val y = event.values.getOrElse(1) { 0f }
                val z = event.values.getOrElse(2) { 0f }
                val magnitude = sqrt(x * x + y * y + z * z)

                // Low-pass filter for smooth 120Hz 3D parallax
                val targetX = (-x / 6.5f).coerceIn(-1f, 1f)
                val targetY = ((y - 5.0f) / 6.5f).coerceIn(-1f, 1f)
                smoothedTiltX += (targetX - smoothedTiltX) * 0.14f
                smoothedTiltY += (targetY - smoothedTiltY) * 0.14f

                val now = System.currentTimeMillis()
                val liftTriggered = magnitude > 11.8f && y > 4.5f &&
                    (now - _telemetry.value.lastLiftGestureTimestamp > 4000L)

                _telemetry.value = _telemetry.value.copy(
                    tiltNormalizedX = smoothedTiltX,
                    tiltNormalizedY = smoothedTiltY,
                    lastLiftGestureTimestamp = if (liftTriggered) now else _telemetry.value.lastLiftGestureTimestamp
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
