package com.example.compose.rally

import androidx.ui.test.createComposeRule
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.theme.RallyTheme
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class RallyTopAppBarTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersRallyTopAppBarForTheAppScreensWithOverviewScreenAsCurrentScreen() {
        renderAppBar(RallyScreen.Overview)

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersRallyTopAppBarForTheAppScreensWithAccountsScreenAsCurrentScreen() {
        renderAppBar(RallyScreen.Accounts)

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersRallyTopAppBarForTheAppScreensWithBillsScreenAsCurrentScreen() {
        renderAppBar(RallyScreen.Bills)

        compareScreenshot(composeTestRule)
    }

    private fun renderAppBar(currentScreen: RallyScreen) {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTheme {
                RallyTopAppBar(
                    allScreens = allScreens,
                    onTabSelected = { },
                    currentScreen = currentScreen
                )
            }
        }
    }
}