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

package com.example.compose.jetchat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.profile.ProfileError
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.profile.ProfileViewModel
import com.example.compose.jetchat.theme.LocalNavAnimatedVisibilityScope
import com.example.compose.jetchat.theme.LocalSharedTransitionScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object ConversationRoute : NavKey

@Serializable
data class ProfileRoute(val userId: String, val sharedElementKey: String? = null) : NavKey

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState = rememberDrawerState(initialValue = Closed)
                    val drawerOpen by viewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()

                    val backStack = rememberNavBackStack(ConversationRoute)
                    var selectedMenu by remember { mutableStateOf("composers") }
                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    val scope = rememberCoroutineScope()

                    JetchatDrawer(
                        drawerState = drawerState,
                        selectedMenu = selectedMenu,
                        onChatClicked = {
                            backStack.removeAll { it !is ConversationRoute }
                            scope.launch {
                                drawerState.close()
                            }
                            selectedMenu = it
                        },
                        onProfileClicked = {
                            backStack.add(ProfileRoute(userId = it))
                            scope.launch {
                                drawerState.close()
                            }
                            selectedMenu = it
                        },
                    ) {
                        val sharedTransitionScope = LocalSharedTransitionScope.current
                        val transitionSpec = slideIn(tween(600)) { IntOffset.Zero } togetherWith
                            slideOut(tween(600)) { IntOffset.Zero }

                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            sharedTransitionScope = sharedTransitionScope,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator(),
                            ),
                            transitionSpec = { transitionSpec },
                            popTransitionSpec = { transitionSpec },
                            entryProvider = entryProvider {
                                entry<ConversationRoute> {
                                    CompositionLocalProvider(
                                        LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
                                    ) {
                                        ConversationContent(
                                            uiState = exampleUiState,
                                            navigateToProfile = { user ->
                                                backStack.add(ProfileRoute(userId = user))
                                            },
                                            navigateToProfileWithKey = { user, sharedElementKey ->
                                                backStack.add(
                                                    ProfileRoute(
                                                        userId = user,
                                                        sharedElementKey = sharedElementKey,
                                                    ),
                                                )
                                            },
                                            onNavIconPressed = {
                                                viewModel.openDrawer()
                                            },
                                        )
                                    }
                                }
                                entry<ProfileRoute> { route ->
                                    CompositionLocalProvider(
                                        LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
                                    ) {
                                        val profileViewModel: ProfileViewModel = viewModel(key = route.userId)
                                        profileViewModel.setUserId(route.userId)
                                        val userData by profileViewModel.userData.observeAsState()

                                        if (userData == null) {
                                            ProfileError()
                                        } else {
                                            ProfileScreen(
                                                userData = userData!!,
                                                sharedElementKey = route.sharedElementKey,
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            },
        )
    }
}
