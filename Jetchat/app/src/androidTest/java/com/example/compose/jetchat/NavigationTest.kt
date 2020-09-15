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

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Providers
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createAndroidComposeRule
import androidx.ui.test.onNodeWithText
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavActivity>(disableTransitions = true)

    // Note that keeping these references is only safe if the activity is not recreated.
    // See: https://issuetracker.google.com/160862278
    private lateinit var navController: NavController
    private lateinit var activity: ComponentActivity

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity: NavActivity ->
            // Store a reference to the activity. Don't do this if the activity is recreated!
            activity = newActivity
            val navHostFragment: View = newActivity.findViewById(R.id.nav_host_fragment)
            // Store a reference to the navigation controller.
            navController = Navigation.findNavController(navHostFragment)
        }

        // Start the app
        composeTestRule.setContent {
            Providers(BackPressedDispatcherAmbient provides activity) {
                JetchatTheme {
                    ConversationContent(
                        uiState = exampleUiState,
                        navigateToProfile = { },
                        onNavIconPressed = { }
                    )
                }
            }
        }
    }

    @Test
    fun app_launches() {
        // Check app launches at the correct destination
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        // Navigate to profile
        composeTestRule.runOnUiThread {
            navController.navigate(R.id.nav_profile)
        }
        // Check profile is displayed
        assertEquals(navController.currentDestination?.id, R.id.nav_profile)
        // Extra UI check
        composeTestRule.onNodeWithText(activity.getString(R.string.textfield_hint)).assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }
}
