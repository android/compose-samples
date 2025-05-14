/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetcaster.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.PlaceholderDefaults
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import com.google.android.horologist.annotations.ExperimentalHorologistApi

/**
 * A placeholder chip to be displayed while the contents of the [Button] is being loaded.
 */
@OptIn(ExperimentalWearMaterialApi::class)
@ExperimentalHorologistApi
@Composable
fun PlaceholderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    placeholderState: PlaceholderState = rememberPlaceholderState(false),
    secondaryLabel: Boolean = true,
    icon: Boolean = true
) {
    var labelText by remember { mutableStateOf("") }
    var imageVector: ImageVector? by remember { mutableStateOf(null) }
    val buttonPlaceholderState = rememberPlaceholderState (
        labelText.isNotEmpty() && imageVector != null
    )
    FilledTonalButton(
        onClick = { onClick },
        enabled = true,
        label = {
            Column {
                Box(
                    modifier = modifier
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .height(12.dp)
                        .placeholder(placeholderState),
                )
                Spacer(Modifier.size(8.dp))
            }
        },
        secondaryLabel = if (secondaryLabel) {
            {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(end = 30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(12.dp)
                        .placeholder(placeholderState),
                )
            }
        } else {
            null
        },
        icon = if (icon) {
            {
                Box(
                    modifier =
                    modifier.size(ButtonDefaults.IconSize).placeholder(buttonPlaceholderState)
                )
            }
        } else {
            null
        },
        modifier = modifier.fillMaxWidth()
            .placeholderShimmer(buttonPlaceholderState)
    )
}
