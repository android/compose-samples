/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.reply.ui.navigation.ReplyDestinations
import com.example.reply.ui.navigation.ReplyNavigationActions
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.EmptyComingSoon
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyApp(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture,
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit = {},
    setSelectedEmail: (Long) -> Unit = {}
) {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     *
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val navigationType: ReplyNavigationType
    val contentType: ReplyContentType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = ReplyNavigationType.BOTTOM_NAVIGATION
            contentType = ReplyContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = ReplyNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                ReplyContentType.LIST_AND_DETAIL
            } else {
                ReplyContentType.LIST_ONLY
            }
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                ReplyNavigationType.NAVIGATION_RAIL
            } else {
                ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = ReplyContentType.LIST_AND_DETAIL
        }
        else -> {
            navigationType = ReplyNavigationType.BOTTOM_NAVIGATION
            contentType = ReplyContentType.LIST_ONLY
        }
    }

    ReplyNavigationWrapperUI(navigationType, contentType, replyHomeUIState, closeDetailScreen, setSelectedEmail)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReplyNavigationWrapperUI(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit,
    setSelectedEmail: (Long) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        ReplyNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: ReplyDestinations.INBOX

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(drawerContent = {
            NavigationDrawerContent(
                selectedDestination,
                navigationActions.navigateToInbox,
                navigationActions.navigateToDM,
                navigationActions.navigateToArticles,
                navigationActions.navigateToGroups,
            )
        }) {
            ReplyAppContent(
                navigationType,
                contentType,
                replyHomeUIState,
                navController,
                selectedDestination,
                navigationActions.navigateToInbox,
                navigationActions.navigateToDM,
                navigationActions.navigateToArticles,
                navigationActions.navigateToGroups,
                closeDetailScreen,
                { emailId ->
                    setSelectedEmail.invoke(emailId)
                }
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent(
                    selectedDestination,
                    navigationActions.navigateToInbox,
                    navigationActions.navigateToDM,
                    navigationActions.navigateToArticles,
                    navigationActions.navigateToGroups,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            ReplyAppContent(
                navigationType,
                contentType,
                replyHomeUIState,
                navController,
                selectedDestination,
                navigationActions.navigateToInbox,
                navigationActions.navigateToDM,
                navigationActions.navigateToArticles,
                navigationActions.navigateToGroups,
                closeDetailScreen,
                { emailId ->
                    setSelectedEmail.invoke(emailId)
                    if (contentType == ReplyContentType.LIST_ONLY) {
                        navigationActions.navigateToDetail.invoke(emailId)
                    }
                }
            ) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}


@Composable
fun ReplyAppContent(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    navController: NavHostController,
    selectedDestination: String,
    navigateToInbox: () -> Unit,
    navigateToDM: () -> Unit,
    navigateToArticles: () -> Unit,
    navigateToGroups: () -> Unit,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail(
                selectedDestination,
                navigateToInbox,
                navigateToDM,
                navigateToArticles,
                navigateToGroups,
                onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            ReplyNavHost(
                Modifier.weight(1f),
                navController,
                contentType,
                replyHomeUIState,
                navigationType,
                closeDetailScreen,
                navigateToDetail
            )
            AnimatedVisibility(visible = navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ReplyBottomNavigationBar(
                    selectedDestination,
                    navigateToInbox,
                    navigateToDM,
                    navigateToArticles,
                    navigateToGroups
                )
            }
        }
    }
}

@Composable
private fun ReplyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    navigationType: ReplyNavigationType,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ReplyDestinations.INBOX,
    ) {
        composable(ReplyDestinations.INBOX) {
            ReplyInboxScreen(contentType, replyHomeUIState, navigationType, closeDetailScreen, navigateToDetail)
        }
        composable(ReplyDestinations.DM) {
            EmptyComingSoon()
        }
        composable(ReplyDestinations.ARTICLES) {
            EmptyComingSoon()
        }
        composable(ReplyDestinations.GROUPS) {
            EmptyComingSoon()
        }
    }
}

