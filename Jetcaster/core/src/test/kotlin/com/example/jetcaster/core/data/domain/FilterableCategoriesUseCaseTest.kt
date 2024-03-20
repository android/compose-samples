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
import com.example.jetcaster.core.data.repository.TestCategoryStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterableCategoriesUseCaseTest {

    private val categoriesStore = TestCategoryStore()
    private val testCategories = listOf(
        Category(1, "News"),
        Category(2, "Arts"),
        Category(4, "Technology"),
        Category(2, "TV & Film"),
    )

    val useCase = FilterableCategoriesUseCase(
        categoryStore = categoriesStore
    )

    @Test
    fun whenNoSelectedCategory_firstCategoryIsSelected() = runTest {
        val filterableCategories = useCase(MutableStateFlow(null))

        categoriesStore.setCategories(testCategories)

        assertTrue(filterableCategories.first()[0].isSelected)
    }

    @Test
    fun whenSelectedCategory_correctFilterableCategoryIsSelected() = runTest {
        val filterableCategories = useCase(MutableStateFlow(testCategories[2]))

        categoriesStore.setCategories(testCategories)

        assertTrue(filterableCategories.first()[2].isSelected)
    }
}
