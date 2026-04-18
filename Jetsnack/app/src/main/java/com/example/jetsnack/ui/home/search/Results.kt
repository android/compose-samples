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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.Button
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.Surface
import com.example.jetsnack.ui.components.Text
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.colors
import com.example.jetsnack.ui.theme.typography
import com.example.jetsnack.ui.utils.formatPrice

@Composable
fun SearchResults(searchResults: List<Snack>, onSnackClick: (Long, String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = {
                textStyle(typography.titleLarge)
                contentColor(colors.textPrimary)
            },
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id, "search") }
            .padding(horizontal = 24.dp),
    ) {
        if (showDivider) {
            JetsnackDivider(
                Modifier.align(Alignment.TopCenter),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            SnackImage(
                imageRes = snack.imageRes,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                Text(
                    text = snack.name,
                    style = {
                        textStyle(typography.titleMedium)
                        contentColor(colors.textSecondary)
                    },
                )
                Text(
                    text = snack.tagline,
                    style = {
                        textStyle(typography.bodyLarge)
                        contentColor(colors.textHelp)
                    },
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formatPrice(snack.price),
                    style = {
                        textStyle(typography.titleMedium)
                        contentColor(colors.textPrimary)
                    },
                )
            }
            Button(
                onClick = { /* todo */ },
                style = {
                    shape(CircleShape)
                    contentPadding(0.dp)
                },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(R.string.label_add),
                )
            }
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
            style = {
                textStyle(typography.titleMedium)
                textAlign(TextAlign.Center)
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.search_no_matches_retry),
            style = {
                textStyle(typography.bodyMedium)
                textAlign(TextAlign.Center)
            },
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
        Surface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { _, _ -> },
                showDivider = false,
            )
        }
    }
}
