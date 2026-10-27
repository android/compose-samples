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
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.components.JetchatDrawerContent
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationViewModel
import com.example.compose.jetchat.data.findMessage
import com.example.compose.jetchat.navigation.ChatList
import com.example.compose.jetchat.navigation.Conversation
import com.example.compose.jetchat.navigation.Profile
import com.example.compose.jetchat.navigation.VideoPlayer
import com.example.compose.jetchat.navigation.openConversation
import com.example.compose.jetchat.navigation.openProfile
import com.example.compose.jetchat.profile.ProfileError
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.profile.ProfileViewModel
import com.example.compose.jetchat.video.FullScreenVideoPlayer
import com.example.compose.jetchat.video.LocalAnimatedVisibilityScope
import com.example.compose.jetchat.video.LocalSharedTransitionScope
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
    // The back stack survives configuration changes and process death
    val backStack = rememberNavBackStack(ChatList, Conversation("composers"))
    val drawerState = rememberDrawerState(initialValue = Closed)
    val scope = rememberCoroutineScope()

    // Override the defaults so that there isn't a horizontal space between the panes, and don't
    // auto-focus the current pane (which would focus the conversation text field).
    // PaneScaffoldDirective.copy() does not expose shouldAutoFocusCurrentDestination, so construct
    // the directive explicitly.
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        with(calculatePaneScaffoldDirective(windowAdaptiveInfo)) {
            PaneScaffoldDirective(
                maxHorizontalPartitions = maxHorizontalPartitions,
                horizontalPartitionSpacerSize = 0.dp,
                maxVerticalPartitions = maxVerticalPartitions,
                verticalPartitionSpacerSize = verticalPartitionSpacerSize,
                defaultPanePreferredWidth = defaultPanePreferredWidth,
                defaultPanePreferredHeight = defaultPanePreferredHeight,
                excludedBounds = excludedBounds,
                shouldAutoFocusCurrentDestination = false,
            )
        }
    }
    val isMultiPane = directive.maxHorizontalPartitions > 1

    val selectedKey = backStack.lastOrNull { it !is VideoPlayer } ?: backStack.last()
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = if (selectedKey is Profile) {
            BackNavigationBehavior.PopLatest
        } else {
            BackNavigationBehavior.PopUntilScaffoldValueChange
        },
        directive = directive,
    )

    LaunchedEffect(isMultiPane) {
        if (isMultiPane && drawerState.isOpen) {
            drawerState.close()
        }
    }

    var isPoppingVideoPlayer by remember { mutableStateOf(false) }

    val onNavigate: (NavKey) -> Unit = { key ->
        when (key) {
            is Conversation -> {
                isPoppingVideoPlayer = false
                backStack.openConversation(key.channel)
            }

            is Profile -> {
                isPoppingVideoPlayer = false
                backStack.openProfile(key.userId, key.isExtraPane)
            }

            is VideoPlayer -> Unit

            ChatList -> Unit
        }
        scope.launch {
            drawerState.close()
        }
    }

    // On single-pane windows the chat list is shown in the drawer instead of a screen. The
    // conversation is never popped, so the displayed back stack is never empty.
    val displayBackStack by remember(isMultiPane) {
        derivedStateOf {
            if (isMultiPane) {
                backStack
            } else {
                backStack.filter { it !is ChatList }.also {
                    check(it.isNotEmpty()) { "A conversation must always be on the back stack" }
                }
            }
        }
    }

    JetchatDrawer(
        // Derived from the back stack, so it can't get out of sync with the displayed screen
        selectedKey = selectedKey,
        onNavigate = onNavigate,
        drawerState = drawerState,
        // The list pane replaces the drawer on wide windows, and disable drawer gestures during video playback
        gesturesEnabled = !isMultiPane && (backStack.lastOrNull() !is VideoPlayer),
    ) {
        BackHandler(enabled = drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }

        SharedTransitionLayout {
            val popBackStack: () -> Unit = {
                isPoppingVideoPlayer = backStack.lastOrNull() is VideoPlayer
                backStack.removeLastOrNull()
            }

            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavDisplay(
                    backStack = displayBackStack,
                    onBack = popBackStack,
                    sharedTransitionScope = this,
                    sceneStrategies = listOf(listDetailStrategy),
                    // Scope the ViewModels to their entry. The ViewModelStoreNavEntryDecorator
                    // requires the default SaveableStateHolderNavEntryDecorator, which saves the
                    // state of each entry.
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                        rememberAnimatedVisibilityScopeNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<ChatList>(
                            metadata = ListDetailSceneStrategy.listPane(),
                        ) {
                            val chatListTitle = stringResource(R.string.chat_list)
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .semantics { paneTitle = chatListTitle },
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
                            ConversationEntry(
                                channel = key.channel,
                                isVideoPlayerOnTop = backStack.lastOrNull() is VideoPlayer,
                                isPoppingVideoPlayer = isPoppingVideoPlayer,
                                onClearPoppingVideoPlayer = { isPoppingVideoPlayer = false },
                                navigateToProfile = { userId ->
                                    backStack.openProfile(userId, isExtraPane = true)
                                },
                                onOpenDrawer = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                                onShowChatList = { backStack.openConversation(key.channel) },
                                onVideoClick = { videoUri, messageId ->
                                    isPoppingVideoPlayer = false
                                    backStack.add(VideoPlayer(videoUri = videoUri, messageId = messageId))
                                },
                            )
                        }
                        entry<Profile>(
                            metadata = { key ->
                                if (key.isExtraPane) {
                                    ListDetailSceneStrategy.extraPane()
                                } else {
                                    ListDetailSceneStrategy.detailPane()
                                }
                            },
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
                        entry<VideoPlayer>(
                            metadata = NavDisplay.transitionSpec {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                    ExitTransition.KeepUntilTransitionsFinished
                            } + NavDisplay.popTransitionSpec {
                                EnterTransition.None togetherWith
                                    fadeOut(animationSpec = tween(300))
                            } + NavDisplay.predictivePopTransitionSpec {
                                EnterTransition.None togetherWith fadeOut()
                            },
                        ) { key ->
                            val message = key.messageId?.let(::findMessage)
                            FullScreenVideoPlayer(
                                videoUri = key.videoUri,
                                messageText = message?.content,
                                messageId = key.messageId,
                                isUserMe = message == null || message.author == stringResource(R.string.author_me),
                                onDismiss = popBackStack,
                                enableBackHandler = false,
                            )
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ConversationEntry(
    channel: String,
    isVideoPlayerOnTop: Boolean,
    isPoppingVideoPlayer: Boolean,
    onClearPoppingVideoPlayer: () -> Unit,
    navigateToProfile: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onShowChatList: () -> Unit,
    onVideoClick: (String, String) -> Unit,
) {
    // The channel comes from the key, so pass it when the ViewModel of this entry is created
    val viewModel = viewModel { ConversationViewModel(channel = channel) }
    // The channel name shows the chat list: it opens the drawer on single-pane windows, where
    // there is no list-detail scene, and pops the profile that hides the list pane on wide
    // windows. Whether the list pane is shown comes from the scaffold, as it depends on the
    // number of panes: on 3-pane windows it stays next to the conversation while a profile is
    // shown.
    val sceneScope = LocalListDetailSceneScope.current
    val isListPaneShown = sceneScope?.scaffoldTransitionScope
        ?.scaffoldStateTransition?.targetState
        ?.get(ListDetailPaneScaffoldRole.List) == PaneAdaptedValue.Expanded

    val navTransition = LocalNavAnimatedContentScope.current.transition
    val isTransitionActive = LocalSharedTransitionScope.current?.isTransitionActive == true
    val isSettledOnConversation =
        navTransition.currentState == EnterExitState.Visible &&
            navTransition.targetState == EnterExitState.Visible
    LaunchedEffect(isPoppingVideoPlayer, isSettledOnConversation, isTransitionActive) {
        if (isPoppingVideoPlayer && isSettledOnConversation && !isTransitionActive) {
            onClearPoppingVideoPlayer()
        }
    }
    val isPredictiveBackFromVideoPlayer =
        isVideoPlayerOnTop &&
            navTransition.currentState == EnterExitState.PreEnter &&
            navTransition.targetState == EnterExitState.Visible

    ConversationContent(
        uiState = viewModel.uiState,
        navigateToProfile = navigateToProfile,
        onNavIconPressed = when {
            sceneScope == null -> onOpenDrawer
            isListPaneShown -> null
            else -> onShowChatList
        },
        navIconContentDescription = stringResource(
            if (sceneScope == null) {
                R.string.navigation_drawer_open
            } else {
                R.string.show_chat_list
            },
        ),
        onMessageLikeToggled = viewModel::toggleLike,
        onVideoClick = onVideoClick,
        isReturningFromVideoPlayer = isPoppingVideoPlayer || isPredictiveBackFromVideoPlayer,
    )
}

/**
 * Exposes Navigation 3's [LocalNavAnimatedContentScope] to every entry as the nullable
 * [LocalAnimatedVisibilityScope] used by the video shared element transition, so it does not have
 * to be provided by each entry individually.
 */
@Composable
private fun rememberAnimatedVisibilityScopeNavEntryDecorator(): NavEntryDecorator<NavKey> = remember {
    NavEntryDecorator { entry ->
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        ) {
            entry.Content()
        }
    }
}
