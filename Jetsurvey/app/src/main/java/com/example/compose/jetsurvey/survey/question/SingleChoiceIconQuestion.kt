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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.theme.JetsurveyTheme

/**
 * Shows a list of possible answers with image and text and a radio button.
 * @param possibleAnswer a list of all possible answers with their icon and text value
 * @param answer the chosen answer (identified by text resource value), can be null if no answer is
 * chosen
 * @param onAnswerSelected callback that is called when a possible answer is clicked
 * @param modifier Modifier to be applied to the [SingleChoiceIconQuestion]
 */
@Composable
fun SingleChoiceIconQuestion(
    possibleAnswer: PossibleAnswer.SingleChoiceIcon,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringIconRes

    Column(modifier) {
        options.forEach { (imgRes, textRes) ->
            SingleChoiceIconAnswer(
                text = stringResource(textRes),
                painter = painterResource(imgRes),
                selected = textRes == answer?.answer,
                onOptionSelected = { onAnswerSelected(textRes) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SingleChoiceIconAnswer(
    text: String,
    painter: Painter,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val answerBorderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }
    val answerBackgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.background
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(width = 1.dp, color = answerBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    onClick = onOptionSelected
                )
                .background(answerBackgroundColor)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Text(text)
            RadioButton(selected = selected, onClick = null, modifier = Modifier.size(48.dp))
        }
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleChoiceIconAnswerPreview(
    @PreviewParameter(SelectedProvider::class, limit = 2) selected: Boolean
) {
    JetsurveyTheme {
        Surface {
            SingleChoiceIconAnswer(
                text = "Preview",
                painter = painterResource(id = R.drawable.frag),
                selected = selected,
                onOptionSelected = { }
            )
        }
    }
}

private class SelectedProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleChoiceIconQuestionPreview() {
    var selectedAnswer: Answer.SingleChoice? by remember {
        mutableStateOf(Answer.SingleChoice(R.string.bugchaos))
    }

    JetsurveyTheme {
        Surface {
            SingleChoiceIconQuestion(
                possibleAnswer = PossibleAnswer.SingleChoiceIcon(
                    listOf(
                        Pair(R.drawable.spark, R.string.spark),
                        Pair(R.drawable.lenz, R.string.lenz),
                        Pair(R.drawable.bug_of_chaos, R.string.bugchaos),
                        Pair(R.drawable.frag, R.string.frag)
                    )
                ),
                answer = selectedAnswer,
                onAnswerSelected = { textRes -> selectedAnswer = Answer.SingleChoice(textRes) }
            )
        }
    }
}
