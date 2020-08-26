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

package com.example.jetnews.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.jetnews.data.Result
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.copyWithResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

/**
 * Result object for [launchUiStateProducer].
 *
 * It is intended that you destructure this class at the call site. Here is an example usage that
 * calls dataSource.loadData() and then displays a UI based on the result.
 *
 * ```
 * val (result, onRefresh, onClearError) = launchUiStateProducer(dataSource) { loadData() }
 * Text(result.value)
 * Button(onClick = onRefresh) { Text("Refresh" }
 * Button(onClick = onClearError) { Text("Clear loading error") }
 * ```
 *
 * @param result (state) the current result of this producer in a state object
 * @param onRefresh (event) triggers a refresh of this producer
 * @param onClearError (event) clear any error values returned by this producer, useful for
 * transient error displays.
 */
data class ProducerResult<T>(
    val result: State<T>,
    val onRefresh: () -> Unit,
    val onClearError: () -> Unit
)

/**
 * Launch a coroutine to create refreshable [UiState] from a suspending producer.
 *
 * [Producer] is any object that has a suspending method that returns [Result]. In the [block] call
 * the suspending method that produces a single value. The result of this call will be returned
 * along with an event to refresh (or call [block] again), and another event to clear error results.
 *
 * It is intended that you destructure the return at the call site. Here is an example usage that
 * calls dataSource.loadData() and then displays a UI based on the result.
 *
 * ```
 * val (result, onRefresh, onClearError) = launchUiStateProducer(dataSource) { loadData() }
 * Text(result.value)
 * Button(onClick = onRefresh) { Text("Refresh" }
 * Button(onClick = onClearError) { Text("Clear loading error") }
 * ```
 *
 * Repeated calls to onRefresh are conflated while a request is in progress.
 *
 * @param producer the data source to load data from
 * @param block suspending lambda that produces a single value from the data source
 * @return data state, onRefresh event, and onClearError event
 */
@Composable
fun <Producer, T> launchUiStateProducer(
    producer: Producer,
    block: suspend Producer.() -> Result<T>
): ProducerResult<UiState<T>> = launchUiStateProducer(producer, Unit, block)

/**
 * Launch a coroutine to create refreshable [UiState] from a suspending producer.
 *
 * [Producer] is any object that has a suspending method that returns [Result]. In the [block] call
 * the suspending method that produces a single value. The result of this call will be returned
 * along with an event to refresh (or call [block] again), and another event to clear error results.
 *
 * It is intended that you destructure the return at the call site. Here is an example usage that
 * calls dataSource.loadData(resourceId) and then displays a UI based on the result.
 *
 * ```
 * val (result, onRefresh, onClearError) = launchUiStateProducer(dataSource, resourceId) {
 *     loadData(resourceId)
 * }
 * Text(result.value)
 * Button(onClick = onRefresh) { Text("Refresh" }
 * Button(onClick = onClearError) { Text("Clear loading error") }
 * ```
 *
 * Repeated calls to onRefresh are conflated while a request is in progress.
 *
 * @param producer the data source to load data from
 * @param key any argument used by production lambda, such as a resource ID
 * @param block suspending lambda that produces a single value from the data source
 * @return data state, onRefresh event, and onClearError event
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun <Producer, T> launchUiStateProducer(
    producer: Producer,
    key: Any?,
    block: suspend Producer.() -> Result<T>
): ProducerResult<UiState<T>> {
    val producerState = remember { mutableStateOf(UiState<T>(loading = true)) }

    // posting to this channel will trigger a single refresh
    val refreshChannel = remember { Channel<Unit>(Channel.CONFLATED) }

    // event for caller to trigger a refresh
    val refresh: () -> Unit = { refreshChannel.offer(Unit) }

    // event for caller to clear any errors on the current result (useful for transient error
    // displays)
    val clearError: () -> Unit = {
        producerState.value = producerState.value.copy(exception = null)
    }

    // whenever Producer or v1 changes, launch a new coroutine to call block() and refresh whenever
    // the onRefresh callback is called
    launchInComposition(producer, key) {
        // whenever the coroutine restarts, clear the previous result immediately as they are no
        // longer valid
        producerState.value = UiState(loading = true)
        // force a refresh on coroutine restart
        refreshChannel.send(Unit)
        // whenever a refresh is triggered, call block again. This for-loop will suspend when
        // refreshChannel is empty, and resume when the next value is offered or sent to the
        // channel.

        // This for-loop will loop until the [launchInComposition] coroutine is cancelled.
        for (refreshEvent in refreshChannel) {
            producerState.value = producerState.value.copy(loading = true)
            producerState.value = producerState.value.copyWithResult(producer.block())
        }
    }
    return ProducerResult(producerState, refresh, clearError)
}
