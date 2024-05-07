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

package com.example.jetcaster.tv.model

import androidx.compose.runtime.Immutable
import com.example.jetcaster.core.model.CategoryInfo

data class CategorySelection(val categoryInfo: CategoryInfo, val isSelected: Boolean = false)

@Immutable
data class CategorySelectionList(
    val member: List<CategorySelection>
) : List<CategorySelection> by member
