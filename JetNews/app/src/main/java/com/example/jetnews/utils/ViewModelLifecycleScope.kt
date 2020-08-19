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

@file:Suppress("DEPRECATION")

package com.example.jetnews.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLifecycleObserver
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewModelStoreOwnerAmbient
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Creates a composition-based scope lifecycle for [viewModel] in compose.
 *
 * All ViewModels created in this scope will be cleared when [ViewModelLifecycleScope] leaves the
 * composition tree.
 *
 * The scoped view model store is isolated from the current [ViewModelStoreOwnerAmbient]. It will
 * not attempt to lookup existing ViewModel instances in any parent [ViewModelStoreOwnerAmbient].
 *
 * Warning: This simple implementation does not retain ViewModel instances during configuration
 * changes like rotation.
 *
 * Usage:
 *
 * ```
 * @Composable
 * fun UsesViewModelScope() = ViewModelLifecycleScope {
 *     // This instance of myViewModel will call [onCleared] as soon as this composable leaves the
 *     // composition tree.
 *     //
 *     // If this composable is added to the composition tree multiple times it will create multiple
 *     // MyViewModels, one for each instance of this composable.
 *     //
 *     // If this composable is removed and added to the composition tree, the MyViewModel will call
 *     // [onCleared] and then a new MyViewModel will be created.
 *     val myViewModel = viewModels<MyViewModel>()
 *     ...
 * }
 * ```
 *
 * @param content composable lambda that uses the new view model scope
 */
@Composable
@Deprecated(
    message = "This code is a stand-in for a pending public API that will be created after " +
        "https://issuetracker.google.com/issues/165642391 lands. If you use this code please " +
        "remove it from your codebase after that ticket is closed.",
    level = DeprecationLevel.WARNING
)
fun ViewModelLifecycleScope(content: @Composable () -> Unit) = Providers(
    ViewModelStoreOwnerAmbient provides rememberScopedViewModelStore(),
    children = content
)

/**
 * Returns a [ViewModelStoreOwner] that will match the lifecycle of the call to
 * [rememberScopedViewModelStore].
 *
 * All [ViewModel]s created in this store will be cleared when the call to
 * [rememberScopedViewModelStore] leaves the composition tree.
 *
 * Warning: This simple implementation does not retain ViewModel instances during configuration
 * changes like rotation.
 *
 * This store is independent of any other [ViewModelStoreOwner] and will not attempt to lookup
 * ViewModels created in another store.
 *
 * @see ViewModelLifecycleScope for usage example
 */
@Composable
@Deprecated(
    message = "This code is a stand-in for a pending public API that will be created after " +
        "https://issuetracker.google.com/issues/165642391 lands. If you use this code please " +
        "remove it from your codebase after that ticket is closed.",
    level = DeprecationLevel.WARNING
)
fun rememberScopedViewModelStore(): ViewModelStoreOwner = remember { ScopedViewModelStoreOwner() }

/**
 * A ViewModelStoreOwner that will automatically clear the store when removed from composition.
 *
 * Warning: This simple implementation does not retain ViewModel instances during configuration
 * changes like rotation.
 */
@Deprecated(
    message = "This code is a stand-in for a pending public API that will be created after " +
        "https://issuetracker.google.com/issues/165642391 lands. If you use this code please " +
        "remove it from your codebase after that ticket is closed.",
    level = DeprecationLevel.WARNING
)
private class ScopedViewModelStoreOwner : ViewModelStoreOwner, CompositionLifecycleObserver {
    private val viewModelStore = ViewModelStore()

    /**
     * Called when CompositionLifecycleObserver is notified that this scope is no longer remembered.
     *
     * @see [CompositionLifecycleObserver.onLeave]
     */
    override fun onLeave() {
        viewModelStore.clear()
    }

    /**
     * @see [ViewModelStoreOwner.getViewModelStore]
     */
    override fun getViewModelStore(): ViewModelStore = viewModelStore
}
