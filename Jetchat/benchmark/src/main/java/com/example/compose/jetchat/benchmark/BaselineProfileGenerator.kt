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
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    private val targetPackageName = "com.example.compose.jetchat"

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = targetPackageName,
        maxIterations = 10
    ) {
        // 1. Startup journey
        pressHome()
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 10000)

        // 2. Scroll journey
        val list = device.wait(Until.findObject(By.scrollable(true)), 15000)
        if (list != null) {
            list.setGestureMargin(device.displayWidth / 5)
            list.scroll(Direction.DOWN, 0.8f)
            device.waitForIdle()
            list.scroll(Direction.UP, 0.8f)
            device.waitForIdle()
        }

        // 3. Open profile journey
        val navIcon = device.wait(Until.findObject(By.desc("Open navigation drawer")), 15000)
        if (navIcon != null) {
            navIcon.click()
            device.waitForIdle()

            val profileItem = device.wait(Until.findObject(By.text("Taylor Brooks")), 2000)
            if (profileItem != null) {
                profileItem.click()
                device.waitForIdle()
                device.pressBack()
                device.waitForIdle()
            }
        }
    }
}
