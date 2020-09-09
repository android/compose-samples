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

package com.example.jetnews

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.snapshots.snapshotFlow
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.state.UiState
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Checks that the Snackbar is shown when the HomeScreen data contains an error.
 */
class HomeScreenSnackbarTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalComposeApi::class
    )
    @Test
    fun postsContainError_snackbarShown() {
        composeTestRule.setContent {
            val snackbarHostState = SnackbarHostState()
            val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

            HomeScreen(
                posts = UiState(exception = IllegalStateException()),
                favorites = emptySet(),
                onToggleFavorite = {},
                onRefreshPosts = {},
                onErrorDismiss = {},
                navigateTo = {},
                scaffoldState = scaffoldState
            )

            val latch = CountDownLatch(1)
            snapshotFlow { snackbarHostState.currentSnackbarData }
                .onEach {
                    if (it != null) {
                        val snackbarText =
                            InstrumentationRegistry.getInstrumentation().targetContext.resources
                                .getString(R.string.load_error)
                        onNodeWithText(snackbarText).assertIsDisplayed()
                        latch.countDown()
                    }
                }
            latch.await(2, TimeUnit.SECONDS)
        }
    }
}
