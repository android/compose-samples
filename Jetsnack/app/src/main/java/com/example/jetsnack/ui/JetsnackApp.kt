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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.ui.ScaffoldStateHolder.Companion.ScaffoldStateHolderSaver
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

/**
 * Creates a [ScaffoldStateHolder] and memoizes it.
 *
 * Pending snackbar messages to show on the screen are remembered across
 * activity and process recreation.
 */
@Composable
private fun rememberScaffoldStateHolder(
    scaffoldState: ScaffoldState = rememberScaffoldState()
): ScaffoldStateHolder {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarHostState = scaffoldState.snackbarHostState

    return rememberSaveable(
        inputs = arrayOf(navController, snackbarHostState, coroutineScope, lifecycle),
        saver = ScaffoldStateHolderSaver(
            navController, snackbarHostState, coroutineScope, lifecycle
        )
    ) {
        ScaffoldStateHolder(
            navController, scaffoldState.snackbarHostState, coroutineScope, lifecycle, listOf()
        )
    }
}

/**
 * State holder for Jetsnack's app scaffold that contains state related to [JetsnackScaffold], and
 * handles Navigation and Snackbar events.
 */
class ScaffoldStateHolder(
    val navController: NavHostController,
    private val snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    lifecycle: Lifecycle,
    initialSnackbarMessages: List<String>
) {
    // Tabs for JetsnackBottomBar
    val tabs = HomeSections.values()

    // Queue a maximum of 3 snackbar messages
    private val snackbarMessages = StateChannel<String>(3, BufferOverflow.DROP_OLDEST)

    init {
        coroutineScope.launch {
            // Restore snackbar messages state
            initialSnackbarMessages.forEach { showSnackbar(it) }

            // Process snackbar events only when the lifecycle is at least STARTED
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                snackbarMessages.stream
                    .collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
            }
        }
    }

    suspend fun showSnackbar(message: String) {
        snackbarMessages.send(message)
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

    companion object {
        fun ScaffoldStateHolderSaver(
            navController: NavHostController,
            snackbarHostState: SnackbarHostState,
            coroutineScope: CoroutineScope,
            lifecycle: Lifecycle
        ) = listSaver<ScaffoldStateHolder, String>(
            save = { it.snackbarMessages.pendingElements },
            restore = { pendingMessages ->
                ScaffoldStateHolder(
                    navController, snackbarHostState, coroutineScope, lifecycle, pendingMessages
                )
            }
        )
    }
}

/**
 * Implementation of a Channel whose buffered elements can be accessed synchronously so that they
 * can be saved/restored on activity and process recreation.
 *
 * In this implementation, the exposed `stream` flow is supposed to be collected once at a time
 * since in case there are multiple collectors, only one will get the event.
 * If events need to be broadcasted to multiple collectors, expose stream using the `.shareIn`
 * operator instead.
 *
 * @param capacity capacity of the underlying Channel. This should be a non-negative integer,
 * and not one of the Channel.* constants.
 * @param onBufferOverflow configures an action on buffer overflow.
 */
class StateChannel<T>(
    private val capacity: Int,
    private val onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) {
    // Cache of the Channel buffer used to restore state
    val pendingElements = mutableStateListOf<T>()
    private val pendingElementsMutex = Mutex()

    private val _channel = Channel<T>(capacity, onBufferOverflow)
    val stream = _channel
        .receiveAsFlow()
        .onEach { pendingElementsMutex.withLock { pendingElements.removeFirstOrNull() } }

    suspend fun send(element: T) {
        _channel.send(element)
        pendingElementsMutex.withLock {
            pendingElements.add(element)
            if (onBufferOverflow == BufferOverflow.DROP_OLDEST) {
                while(pendingElements.size > capacity) { pendingElements.removeFirstOrNull() }
            } else if (onBufferOverflow == BufferOverflow.DROP_LATEST) {
                while(pendingElements.size > capacity) { pendingElements.removeLastOrNull() }
            }
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
