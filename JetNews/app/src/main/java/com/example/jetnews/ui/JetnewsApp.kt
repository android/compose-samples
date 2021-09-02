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

package com.example.jetnews.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.WindowSize
import com.example.jetnews.utils.getWindowSize
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun JetnewsApp(
    appContainer: AppContainer
) {
    JetnewsTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val darkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
            }

            val navController = rememberNavController()
            val navigationActions = remember(navController) { JetnewsNavigationActions(navController) }

            val coroutineScope = rememberCoroutineScope()
            // This top level scaffold contains the app drawer, which needs to be accessible
            // from multiple screens. An event to open the drawer is passed down to each
            // screen that needs it.
            val scaffoldState = rememberScaffoldState()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: JetnewsDestinations.HOME_ROUTE

            BoxWithConstraints {
                val windowSize = getWindowSize(maxWidth)
                Scaffold(
                    scaffoldState = scaffoldState,
                    // If the window size is Compact, show the AppDrawer.
                    // Otherwise, a NavRail will be shown in individual screens
                    drawerContent = if (windowSize == WindowSize.Compact) {
                        {
                            AppDrawer(
                                currentRoute = currentRoute,
                                navigateToHome = navigationActions.navigateToHome,
                                navigateToInterests = navigationActions.navigateToInterests,
                                closeDrawer = {
                                    coroutineScope.launch { scaffoldState.drawerState.close() }
                                }
                            )
                        }
                    } else {
                        null
                    }
                ) { innerPaddingModifier ->
                    JetnewsNavGraph(
                        appContainer = appContainer,
                        showNavRail = windowSize != WindowSize.Compact,
                        navController = navController,
                        scaffoldState = scaffoldState,
                        navigationActions = navigationActions,
                        modifier = Modifier.padding(innerPaddingModifier)
                    )
                }
            }
        }
    }
}
