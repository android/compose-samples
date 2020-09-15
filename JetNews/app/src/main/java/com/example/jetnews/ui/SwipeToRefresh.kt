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

package com.example.jetnews.ui

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp

private val RefreshDistance = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val refreshDistance = with(DensityAmbient.current) { RefreshDistance.toPx() }
    val state = rememberSwipeableState(refreshingState)
    // TODO (https://issuetracker.google.com/issues/164113834): This state->event trampoline is a
    //  workaround for a bug in the SwipableState API. It should be replaced with a correct solution
    //  when that bug closes.
    onCommit(refreshingState) {
        state.animateTo(refreshingState)
    }
    // TODO (https://issuetracker.google.com/issues/164113834): Hoist state changes when bug is
    //  fixed and do this logic in the ViewModel. Currently, state.value is a duplicated source of
    //  truth of refreshingState
    onCommit(state.value) {
        if (state.value) {
            onRefresh()
        }
    }

    Stack(
        modifier = Modifier.swipeable(
            state = state,
            enabled = !state.value,
            anchors = mapOf(
                -refreshDistance to false,
                refreshDistance to true
            ),
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Vertical
        )
    ) {
        content()
        Box(Modifier.align(Alignment.TopCenter).offsetPx(y = state.offset)) {
            if (state.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }
    }
}
