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

package com.example.compose.jetchat.conversation

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.compose.jetchat.R

private val bottomOffset = DpPropKey("Bottom Offset")

private val definition = transitionDefinition<Visibility> {
    state(Visibility.GONE) {
        this[bottomOffset] = (-32).dp
    }
    state(Visibility.VISIBLE) {
        this[bottomOffset] = 32.dp
    }
}

private enum class Visibility {
    VISIBLE,
    GONE
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Show Jump to Bottom button
    val transition = transition(
        definition = definition,
        toState = if (enabled) Visibility.VISIBLE else Visibility.GONE
    )
    if (transition[bottomOffset] > 0.dp) {
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
            contentColor = MaterialTheme.colors.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -transition[bottomOffset])
                .preferredHeight(36.dp)
        )
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
