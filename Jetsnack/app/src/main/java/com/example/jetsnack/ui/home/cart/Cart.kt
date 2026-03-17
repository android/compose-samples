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

package com.example.jetsnack.ui.home.cart

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.jetsnack.ui.components.Text
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.textStyleWithFontFamilyFix
import com.example.jetsnack.ui.home.DestinationBar
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.typography
import com.example.jetsnack.ui.utils.formatPrice

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
    com.example.jetsnack.ui.components.Surface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp)),
                ),
            )
            Text(
                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
                style = {
                    textStyleWithFontFamilyFix(typography.titleLarge)
                    contentColor(colors.brand)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight(),
            )
        }
        items(orderLines, key = { it.snack.id }) { orderLine ->
            SwipeDismissItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = itemAnimationSpecFade,
                    fadeOutSpec = itemAnimationSpecFade,
                    placementSpec = itemPlacementSpec,
                ),
                background = { progress ->
                    SwipeDismissItemBackground(progress)
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
                highlight = false,
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}
private val swipeDismissProgressKey = StyleStateKey(0f)
private var MutableStyleState.swipeDismissProgress
    get() = this[swipeDismissProgressKey]
    set(value) { this[swipeDismissProgressKey] = value }

// this feels odd, i dont think im doing this right... I want to get access to the progress or do some
// conditionals on the progress, maybe we need some built in conditionals or just an easier sample to copy from.

private fun StyleScope.swipeDismissProgressGreaterThan(amount: Float, value: Style) {
    state(swipeDismissProgressKey, value) { key, state -> state[key] > amount }
}
private fun StyleScope.swipeDismissProgressLessThan(amount: Float, value: Style) {
    state(swipeDismissProgressKey, value) { key, state -> state[key] < amount }
}

// todo migrate to custom style state
@Composable
private fun SwipeDismissItemBackground(progress: Float) {
    Column(
        modifier = Modifier
            .background(JetsnackTheme.colors.uiBackground)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center,
    ) {

        Box(
            Modifier
                .fillMaxWidth(progress),
        ) {
            val mutableInteractionSource = remember {
                MutableInteractionSource()
            }
            val styleState = rememberUpdatedStyleState(mutableInteractionSource, {
                it.swipeDismissProgress = progress
            })
            com.example.jetsnack.ui.components.Surface(
                style = {
                    contentPadding(0.dp)
                    swipeDismissProgressLessThan(0.5f) {
                        animate {
                            contentPadding(4.dp)
                        }
                    }
                    // TODO how do I query the progress here, this doesn't seem to work
                    // its not cropping to the shape properly not sure if its a layering/placement of style parameter vs progress just not working
                    shape(RoundedCornerShape((1 - progress) * 100))
                    background(colors.error)
                    clip(true)
                },
                styleState = styleState,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Icon must be visible while in this width range
                    if (progress in 0.125f..0.475f) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete_forever),
                            modifier = Modifier
                                .styleable(styleState, {
                                    // Icon alpha decreases as it is about to disappear
                                    alpha(1f)
                                    swipeDismissProgressGreaterThan(0.4f) {
                                        animate {
                                            alpha(0.5f)
                                        }
                                    }
                                })
                                .size(32.dp),
                            tint = JetsnackTheme.colors.uiBackground,
                            contentDescription = null,
                        )
                    }
                    if (progress > 0.5f) {
                        Text(
                            text = stringResource(id = R.string.remove_item),
                            style = {
                                textStyleWithFontFamilyFix(typography.titleMedium)
                                contentColor(colors.uiBackground)
                                textAlign(TextAlign.Center)
                                alpha(0.5f)
                                /* Text opacity increases as the text is supposed to appear in
                                          the screen*/
                                swipeDismissProgressGreaterThan(0.5f) {
                                    animate {
                                        alpha(1f)
                                    }
                                }
                            }
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
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SnackImage(
                imageRes = snack.imageRes,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(100.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = snack.name,
                        style = {
                            textStyleWithFontFamilyFix(typography.titleMedium)
                            contentColor(colors.textSecondary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, end = 16.dp),
                    )
                    IconButton(
                        onClick = { removeSnack(snack.id) },
                        modifier = Modifier.padding(top = 12.dp),
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
                        textStyleWithFontFamilyFix(typography.bodyLarge)
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
                contentColor(colors.brand)
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
        modifier.background(
            JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
        ),
    ) {

        JetsnackDivider()
        Row {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { /* todo */ },
                style = {
                    shape(RectangleShape)
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f),
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
    JetsnackTheme {
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
