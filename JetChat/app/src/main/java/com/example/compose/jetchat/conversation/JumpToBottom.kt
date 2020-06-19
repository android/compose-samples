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

package com.example.compose.jetchat.conversation

import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.DpPropKey
import androidx.ui.animation.Transition
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.offset
import androidx.ui.layout.preferredHeight
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowDownward
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.Blue200

private val bottomPadding = DpPropKey()

private val definition = transitionDefinition {
    state(Visibility.GONE) {
        this[bottomPadding] = 0.dp
    }
    state(Visibility.VISIBLE) {
        this[bottomPadding] = 32.dp
    }
}

private enum class Visibility {
    VISIBLE,
    GONE
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
// TODO: when a new message is added, the button shows up while the scroll is animating.
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    // This is used to prevent glitches. Only animate when the state changes.
    val isShown = state { false }

    if (enabled) {
        // Show Jump to Bottom button
        Transition(
            definition = definition,
            // Disable transition if there's no state change
            initState = if (isShown.value) Visibility.VISIBLE else Visibility.GONE,
            toState = Visibility.VISIBLE,
            onStateChangeFinished = {
                isShown.value = true
            }
        ) { transition ->

            val contentColor = if (MaterialTheme.colors.isLight) {
                MaterialTheme.colors.primary
            } else {
                Blue200
            }
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        asset = Icons.Filled.ArrowDownward,
                        modifier = Modifier.preferredHeight(18.dp)
                    )
                },
                text = {
                    Text(text = stringResource(id = R.string.jumpBottom))
                },
                onClick = onClicked,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = contentColor,
                modifier = modifier
                    .offset(x = 0.dp, y = -transition[bottomPadding])
                    .preferredHeight(36.dp)
            )
        }
    } else {
        isShown.value = false
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
