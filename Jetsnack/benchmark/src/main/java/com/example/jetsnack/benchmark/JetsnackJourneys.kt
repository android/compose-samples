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

package com.example.jetsnack.benchmark

import android.content.Intent
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

const val TARGET_PACKAGE = "com.example.jetsnack"

fun MacrobenchmarkScope.launchJetsnack() {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 15_000)
}

fun MacrobenchmarkScope.waitForFeed(): UiObject2 {
    device.wait(Until.hasObject(By.text("Android's picks")), 15_000)
    val list = device.findObjects(By.scrollable(true))
        .maxByOrNull { it.visibleBounds.height() }
        ?: error("Feed vertical LazyColumn not found")
    list.setGestureMargins(
        device.displayWidth / 5,
        device.displayHeight / 4,
        device.displayWidth / 5,
        device.displayHeight / 4,
    )
    return list
}

fun MacrobenchmarkScope.scrollFeedList(feedList: UiObject2) {
    // Scroll horizontal HighlightedSnacks carousel
    val highlightRow = device.findObjects(By.scrollable(true))
        .filter { it.visibleBounds.height() < device.displayHeight * 2 / 3 }
        .maxByOrNull { it.visibleBounds.height() }
    if (highlightRow != null) {
        highlightRow.setGestureMargin(device.displayWidth / 5)
        repeat(2) {
            highlightRow.fling(Direction.RIGHT)
            device.waitForIdle()
        }
        repeat(2) {
            highlightRow.fling(Direction.LEFT)
            device.waitForIdle()
        }
    }

    // Scroll vertical Feed LazyColumn down and back up
    repeat(2) {
        feedList.fling(Direction.DOWN)
        device.waitForIdle()
    }
    repeat(2) {
        feedList.fling(Direction.UP)
        device.waitForIdle()
    }
}
