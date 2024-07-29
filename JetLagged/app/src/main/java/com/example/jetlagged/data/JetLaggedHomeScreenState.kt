/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetlagged.data

import com.example.jetlagged.sleep.SleepGraphData

data class JetLaggedHomeScreenState(
    val sleepGraphData: SleepGraphData = sleepData,
    val wellnessData: WellnessData = WellnessData(10, 4, 5),
    val heartRateData: HeartRateOverallData = HeartRateOverallData()
)

data class WellnessData(
    val snoring: Int,
    val coughing: Int,
    val respiration: Int
)

data class HeartRateOverallData(
    val averageBpm: Int = 65,
    val listData: List<HeartRateData> = heartRateGraphData
)
