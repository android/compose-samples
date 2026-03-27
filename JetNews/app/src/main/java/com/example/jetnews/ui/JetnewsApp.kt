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

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.components.AppNavRail
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.interests.InterestsKey
import com.example.jetnews.ui.navigation.Navigator
import com.example.jetnews.ui.navigation.PopUpTo
import com.example.jetnews.ui.navigation.TOP_LEVEL_ROUTES
import com.example.jetnews.ui.navigation.rememberNavigationState
import com.example.jetnews.ui.theme.JetnewsTheme
import kotlinx.coroutines.launch

@Composable
fun JetnewsApp(appContainer: AppContainer, isBackEnabled: Boolean, initialBackStack: List<NavKey>) {

    val navigationState = rememberNavigationState(
        mainTopLevelRoute = HomeKey,
        topLevelRoutes = setOf(HomeKey, InterestsKey),
        initialBackStack = initialBackStack,
    )

    val navigator = remember(navigationState) { Navigator(navigationState) }

    val coroutineScope = rememberCoroutineScope()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpandedScreen = remember(windowSizeClass) {
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    }

    JetnewsTheme {
        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    drawerState = sizeAwareDrawerState,
                    currentTopLevelRoute = navigationState.topLevelRoute,
                    navigate = { navKey -> navigator.navigate(navKey, PopUpTo(navKey)) },
                    navigationItems = TOP_LEVEL_ROUTES,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                )
            },
            drawerState = sizeAwareDrawerState,
            // Only enable opening the drawer via gestures if the screen is not expanded
            gesturesEnabled = !isExpandedScreen,
        ) {
            Row {
                if (isExpandedScreen) {
                    AppNavRail(
                        currentTopLevelRoute = navigationState.topLevelRoute,
                        navigationItems = TOP_LEVEL_ROUTES,
                        navigate = { navKey -> navigator.navigate(navKey) },
                    )
                }
                JetnewsNavDisplay(
                    navigationState = navigationState,
                    navigator = navigator,
                    appContainer = appContainer,
                    onBack = navigator::goUp,
                    isExpandedScreen = isExpandedScreen,
                    isBackEnabled = isBackEnabled,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}
