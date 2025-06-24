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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.formatPrice

@Composable
fun SearchResults(searchResults: List<Snack>, onSnackClick: (Long, String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = MaterialTheme.typography.titleLarge,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
        )
        LazyColumn {
            itemsIndexed(searchResults) { index, snack ->
                SearchResult(snack, onSnackClick, index != 0)
            }
        }
    }
}

@Composable
private fun SearchResult(snack: Snack, onSnackClick: (Long, String) -> Unit, showDivider: Boolean, modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id, "search") }
            .padding(horizontal = 24.dp),
    ) {
        val (divider, image, name, tag, priceSpacer, price, add) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        if (showDivider) {
            JetsnackDivider(
                Modifier.constrainAs(divider) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(parent.top)
                },
            )
        }
        SnackImage(
            imageRes = snack.imageRes,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(image) {
                    linkTo(
                        top = parent.top,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp,
                    )
                    start.linkTo(parent.start)
                },
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.titleMedium,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f,
                )
            },
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f,
                )
            },
        )
        Spacer(
            Modifier
                .height(8.dp)
                .constrainAs(priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                },
        )
        Text(
            text = formatPrice(snack.price),
            style = MaterialTheme.typography.titleMedium,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(price) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f,
                )
            },
        )
        JetsnackButton(
            onClick = { /* todo */ },
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(36.dp)
                .constrainAs(add) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    end.linkTo(parent.end)
                },
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(R.string.label_add),
            )
        }
    }
}

@Composable
fun NoResults(query: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp),
    ) {
        Image(
            painterResource(R.drawable.empty_state_search),
            contentDescription = null,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.search_no_matches, query),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.search_no_matches_retry),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchResultPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { _, _ -> },
                showDivider = false,
            )
        }
    }
}
