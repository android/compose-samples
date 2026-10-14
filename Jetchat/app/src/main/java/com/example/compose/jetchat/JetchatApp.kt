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

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationViewModel
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
@Composable
fun JetchatApp() {
    // The back stack survives configuration changes and process death
    val backStack = rememberNavBackStack(Conversation("composers"))
    val drawerState = rememberDrawerState(initialValue = Closed)
    val scope = rememberCoroutineScope()

    JetchatDrawer(
        // Derived from the back stack, so it can't get out of sync with the displayed screen
        selectedKey = backStack.last(),
        onNavigate = { key ->
            when (key) {
                is Conversation -> backStack.openConversation(key.channel)
                is Profile -> backStack.openProfile(key.userId)
            }
            scope.launch {
                drawerState.close()
            }
        },
        drawerState = drawerState,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
            },
            // Scope the ViewModels to their entry. The ViewModelStoreNavEntryDecorator
            // requires the default SaveableStateHolderNavEntryDecorator, which saves the
            // state of each entry.
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Conversation> { key ->
                    // The channel comes from the key, so pass it when the ViewModel of this
                    // entry is created
                    val viewModel = viewModel { ConversationViewModel(channel = key.channel) }
                    ConversationContent(
                        uiState = viewModel.uiState,
                        navigateToProfile = { userId -> backStack.openProfile(userId) },
                        onNavIconPressed = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        onMessageLikeToggled = viewModel::toggleLike,
                    )
                }
                entry<Profile> { key ->
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
