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

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class CollapsingContentState {
    var collapsingContentHeight by mutableStateOf(0)
        internal set

    var collapsingContentOffset by mutableStateOf(0)
        internal set

    internal suspend fun animateTo(
        offset: Int,
        initialVelocity: Float = 0f,
    ): Float {
        var lastVelocity = initialVelocity
        animate(
            initialValue = collapsingContentOffset.toFloat(),
            targetValue = offset.toFloat(),
            initialVelocity = initialVelocity,
        ) { value, velocity ->
            collapsingContentOffset = value.roundToInt()
            lastVelocity = velocity
        }
        return lastVelocity
    }

}

private class CollapsingContentNestedScrollConnection(
    val state: CollapsingContentState
) : NestedScrollConnection {
    var snapOnRelease: Boolean = false
    var contentPaddingTopPx: Int = 0

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        // If the user is swiping up, handle it
        available.y < 0 -> onScroll(available)
        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        // If the user is swiping down and there's y remaining, handle it
        available.y > 0 -> onScroll(available)
        else -> Offset.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        if (!snapOnRelease) {
            return Velocity.Zero
        }
        if (state.collapsingContentOffset == 0 || state.collapsingContentOffset == scrollRange) {
            return Velocity.Zero
        }

        val targetOffset = when {
            available.y > 400f -> 0
            available.y < -400f -> scrollRange
            state.collapsingContentOffset > scrollRange / 2 -> 0
            else -> scrollRange
        }

        return Velocity(
            x = 0f,
            y = state.animateTo(offset = targetOffset, initialVelocity = available.y)
        )
    }

    private fun onScroll(available: Offset): Offset {
        val newOffset = (available.y + state.collapsingContentOffset)
            .coerceIn(scrollRange.toFloat(), 0f)
        val dragConsumed = newOffset - state.collapsingContentOffset

        state.collapsingContentOffset = newOffset.roundToInt()

        return if (dragConsumed.absoluteValue >= 0.5f) {
            // Return the consumed Y
            Offset(x = 0f, y = dragConsumed)
        } else {
            Offset.Zero
        }
    }

    private val scrollRange: Int
        get() = contentPaddingTopPx - state.collapsingContentHeight
}


@Composable
fun CollapsingContent(
    modifier: Modifier = Modifier,
    state: CollapsingContentState = remember { CollapsingContentState() },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    collapsingHeader: @Composable () -> Unit,
    snapOnRelease: Boolean = false,
    content: @Composable () -> Unit,
) {
    val contentPaddingTopPx = with(LocalDensity.current) {
        contentPadding.calculateTopPadding().roundToPx()
    }

    val nestedScrollConnection = remember(state) {
        CollapsingContentNestedScrollConnection(state)
    }.apply {
        this.snapOnRelease = snapOnRelease
        this.contentPaddingTopPx = contentPaddingTopPx
    }

    Box(
        modifier
            .clipToBounds()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            Modifier
                .onSizeChanged { state.collapsingContentHeight = it.height }
                .offset { IntOffset(x = 0, y = state.collapsingContentHeight) }
        ) {
            collapsingHeader()
        }

        Box(
            Modifier
                .matchParentSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = state.collapsingContentHeight + state.collapsingContentOffset
                    )
                }
        ) {
            content()
        }
    }
}
