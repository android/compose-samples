/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.compose.samples.crane.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import java.util.concurrent.TimeUnit
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ITERATIONS = 5

@Ignore("This test cannot run on an emulator so is disabled for CI, it is here as a sample.")
@RunWith(AndroidJUnit4::class)
class CalendarScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * When developing a screen, you can use a benchmark test like this one to
     * tweak and optimise it's performance. For example, wondering if you should remember something?
     * Try it without remember and save the results of the test, try it again with remember and
     * see if the results were improved.
     **/
    @Test
    fun testCalendarScroll() {
        benchmarkRule.measureRepeated(
            // [START_EXCLUDE]
            packageName = "androidx.compose.samples.crane",
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = ITERATIONS,
            // [END_EXCLUDE]
            setupBlock = {
                startActivityAndWait()
                val buttonSelector = By.text("Select Dates")

                device.wait(
                    Until.hasObject(buttonSelector),
                    TimeUnit.SECONDS.toMillis(3)
                )

                val calendarButton = device.findObject(buttonSelector)
                calendarButton.click()

                // Select some dates to ensure selection composables are taken in
                // to account.
                // Text in the form of "August 15 2022"
                val firstDateSelector = By.textContains(" 15")
                device.wait(
                    Until.hasObject(firstDateSelector),
                    TimeUnit.SECONDS.toMillis(3)
                )
                device.findObject(firstDateSelector).click()
                device.findObject(By.textContains(" 19")).click()
            }
        ) {
            val column = device.findObject(By.scrollable(true))

            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            column.setGestureMargin(device.displayWidth / 5)

            // Scroll down several times
            repeat(3) { column.fling(Direction.DOWN) }
        }
    }
}
