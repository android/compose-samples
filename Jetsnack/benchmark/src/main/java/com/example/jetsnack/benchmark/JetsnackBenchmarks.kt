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

package com.example.jetsnack.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JetsnackBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val targetPackageName = "com.example.jetsnack"

    @Test
    fun scrollFeedFrameTiming() = benchmarkRule.measureRepeated(
        packageName = targetPackageName,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        startupMode = StartupMode.COLD,
        iterations = 10,
    ) {
        pressHome()
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 15000)

        // Wait for the feed list
        val listSelector = By.res("feed_list")
        val list = device.wait(Until.findObject(listSelector), 15000)
            ?: device.wait(Until.findObject(By.scrollable(true)), 15000)
        requireNotNull(list) { "Feed list not found!" }

        list.setGestureMargin(device.displayWidth / 5)

        // Scroll down through feed
        repeat(3) {
            list.fling(Direction.DOWN)
            device.waitForIdle()
        }

        // Scroll back up
        repeat(3) {
            list.fling(Direction.UP)
            device.waitForIdle()
        }
    }
}
