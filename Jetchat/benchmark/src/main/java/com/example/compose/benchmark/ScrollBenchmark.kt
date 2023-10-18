package com.example.compose.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollConversation() = benchmarkRule.measureRepeated(
        packageName = "com.example.compose.jetchat",
        metrics = listOf(
            FrameTimingMetric()
        ),
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = {

        }
    ) {
        startActivityAndWait()

        // Wait for app to load the messages
        val messagesListSelector = By.res("ConversationTestTag")
        device.wait(Until.hasObject(messagesListSelector), 3000)

        val messagesList = device.findObject(messagesListSelector)
        // Setting a gesture margin is important otherwise gesture nav is triggered.
        messagesList.setGestureMargin(device.displayWidth / 5)

        // Scroll the list
        repeat(2) {
            messagesList.fling(Direction.UP)
            device.waitForIdle()
        }
    }
}