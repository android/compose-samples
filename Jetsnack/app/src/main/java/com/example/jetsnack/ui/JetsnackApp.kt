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

package com.example.jetsnack.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.ui.SnackbarMessagesState.Companion.SnackbarMessagesStateSaver
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun JetsnackApp() {
    ProvideWindowInsets {
        JetsnackTheme {
            val scaffoldState = rememberScaffoldState()
            val scaffoldStateHolder = rememberScaffoldStateHolder(scaffoldState)

            JetsnackScaffold(
                scaffoldState = scaffoldState,
                bottomBar = {
                    JetsnackBottomBar(scaffoldStateHolder.navController, scaffoldStateHolder.tabs)
                }
            ) { innerPaddingModifier ->
                JetsnackNavGraph(
                    scaffoldStateHolder = scaffoldStateHolder,
                    modifier = Modifier.padding(innerPaddingModifier)
                )
            }
        }
    }
}

@Composable
private fun rememberScaffoldStateHolder(
    scaffoldState: ScaffoldState = rememberScaffoldState()
): ScaffoldStateHolder {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val snackbarMessages = rememberSnackbarMessagesState(listOf())
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    return remember(
        navController, scaffoldState.snackbarHostState,
        snackbarMessages, coroutineScope, lifecycle
    ) {
        ScaffoldStateHolder(
            navController, scaffoldState.snackbarHostState, snackbarMessages,
            coroutineScope, lifecycle
        )
    }
}

class ScaffoldStateHolder(
    val navController: NavHostController,
    private val snackbarHostState: SnackbarHostState,
    private val snackbarMessagesState: SnackbarMessagesState,
    coroutineScope: CoroutineScope,
    lifecycle: Lifecycle
) {
    val tabs = HomeSections.values()
    init {
        // Process snackbar events only when the lifecycle is at least STARTED
        coroutineScope.launch {
            snackbarMessagesState.snackbarMessages
                .flowWithLifecycle(lifecycle)
                .collect { message ->
                    snackbarHostState.showSnackbar(message, message)
                }
        }
    }

    fun showSnackbar(message: String) {
        snackbarMessagesState.showSnackbar(message)
    }

    fun navigateToSnackDetail(from: NavBackStackEntry, snackId: Long) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.SNACK_DETAIL_ROUTE}/$snackId")
        }
    }

    fun onBackPress() {
        navController.navigateUp()
    }
}

@Composable
fun rememberSnackbarMessagesState(initialMessages: List<String>): SnackbarMessagesState =
    rememberSaveable(
        inputs = arrayOf(initialMessages),
        saver = SnackbarMessagesStateSaver
    ) {
        SnackbarMessagesState(initialMessages)
    }

class SnackbarMessagesState(initialMessages: List<String>) {
    // Synchronous cache of the Channel used to restore state
    private val pendingMessages = mutableListOf<String>()

    // Queue a maximum of 3 snackbars
    private val _snackbarMessages = Channel<String>(3, BufferOverflow.DROP_OLDEST)
    val snackbarMessages: Flow<String> =
        _snackbarMessages.receiveAsFlow().onEach { pendingMessages.remove(it) }

    init {
        initialMessages.forEach { showSnackbar(it) }
    }

    fun showSnackbar(message: String) {
        val result = _snackbarMessages.trySend(message)
        if (result.isSuccess) { pendingMessages.add(message) }
    }

    companion object {
        val SnackbarMessagesStateSaver = listSaver<SnackbarMessagesState, String>(
            save = { it.pendingMessages },
            restore = { SnackbarMessagesState(it) }
        )
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
