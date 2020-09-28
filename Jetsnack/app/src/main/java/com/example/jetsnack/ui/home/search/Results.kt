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

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ChainStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun SearchResults(
    searchResults: List<Snack>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit
) {
    Column {
        FilterBar(filters)
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        LazyColumnForIndexed(searchResults) { index, snack ->
            SearchResult(snack, onSnackClick, index != 0)
        }
    }
}

@Composable
private fun SearchResult(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .padding(horizontal = 24.dp)
    ) {
        val (divider, image, name, tag, priceSpacer, price, add) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        if (showDivider) {
            JetsnackDivider(
                Modifier.constrainAs(divider) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(parent.top)
                }
            )
        }
        SnackImage(
            imageUrl = snack.imageUrl,
            modifier = Modifier
                .preferredSize(100.dp)
                .constrainAs(image) {
                    linkTo(
                        top = parent.top,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp
                    )
                    linkTo(start = parent.start, end = name.start)
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
                    end = add.start,
                    endMargin = 16.dp
                )
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(tag) {
                linkTo(start = name.start, end = name.end)
                width = Dimension.fillToConstraints
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
                linkTo(start = name.start, end = name.end)
                width = Dimension.fillToConstraints
            }
        )
        JetsnackButton(
            onClick = { /* todo */ },
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .preferredSize(36.dp)
                .constrainAs(add) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = name.end, end = parent.end)
                }
        ) {
            Icon(Icons.Outlined.Add)
        }
    }
}

@Composable
fun NoResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {
        Image(vectorResource(R.drawable.empty_state_search))
        Spacer(Modifier.preferredHeight(24.dp))
        Text(
            text = stringResource(R.string.search_no_matches, query),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.preferredHeight(16.dp))
        Text(
            text = stringResource(R.string.search_no_matches_retry),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview("Search Result")
@Composable
private fun SearchResultPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { },
                showDivider = false
            )
        }
    }
}

@Preview("Search Result • Dark")
@Composable
private fun SearchResultDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        JetsnackSurface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { },
                showDivider = false
            )
        }
    }
}
