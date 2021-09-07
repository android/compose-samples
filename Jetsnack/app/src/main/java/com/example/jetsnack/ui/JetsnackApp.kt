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

package com.example.jetsnack.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.model.SnackbarManager
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun JetsnackApp() {
    ProvideWindowInsets {
        JetsnackTheme {
            val tabs = remember { HomeSections.values() }
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()

            JetsnackScaffold(
                bottomBar = { JetsnackBottomBar(navController = navController, tabs = tabs) },
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.systemBarsPadding(),
                        snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
                    )
                },
                scaffoldState = scaffoldState
            ) { innerPaddingModifier ->
                JetsnackNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPaddingModifier)
                )
            }

            // Handle Snackbar messages
            val currentMessages by SnackbarManager.messages.collectAsState()
            if (currentMessages.isNotEmpty()) {
                val message = currentMessages[0]
                val messageText: String = stringResource(message.messageId)

                // Effect running in a coroutine that displays the Snackbar on the screen
                // If there's a change to messageText, SnackbarManager, or scaffoldState, the
                // previous effect will be cancelled and a new one will start with the new values
                LaunchedEffect(messageText, SnackbarManager, scaffoldState) {
                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    scaffoldState.snackbarHostState.showSnackbar(messageText)
                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
                    SnackbarManager.setMessageShown(message.id)
                }
            }
        }
    }
}
