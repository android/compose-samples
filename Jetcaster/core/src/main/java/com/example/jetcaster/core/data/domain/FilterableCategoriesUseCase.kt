package com.example.jetcaster.core.data.domain

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.model.FilterableCategory
import com.example.jetcaster.core.data.repository.CategoryStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach

/**
 * Use case for categories that can be used to filter podcasts.
 */
class FilterableCategoriesUseCase(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(
        selectedCategoryStateFlow: MutableStateFlow<Category?>
    ): Flow<List<FilterableCategory>> =
        combine(
            categoryStore.categoriesSortedByPodcastCount()
                .onEach { categories ->
                    // If we haven't got a selected category yet, select the first
                    if (categories.isNotEmpty() && selectedCategoryStateFlow.value == null) {
                        selectedCategoryStateFlow.value = categories[0]
                    }
                },
            selectedCategoryStateFlow
        ) { categories, selectedCategory ->
            categories.map { c ->
                FilterableCategory(c, c == selectedCategory)
            }
        }

}
