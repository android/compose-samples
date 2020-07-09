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

import androidx.compose.Composable
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.animation.animate
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.foundation.selection.toggleable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
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
import androidx.ui.material.Divider
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.ArrowForward
import androidx.ui.material.icons.outlined.ExpandMore
import androidx.ui.material.icons.rounded.FilterList
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.example.jetsnack.model.CollectionType
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.filters
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.JetsnackCard
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.fadeInGradientBorder
import com.example.jetsnack.ui.components.gradientBackground
import com.example.jetsnack.ui.components.gradientBorder
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.systemBarPadding
import dev.chrisbanes.accompanist.coil.CoilImage

private val HighlightCardWidth = 170.dp
private val HighlightCardPadding = 16.dp

@Composable
fun Home(onSnackClick: (Long) -> Unit) {
    val data = remember { SnackRepo.getSnacks() }
    val filters = remember { filters }
    JetsnackSurface(modifier = Modifier.fillMaxSize()) {
        Stack(modifier = Modifier.systemBarPadding(horizontal = true)) {
            SnackCollectionList(data, filters, onSnackClick)
            DestinationBar()
        }
    }
}

@Composable
private fun SnackCollectionList(
    data: List<SnackCollection>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalScroller(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .systemBarPadding(top = true)
                .preferredHeight(56.dp)
        )
        FilterBar(filters)
        data.forEachIndexed { index, snackCollection ->
            if (index > 0) {
                Divider(
                    color = JetsnackTheme.colors.uiBorder,
                    thickness = 2.dp
                )
            }
            key(snackCollection.id) {
                SnackCollection(snackCollection, index, onSnackClick)
            }
        }
        Spacer(
            modifier = Modifier
                .systemBarPadding(bottom = true)
                .preferredHeight(8.dp)
        )
    }
}

@Composable
private fun SnackCollection(
    snackCollection: SnackCollection,
    index: Int,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
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
        if (snackCollection.type == CollectionType.Highlight) {
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
        Column(modifier = Modifier.clickable(onClick = { onSnackClick(snack.id) })) {
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
                        .gradientBackground(gradient, gradientWidth, gradientOffset)
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
private fun SnackImage(
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

@Composable
private fun DestinationBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.systemBarPadding(top = true)) {
        TopAppBar(
            backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
            contentColor = JetsnackTheme.colors.textSecondary,
            elevation = 0.dp
        ) {
            Text(
                text = "Delivery to 1600 Amphitheater Way",
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .gravity(Alignment.CenterVertically)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.gravity(Alignment.CenterVertically)
            ) {
                Icon(
                    asset = Icons.Outlined.ExpandMore,
                    tint = JetsnackTheme.colors.brand
                )
            }
        }
        Divider(color = JetsnackTheme.colors.uiBorder)
    }
}

@Composable
private fun FilterBar(filters: List<Filter>) {
    HorizontalScroller(modifier = Modifier.preferredHeightIn(minHeight = 56.dp)) {
        Spacer(Modifier.preferredWidth(8.dp))
        IconButton(
            onClick = { /* todo */ },
            modifier = Modifier.gravity(Alignment.CenterVertically)
        ) {
            Icon(
                asset = Icons.Rounded.FilterList,
                tint = JetsnackTheme.colors.brand,
                modifier = Modifier.gradientBorder(
                    colors = JetsnackTheme.colors.interactiveSecondary,
                    shape = CircleShape
                )
            )
        }

        filters.forEach { filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            Spacer(Modifier.preferredWidth(8.dp))
        }
    }
}

@Composable
private fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected, setSelected) = filter.enabled
    val backgroundColor =
        animate(if (selected) JetsnackTheme.colors.brand else JetsnackTheme.colors.uiBackground)
    val border = Modifier.fadeInGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor = animate(
        if (selected) JetsnackTheme.colors.textInteractive else JetsnackTheme.colors.textSecondary
    )
    JetsnackSurface(
        modifier = modifier.preferredHeight(28.dp) + border,
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        Box(
            modifier = Modifier.toggleable(
                value = selected,
                onValueChange = setSelected
            )
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}

@Preview("Home")
@Composable
fun HomePreview() {
    JetsnackTheme {
        Home(onSnackClick = { })
    }
}

@Preview("Home • Dark Theme")
@Composable
fun HomeDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        Home(onSnackClick = { })
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
            gradientWidth = 500f,
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
            gradientWidth = 500f,
            scroll = 0f
        )
    }
}
