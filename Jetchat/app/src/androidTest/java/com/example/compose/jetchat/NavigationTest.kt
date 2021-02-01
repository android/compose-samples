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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavActivity>()

    @Test
    fun app_launches() {
        // Check app launches at the correct destination
        assertEquals(getNavController().currentDestination?.id, R.id.nav_home)
    }

    @Test
    @Ignore("Issue with keyboard sync https://issuetracker.google.com/169235317")
    fun profileScreen_back_conversationScreen() {
        val navController = getNavController()
        // Navigate to profile
        composeTestRule.runOnUiThread {
            navController.navigate(R.id.nav_profile)
        }
        // Check profile is displayed
        assertEquals(navController.currentDestination?.id, R.id.nav_profile)
        // Extra UI check
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }

    private fun getNavController(): NavController {
        return composeTestRule.activity.findNavController(R.id.nav_host_fragment)
    }
}
