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

package com.example.jetsnack.ui.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ChainStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.LastBaseline
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.statusBarsHeight

@Composable
fun Cart(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartSnacks = remember { SnackRepo.getCart() }
    val inspiredByCart = remember { SnackRepo.getInspiredByCart() }
    Cart(cartSnacks, inspiredByCart, onSnackClick, modifier)
}

@Composable
fun Cart(
    cartSnacks: List<Snack>,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box {
            CartContent(
                cartSnacks = cartSnacks,
                inspiredByCart = inspiredByCart,
                onSnackClick = onSnackClick,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            DestinationBar(modifier = Modifier.align(Alignment.TopCenter))
            CheckoutBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun CartContent(
    cartSnacks: List<Snack>,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            Spacer(Modifier.statusBarsHeight(additional = 56.dp))
            Text(
                text = stringResource(R.string.cart_order_header, cartSnacks.size),
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .preferredHeightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )
        }
        items(cartSnacks) { cartSnack ->
            CartItem(cartSnack, onSnackClick)
        }
        item {
            SummaryItem()
        }
        item {
            SnackCollection(
                snackCollection = inspiredByCart,
                onSnackClick = onSnackClick,
                highlight = false
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}

@Composable
fun CartItem(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val (count, updateCount) = remember { mutableStateOf(1) }
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .padding(horizontal = 24.dp)
    ) {
        val (divider, image, name, tag, priceSpacer, price, remove, quantity) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        SnackImage(
            imageUrl = snack.imageUrl,
            modifier = Modifier
                .preferredSize(100.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = remove.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        IconButton(
            onClick = { /* todo */ },
            modifier = Modifier.constrainAs(remove) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }.padding(top = 12.dp)
        ) {
            Icon(asset = Icons.Filled.Close, tint = JetsnackTheme.colors.brand)
        }
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = parent.end,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            Modifier
                .preferredHeight(8.dp)
                .constrainAs(priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                }
        )
        Text(
            text = "$12.99",
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(price) {
                linkTo(
                    start = image.end,
                    end = quantity.start,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        QuantitySelector(
            count = count,
            decreaseItemCount = { if (count > 0) updateCount(count - 1) },
            increaseItemCount = { updateCount(count + 1) },
            modifier = Modifier.constrainAs(quantity) {
                baseline.linkTo(price.baseline)
                end.linkTo(parent.end)
            }
        )
        JetsnackDivider(
            Modifier.constrainAs(divider) {
                linkTo(start = parent.start, end = parent.end)
                top.linkTo(parent.bottom)
            }
        )
    }
}

// TODO: Hoist state instead of using hard-coded total price
@Composable
fun SummaryItem(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.cart_summary_header),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .preferredHeightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.cart_subtotal_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = "\$27.47",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_shipping_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = "\$3.69",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_total_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(Alignment.End)
                    .alignBy(LastBaseline)
            )
            Text(
                text = "\$31.16",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        JetsnackDivider()
    }
}

@Composable
private fun CheckoutBar(modifier: Modifier = Modifier) {
    Column(
        modifier.background(
            JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {
        JetsnackDivider()
        Row {
            Spacer(Modifier.weight(1f))
            JetsnackButton(
                onClick = { /* todo */ },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.cart_checkout)
                )
            }
        }
    }
}

@Preview("Cart")
@Composable
fun CartPreview() {
    JetsnackTheme {
        Cart(onSnackClick = { })
    }
}

@Preview("Cart • Dark Theme")
@Composable
fun CartDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        Cart(onSnackClick = { })
    }
}
