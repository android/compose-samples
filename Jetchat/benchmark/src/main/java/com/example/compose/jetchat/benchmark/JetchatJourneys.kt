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
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

const val TARGET_PACKAGE = "com.example.compose.jetchat"

/**
 * Launches Jetchat without [MacrobenchmarkScope.startActivityAndWait], which fails on this device
 * with "Unable to confirm activity launch completion".
 */
fun MacrobenchmarkScope.launchJetchat() {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 15_000)
}

/** Waits for the conversation list to show loaded messages and returns it. */
fun MacrobenchmarkScope.waitForConversation(): UiObject2 {
    // "Today" is a day header that only exists once messages are in the list
    device.wait(Until.hasObject(By.text("Today")), 15_000)
    // Non-debuggable builds strip testTag, so pick the tallest scrollable container
    val list = device.findObjects(By.scrollable(true))
        .maxByOrNull { it.visibleBounds.height() }
        ?: error("Conversation list not found")
    // The list is edge-to-edge behind the top app bar and the message input. Keep gestures in the
    // middle third of the screen so they start on the list, not on those bars.
    list.setGestureMargins(
        device.displayWidth / 5,
        device.displayHeight / 3,
        device.displayWidth / 5,
        device.displayHeight / 3,
    )
    return list
}

/**
 * Scrolls the reverse-layout conversation up through older messages and back down to the newest.
 */
fun MacrobenchmarkScope.scrollConversation(list: UiObject2) {
    repeat(3) {
        list.fling(Direction.UP)
        device.waitForIdle()
    }
    repeat(3) {
        list.fling(Direction.DOWN)
        device.waitForIdle()
    }
}
