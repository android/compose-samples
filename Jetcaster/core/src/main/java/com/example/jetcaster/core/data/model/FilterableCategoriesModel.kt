package com.example.jetcaster.core.data.model

import com.example.jetcaster.core.data.database.model.Category

/**
 * Model holding a list of categories and a selected category in the collection
 */
data class FilterableCategoriesModel(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)
