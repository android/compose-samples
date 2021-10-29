/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetsnack.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
class Filter(
    val name: String,
    enabled: Boolean = false,
    val icon: ImageVector? = null
) {
    val enabled = mutableStateOf(enabled)
}
val filters = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)
val priceFilters = listOf(
    Filter(name = "$"),
    Filter(name = "$$"),
    Filter(name = "$$$"),
    Filter(name = "$$$$")
)
val sortFilters = listOf(
    Filter(name = "Android's favorite (default)", icon = Icons.Filled.Android),
    Filter(name = "Rating", icon = Icons.Filled.Star),
    Filter(name = "Alphabetical", icon = Icons.Filled.SortByAlpha)
)

val categoryFilters = listOf(
    Filter(name = "Chips & crackers"),
    Filter(name = "Fruit snacks"),
    Filter(name = "Desserts"),
    Filter(name = "Nuts")
)
val lifeStyleFilters = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)

var sortDefault = sortFilters.get(0).name
