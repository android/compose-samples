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

package com.example.jetcaster.ui.home.discover

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.jetcaster.data.Category
import com.example.jetcaster.ui.home.category.PodcastCategory
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.ItemSwitcher
import com.example.jetcaster.util.ItemTransitionState

@Composable
fun Discover(
    modifier: Modifier = Modifier
) {
    val viewModel: DiscoverViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()

    val selectedCategory = viewState.selectedCategory

    if (viewState.categories.isNotEmpty() && selectedCategory != null) {
        Column(modifier) {
            Spacer(Modifier.preferredHeight(8.dp))

            // We need to keep track of the previously selected category, to determine the
            // change direction below for the transition
            var previousSelectedCategory by remember { mutableStateOf<Category?>(null) }

            PodcastCategoryTabs(
                categories = viewState.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.preferredHeight(8.dp))

            // We need to reverse the transition if the new category is to the left/start
            // of the previous category
            val reverseTransition = previousSelectedCategory?.let { p ->
                viewState.categories.indexOf(selectedCategory) < viewState.categories.indexOf(p)
            } ?: false
            val transitionOffset = with(AmbientDensity.current) { 16.dp.toPx() }

            ItemSwitcher(
                current = selectedCategory,
                transitionDefinition = getChoiceChipTransitionDefinition(
                    reverse = reverseTransition,
                    offsetPx = transitionOffset
                ),
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
            ) { category, transitionState ->
                /**
                 * TODO, need to think about how this will scroll within the outer VerticalScroller
                 */
                PodcastCategory(
                    categoryId = category.id,
                    modifier = Modifier.fillMaxSize()
                        .graphicsLayer {
                            translationX = transitionState[Offset]
                            alpha = transitionState[Alpha]
                        }
                )
            }

            onCommit(selectedCategory) {
                // Update our tracking of the previously selected category
                previousSelectedCategory = selectedCategory
            }
        }
    } else {
        // TODO: empty state
    }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@Composable
private fun PodcastCategoryTabs(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        divider = emptyContent(), /* Disable the built-in divider */
        edgePadding = Keyline1,
        indicator = emptyTabIndicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) }
            ) {
                ChoiceChipContent(
                    text = category.name,
                    selected = index == selectedIndex,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = when {
            selected -> MaterialTheme.colors.primary.copy(alpha = 0.08f)
            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        },
        contentColor = when {
            selected -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

private val Alpha = FloatPropKey("alpha")
private val Offset = FloatPropKey("offset")

@Composable
private fun getChoiceChipTransitionDefinition(
    duration: Int = 183,
    offsetPx: Float,
    reverse: Boolean = false
): TransitionDefinition<ItemTransitionState> = remember(reverse, offsetPx, duration) {
    transitionDefinition {
        state(ItemTransitionState.Visible) {
            this[Alpha] = 1f
            this[Offset] = 0f
        }
        state(ItemTransitionState.BecomingVisible) {
            this[Alpha] = 0f
            this[Offset] = if (reverse) -offsetPx else offsetPx
        }
        state(ItemTransitionState.BecomingNotVisible) {
            this[Alpha] = 0f
            this[Offset] = if (reverse) offsetPx else -offsetPx
        }

        val halfDuration = duration / 2

        transition(
            fromState = ItemTransitionState.BecomingVisible,
            toState = ItemTransitionState.Visible
        ) {
            // TODO: look at whether this can be implemented using `spring` to enable
            //  interruptions, etc
            Alpha using tween(
                durationMillis = halfDuration,
                delayMillis = halfDuration,
                easing = LinearEasing
            )
            Offset using tween(
                durationMillis = halfDuration,
                delayMillis = halfDuration,
                easing = LinearOutSlowInEasing
            )
        }

        transition(
            fromState = ItemTransitionState.Visible,
            toState = ItemTransitionState.BecomingNotVisible
        ) {
            Alpha using tween(
                durationMillis = halfDuration,
                easing = LinearEasing,
                delayMillis = DelayForContentToLoad
            )
            Offset using tween(
                durationMillis = halfDuration,
                easing = LinearOutSlowInEasing,
                delayMillis = DelayForContentToLoad
            )
        }
    }
}

/**
 * This is a hack. Compose currently has no concept of delayed transitions, something akin to
 * Fragment postponing of transitions while content loads. To workaround that for now, we
 * introduce an initial hardcoded delay of 24ms.
 */
private const val DelayForContentToLoad = 24
