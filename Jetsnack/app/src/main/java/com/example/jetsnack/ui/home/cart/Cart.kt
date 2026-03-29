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

@file:OptIn(ExperimentalMediaQueryApi::class)

package com.example.jetsnack.ui.home.cart

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetsnack.R
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.Button
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.Text
import com.example.jetsnack.ui.components.textStyleWithFontFamilyFix
import com.example.jetsnack.ui.home.DestinationBar
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.shapes
import com.example.jetsnack.ui.theme.typography
import com.example.jetsnack.ui.utils.JetsnackThemeWrapper
import com.example.jetsnack.ui.utils.UiMediaScopeWrapper
import com.example.jetsnack.ui.utils.formatPrice
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun Cart(
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory()),
) {
    val orderLines by viewModel.orderLines.collectAsStateWithLifecycle()
    val inspiredByCart = remember { SnackRepo.getInspiredByCart() }
    Cart(
        orderLines = orderLines,
        removeSnack = viewModel::removeSnack,
        increaseItemCount = viewModel::increaseSnackCount,
        decreaseItemCount = viewModel::decreaseSnackCount,
        inspiredByCart = inspiredByCart,
        onSnackClick = onSnackClick,
        modifier = modifier,
    )
}

@Composable
fun Cart(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    com.example.jetsnack.ui.components.Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        CartContent(
            orderLines = orderLines,
            removeSnack = removeSnack,
            increaseItemCount = increaseItemCount,
            decreaseItemCount = decreaseItemCount,
            inspiredByCart = inspiredByCart,
            onSnackClick = onSnackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        DestinationBar(modifier = Modifier.align(Alignment.TopCenter))
        CheckoutBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun CartContent(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current
    val snackCountFormattedString = remember(orderLines.size, resources) {
        resources.getQuantityString(
            R.plurals.cart_order_count,
            orderLines.size, orderLines.size,
        )
    }
    val itemAnimationSpecFade = nonSpatialExpressiveSpring<Float>()
    val itemPlacementSpec = spatialExpressiveSpring<IntOffset>()
    LazyColumn(modifier) {
        item(key = "title") {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.systemBars.add(WindowInsets(top = 56.dp)),
                ),
            )
            Text(
                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
                style = {
                    textStyleWithFontFamilyFix(typography.titleLarge)
                    contentColor(colors.textPrimary)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp)
                    .wrapContentHeight(),
            )
        }
        items(orderLines, key = { it.snack.id }) { orderLine ->
            val swipeDismissState = rememberSwipeToDismissBoxState()
            SwipeDismissItem(
                dismissState = swipeDismissState,
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec,
                ),
                background = {
                    SwipeDismissItemBackground(swipeDismissState)
                },
            ) {
                CartItem(
                    orderLine = orderLine,
                    removeSnack = removeSnack,
                    increaseItemCount = increaseItemCount,
                    decreaseItemCount = decreaseItemCount,
                    onSnackClick = onSnackClick,
                )
            }
        }
        item("summary") {
            SummaryItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec,
                ),
                subtotal = orderLines.sumOf { it.snack.price * it.count },
                shippingCosts = 369,
            )
        }
        item(key = "inspiredByCart") {
            SnackCollection(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec,
                ),
                snackCollection = inspiredByCart,
                onSnackClick = onSnackClick,
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}

