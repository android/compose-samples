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

@file:OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalFoundationStyleApi::class,
    ExperimentalMediaQueryApi::class
)

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.ui.FilterSharedElementKey
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.shapes
import com.example.jetsnack.ui.theme.typography
import com.example.jetsnack.ui.utils.JetsnackThemeWrapper
import com.example.jetsnack.ui.utils.UiMediaScopeWrapper

@Composable
fun FilterBar(
    filters: List<Filter>,
    onShowFilters: () -> Unit,
    filterScreenVisible: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = modifier.heightIn(min = 56.dp),
        ) {
            item {
                AnimatedVisibility(visible = !filterScreenVisible) {
                    IconButton(
                        onClick = onShowFilters,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(FilterSharedElementKey),
                                animatedVisibilityScope = this@AnimatedVisibility,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                            ),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_filter_list),
                            tint = JetsnackTheme.colors.iconPrimary,
                            contentDescription = stringResource(R.string.label_filters),
                            modifier = Modifier
                                .styleable(null) {
                                    contentPaddingHorizontal(2.dp)
                                    minHeight(32.dp)
                                    border(3.dp, Brush.linearGradient(colors.interactiveSecondary))
                                    shape(RoundedCornerShape(50))
                                },
                        )
                    }
                }
            }
            items(filters) { filter ->
                FilterChip(
                    filter = filter,
                    style = Style {
                        shape(shapes.small)
                    },
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: Style = Style,
) {

    val (selected, setSelected) = filter.enabled
    val styleState = rememberUpdatedStyleState(
        interactionSource,
        {
            it.isSelected = selected
        },
    )

    Surface(
        modifier = modifier
            .toggleable(
                value = selected,
                onValueChange = setSelected,
                interactionSource = interactionSource,
                indication = null,
            ),
        style = JetsnackTheme.styles.filterChipStyle then style,
        styleState = styleState,
    ) {
        Text(
            text = filter.name,
            style = {
                textStyleWithFontFamilyFix(typography.labelSmall)
            },
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 6.dp,
            ),
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterDisabledPreview() {
    JetsnackThemeWrapper {
        FilterChip(Filter(name = "Demo", enabled = false), Modifier.padding(4.dp))
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterEnabledPreview() {
    JetsnackThemeWrapper {
        FilterChip(Filter(name = "Demo", enabled = true))
    }
}

@Preview("hovered focused")
@Preview("dark theme hovered focused", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterPreviewHoveredFocused() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, pointerPrecision = UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(HoverInteraction.Enter())
            interactionSource.emit(FocusInteraction.Focus())
        }
        JetsnackThemeWrapper {
            FilterChip(
                filter = Filter(name = "Demo"),
                interactionSource = interactionSource,
            )
        }
    }
}

@Preview("pressed focused")
@Preview("dark theme pressed focused", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterPreviewPressedFocused() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, pointerPrecision = UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(FocusInteraction.Focus())
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
        }
        JetsnackThemeWrapper {
            FilterChip(
                filter = Filter(name = "Demo"),
                interactionSource = interactionSource,
            )
        }
    }
}

@Preview("pressed hovered")
@Preview("dark theme pressed hovered", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterPreviewPressedHovered() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, pointerPrecision = UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
            interactionSource.emit(HoverInteraction.Enter())
        }
        JetsnackThemeWrapper {
            FilterChip(
                filter = Filter(name = "Demo"),
                interactionSource = interactionSource,
            )
        }
    }
}

@Preview("pressed")
@Preview("dark theme pressed", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterPreviewPressed() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, pointerPrecision = UiMediaScope.PointerPrecision.Blunt) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
        }
        JetsnackThemeWrapper {
            FilterChip(
                filter = Filter(name = "Demo"),
                interactionSource = interactionSource,
            )
        }
    }
}
