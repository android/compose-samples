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

package com.example.jetsnack.ui.home.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.SearchCategory
import com.example.jetsnack.model.SearchCategoryCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.Text
import com.example.jetsnack.ui.components.textStyleWithFontFamilyFix
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.typography
import kotlin.math.max

@Composable
fun SearchCategories(categories: List<SearchCategoryCollection>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        categories.forEachIndexed { index, collection ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = collection.name,
                    style = {
                        textStyleWithFontFamilyFix(typography.titleLarge)
                        contentColor(colors.textSecondary)
                    },
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .wrapContentHeight(),
                )
            }
            val borderColor = when (index % 2) {
                0 -> Color(0xFF8BDEBE)
                else -> Color(0xffFFC8A4)
            }
            items(collection.categories, key = {
                it.name
            }) { category ->
                SearchCategory(
                    category = category,
                    modifier = Modifier.padding(4.dp),
                    style = {
                        border(1.dp, borderColor)
                    },
                )
            }
        }
    }
}

private val MinImageSize = 134.dp
private val CategoryShape = RoundedCornerShape(24.dp)
private const val CategoryTextProportion = 0.55f

@Composable
private fun SearchCategory(category: SearchCategory, modifier: Modifier = Modifier, style: Style = Style) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    Layout(
        modifier = modifier
            .aspectRatio(1.85f)
            .styleable(
                styleState,
                Style {
                    shape(CategoryShape)
                    clip(true)
                    background(colors.uiBackground)
                    dropShadow(Shadow(color = Color(0xffE5E1E2), radius = 8.dp))
                    textStyleWithFontFamilyFix(typography.titleSmall)
                    contentColor(colors.textPrimary)
                } then style,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
            ) { /* todo */ },
        content = {
            Text(
                text = category.name,
                modifier = Modifier
                    .padding(4.dp)
                    .padding(start = 8.dp),
            )
            SnackImage(
                imageRes = category.imageRes,
                contentDescription = null,
                style = {
                    shape(RoundedCornerShape(topStartPercent = 48))
                },
                modifier = Modifier.fillMaxSize(),
            )
        },
    ) { measurables, constraints ->
        // Text given a set proportion of width (which is determined by the aspect ratio)
        val textWidth = (constraints.maxWidth * CategoryTextProportion).toInt()
        val textPlaceable = measurables[0].measure(Constraints.fixedWidth(textWidth))

        // Image is sized to the larger of height of item, or a minimum value
        // i.e. may appear larger than item (but clipped to the item bounds)
        val imageSize = max(MinImageSize.roundToPx(), constraints.maxHeight)
        val imagePlaceable = measurables[1].measure(Constraints.fixed(imageSize, imageSize))
        layout(
            width = constraints.maxWidth,
            height = constraints.minHeight,
        ) {
            textPlaceable.placeRelative(
                x = 0,
                y = (constraints.maxHeight - textPlaceable.height) / 2, // centered
            )
            imagePlaceable.placeRelative(
                // image is placed to end of text i.e. will overflow to the end (but be clipped)
                x = textWidth,
                y = (constraints.maxHeight - imagePlaceable.height) / 2, // centered
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchCategoryPreview() {
    JetsnackTheme {
        SearchCategory(
            category = SearchCategory(
                name = "Desserts",
                imageRes = R.drawable.desserts,
            ),
            style = {
                border(1.dp, Color(0xFF8BDEBE))
            },
        )
    }
}
