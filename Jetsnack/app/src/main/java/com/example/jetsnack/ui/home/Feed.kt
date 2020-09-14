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

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope.align
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.navigationBarsPadding
import com.example.jetsnack.ui.utils.statusBarsPadding

@Composable
fun Feed(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val data = remember { SnackRepo.getSnacks() }
    val filters = remember { SnackRepo.getFilters() }
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Stack(modifier = Modifier.navigationBarsPadding(left = true, right = true)) {
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
    ScrollableColumn(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .statusBarsPadding()
                .preferredHeight(56.dp)
        )
        FilterBar(filters)
        data.forEachIndexed { index, snackCollection ->
            if (index > 0) {
                JetsnackDivider(thickness = 2.dp)
            }
            key(snackCollection.id) {
                SnackCollection(
                    snackCollection = snackCollection,
                    onSnackClick = onSnackClick,
                    index = index
                )
            }
        }
        Spacer(
            modifier = Modifier
                .navigationBarsPadding(left = false, right = false)
                .preferredHeight(8.dp)
        )
    }
}

@Composable
private fun DestinationBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.statusBarsPadding()) {
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
                    .align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    asset = Icons.Outlined.ExpandMore,
                    tint = JetsnackTheme.colors.brand
                )
            }
        }
        JetsnackDivider()
    }
}

@Preview("Home")
@Composable
fun HomePreview() {
    JetsnackTheme {
        Feed(onSnackClick = { })
    }
}

@Preview("Home • Dark Theme")
@Composable
fun HomeDarkPreview() {
    JetsnackTheme(darkTheme = true) {
        Feed(onSnackClick = { })
    }
}
