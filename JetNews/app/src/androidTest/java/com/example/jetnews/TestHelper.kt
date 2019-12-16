/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews

import androidx.compose.Composable
import androidx.ui.core.semantics.getOrNull
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.semantics.SemanticsProperties
import androidx.ui.test.ComposeTestRule
import androidx.ui.test.SemanticsNodeInteraction
import androidx.ui.test.findAll
import com.example.jetnews.ui.JetnewsApp
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen

/**
 * Launches the app from a test context
 */
fun ComposeTestRule.launchJetNewsApp() {
    setContent {
        JetnewsStatus.resetState()
        JetnewsApp()
    }
}

/**
 * Resets the state of the app. Needs to be executed in Compose code (within a frame)
 */
fun JetnewsStatus.resetState() {
    currentScreen = Screen.Home
    favorites.clear()
    selectedTopics.clear()
}

/**
 * Helper method that can be used to test Jetnews UI Composables in isolation
 */
fun ComposeTestRule.setMaterialContent(children: @Composable() () -> Unit) {
    setContent {
        MaterialTheme {
            Surface {
                children()
            }
        }
    }
}

/**
 * Workarounds, these functions should be removed when UI testing improves
 */

fun waitForComposeToBeIdle() {
    // Temporary workaround - use waitForIdle in dev04
    Thread.sleep(500)
}

fun findAllByText(text: String, ignoreCase: Boolean = false): List<SemanticsNodeInteraction> {
    return findAll {
        getOrNull(SemanticsProperties.AccessibilityLabel).equals(text, ignoreCase)
    }
}
