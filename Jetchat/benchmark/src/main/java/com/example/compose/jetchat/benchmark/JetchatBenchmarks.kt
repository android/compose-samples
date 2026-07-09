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

package com.example.compose.jetchat.benchmark

import android.content.Intent
import android.graphics.Point
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
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
class JetchatBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val targetPackageName = "com.example.compose.jetchat"

    @Test
    fun scrollMessagesFrameTiming() = benchmarkRule.measureRepeated(
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
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 10000)

        // Find the message list
        val list = device.wait(Until.findObject(By.scrollable(true)), 15000)
        requireNotNull(list) { "Conversation list not found!" }

        // Set gesture margin to avoid navigating back via system gestures
        list.setGestureMargin(device.displayWidth / 5)

        // Scroll down (since it's a reverse layout message list, scrolling down means moving finger down to see older messages)
        list.scroll(Direction.DOWN, 0.8f)
        device.waitForIdle()

        // Scroll back up
        list.scroll(Direction.UP, 0.8f)
        device.waitForIdle()
    }

    @Test
    fun openProfileFrameTiming() = benchmarkRule.measureRepeated(
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
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 10000)

        // Find and click the navigation drawer icon (using its content description)
        val navIcon = device.wait(Until.findObject(By.desc("Open navigation drawer")), 15000)
        requireNotNull(navIcon) { "Navigation drawer icon not found!" }
        navIcon.click()
        device.waitForIdle()

        // Find Taylor Brooks profile item in the drawer and click it
        val profileItem = device.wait(Until.findObject(By.text("Taylor Brooks")), 2000)
        requireNotNull(profileItem) { "Profile item 'Taylor Brooks' not found!" }
        profileItem.click()
        device.waitForIdle()

        // Navigate back to the conversation
        device.pressBack()
        device.waitForIdle()
    }
}
