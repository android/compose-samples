/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import com.example.jetcaster.tv.ui.discover.DiscoverScreen
import com.example.jetcaster.tv.ui.library.LibraryScreen
import com.example.jetcaster.tv.ui.profile.ProfileScreen
import com.example.jetcaster.tv.ui.search.SearchScreen
import com.example.jetcaster.tv.ui.settings.SettingsScreen

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun JetcasterApp(jetcasterAppState: JetcasterAppState = rememberJetcasterAppState()) {
    val (menu, discover) = FocusRequester.createRefs()

    NavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .padding(
                        JetcasterAppDefaults.overScanMargin
                            .copy(
                                start = 0.dp,
                                end = 0.dp
                            )
                            .intoPaddingValues()
                    )
                    .focusRequester(menu)
                    .focusRestorer { discover }
            ) {

                NavigationDrawerItem(
                    selected = false,
                    onClick = jetcasterAppState::navigateToProfile,
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                ) {
                    Column {
                        Text(text = "Name")
                        Text(text = "Switch Account", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    selected = false,
                    onClick = jetcasterAppState::navigateToSearch,
                    leadingContent = { Icon(Icons.Default.Search, contentDescription = null) }
                ) {
                    Text(text = "Search")
                }
                NavigationDrawerItem(
                    selected = false,
                    onClick = jetcasterAppState::navigateToDiscover,
                    leadingContent = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.focusRequester(discover)
                ) {
                    Text(text = "Discover")
                }
                NavigationDrawerItem(
                    selected = false,
                    onClick = jetcasterAppState::navigateToLibrary,
                    leadingContent = { Icon(Icons.Default.VideoLibrary, contentDescription = null) }
                ) {
                    Text(text = "Library")
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    selected = false,
                    onClick = jetcasterAppState::navigateToSettings,
                    leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) }
                ) {
                    Text(text = "Settings")
                }
            }
        }
    ) {
        Route(jetcasterAppState = jetcasterAppState)
    }
}

data object JetcasterAppDefaults {
    val overScanMargin = OverScanMargin()
}

data class OverScanMargin(
    val top: Dp = 24.dp,
    val bottom: Dp = 24.dp,
    val start: Dp = 48.dp,
    val end: Dp = 48.dp,
) {
    fun intoPaddingValues(): PaddingValues {
        return PaddingValues(start, top, end, bottom)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Route(jetcasterAppState: JetcasterAppState) {
    NavHost(navController = jetcasterAppState.navHostController, Screen.Discover.route) {
        composable(Screen.Discover.route) {
            DiscoverScreen(
                modifier = Modifier.padding(JetcasterAppDefaults.overScanMargin.intoPaddingValues())
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                modifier = Modifier.padding(JetcasterAppDefaults.overScanMargin.intoPaddingValues())
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                modifier = Modifier.padding(JetcasterAppDefaults.overScanMargin.intoPaddingValues())
            )
        }

        composable(Screen.Show.route) {
            Text(text = "Show")
        }

        composable(Screen.Player.route) {
            Text(text = "Player")
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                modifier = Modifier.padding(JetcasterAppDefaults.overScanMargin.intoPaddingValues())
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                modifier = Modifier.padding(JetcasterAppDefaults.overScanMargin.intoPaddingValues())
            )
        }
    }
}
