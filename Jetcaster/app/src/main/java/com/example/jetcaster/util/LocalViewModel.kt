/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.util

// TODO: This is a temporary fix, delete this file and import the dependency from androidx package instead

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Returns an existing [ViewModel] or creates a new one in the scope (usually, a fragment or
 * an activity)
 *
 * The created [ViewModel] is associated with the given scope and will be retained
 * as long as the scope is alive (e.g. if it is an activity, until it is
 * finished or process is killed).
 *
 * @param key The key to use to identify the [ViewModel].
 * @return A [ViewModel] that is an instance of the given [T] type.
 */
@Suppress("MissingJvmstatic")
@Composable
public inline fun <reified T : ViewModel> viewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): T = viewModel(T::class.java, key, factory)

/**
 * Returns an existing [ViewModel] or creates a new one in the scope (usually, a fragment or
 * an activity)
 *
 * The created [ViewModel] is associated with the given scope and will be retained
 * as long as the scope is alive (e.g. if it is an activity, until it is
 * finished or process is killed).
 *
 * @param modelClass The class of the [ViewModel] to create an instance of it if it is not
 * present.
 * @param key The key to use to identify the [ViewModel].
 * @return A [ViewModel] that is an instance of the given [T] type.
 */
@Suppress("MissingJvmstatic")
@Composable
public fun <T : ViewModel> viewModel(
    modelClass: Class<T>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): T = LocalViewModelStoreOwner.current.get(modelClass, key, factory)

private fun <T : ViewModel> ViewModelStoreOwner.get(
    javaClass: Class<T>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): T {
    val provider = if (factory != null) {
        ViewModelProvider(this, factory)
    } else {
        ViewModelProvider(this)
    }
    return if (key != null) {
        provider.get(key, javaClass)
    } else {
        provider.get(javaClass)
    }
}
