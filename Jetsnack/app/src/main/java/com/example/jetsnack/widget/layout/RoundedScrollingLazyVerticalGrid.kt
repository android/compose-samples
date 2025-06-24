/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.jetsnack.widget.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.LazyVerticalGridScope
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import kotlin.math.ceil

/**
 * A variant of [LazyVerticalGrid] that clips its scrolling content to a rounded rectangle.
 *
 * @param gridCells the number of columns in the grid.
 * @param modifier the modifier to apply to this layout
 * @param horizontalAlignment the horizontal alignment applied to the items.
 * @param content a block which describes the content. Inside this block you can use methods like
 * [LazyVerticalGridScope.item] to add a single item or [LazyVerticalGridScope.items] to add a list
 * of items. If the item has more than one top-level child, they will be automatically wrapped in a
 * Box.
 * @see LazyVerticalGrid
 */
@Composable
fun RoundedScrollingLazyVerticalGrid(
    gridCells: GridCells,
    modifier: GlanceModifier = GlanceModifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyVerticalGridScope.() -> Unit,
) {
    Box(
        modifier = GlanceModifier
            .cornerRadius(16.dp) // to present a rounded scrolling experience
            .then(modifier),
    ) {
        LazyVerticalGrid(
            gridCells = gridCells,
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }
}

/**
 * A variant of [LazyVerticalGrid] that clips its scrolling content to a rounded rectangle and
 * spaces out each item in the grid with a default 8.dp spacing.
 *
 * @param gridCells number of columns in the grid
 * @param items the list of data items to be displayed in the list
 * @param itemContentProvider a lambda function that provides item content without any spacing
 * @param modifier the modifier to apply to this layout
 * @param horizontalAlignment the horizontal alignment applied to the items.
 * @param cellSpacing horizontal and vertical spacing between cells
 * @see LazyVerticalGrid
 */
@Composable
fun <T> RoundedScrollingLazyVerticalGrid(
    gridCells: Int,
    items: List<T>,
    itemContentProvider: @Composable (item: T) -> Unit,
    modifier: GlanceModifier = GlanceModifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    cellSpacing: Dp = 12.dp,
) {
    val numRows = ceil(items.size.toDouble() / gridCells).toInt()

    // Cell spacing is achieved by allocating equal amount of padding to each cell. Cells on edge
    // apply it completely to inner sides, while cells not on edge apply it evenly on sides.
    val perCellHorizontalPadding = (cellSpacing * (gridCells - 1)) / gridCells
    val perCellVerticalPadding = (cellSpacing * (numRows - 1)) / numRows

    RoundedScrollingLazyVerticalGrid(
        gridCells = GridCells.Fixed(gridCells),
        horizontalAlignment = horizontalAlignment,
        modifier = modifier,
    ) {
        itemsIndexed(items) { index, item ->
            val row = index / gridCells
            val column = index % gridCells

            val cellTopPadding = when (row) {
                0 -> 0.dp
                numRows - 1 -> perCellVerticalPadding
                else -> perCellVerticalPadding / 2
            }

            val cellBottomPadding = when (row) {
                0 -> perCellVerticalPadding
                numRows - 1 -> 0.dp
                else -> perCellVerticalPadding / 2
            }

            val cellStartPadding = when (column) {
                0 -> 0.dp
                gridCells - 1 -> perCellHorizontalPadding
                else -> perCellHorizontalPadding / 2
            }

            val cellEndPadding = when (column) {
                0 -> perCellHorizontalPadding
                gridCells - 1 -> 0.dp
                else -> perCellHorizontalPadding / 2
            }

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = cellStartPadding,
                        end = cellEndPadding,
                        top = cellTopPadding,
                        bottom = cellBottomPadding,
                    ),
            ) {
                itemContentProvider(item)
            }
        }
    }
}
