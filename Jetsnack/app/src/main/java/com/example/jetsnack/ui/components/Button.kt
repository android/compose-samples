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

@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMediaQueryApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.LoadingState
import com.example.jetsnack.ui.theme.loadingState
import com.example.jetsnack.ui.utils.UiMediaScopeWrapper

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    loadingState: LoadingState = LoadingState.Loaded,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
        it.loadingState = loadingState
    }
    Row(
        modifier = modifier
            .semantics(
                properties = {
                    role = Role.Button
                },
            )
            .styleable(styleState, JetsnackTheme.styles.buttonStyle, style)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            ),
        content = content,
        verticalAlignment = Alignment.CenterVertically,
    )
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreview() {
    JetsnackTheme {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(modifier = Modifier.padding(32.dp)) {
            Box(Modifier.focusTarget())
            UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
                var loadingState by remember {
                    mutableStateOf(LoadingState.Loaded)
                }
                val style = remember {
                   Style {
                        width(130.dp)
                        textAlign(TextAlign.Center)
                    }
                }
                Button(
                    onClick = {
                        loadingState = when (loadingState) {
                            LoadingState.Loaded -> LoadingState.Loading
                            LoadingState.Loading -> LoadingState.Error
                            LoadingState.Error -> LoadingState.Loaded
                        }
                    },
                    enabled = true,
                    loadingState = loadingState,
                    style = style
                ) {
                    val text = when (loadingState) {
                        LoadingState.Loading -> "Loading..."
                        LoadingState.Error -> "Error"
                        LoadingState.Loaded -> "Add to cart"
                    }
                    Text(text = text)
                }
            }
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewLoading() {
    JetsnackTheme {
        Box(Modifier.focusTarget())
        UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
            Button(
                onClick = {},
                enabled = true,
                loadingState = LoadingState.Loading,
            ) {
                Text(text = "Loading...")
            }
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewError() {
    JetsnackTheme {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
            Button(
                onClick = {},
                enabled = true,
                loadingState = LoadingState.Error,
            ) {
                Text(text = "Error")
            }
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewDisabled() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        Button(
            onClick = {},
            enabled = false,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewPressed() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewHovered() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(HoverInteraction.Enter())
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewFocused() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(FocusInteraction.Focus())
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewHoveredFocused() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(HoverInteraction.Enter())
            interactionSource.emit(FocusInteraction.Focus())
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewPressedFocused() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(FocusInteraction.Focus())
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

// TODO this state is broken visually
@Preview
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreviewPressedHovered() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
            interactionSource.emit(HoverInteraction.Enter())
        }
        Button(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Composable
private fun ButtonDesktopPreview() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Physical, UiMediaScope.PointerPrecision.Fine) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        Button(
            onClick = {},
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview
@Composable
private fun ButtonDesktopPreviewDisabled() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Physical, UiMediaScope.PointerPrecision.Fine) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        Button(
            onClick = {},
            enabled = false,
        ) {
            Text(text = "Add to cart")
        }
    }
}

@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        // Previews are by default focused, to ensure accuracy, focus a box first
        Box(Modifier.focusTarget())
        Button(
            onClick = {},
            style = {
                shape(RoundedCornerShape(4.dp))
            },
        ) {
            Text(text = "Add to cart")
        }
    }
}
