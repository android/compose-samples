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

typealias RepositoryCall<T> = ((Result<T>) -> Unit) -> Unit

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: Exception) : UiState<Nothing>()
}

/**
 * UiState factory that updates its internal state with the [com.example.jetnews.data.Result]
 * of a repository called as a parameter.
 *
 * To load asynchronous data, effects are better pattern than using @Model classes since
 * effects are Compose lifecycle aware.
 */
@Composable
fun <T> uiStateFrom(
    repositoryCall: RepositoryCall<T>
): UiState<T> {
    var state: UiState<T> by state<UiState<T>> { UiState.Loading }

    // Whenever this effect is used in a composable function, it'll load data from the repository
    // when the first composition is applied
    onActive {
        repositoryCall { result ->
            state = when (result) {
                is Result.Success -> UiState.Success(result.data)
                is Result.Error -> UiState.Error(result.exception)
            }
        }
    }

    return state
}

/**
 * Helper function that loads data from a repository call. Only use in Previews!
 */
@Composable
fun <T> previewDataFrom(
    repositoryCall: RepositoryCall<T>
): T {
    var state: T? = null
    repositoryCall { result ->
        state = (result as Result.Success).data
    }
    return state!!
}
