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

package com.example.jetcaster.util

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.createAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.invalidate
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientAnimationClock

/**
 * [ItemSwitcher] allows to switch between two layouts with a transition defined by
 * [transitionDefinition].
 *
 * @param current is a key representing your current layout state. Every time you change a key
 * the animation will be triggered. The [content] called with the old key will be animated out while
 * the [content] called with the new key will be animated in.
 * @param transitionDefinition is a [TransitionDefinition] using [ItemTransitionState] as
 * the state type.
 * @param modifier Modifier to be applied to the animation container.
 */
@Composable
fun <T> ItemSwitcher(
    current: T,
    transitionDefinition: TransitionDefinition<ItemTransitionState>,
    modifier: Modifier = Modifier,
    content: @Composable (T, TransitionState) -> Unit
) {
    val state = remember { ItemTransitionInnerState<T>() }

    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()

        keys.mapTo(state.items) { key ->
            ItemTransitionItem(key) { children ->
                val clock = AmbientAnimationClock.current.asDisposableClock()
                val visible = key == current

                val anim = remember(clock, transitionDefinition) {
                    transitionDefinition.createAnimation(
                        clock = clock,
                        initState = when {
                            visible -> ItemTransitionState.BecomingVisible
                            else -> ItemTransitionState.Visible
                        }
                    )
                }

                onCommit(visible) {
                    anim.onStateChangeFinished = { _ ->
                        if (key == state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key != state.current }
                            state.invalidate()
                        }
                    }
                    anim.onUpdate = { state.invalidate() }

                    val targetState = when {
                        visible -> ItemTransitionState.Visible
                        else -> ItemTransitionState.BecomingNotVisible
                    }
                    anim.toState(targetState)
                }

                children(anim)
            }
        }
    }
    Box(modifier) {
        state.invalidate = invalidate
        state.items.forEach { (item, transition) ->
            key(item) {
                transition { transitionState ->
                    content(item, transitionState)
                }
            }
        }
    }
}

enum class ItemTransitionState {
    Visible, BecomingNotVisible, BecomingVisible,
}

private class ItemTransitionInnerState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<ItemTransitionItem<T>>()
    var invalidate: () -> Unit = { }
}

private data class ItemTransitionItem<T>(
    val key: T,
    val content: ItemTransitionContent
)

private typealias ItemTransitionContent = @Composable (children: @Composable (TransitionState) -> Unit) -> Unit
