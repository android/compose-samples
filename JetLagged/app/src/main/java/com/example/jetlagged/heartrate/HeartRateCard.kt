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

package com.example.jetlagged.heartrate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.R
import com.example.jetlagged.data.HeartRateOverallData
import com.example.jetlagged.ui.theme.JetLaggedTheme
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.TitleStyle

@Preview
@Composable
fun HeartRateCard(
    modifier: Modifier = Modifier,
    heartRateData: HeartRateOverallData = HeartRateOverallData()
) {
    BasicInformationalCard(
        borderColor = JetLaggedTheme.extraColors.heart,
        modifier = modifier
            .height(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            HomeScreenCardHeading(text = stringResource(R.string.heart_rate_heading))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    heartRateData.averageBpm.toString(),
                    style = TitleStyle,
                    modifier = Modifier.alignByBaseline(),
                    textAlign = TextAlign.Center
                )
                Text(
                    "bpm",
                    modifier = Modifier.alignByBaseline(),
                    style = SmallHeadingStyle
                )
            }
            HeartRateGraph(heartRateData.listData)
        }
    }
}
