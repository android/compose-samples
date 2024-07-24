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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.data.repository.PodcastsRepository
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.tv.model.CategoryInfoList
import com.example.jetcaster.tv.model.CategorySelection
import com.example.jetcaster.tv.model.CategorySelectionList
import com.example.jetcaster.tv.model.PodcastList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val podcastStore: PodcastStore,
    categoryStore: CategoryStore,
) : ViewModel() {

    private val keywordFlow = MutableStateFlow("")
    private val selectedCategoryListFlow = MutableStateFlow<List<CategoryInfo>>(emptyList())

    private val categoryInfoListFlow =
        categoryStore.categoriesSortedByPodcastCount().map(CategoryInfoList::from)

    private val searchConditionFlow =
        combine(
            keywordFlow,
            selectedCategoryListFlow,
            categoryInfoListFlow
        ) { keyword, selectedCategories, categories ->
            val selected = selectedCategories.ifEmpty {
                categories
            }
            SearchCondition(keyword, selected)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResultFlow = searchConditionFlow.flatMapLatest {
        podcastStore.searchPodcastByTitleAndCategories(
            it.keyword,
            it.selectedCategories.intoCategoryList()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val categorySelectionFlow =
        combine(
            categoryInfoListFlow,
            selectedCategoryListFlow
        ) { categoryList, selectedCategories ->
            val list = categoryList.map {
                CategorySelection(it, selectedCategories.contains(it))
            }
            CategorySelectionList(list)
        }

    val uiStateFlow =
        combine(
            keywordFlow,
            categorySelectionFlow,
            searchResultFlow
        ) { keyword, categorySelection, result ->
            val podcastList = result.map { it.asExternalModel() }
            when {
                result.isEmpty() -> SearchScreenUiState.Ready(keyword, categorySelection)
                else -> SearchScreenUiState.HasResult(keyword, categorySelection, podcastList)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchScreenUiState.Loading,
        )

    fun setKeyword(keyword: String) {
        keywordFlow.value = keyword
    }

    fun addCategoryToSelectedCategoryList(category: CategoryInfo) {
        val list = selectedCategoryListFlow.value
        if (!list.contains(category)) {
            selectedCategoryListFlow.value = list + listOf(category)
        }
    }

    fun removeCategoryFromSelectedCategoryList(category: CategoryInfo) {
        val list = selectedCategoryListFlow.value
        if (list.contains(category)) {
            val mutable = list.toMutableList()
            mutable.remove(category)
            selectedCategoryListFlow.value = mutable.toList()
        }
    }

    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts(false)
        }
    }
}

private data class SearchCondition(val keyword: String, val selectedCategories: CategoryInfoList) {
    constructor(keyword: String, categoryInfoList: List<CategoryInfo>) : this(
        keyword,
        CategoryInfoList(categoryInfoList)
    )
}

sealed interface SearchScreenUiState {
    data object Loading : SearchScreenUiState
    data class Ready(
        val keyword: String,
        val categorySelectionList: CategorySelectionList
    ) : SearchScreenUiState

    data class HasResult(
        val keyword: String,
        val categorySelectionList: CategorySelectionList,
        val result: PodcastList
    ) : SearchScreenUiState
}
