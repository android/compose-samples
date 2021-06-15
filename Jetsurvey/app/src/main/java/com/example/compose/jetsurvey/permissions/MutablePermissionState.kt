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

package com.example.compose.jetsurvey.permissions

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Creates a [MutablePermissionState] that is remembered across compositions.
 *
 * It's recommended that apps exercise the permissions workflow as described in the
 * [documentation](https://developer.android.com/training/permissions/requesting#workflow_for_requesting_permissions).
 *
 * @param permission the permission to control and observe.
 */
@Composable
internal fun rememberMutablePermissionState(
    permission: String
): MutablePermissionState {
    val context = LocalContext.current
    val permissionState = remember(permission) {
        MutablePermissionState(permission, context, context.findActivity())
    }

    // Refresh the permission status when the lifecycle is resumed
    PermissionLifecycleCheckerEffect(permissionState)

    // Remember RequestPermission launcher and assign it to permissionState
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.hasPermission = it
        permissionState.permissionRequested = true
    }
    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

/**
 * A mutable state object that can be used to control and observe permission status changes.
 *
 * In most cases, this will be created via [rememberMutablePermissionState].
 *
 * @param permission the permission to control and observe.
 * @param context to check the status of the [permission].
 * @param activity to check if the user should be presented with a rationale for [permission].
 */
@Stable
internal class MutablePermissionState(
    override val permission: String,
    private val context: Context,
    private val activity: Activity
) : PermissionState {

    private var _hasPermission by mutableStateOf(context.checkPermission(permission))
    override var hasPermission: Boolean
        internal set(value) {
            _hasPermission = value
            refreshShouldShowRationale()
        }
        get() = _hasPermission

    override var shouldShowRationale: Boolean by mutableStateOf(
        activity.shouldShowRationale(permission)
    )
        private set

    override var permissionRequested: Boolean by mutableStateOf(false)

    override fun launchPermissionRequest() {
        launcher?.launch(
            permission
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    internal fun refreshHasPermission() {
        hasPermission = context.checkPermission(permission)
    }

    private fun refreshShouldShowRationale() {
        shouldShowRationale = activity.shouldShowRationale(permission)
    }
}
