package com.example.jetcaster.core.data.model

import com.example.jetcaster.core.data.database.model.Category

data class CategoryInfo(
    val id: Long,
    val name: String
)

fun Category.asExternalModel() =
    CategoryInfo(
        id = id,
        name = name
    )
