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
import androidx.navigation3.runtime.NavKey
import com.example.compose.jetchat.theme.JetchatTheme

/**
 * Navigation drawer of the app.
 *
 * @param selectedKey the key of the screen currently displayed, used to highlight its drawer item
 * @param onNavigate called with the key of the drawer item the user tapped
 */
@Composable
fun JetchatDrawer(
    selectedKey: NavKey,
    onNavigate: (NavKey) -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
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
                        selectedKey = selectedKey,
                        onNavigate = onNavigate,
                    )
                }
            },
            content = content,
        )
    }
}
