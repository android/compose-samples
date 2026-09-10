/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

/**
 * Shake detector using [Sensor.TYPE_ACCELEROMETER] with gravity compensation.
 * Debounced to trigger [onShake] when an intentional shake gesture is detected.
 */
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var gravity = floatArrayOf(0f, 0f, SensorManager.GRAVITY_EARTH)
    private var lastShakeTimestamp = 0L
    private var shakeCount = 0
    private var lastPulseTimestamp = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Alpha low-pass filter to isolate gravity component
        val alpha = 0.8f
        gravity[0] = alpha * gravity[0] + (1 - alpha) * x
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z

        // Linear acceleration excluding gravity
        val linearX = x - gravity[0]
        val linearY = y - gravity[1]
        val linearZ = z - gravity[2]

        val linearAcc = sqrt((linearX * linearX + linearY * linearY + linearZ * linearZ).toDouble()).toFloat()
        val now = System.currentTimeMillis()

        // Detect acceleration pulse above threshold (~1.1 Gs)
        if (linearAcc > 11.0f) {
            if (now - lastPulseTimestamp < 600L) {
                shakeCount++
            } else {
                shakeCount = 1
            }
            lastPulseTimestamp = now

            // Trigger when multiple pulses detected or strong impulse, debounced by 850ms
            if ((shakeCount >= 2 || linearAcc > 14f) && now - lastShakeTimestamp > 850L) {
                lastShakeTimestamp = now
                shakeCount = 0
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/**
 * Composable helper that registers a [ShakeDetector] during the composable lifecycle.
 * Also registers a broadcast receiver for `com.example.compose.jetchat.TOGGLE_CYBERPUNK`
 * allowing triggers via adb commands during development or demos.
 */
@Composable
fun rememberShakeDetector(onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val detector = ShakeDetector { currentOnShake() }

        sensorManager?.registerListener(
            detector,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI,
        )

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                android.util.Log.d("CyberpunkMode", "Broadcast received: TOGGLE_CYBERPUNK")
                currentOnShake()
            }
        }
        val filter = IntentFilter("com.example.compose.jetchat.TOGGLE_CYBERPUNK")
        ContextCompat.registerReceiver(
            context.applicationContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        onDispose {
            sensorManager?.unregisterListener(detector)
            try {
                context.applicationContext.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {}
        }
    }
}
