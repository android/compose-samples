package com.example.jetsnack.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.example.jetsnack.util.BaseUITestHelper
import com.example.jetsnack.util.Constants

/**
 * Created by André Schabrocker on 2022-02-01
 */
object NavigationBar : BaseUITestHelper() {

    fun navigateToTab(destination: String, composeTestRule: ComposeTestRule) {
        //check for Navigation-bar and navigate to destination-tab
        composeTestRule.onNodeWithText(Constants.NavigationBar.HomeTab.id)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(destination)
            .performClick()
            .assertIsDisplayed()
    }
}
