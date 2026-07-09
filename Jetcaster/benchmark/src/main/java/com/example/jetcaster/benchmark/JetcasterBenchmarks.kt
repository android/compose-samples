/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.jetcaster.benchmark

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
class JetcasterBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val targetPackageName = "com.example.jetcaster"

    @Test
    fun scrollHomeGridFrameTiming() = benchmarkRule.measureRepeated(
        packageName = targetPackageName,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.COLD,
        iterations = 5
    ) {
        pressHome()
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 15000)

        // Find the vertical scrollable grid
        val grid = device.wait(Until.findObject(By.scrollable(true)), 15000)
        requireNotNull(grid) { "Home vertical grid not found!" }

        grid.setGestureMargin(device.displayWidth / 5)

        // Scroll down
        grid.scroll(Direction.DOWN, 0.8f)
        device.waitForIdle()

        // Scroll back up
        grid.scroll(Direction.UP, 0.8f)
        device.waitForIdle()
    }

    @Test
    fun navigateToPodcastDetailsFrameTiming() = benchmarkRule.measureRepeated(
        packageName = targetPackageName,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.COLD,
        iterations = 5
    ) {
        pressHome()
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 15000)

        // Find the vertical grid first
        val grid = device.wait(Until.findObject(By.scrollable(true)), 15000)
        requireNotNull(grid) { "Home vertical grid not found!" }

        // Find the horizontal carousel of podcasts nested inside it
        val carousel = grid.findObject(By.scrollable(true))
        requireNotNull(carousel) { "Podcast carousel not found!" }

        // Find the first clickable card inside the carousel
        val firstCard = carousel.findObject(By.clickable(true))
        requireNotNull(firstCard) { "First podcast card not found!" }
        firstCard.click()
        device.waitForIdle()

        // Wait for the podcast details screen to load
        device.wait(Until.findObject(By.scrollable(true)), 5000)

        // Navigate back
        device.pressBack()
        device.waitForIdle()
    }
}
