package com.example.compose.benchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()


    @Test
    fun startupConversation() = benchmarkRule.measureRepeated(
        packageName = "com.example.compose.jetchat",
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric("ConversationContent")
        ),
        iterations = 10,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupProfile() = benchmarkRule.measureRepeated(
        packageName = "com.example.compose.jetchat",
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric("ProfileContent")
        ),
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = {

        }
    ) {
        pressHome()
        startActivityAndWait()

        val drawerIconSelector = By.res("drawer_nav_icon")
        device.wait(Until.hasObject(drawerIconSelector), 3000)
        device.findObject(drawerIconSelector).click()

        val profileSelector = By.text("Taylor Brooks")
        device.wait(Until.hasObject(profileSelector), 3000)
        device.findObject(profileSelector).click()
    }
}