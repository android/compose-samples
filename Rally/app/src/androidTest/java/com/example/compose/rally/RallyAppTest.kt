package com.example.compose.rally

import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.performClick
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class RallyAppTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersRallyAppInTheDefaultTab() {
        renderRallyAppComponent()

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersRallyAppAccountTabWhenTappingOnThisComponent() {
        renderRallyAppComponent()

        clickOnTab(RallyScreen.Accounts)

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersRallyAppBillsTabWhenTappingOnThisComponent() {
        renderRallyAppComponent()

        clickOnTab(RallyScreen.Bills)

        compareScreenshot(composeTestRule)
    }

    private fun clickOnTab(screen: RallyScreen) =
        composeTestRule.onNodeWithTag(screen.name.toUpperCase()).performClick()

    private fun renderRallyAppComponent() {
        composeTestRule.setContent {
            RallyApp()
        }
    }
}