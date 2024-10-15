/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.CategorySelectionList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PodcastCard
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
fun SearchScreen(
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val uiState by searchScreenViewModel.uiStateFlow.collectAsState()

    when (val s = uiState) {
        SearchScreenUiState.Loading -> Loading(modifier = modifier)
        is SearchScreenUiState.Ready -> Ready(
            keyword = s.keyword,
            categorySelectionList = s.categorySelectionList,
            onKeywordInput = searchScreenViewModel::setKeyword,
            onCategorySelected = searchScreenViewModel::addCategoryToSelectedCategoryList,
            onCategoryUnselected = searchScreenViewModel::removeCategoryFromSelectedCategoryList,
            modifier = modifier
        )

        is SearchScreenUiState.HasResult -> HasResult(
            keyword = s.keyword,
            categorySelectionList = s.categorySelectionList,
            podcastList = s.result,
            onKeywordInput = searchScreenViewModel::setKeyword,
            onCategorySelected = searchScreenViewModel::addCategoryToSelectedCategoryList,
            onCategoryUnselected = searchScreenViewModel::removeCategoryFromSelectedCategoryList,
            onPodcastSelected = onPodcastSelected,
            modifier = modifier,
        )
    }
}

@Composable
private fun Ready(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Controls(
        keyword = keyword,
        categorySelectionList = categorySelectionList,
        onKeywordInput = onKeywordInput,
        onCategorySelected = onCategorySelected,
        onCategoryUnselected = onCategoryUnselected,
        modifier = modifier,
        toRequestFocus = true
    )
}

@Composable
private fun HasResult(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    podcastList: PodcastList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchResult(
        podcastList = podcastList,
        onPodcastSelected = onPodcastSelected,
        header = {
            Controls(
                keyword = keyword,
                categorySelectionList = categorySelectionList,
                onKeywordInput = onKeywordInput,
                onCategorySelected = onCategorySelected,
                onCategoryUnselected = onCategoryUnselected,
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Controls(
    keyword: String,
    categorySelectionList: CategorySelectionList,
    onKeywordInput: (String) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    toRequestFocus: Boolean = false
) {
    LaunchedEffect(toRequestFocus) {
        if (toRequestFocus) {
            focusRequester.requestFocus()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
        modifier = modifier
    ) {
        KeywordInput(
            keyword = keyword,
            onKeywordInput = onKeywordInput,
        )
        CategorySelection(
            categorySelectionList = categorySelectionList,
            onCategorySelected = onCategorySelected,
            onCategoryUnselected = onCategoryUnselected,
            modifier = Modifier
                .focusRestorer()
                .focusRequester(focusRequester)
        )
    }
}

@Composable
private fun KeywordInput(
    keyword: String,
    onKeywordInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
    BasicTextField(
        value = keyword,
        onValueChange = onKeywordInput,
        textStyle = textStyle,
        cursorBrush = cursorBrush,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(percent = 50)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.label_search),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    innerTextField()
                }
            }
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CategorySelection(
    categorySelectionList: CategorySelectionList,
    onCategorySelected: (CategoryInfo) -> Unit,
    onCategoryUnselected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.chip),
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.chip),
    ) {
        categorySelectionList.forEach {
            FilterChip(
                selected = it.isSelected,
                onClick = {
                    if (it.isSelected) {
                        onCategoryUnselected(it.categoryInfo)
                    } else {
                        onCategorySelected(it.categoryInfo)
                    }
                }
            ) {
                Text(text = it.categoryInfo.name)
            }
        }
    }
}

@Composable
private fun SearchResult(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.podcastRow),
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.podcastRow),
        modifier = modifier,
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            header()
        }
        items(podcastList) {
            PodcastCard(podcastInfo = it, onClick = { onPodcastSelected(it) })
        }
    }
}
