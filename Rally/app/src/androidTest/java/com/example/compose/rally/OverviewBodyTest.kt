package com.example.compose.rally

import androidx.ui.test.createComposeRule
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.theme.RallyTheme
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class OverviewBodyTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersDefaultOverviewBody() {
        renderOverviewBody()

        compareScreenshot(composeTestRule)
    }

    private fun renderOverviewBody() {
        composeTestRule.setContent {
            RallyTheme {
                OverviewBody()
            }
        }
    }

}