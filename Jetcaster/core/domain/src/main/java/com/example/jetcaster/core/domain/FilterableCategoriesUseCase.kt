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

package com.example.jetcaster.core.domain

import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.asExternalModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for categories that can be used to filter podcasts.
 */
class FilterableCategoriesUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    /**
     * Created a [FilterableCategoriesModel] from the list of categories in [categoryStore].
     * @param selectedCategory the currently selected category. If null, the first category
     *        returned by the backing category list will be selected in the returned
     *        FilterableCategoriesModel
     */
    operator fun invoke(selectedCategory: CategoryInfo?): Flow<FilterableCategoriesModel> =
        categoryStore.categoriesSortedByPodcastCount()
            .map { categories ->
                FilterableCategoriesModel(
                    categories = categories.map { it.asExternalModel() },
                    selectedCategory = selectedCategory
                        ?: categories.firstOrNull()?.asExternalModel()
                )
            }
}
