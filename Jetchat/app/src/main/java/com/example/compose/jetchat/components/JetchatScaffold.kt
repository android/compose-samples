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

package com.example.compose.jetchat.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import com.example.compose.jetchat.theme.JetchatTheme
/**
 * JetchatScaffold.kt - Main scaffold component combining drawer and app bar for Jetchat.
 *
 * This file is intentionally minimal and serves as a foundation for the main screen
 * structure. The [JetchatDrawer] composable handles all drawer-related UI and interactions.
 *
 * This file can be extended in the future to add:
 * - Floating action buttons
 * - Bottom app bars
 * - Snackbar hosts
 * - Other Material Design 3 scaffold features
 *
 * @see JetchatDrawer
 * @see JetchatAppBar
 */
@Composable
fun JetchatDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    selectedMenu: String,
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    JetchatTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = drawerState,
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerContentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    JetchatDrawerContent(
                        onProfileClicked = onProfileClicked,
                        onChatClicked = onChatClicked,
                        selectedMenu = selectedMenu,
                    )
                }
            },
            content = content,
        )
    }
}
