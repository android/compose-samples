/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetsnack.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.jetsnack.R
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterScreen(
    onDismiss: () -> Unit
) {
    var sortState by remember { mutableStateOf(SnackRepo.getSortDefault()) }
    var maxCalories by remember { mutableStateOf(0f) }
    val defaultFilter = SnackRepo.getSortDefault()

    Dialog(onDismissRequest = onDismiss) {

        val priceFilters = remember { SnackRepo.getPriceFilters() }
        val categoryFilters = remember { SnackRepo.getCategoryFilters() }
        val lifeStyleFilters = remember { SnackRepo.getLifeStyleFilters() }
        JetsnackScaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(id = R.string.close)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(id = R.string.label_filters),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6
                        )
                    },
                    actions = {
                        var resetEnabled = sortState != defaultFilter
                        IconButton(
                            onClick = { /* TODO: Open search */ },
                            enabled = resetEnabled
                        ) {
                            val alpha = if (resetEnabled) {
                                ContentAlpha.high
                            } else {
                                ContentAlpha.disabled
                            }
                            CompositionLocalProvider(LocalContentAlpha provides alpha) {
                                Text(
                                    text = stringResource(id = R.string.reset),
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    },
                    backgroundColor = JetsnackTheme.colors.uiBackground
                )
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                SortFiltersSection(
                    sortState = sortState,
                    onFilterChange = { filter ->
                        sortState = filter.name
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.price),
                    filters = priceFilters
                )
                FilterChipSection(
                    title = stringResource(id = R.string.category),
                    filters = categoryFilters
                )

                MaxCalories(
                    sliderPosition = maxCalories,
                    onValueChanged = { newValue ->
                        maxCalories = newValue
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.lifestyle),
                    filters = lifeStyleFilters
                )
            }
        }
    }
}

@Composable
fun FilterChipSection(title: String, filters: List<Filter>) {
    FilterTitle(text = title)
    FlowRow(
        mainAxisAlignment = FlowMainAxisAlignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp)
            .padding(horizontal = 4.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun SortFiltersSection(sortState: String, onFilterChange: (Filter) -> Unit) {
    FilterTitle(text = stringResource(id = R.string.sort))
    Column(Modifier.padding(bottom = 24.dp)) {
        SortFilters(
            sortState = sortState,
            onChanged = onFilterChange
        )
    }
}

@Composable
fun SortFilters(
    sortFilters: List<Filter> = SnackRepo.getSortFilters(),
    sortState: String,
    onChanged: (Filter) -> Unit
) {

    sortFilters.forEach { filter ->
        SortOption(
            text = filter.name,
            icon = filter.icon,
            selected = sortState == filter.name,
            onClickOption = {
                onChanged(filter)
            }
        )
    }
}

@Composable
fun MaxCalories(sliderPosition: Float, onValueChanged: (Float) -> Unit) {
    FlowRow {
        FilterTitle(text = stringResource(id = R.string.max_calories))
        Text(
            text = stringResource(id = R.string.per_serving),
            style = MaterialTheme.typography.body2,
            color = JetsnackTheme.colors.brand,
            modifier = Modifier.padding(top = 5.dp, start = 10.dp)
        )
    }
    Slider(
        value = sliderPosition,
        onValueChange = { newValue ->
            onValueChanged(newValue)
        },
        valueRange = 0f..300f,
        steps = 5,
        modifier = Modifier
            .fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = JetsnackTheme.colors.brand,
            activeTrackColor = JetsnackTheme.colors.brand
        )
    )
}

@Composable
fun FilterTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = JetsnackTheme.colors.brand,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
@Composable
fun SortOption(
    text: String,
    icon: ImageVector?,
    onClickOption: () -> Unit,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 14.dp)
            .selectable(selected) { onClickOption() }
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = JetsnackTheme.colors.brand
            )
        }
    }
}
@Preview("filter screen")
@Composable
fun FilterScreenPreview() {
    FilterScreen(onDismiss = {})
}
