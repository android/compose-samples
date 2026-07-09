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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManualBaselineProfileGenerator {

    private val targetPackageName = "com.example.jetcaster"

    @Test
    fun generateProfile() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        val context = instrumentation.context

        // 1. Reset profile
        device.executeShellCommand("cmd package compile --reset $targetPackageName")

        // 2. Run journeys 3 times to accumulate hot paths
        for (i in 1..3) {
            device.pressHome()
            val intent = context.packageManager.getLaunchIntentForPackage(targetPackageName)!!
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            device.wait(Until.hasObject(By.pkg(targetPackageName).depth(0)), 15000)

            // Scroll vertical grid
            val grid = device.wait(Until.findObject(By.scrollable(true)), 15000)
            if (grid != null) {
                grid.setGestureMargin(device.displayWidth / 5)
                grid.scroll(Direction.DOWN, 0.8f)
                device.waitForIdle()
                grid.scroll(Direction.UP, 0.8f)
                device.waitForIdle()

                // Click podcast card in horizontal carousel (nested inside the grid)
                val carousel = grid.findObject(By.scrollable(true))
                if (carousel != null) {
                    val firstCard = carousel.findObject(By.clickable(true))
                    if (firstCard != null) {
                        firstCard.click()
                        device.waitForIdle()
                        device.wait(Until.findObject(By.scrollable(true)), 5000)
                        device.pressBack()
                        device.waitForIdle()
                    }
                }
            }
        }

        // 3. Force flush profiles (SIGUSR1)
        device.executeShellCommand("killall -s SIGUSR1 $targetPackageName")
        Thread.sleep(2000) // Wait for flush

        // 4. Dump profiles
        val dumpOutput = device.executeShellCommand("pm dump-profiles --dump-classes-and-methods $targetPackageName")
        System.out.println("Dump output: $dumpOutput")

        // 5. Read the raw profile from the device
        val profileContent = device.executeShellCommand("cat /data/misc/profman/$targetPackageName-primary.prof.txt")
        
        System.out.println("START_BASELINE_PROFILE")
        System.out.println(profileContent)
        System.out.println("END_BASELINE_PROFILE")
    }
}
