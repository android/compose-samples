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
