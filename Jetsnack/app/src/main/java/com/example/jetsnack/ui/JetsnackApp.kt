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
    private val snackbarMessages = Channel<String>(3, BufferOverflow.DROP_OLDEST)
    // Cache of the Channel buffer used to restore state
    private val pendingSnackbarMessages = mutableListOf<String>()

    init {
        // Restore snackbar messages state
        initialSnackbarMessages.forEach { showSnackbar(it) }

        // Process snackbar events only when the lifecycle is at least STARTED
        coroutineScope.launch {
            snackbarMessages
                .receiveAsFlow()
                .onEach { pendingSnackbarMessages.remove(it) }
                .flowWithLifecycle(lifecycle)
                .collect { message ->
                    snackbarHostState.showSnackbar(message)
                }
        }
    }

    fun showSnackbar(message: String) {
        val result = snackbarMessages.trySend(message)
        if (result.isSuccess) { pendingSnackbarMessages.add(message) }
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
            save = { it.pendingSnackbarMessages },
            restore = { pendingMessages ->
                ScaffoldStateHolder(
                    navController, snackbarHostState, coroutineScope, lifecycle, pendingMessages
                )
            }
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
