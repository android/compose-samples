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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.jetsnack.R
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.FilterChip
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterScreen(
    onDismiss: () -> Unit
) {
    var sortChanged by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {

        val priceFilters = remember { SnackRepo.getPriceFilters() }
        val categoryFilters = remember { SnackRepo.getCategoryFilters() }
        val lifeStyleFilters = remember { SnackRepo.getFilters() }
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null
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
                        var resetEnabled = sortChanged
                        IconButton(
                            onClick = { /* TODO: Open search */ },
                            enabled = resetEnabled
                        ) {
                            val alpha = if (resetEnabled) 1f else 0.5f

                            Text(
                                text = stringResource(id = R.string.reset),
                                style = MaterialTheme.typography.body2,
                                color = JetsnackTheme.colors.textLink,
                                modifier = Modifier.alpha(alpha)
                            )
                        }
                    },
                    backgroundColor = JetsnackTheme.colors.uiBackground
                )
            },
            backgroundColor = JetsnackTheme.colors.uiBackground,
            modifier = Modifier.padding(0.dp)
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 15.dp)
                    .fillMaxSize(),
            ) {
                FilterTitle(text = stringResource(id = R.string.sort))
                Column(Modifier.padding(bottom = 24.dp)) {
                    SortFilters(
                        onChanged = {
                            sortChanged = it
                        }
                    )
                }
                FilterTitle(text = stringResource(id = R.string.price))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    priceFilters.forEach {
                        FilterChip(
                            filter = it,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                    }
                }
                FilterTitle(text = stringResource(id = R.string.category))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 28.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        categoryFilters.forEach {
                            FilterChip(
                                filter = it,
                                modifier = Modifier.padding(end = 5.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
                Row {
                    FilterTitle(text = stringResource(id = R.string.max_calories))
                    Text(
                        text = stringResource(id = R.string.per_serving),
                        style = MaterialTheme.typography.body2,
                        color = JetsnackTheme.colors.brand,
                        modifier = Modifier.padding(top = 5.dp, start = 10.dp)
                    )
                }
                MaxCaloriesSlider()
                FilterTitle(text = stringResource(id = R.string.lifestyle))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 28.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        lifeStyleFilters.forEach {
                            FilterChip(
                                filter = it,
                                modifier = Modifier.padding(end = 5.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortFilters(onChanged: (Boolean) -> Unit) {
    val sortFilters = SnackRepo.getSortFilters()
    val defaultValue = SnackRepo.getSortDefault()
    var sortState by remember { mutableStateOf(SnackRepo.getSortDefault()) }
    sortFilters.forEach {
        SortOption(
            text = it.name,
            icon = it.icon,
            selected = sortState == it.name,
            onClickOption = {
                sortState = it.name
                onChanged(it.name != defaultValue)
            }
        )
    }
}

@Composable
fun MaxCaloriesSlider() {
    var sliderPosition by remember { mutableStateOf(0f) }

    Slider(
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
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
fun FilterTitle(text: String,) {
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
            .clickable { onClickOption() }
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