@Composable
private fun SwipeDismissItemBackground(swipeDismissState: SwipeToDismissBoxState) {
    BoxWithConstraints(
        modifier = Modifier
            .background(JetsnackTheme.colors.uiBackground)
            .fillMaxSize(),
    ) {
        val progress = remember(swipeDismissState, constraints.maxWidth) {
            derivedStateOf {
                if (constraints.maxWidth > 0) {
                    val offset = try {
                        swipeDismissState.requireOffset()
                    } catch ( _ : IllegalStateException) {
                        0f
                    }
                    (abs(offset) / constraints.maxWidth).coerceIn(0f, 1f)
                } else {
                    0f
                }
            }
        }.value

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            // Set 4.dp padding only if progress is less than halfway
            val padding: Dp by animateDpAsState(
                if (progress < 0.5f) 4.dp else 0.dp, label = "padding",
            )

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .padding(padding),
                shape = RoundedCornerShape(percent = ((1 - progress) * 100).roundToInt()),
                color = JetsnackTheme.colors.error,
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    // Icon must be visible while in this width range
                    if (progress in 0.125f..0.4f) {
                        // Icon alpha decreases as it is about to disappear
                        val iconAlpha: Float by animateFloatAsState(
                            if (progress > 0.4f) 0.5f else 1f, label = "icon alpha",
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete_forever),
                            modifier = Modifier
                                .size(28.dp)
                                .graphicsLayer(alpha = iconAlpha),
                            tint = JetsnackTheme.colors.uiBackground,
                            contentDescription = null,
                        )
                    }
                    /*Text opacity increases as the text is supposed to appear in
                                            the screen*/
                    val textAlpha by animateFloatAsState(
                        if (progress > 0.4f) 1f else 0.5f, label = "text alpha",
                    )
                    if (progress > 0.40f) {
                        Text(
                            text = stringResource(id = R.string.remove_item),
                            style = {
                                textStyleWithFontFamilyFix(typography.titleMedium)
                                contentColor(colors.uiBackground)
                                textAlign(TextAlign.Center)
                            },
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = textAlpha,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItem(
    orderLine: OrderLine,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    onSnackClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snack = orderLine.snack
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id, "cart") }
            .background(JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SnackImage(
                imageRes = snack.imageRes,
                contentDescription = null,
                style = {
                    shape(shapes.small)
                    externalPaddingVertical(8.dp)
                    size(100.dp)
                },
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = snack.name,
                        style = {
                            textStyleWithFontFamilyFix(typography.bodyLarge)
                            contentColor(colors.textSecondary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, end = 16.dp),
                    )
                    IconButton(
                        onClick = { removeSnack(snack.id) },
                        modifier = Modifier.offset(x = 12.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = JetsnackTheme.colors.iconSecondary,
                            contentDescription = stringResource(R.string.label_remove),
                        )
                    }
                }
                Text(
                    text = snack.tagline,
                    style = {
                        textStyleWithFontFamilyFix(typography.bodySmall)
                        contentColor(colors.textHelp)
                    },
                    modifier = Modifier.padding(end = 16.dp),
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = formatPrice(snack.price),
                        style = {
                            textStyleWithFontFamilyFix(typography.titleMedium)
                            contentColor(colors.textPrimary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .alignBy(LastBaseline),
                    )
                    QuantitySelector(
                        count = orderLine.count,
                        decreaseItemCount = { decreaseItemCount(snack.id) },
                        increaseItemCount = { increaseItemCount(snack.id) },
                        modifier = Modifier.alignBy(LastBaseline),
                    )
                }
            }
        }
        JetsnackDivider()
    }
}

@Composable
fun SummaryItem(subtotal: Long, shippingCosts: Long, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.cart_summary_header),
            style = {
                textStyleWithFontFamilyFix(typography.titleLarge)
                contentColor(colors.textPrimary)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight(),
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.cart_subtotal_label),
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline),
            )
            Text(
                text = formatPrice(subtotal),
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                },
                modifier = Modifier.alignBy(LastBaseline),
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_shipping_label),
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline),
            )
            Text(
                text = formatPrice(shippingCosts),
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                },
                modifier = Modifier.alignBy(LastBaseline),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_total_label),
                style = {
                    textStyleWithFontFamilyFix(typography.bodyLarge)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(Alignment.End)
                    .alignBy(LastBaseline),
            )
            Text(
                text = formatPrice(subtotal + shippingCosts),
                style = {
                    textStyleWithFontFamilyFix(typography.titleMedium)
                },
                modifier = Modifier.alignBy(LastBaseline),
            )
        }
        JetsnackDivider()
    }
}

@Composable
private fun CheckoutBar(modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(
                JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { },
            ),
    ) {

        JetsnackDivider()
        Row {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { /* todo */ },
                style = {
                    shape(RoundedCornerShape(4.dp))
                },
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.cart_checkout),
                    modifier = Modifier.fillMaxWidth(),
                    style = {
                        textAlign(TextAlign.Left)
                    },
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CartPreview() {
    JetsnackThemeWrapper {
        UiMediaScopeWrapper {
            Cart(
                orderLines = SnackRepo.getCart(),
                removeSnack = {},
                increaseItemCount = {},
                decreaseItemCount = {},
                inspiredByCart = SnackRepo.getInspiredByCart(),
                onSnackClick = { _, _ -> },
            )
        }
    }
}
