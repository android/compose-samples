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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.compose.jetchat.conversation.ChannelNameBar
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

@Composable
fun DrawerScaffold(
    scaffoldState: ScaffoldState,
    findNavController: () -> NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    DrawerScaffold(
        scaffoldState,
        onChatClicked = {
            findNavController().popBackStack(R.id.nav_home, true)
            scope.launch {
                scaffoldState.drawerState.close()
            }
        },
        onProfileClicked = {
            val bundle = bundleOf("userId" to it)
            findNavController().navigate(R.id.nav_profile, bundle)
            scope.launch {
                scaffoldState.drawerState.close()
            }
        },
        content = content
    )
}

@Composable
private fun DrawerScaffold(
    scaffoldState: ScaffoldState,
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    JetchatTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                JetchatDrawer(
                    onProfileClicked = onProfileClicked,
                    onChatClicked = onChatClicked
                )
            },
            content = content
        )
    }
}

@Composable
fun TopBarScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavIconClicked: () -> Unit,
    complexTopBar: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    JetchatTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (complexTopBar) {
                    ChannelNameBar(
                        channelName = exampleUiState.channelName,
                        channelMembers = exampleUiState.channelMembers,
                        onNavIconPressed = onNavIconClicked,
                        // Use statusBarsPadding() to move the app bar content below the status bar
                        modifier = Modifier.statusBarsPadding(),
                    )
                } else {
                    JetchatAppBar(
                        // Use statusBarsPadding() to move the app bar content below the status bar
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        onNavIconPressed = onNavIconClicked,
                        title = { },
                        actions = {
                            CompositionLocalProvider(
                                LocalContentAlpha provides ContentAlpha.medium
                            ) {
                                // More icon
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    modifier = Modifier
                                        .clickable(
                                            onClick = {
                                                functionalityNotAvailablePopupShown = true
                                            }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 16.dp)
                                        .height(24.dp),
                                    contentDescription = stringResource(id = R.string.more_options)
                                )
                            }
                        }
                    )
                }
            },
            content = {
                if (functionalityNotAvailablePopupShown) {
                    FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
                }
                content(it)
            }
        )
    }
}
