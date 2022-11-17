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

package com.example.compose.jetsurvey.survey.question

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.theme.JetsurveyTheme

@Composable
fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider?,
    onAnswerSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableStateOf(answer?.answerValue ?: possibleAnswer.defaultValue)
    }
    Column(modifier = modifier) {
        Row {
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    onAnswerSelected(it)
                },
                valueRange = possibleAnswer.range,
                steps = possibleAnswer.steps,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        }
        Row {
            Text(
                text = stringResource(id = possibleAnswer.startText),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
            Text(
                text = stringResource(id = possibleAnswer.neutralText),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
            Text(
                text = stringResource(id = possibleAnswer.endText),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
        }
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SliderQuestionPreview() {
    JetsurveyTheme {
        Surface {
            SliderQuestion(
                possibleAnswer = PossibleAnswer.Slider(
                    range = 1f..10f,
                    steps = 3,
                    startText = R.string.strongly_dislike,
                    endText = R.string.strongly_like,
                    neutralText = R.string.neutral
                ),
                answer = Answer.Slider(5f),
                onAnswerSelected = {}
            )
        }
    }
}
