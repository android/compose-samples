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

package com.example.jetcaster.core.data.domain

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.model.FilterableCategory
import com.example.jetcaster.core.data.repository.CategoryStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Use case for categories that can be used to filter podcasts.
 */
class FilterableCategoriesUseCase(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(
        selectedCategory: Category?,
        onEmptySelectedCategory: (Category) -> Unit
    ): Flow<List<FilterableCategory>> =
        categoryStore.categoriesSortedByPodcastCount()
            .onEach { categories ->
                // If we haven't got a selected category yet, select the first
                if (categories.isNotEmpty() && selectedCategory == null) {
                    onEmptySelectedCategory(categories[0])
                }
            }
            .map { categories ->
                categories.map { c ->
                    FilterableCategory(c, c == selectedCategory)
                }
            }
        }
}
