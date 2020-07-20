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

package com.example.jetsnack.ui.home

import androidx.animation.AnimationBuilder
import androidx.animation.PhysicsBuilder
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.core.os.ConfigurationCompat
import androidx.ui.animation.AnimatedFloatModel
import androidx.ui.animation.Crossfade
import androidx.ui.animation.animate
import androidx.ui.animation.animatedFloat
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.Layout
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.Placeable
import androidx.ui.core.TransformOrigin
import androidx.ui.core.clip
import androidx.ui.core.drawLayer
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.selection.selectable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AccountCircle
import androidx.ui.material.icons.outlined.Home
import androidx.ui.material.icons.outlined.Search
import androidx.ui.material.icons.outlined.ShoppingCart
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.util.lerp
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.systemBarPadding

@Composable
fun Home() {
    val (currentSection, setCurrentSection) = state { HomeSections.Feed }
    val navItems = HomeSections.values().toList()
    JetsnackScaffold(
        bottomBar = {
            JetsnackBottomNav(
                currentSection = currentSection,
                onSectionSelected = setCurrentSection,
                items = navItems
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        Crossfade(currentSection) { section ->
            when (section) {
                HomeSections.Feed -> Feed(
                    onSnackClick = { /* todo */ },
                    modifier = modifier
                )
                HomeSections.Search -> Search(modifier)
                HomeSections.Cart -> Cart(modifier)
                HomeSections.Profile -> Profile(modifier)
            }
        }
    }
}

@Composable
private fun JetsnackBottomNav(
    currentSection: HomeSections,
    onSectionSelected: (HomeSections) -> Unit,
    items: List<HomeSections>,
    color: Color = JetsnackTheme.colors.iconPrimary,
    contentColor: Color = JetsnackTheme.colors.iconInteractive
) {
    JetsnackSurface(
        color = color,
        contentColor = contentColor
    ) {
        val animBuilder = remember {
            PhysicsBuilder<Float>(
                // Determined experimentally
                stiffness = 800f,
                dampingRatio = 0.8f
            )
        }
        JetsnackBottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = items.size,
            indicator = { JetsnackBottomNavIndicator() },
            animBuilder = animBuilder,
            modifier = Modifier.systemBarPadding(bottom = true)
        ) {
            items.forEach { section ->
                val selected = section == currentSection
                val tint = animate(if (selected) {
                    JetsnackTheme.colors.iconInteractive
                } else {
                    JetsnackTheme.colors.iconInteractiveInactive
                })

                JetsnackBottomNavigationItem(
                    icon = {
                        Icon(
                            asset = section.icon,
                            tint = tint
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(section.title).toUpperCase(
                                ConfigurationCompat.getLocales(
                                    ConfigurationAmbient.current
                                ).get(0)
                            ),
                            color = tint,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { onSectionSelected(section) },
                    animBuilder = animBuilder,
                    modifier = BottomNavigationItemPadding
                        .clip(BottomNavIndicatorShape)
                )
            }
        }
    }
}

@Composable
private fun JetsnackBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animBuilder: AnimationBuilder<Float>,
    indicator: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val clock = AnimationClockAmbient.current
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            AnimatedFloatModel(if (i == selectedIndex) 1f else 0f, clock)
        }
    }

    // When selection changes, animate the selection fractions
    onCommit(selectedIndex) {
        selectionFractions.forEachIndexed { index, selectionFraction ->
            val target = if (index == selectedIndex) 1f else 0f
            if (selectionFraction.targetValue != target) {
                selectionFraction.animateTo(target, animBuilder)
            }
        }
    }
    // Animate the position of the indicator
    val indicatorLeft = animatedFloat(0f)

    Layout(
        modifier = modifier.preferredHeight(BottomNavHeight),
        children = {
            content()
            Box(Modifier.tag("indicator"), children = indicator)
        }
    ) { measurables, constraints, _ ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = constraints.maxWidth - (itemCount - 1) * unselectedWidth
        val indicatorMeasurable = measurables.first { it.tag == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        // Animate the indicator position
        val targetIndicatorLeft = selectedIndex * unselectedWidth.toFloat()
        if (indicatorLeft.targetValue != targetIndicatorLeft) {
            indicatorLeft.animateTo(targetIndicatorLeft, animBuilder)
        }

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxBy { it.height }?.height ?: 0
        ) {
            indicatorPlaceable.place(x = indicatorLeft.value.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.place(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun JetsnackBottomNavigationItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animBuilder: AnimationBuilder<Float>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        gravity = ContentGravity.Center
    ) {
        // Animate the icon/text positions within the item based on selection
        val animationProgress = animate(if (selected) 1f else 0f, animBuilder)
        JetsnackBottomNavItemLayout(
            icon = icon,
            text = text,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun JetsnackBottomNavItemLayout(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
    Layout(
        children = {
            Box(Modifier.tag("icon"), children = icon)
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .tag("text")
                    .drawLayer(
                        alpha = animationProgress,
                        scaleX = scale,
                        scaleY = scale,
                        transformOrigin = BottomNavLabelTransformOrigin
                    ),
                paddingStart = TextIconSpacing,
                children = text
            )
        }
    ) { measurables, constraints, _ ->
        val iconPlaceable = measurables.first { it.tag == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.tag == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureScope.MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.place(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.place(textX.toInt(), textY)
        }
    }
}

@Composable
private fun JetsnackBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = JetsnackTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            + BottomNavigationItemPadding
            .drawBorder(strokeWidth, color, shape)
    )
}

private val TextIconSpacing = 4.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

private enum class HomeSections(
    @StringRes val title: Int,
    val icon: VectorAsset
) {
    Feed(R.string.home_feed, Icons.Outlined.Home),
    Search(R.string.home_search, Icons.Outlined.Search),
    Cart(R.string.home_cart, Icons.Outlined.ShoppingCart),
    Profile(R.string.home_profile, Icons.Outlined.AccountCircle)
}

@Preview
@Composable
private fun JsetsnackBottomNavPreview() {
    JetsnackTheme {
        JetsnackBottomNav(
            currentSection = HomeSections.Feed,
            onSectionSelected = { },
            items = HomeSections.values().toList()
        )
    }
}
