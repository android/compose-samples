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

package com.example.jetsnack.ui.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.ArrowForward
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.example.jetsnack.model.CollectionType
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.theme.JetsnackTheme
import dev.chrisbanes.accompanist.coil.CoilImage

private val HighlightCardWidth = 170.dp
private val HighlightCardPadding = 16.dp

// The Cards show a gradient which spans 3 cards and scrolls with parallax.
@Composable
private val gradientWidth
    get() = with(DensityAmbient.current) {
        (3 * (HighlightCardWidth + HighlightCardPadding).toPx())
    }

@Composable
fun SnackCollection(
    snackCollection: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    highlight: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            verticalGravity = Alignment.CenterVertically,
            modifier = Modifier
                .preferredHeightIn(minHeight = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = snackCollection.name,
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.gravity(Alignment.CenterVertically)
            ) {
                Icon(
                    asset = Icons.Outlined.ArrowForward,
                    tint = JetsnackTheme.colors.brand
                )
            }
        }
        if (highlight && snackCollection.type == CollectionType.Highlight) {
            HighlightedSnacks(index, snackCollection.snacks, onSnackClick)
        } else {
            Snacks(snackCollection.snacks, onSnackClick)
        }
    }
}

@Composable
private fun HighlightedSnacks(
    index: Int,
    snacks: List<Snack>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = ScrollerPosition()
    val gradient = when (index % 2) {
        0 -> JetsnackTheme.colors.interactivePrimary
        else -> JetsnackTheme.colors.interactiveSecondary
    }
    // The Cards show a gradient which spans 3 cards and scrolls with parallax.
    val gradientWidth = with(DensityAmbient.current) {
        (3 * (HighlightCardWidth + HighlightCardPadding).toPx())
    }
    HorizontalScroller(
        scrollerPosition = scroll,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.preferredWidth(24.dp))
        snacks.forEachIndexed { index, snack ->
            HighlightSnackItem(snack, onSnackClick, index, gradient, gradientWidth, scroll.value)
            Spacer(modifier = Modifier.preferredWidth(16.dp))
        }
    }
}

@Composable
private fun Snacks(
    snacks: List<Snack>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalScroller(modifier = modifier) {
        Spacer(modifier = Modifier.preferredWidth(16.dp))
        snacks.forEach { snack ->
            SnackItem(snack, onSnackClick)
            Spacer(modifier = Modifier.preferredWidth(8.dp))
        }
    }
}

@Composable
fun SnackItem(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Column(
            horizontalGravity = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = { onSnackClick(snack.id) })
                .padding(8.dp)
        ) {
            SnackImage(
                imageUrl = snack.imageUrl,
                elevation = 4.dp
            )
            Text(
                text = snack.name,
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
private fun HighlightSnackItem(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    index: Int,
    gradient: List<Color>,
    gradientWidth: Float,
    scroll: Float,
    modifier: Modifier = Modifier
) {
    val left = index * with(DensityAmbient.current) {
        (HighlightCardWidth + HighlightCardPadding).toPx()
    }
    JetsnackCard(
        elevation = 4.dp,
        modifier = modifier
            .preferredSize(
                width = 170.dp,
                height = 250.dp
            )
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = { onSnackClick(snack.id) })
                .fillMaxSize()
        ) {
            Stack(
                modifier = Modifier
                    .preferredHeight(160.dp)
                    .fillMaxWidth()
            ) {
                val gradientOffset = left - (scroll / 3f)
                Box(
                    modifier = Modifier
                        .preferredHeight(100.dp)
                        .fillMaxWidth()
                        .offsetGradientBackground(gradient, gradientWidth, gradientOffset)
                )
                SnackImage(
                    imageUrl = snack.imageUrl,
                    modifier = Modifier.gravity(Alignment.BottomCenter)
                )
            }
            Spacer(modifier = Modifier.preferredHeight(8.dp))
            Text(
                text = snack.name,
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.preferredHeight(4.dp))
            Text(
                text = snack.tagline,
                style = MaterialTheme.typography.body1,
                color = JetsnackTheme.colors.textHelp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun SnackImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    JetsnackSurface(
        color = Color.LightGray,
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier.preferredSize(120.dp)
    ) {
        CoilImage(
            data = imageUrl,
            contentScale = ContentScale.Crop
        )
    }
}

@Preview("Highlight snack card")
@Composable
fun SnackCardPreview() {
    JetsnackTheme {
        val snack = snacks.first()
        HighlightSnackItem(
            snack = snack,
            onSnackClick = { },
            index = 0,
            gradient = JetsnackTheme.colors.gradient6_1,
            gradientWidth = gradientWidth,
            scroll = 0f
        )
    }
}

@Preview("Highlight snack card • Dark Theme")
@Composable
fun SnackCardDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        val snack = snacks.first()
        HighlightSnackItem(
            snack = snack,
            onSnackClick = { },
            index = 0,
            gradient = JetsnackTheme.colors.gradient6_1,
            gradientWidth = gradientWidth,
            scroll = 0f
        )
    }
}
