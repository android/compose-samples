/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.espresso.Espresso
import com.example.compose.jetchat.navigation.DEST_ROUTE_CONVERSATION
import com.example.compose.jetchat.navigation.DEST_ROUTE_PROFILE
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavActivity>()

    lateinit var navHostController: NavHostController

    @Before
    fun setUpNavHost() {
        // wait for the NavHostController to be initialized in activity
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            navHostController = composeTestRule.activity.navHostCont
            true
        }
    }

    @Test
    fun app_launches() {
        // Assert conversation screen is visible
        assertEquals(
            composeTestRule.activity.navHostCont.currentBackStackEntry?.destination?.route,
            DEST_ROUTE_CONVERSATION
        )
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        // Navigate to profile
        navigateToProfile("Ali Conors (you)")
        // Check profile is displayed
        assertEquals(
            navHostController.currentBackStackEntry?.destination?.route,
            "$DEST_ROUTE_PROFILE/{userId}"
        )
        assertEquals(
            navHostController.currentBackStackEntry?.arguments?.getString("userId"),
            "me"
        )
        // Extra UI check
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home
        assertEquals(
            navHostController.currentBackStackEntry?.destination?.route,
            DEST_ROUTE_CONVERSATION
        )
    }

    /**
     * Regression test for https://github.com/android/compose-samples/issues/670
     */
    @Test
    fun drawer_conversationScreen_backstackPopUp() {
        navigateToProfile("Ali Conors (you)")
        navigateToHome()
        navigateToProfile("Taylor Brooks")
        navigateToHome()

        // Chewie, we're home
        assertEquals(
            navHostController.currentBackStackEntry?.destination?.route,
            DEST_ROUTE_CONVERSATION
        )
    }

    private fun navigateToProfile(name: String) {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.navigation_drawer_open),
        ).performClick()

        composeTestRule.onNode(hasText(name) and isInDrawer()).performClick()
    }

    private fun isInDrawer() = hasAnyAncestor(isDrawer())

    private fun isDrawer() = SemanticsMatcher.expectValue(
        SemanticsProperties.PaneTitle,
        composeTestRule.activity.getString(androidx.compose.ui.R.string.navigation_menu),
    )

    private fun navigateToHome() {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.navigation_drawer_open),
        ).performClick()

        composeTestRule.onNode(hasText("composers") and isInDrawer()).performClick()
    }
}
