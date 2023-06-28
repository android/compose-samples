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

@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.jetlagged

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import com.example.jetlagged.backgrounds.yellowBackground
import com.example.jetlagged.data.JetLaggedHomeScreenViewModel
import com.example.jetlagged.heartrate.HeartRateCard
import com.example.jetlagged.sleep.JetLaggedHeader
import com.example.jetlagged.sleep.JetLaggedSleepGraphCard
import com.example.jetlagged.ui.util.MultiDevicePreview

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@MultiDevicePreview
@Composable
fun JetLaggedScreen(
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: JetLaggedHomeScreenViewModel = JetLaggedHomeScreenViewModel() // todo fix this to come from nav
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        Column(modifier = Modifier.yellowBackground()) {
            JetLaggedHeader(modifier = Modifier.fillMaxWidth())
        }

        val uiState =
            viewModel.uiState.collectAsState() // todo do i change to collectAsStateWithLifecycle
        val timeSleepSummaryCards = remember {
            movableContentOf {
                AverageTimeInBedCard(modifier = Modifier)
                AverageTimeAsleepCard(modifier = Modifier)
            }
        }
        LookaheadScope {
            FlowRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 3
            ) {
                JetLaggedSleepGraphCard(uiState.value.sleepGraphData)
                if (windowSizeClass == WindowWidthSizeClass.Compact) {
                    timeSleepSummaryCards()
                } else {
                    FlowColumn {
                        timeSleepSummaryCards()
                    }
                }
                if (windowSizeClass == WindowWidthSizeClass.Compact) {
                    WellnessCard(uiState.value.wellnessData)
                    HeartRateCard(uiState.value.heartRateData)
                } else {
                    FlowColumn {
                        WellnessCard(uiState.value.wellnessData)
                        HeartRateCard(uiState.value.heartRateData)
                    }
                }
                AmbienceCard()
                RoomTemperature()
            }
        }
    }
}
