package com.example.jetcaster.core.data.model

import com.example.jetcaster.core.data.database.model.Category

/**
 * Category model that can be selected.
 */
data class FilterableCategory(
    val category: Category,
    val isSelected: Boolean
)
