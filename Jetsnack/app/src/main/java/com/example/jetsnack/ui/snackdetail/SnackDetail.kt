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

package com.example.jetsnack.ui.snackdetail

import androidx.compose.Composable
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Constraints
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
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
import androidx.ui.layout.preferredWidthIn
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AddCircleOutline
import androidx.ui.material.icons.outlined.ArrowBack
import androidx.ui.material.icons.outlined.RemoveCircleOutline
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import androidx.ui.unit.sp
import androidx.ui.util.lerp
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackGradientTintedIconButton
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.horizontalGradientBackground
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Neutral8
import com.example.jetsnack.ui.utils.systemBarPadding
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlin.math.max
import kotlin.math.min

private val BottomBarHeight = 56.dp
private val TitleHeight = 128.dp
private val GradientScroll = 180.dp
private val ImageOverlap = 115.dp
private val MinTitleOffset = 56.dp
private val MinImageOffset = 12.dp
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll
private val ExpandedImageSize = 300.dp
private val CollapsedImageSize = 150.dp
private val HzPadding = Modifier.padding(horizontal = 24.dp)

@Composable
fun SnackDetail(
    snackId: Long,
    upPress: () -> Unit
) {
    val snack = remember(snackId) { SnackRepo.getSnack(snackId) }
    val related = remember(snackId) { SnackRepo.getRelated(snackId) }

    Stack(Modifier.fillMaxSize()) {
        val scroll = ScrollerPosition()
        Header()
        Body(related, scroll)
        Title(snack, scroll.value)
        Image(snack.imageUrl, scroll.value)
        Up(upPress)
        CartBottomBar(modifier = Modifier.gravity(Alignment.BottomCenter))
    }
}

@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .preferredHeight(280.dp)
            .fillMaxWidth()
            .horizontalGradientBackground(JetsnackTheme.colors.interactivePrimary)
    )
}

@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .systemBarPadding(top = true)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .preferredSize(36.dp)
            .drawBackground(
                color = Neutral8.copy(alpha = 0.32f),
                shape = CircleShape
            )
    ) {
        Icon(
            asset = Icons.Outlined.ArrowBack,
            tint = JetsnackTheme.colors.iconInteractive
        )
    }
}

@Composable
private fun Body(
    related: List<SnackCollection>,
    scroll: ScrollerPosition
) {
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarPadding(top = true)
                .preferredHeight(MinTitleOffset)
        )
        VerticalScroller(scroll) {
            Spacer(Modifier.preferredHeight(GradientScroll))
            JetsnackSurface(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.preferredHeight(ImageOverlap))
                    Spacer(Modifier.preferredHeight(TitleHeight))

                    Spacer(Modifier.preferredHeight(16.dp))
                    Text(
                        text = stringResource(R.string.detail_header),
                        style = MaterialTheme.typography.overline,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Text(
                        text = stringResource(R.string.detail_placeholder),
                        style = MaterialTheme.typography.body1,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.preferredHeight(40.dp))
                    Text(
                        text = stringResource(R.string.ingredients),
                        style = MaterialTheme.typography.overline,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Text(
                        text = stringResource(R.string.ingredients_list),
                        style = MaterialTheme.typography.body1,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.preferredHeight(16.dp))
                    JetsnackDivider()

                    related.forEach { snackCollection ->
                        key(snackCollection.id) {
                            SnackCollection(
                                snackCollection = snackCollection,
                                onSnackClick = { },
                                highlight = false
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .padding(bottom = BottomBarHeight)
                            .systemBarPadding(bottom = true)
                            .preferredHeight(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(snack: Snack, scroll: Float) {
    val maxOffset = with(DensityAmbient.current) { MaxTitleOffset.toPx() }
    val minOffset = with(DensityAmbient.current) { MinTitleOffset.toPx() }
    val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .preferredHeightIn(minHeight = TitleHeight)
            .systemBarPadding(top = true)
            .drawLayer(translationY = offset)
            .drawBackground(color = JetsnackTheme.colors.uiBackground)
    ) {
        Spacer(Modifier.preferredHeight(16.dp))
        Text(
            text = snack.name,
            style = MaterialTheme.typography.h4,
            color = JetsnackTheme.colors.textSecondary,
            modifier = HzPadding
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 20.sp,
            color = JetsnackTheme.colors.textHelp,
            modifier = HzPadding
        )
        Spacer(Modifier.preferredHeight(4.dp))
        Text(
            text = "$12.99",
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = HzPadding
        )

        Spacer(Modifier.preferredHeight(8.dp))
        JetsnackDivider()
    }
}

@Composable
private fun Image(
    imageUrl: String,
    scroll: Float
) {
    val collapseRange = with(DensityAmbient.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFraction = (scroll / collapseRange).coerceIn(0f, 1f)

    CollapsingImageLayout(
        collapseFraction = collapseFraction,
        modifier = HzPadding + Modifier.systemBarPadding(top = true)
    ) {
        JetsnackSurface(
            color = Color.LightGray,
            shape = CircleShape,
            modifier = Modifier.fillMaxSize()
        ) {
            CoilImage(
                data = imageUrl,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFraction: Float,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        children = image
    ) { measurables, constraints, _ ->
        check(measurables.size == 1)

        val imageMaxSize = min(ExpandedImageSize.toIntPx(), constraints.maxWidth)
        val imageMinSize = max(CollapsedImageSize.toIntPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(MinTitleOffset, MinImageOffset, collapseFraction).toIntPx()
        val imageX = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.place(imageX, imageY)
        }
    }
}

@Composable
private fun CartBottomBar(modifier: Modifier = Modifier) {
    val (count, updateCount) = state { 1 }
    JetsnackSurface(modifier) {
        Column {
            JetsnackDivider()
            Row(
                verticalGravity = Alignment.CenterVertically,
                modifier = Modifier
                    .systemBarPadding(bottom = true)
                    + HzPadding
                    .preferredHeightIn(minHeight = BottomBarHeight)
            ) {
                Text(
                    text = stringResource(R.string.quantity),
                    style = MaterialTheme.typography.subtitle1,
                    color = JetsnackTheme.colors.textSecondary
                )
                JetsnackGradientTintedIconButton(
                    asset = Icons.Outlined.RemoveCircleOutline,
                    onClick = { if (count > 0) updateCount(count - 1) }
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 18.sp,
                    color = JetsnackTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.preferredWidthIn(minWidth = 24.dp)
                )
                JetsnackGradientTintedIconButton(
                    asset = Icons.Outlined.AddCircleOutline,
                    onClick = { updateCount(count + 1) }
                )
                Spacer(Modifier.preferredWidth(16.dp))
                JetsnackButton(
                    onClick = { /* todo */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.add_to_cart),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview("Snack Detail")
@Composable
private fun SnackDetailPreview() {
    JetsnackTheme {
        SnackDetail(
            snackId = 1L,
            upPress = { }
        )
    }
}

@Preview("Snack Detail • Dark")
@Composable
private fun SnackDetailDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        SnackDetail(
            snackId = 1L,
            upPress = { }
        )
    }
}
