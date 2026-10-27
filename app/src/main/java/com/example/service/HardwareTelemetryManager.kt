package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val pixelShiftCycleCount: Int = 1
)

class HardwareTelemetryManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
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

            _telemetry.value = _telemetry.value.copy(
                batteryPercent = pct,
                isCharging = charging,
                batteryTempCelsius = tempTenths / 10f,
                batteryVoltageMv = voltage
            )
        }
    }

    fun startMonitoring() {
        if (isRegistered) return
        isRegistered = true
        runCatching {
            context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }
        proximitySensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
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

    fun triggerLiftToWakeEvent() {
        _telemetry.value = _telemetry.value.copy(
            lastLiftGestureTimestamp = System.currentTimeMillis(),
            isProximityCovered = false
        )
    }

    fun advanceOrbitalPixelShift(stepIndex: Int) {
        // Moves content along a smooth 8-point orbital path within [-6dp..+6dp] to prevent OLED burn-in
        val angles = listOf(0.0, 0.78, 1.57, 2.35, 3.14, 3.92, 4.71, 5.49)
        val angle = angles[stepIndex % angles.size]
        val radius = 5.0f
        val dx = ( kotlin.math.cos(angle) * radius ).toFloat()
        val dy = ( kotlin.math.sin(angle) * radius ).toFloat()
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
                // Detect gentle lift/tilt movement
                if (magnitude > 11.8f && y > 4.5f) {
                    val now = System.currentTimeMillis()
                    if (now - _telemetry.value.lastLiftGestureTimestamp > 4000L) {
                        _telemetry.value = _telemetry.value.copy(lastLiftGestureTimestamp = now)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
