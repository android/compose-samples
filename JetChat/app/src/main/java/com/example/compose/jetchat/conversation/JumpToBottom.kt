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
import androidx.ui.animation.transition
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
    if (enabled) {
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
                .offset(x = 0.dp, y = -transition[bottomPadding])
                .preferredHeight(36.dp)
        )
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
