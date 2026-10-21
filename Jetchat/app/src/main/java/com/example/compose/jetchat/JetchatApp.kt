/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.components.JetchatDrawerContent
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationViewModel
import com.example.compose.jetchat.navigation.ChatList
import com.example.compose.jetchat.navigation.Conversation
import com.example.compose.jetchat.navigation.Profile
import com.example.compose.jetchat.navigation.openConversation
import com.example.compose.jetchat.navigation.openProfile
import com.example.compose.jetchat.profile.ProfileError
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.profile.ProfileViewModel
import kotlinx.coroutines.launch

/**
 * Root composable of the app: the navigation drawer around a Navigation 3 [NavDisplay].
 *
 * The back stack is the single source of truth for navigation. The drawer derives its selection
 * from it and sends navigation events back to it, following unidirectional data flow.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun JetchatApp() {
    // The back stack survives configuration changes and process death. On wide screens,
    // ChatList and Conversation are shown side by side via ListDetailSceneStrategy; on
    // single-pane phones, the existing ModalNavigationDrawer flow is used.
    val backStack = rememberNavBackStack(ChatList, Conversation("composers"))
    val drawerState = rememberDrawerState(initialValue = Closed)
    val scope = rememberCoroutineScope()

    // Override the defaults so that there isn't a horizontal space between the panes.
    // See b/418201867
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val isMultiPane = directive.maxHorizontalPartitions > 1
    val isListAndDetailVisible = isMultiPane && backStack.lastOrNull() is Conversation
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    LaunchedEffect(isListAndDetailVisible) {
        if (isListAndDetailVisible && drawerState.isOpen) {
            drawerState.close()
        }
    }

    val selectedKey = backStack.lastOrNull() ?: Conversation("composers")
    val onNavigate: (NavKey) -> Unit = { key ->
        when (key) {
            is Conversation -> backStack.openConversation(key.channel)
            is Profile -> backStack.openProfile(key.userId)
        }
        scope.launch {
            drawerState.close()
        }
    }

    val displayBackStack = if (isMultiPane) {
        backStack
    } else {
        backStack.filter { it !is ChatList }
    }

    JetchatDrawer(
        // Derived from the back stack, so it can't get out of sync with the displayed screen
        selectedKey = selectedKey,
        onNavigate = onNavigate,
        drawerState = drawerState,
        gesturesEnabled = !isListAndDetailVisible,
    ) {
        BackHandler(enabled = drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }

        NavDisplay(
            backStack = displayBackStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailStrategy),
            // Scope the ViewModels to their entry. The ViewModelStoreNavEntryDecorator
            // requires the default SaveableStateHolderNavEntryDecorator, which saves the
            // state of each entry.
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<ChatList>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = { ChatPlaceholder() },
                    ),
                ) {
                    val navigationMenuTitle = stringResource(
                        androidx.compose.ui.R.string.navigation_menu,
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { paneTitle = navigationMenuTitle },
                        color = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ) {
                        JetchatDrawerContent(
                            selectedKey = selectedKey,
                            onNavigate = onNavigate,
                        )
                    }
                }
                entry<Conversation>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) { key ->
                    // The channel comes from the key, so pass it when the ViewModel of this
                    // entry is created
                    val viewModel = viewModel { ConversationViewModel(channel = key.channel) }
                    // Detail screens hide the navigation drawer button when in the
                    // multi-pane list-detail scene
                    val inListDetailScene = LocalListDetailSceneScope.current != null
                    ConversationContent(
                        uiState = viewModel.uiState,
                        navigateToProfile = { userId -> backStack.openProfile(userId) },
                        onNavIconPressed = if (inListDetailScene) {
                            null
                        } else {
                            {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        },
                        onMessageLikeToggled = viewModel::toggleLike,
                    )
                }
                entry<Profile>(
                    metadata = ListDetailSceneStrategy.extraPane(),
                ) { key ->
                    // The user id comes from the key, so set it when the ViewModel of this
                    // entry is created
                    val viewModel = viewModel {
                        ProfileViewModel().apply { setUserId(key.userId) }
                    }
                    val userData by viewModel.userData.observeAsState()

                    if (userData == null) {
                        ProfileError()
                    } else {
                        ProfileScreen(
                            userData = userData!!,
                        )
                    }
                }
            },
        )
    }
}

/** Shown in the detail pane when no conversation is selected. */
@Composable
private fun ChatPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.select_a_chat),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}
