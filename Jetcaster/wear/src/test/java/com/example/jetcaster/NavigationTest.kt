/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.jetcaster

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.jetcaster.ui.JetcasterNavController.navigateToUpNext
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NavigationTest {
    @get:Rule
    val rule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun launchAndNavigate() {
        val activity = rule.activity

        val navController = activity.navController

        rule.waitUntil {
            navController.currentDestination?.route != null
        }

        assertEquals("player?page={page}", navController.currentDestination?.route)

        navController.navigateToUpNext()

        assertEquals("upNext", navController.currentDestination?.route)
    }
}
