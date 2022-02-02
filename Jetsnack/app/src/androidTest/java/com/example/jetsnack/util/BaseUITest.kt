package com.example.jetsnack.util

import androidx.annotation.CallSuper
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.jetsnack.ui.MainActivity
import org.junit.*

/**
 * Created by André Schabrocker on 2022-01-31
 */
@Ignore abstract class BaseUITest {
    protected lateinit var device: UiDevice
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before @CallSuper fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }
}
