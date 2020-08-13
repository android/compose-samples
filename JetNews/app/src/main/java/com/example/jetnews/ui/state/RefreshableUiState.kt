/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.state

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.setValue
import androidx.compose.state
import com.example.jetnews.data.Result

/**
 * Model for UiStates that can refresh. The Success state contains whether there's data loading
 * apart from the current data to display on the screen. The error state also returns previously
 * loaded data apart from the error.
 */
sealed class RefreshableUiState<out T> {
    data class Success<out T>(val data: T?, val loading: Boolean) : RefreshableUiState<T>()
    data class Error<out T>(val exception: Exception, val previousData: T?) :
        RefreshableUiState<T>()
}

/**
 * Handler that allows getting the current RefreshableUiState and refresh its content.
 */
data class RefreshableUiStateHandler<out T>(
    val state: RefreshableUiState<T>,
    val refreshAction: () -> Unit
)

/**
 * Refreshable UiState factory that updates its internal state with the
 * [com.example.jetnews.data.Result] of a callback passed as a parameter.
 *
 * To load asynchronous data, effects are better pattern than using @Model classes since
 * effects are Compose lifecycle aware.
 */
@Composable
fun <T> refreshableUiStateFrom(
    repositoryCall: RepositoryCall<T>
): RefreshableUiStateHandler<T> {

    var state: RefreshableUiState<T> by state<RefreshableUiState<T>> {
        RefreshableUiState.Success(data = null, loading = true)
    }

    val refresh = {
        state = RefreshableUiState.Success(data = state.currentData, loading = true)
        repositoryCall { result ->
            state = when (result) {
                is Result.Success -> RefreshableUiState.Success(
                    data = result.data, loading = false
                )
                is Result.Error -> RefreshableUiState.Error(
                    exception = result.exception, previousData = state.currentData
                )
            }
        }
    }

    onActive {
        refresh()
    }

    return RefreshableUiStateHandler(state, refresh)
}

val <T> RefreshableUiState<T>.loading: Boolean
    get() = this is RefreshableUiState.Success && this.loading && this.data == null

val <T> RefreshableUiState<T>.refreshing: Boolean
    get() = this is RefreshableUiState.Success && this.loading && this.data != null

val <T> RefreshableUiState<T>.currentData: T?
    get() = when (this) {
        is RefreshableUiState.Success -> this.data
        is RefreshableUiState.Error -> this.previousData
    }
