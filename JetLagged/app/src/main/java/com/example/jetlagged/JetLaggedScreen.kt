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

package com.example.jetlagged

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetlagged.backgrounds.movingStripesBackground
import com.example.jetlagged.data.JetLaggedHomeScreenViewModel
import com.example.jetlagged.heartrate.HeartRateCard
import com.example.jetlagged.sleep.JetLaggedHeader
import com.example.jetlagged.sleep.JetLaggedSleepGraphCard
import com.example.jetlagged.ui.theme.JetLaggedTheme
import com.example.jetlagged.ui.util.MultiDevicePreview

@OptIn(ExperimentalLayoutApi::class)
@MultiDevicePreview
@Composable
fun JetLaggedScreen(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: JetLaggedHomeScreenViewModel = viewModel(),
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.movingStripesBackground(
                stripeColor = JetLaggedTheme.extraColors.header,
                backgroundColor = MaterialTheme.colorScheme.background,
            )
        ) {
            JetLaggedHeader(
                modifier = Modifier.fillMaxWidth(),
                onDrawerClicked = onDrawerClicked
            )
        }

        val uiState =
            viewModel.uiState.collectAsStateWithLifecycle()
        val insets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
        )
        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3
        ) {
            JetLaggedSleepGraphCard(uiState.value.sleepGraphData, Modifier.widthIn(max = 600.dp))
            if (windowSizeClass == WindowWidthSizeClass.Compact) {
                AverageTimeInBedCard()
                AverageTimeAsleepCard()
            } else {
                FlowColumn {
                    AverageTimeInBedCard()
                    AverageTimeAsleepCard()
                }
            }
            if (windowSizeClass == WindowWidthSizeClass.Compact) {
                WellnessCard(
                    wellnessData = uiState.value.wellnessData,
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .heightIn(min = 200.dp)
                )
                HeartRateCard(
                    modifier = Modifier.widthIn(max = 400.dp, min = 200.dp),
                    uiState.value.heartRateData
                )
            } else {
                FlowColumn {
                    WellnessCard(
                        wellnessData = uiState.value.wellnessData,
                        modifier = Modifier
                            .widthIn(max = 400.dp)
                            .heightIn(min = 200.dp)
                    )
                    HeartRateCard(
                        modifier = Modifier.widthIn(max = 400.dp, min = 200.dp),
                        uiState.value.heartRateData
                    )
                }
            }
        }
    }
}
