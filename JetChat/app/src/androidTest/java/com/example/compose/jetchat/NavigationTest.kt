/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.findByText
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    @get:Rule
    val composeTestRule = AndroidComposeTestRule<NavActivity>()

    lateinit var navController: NavController

    @Before
    fun setUp() {
        // Grab a reference to the navigation controller
        val navHostFragment: View =
            composeTestRule.activityTestRule.activity.requireViewById(R.id.nav_host_fragment)
        navController = Navigation.findNavController(navHostFragment)
        // Start the app
        composeTestRule.startConversationScreen()
    }

    @Test
    fun app_launches() {
        // Check app launches at the correct destination
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        // Navigate to profile
        navController.navigate(R.id.nav_profile)
        // Check profile is displayed
        assertEquals(navController.currentDestination?.id, R.id.nav_profile)
        // Extra UI check
        findByText(composeTestRule.getString(R.string.textfield_hint)).assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }
}
