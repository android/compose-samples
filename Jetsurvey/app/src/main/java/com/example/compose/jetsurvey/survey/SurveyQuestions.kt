/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetsurvey.survey

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R

@Composable
fun Question(
    question: Question,
    answer: Answer<*>?,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
    ) {
        Spacer(modifier = Modifier.preferredHeight(44.dp))
        Text(
            text = stringResource(id = question.questionText),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.preferredHeight(24.dp))
        when (question.answer) {
            is PossibleAnswer.SingleChoice -> SingleChoiceQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.SingleChoice?,
                onAnswerSelected = { answer -> onAnswer(Answer.SingleChoice(answer)) },
                modifier = Modifier.fillMaxWidth()
            )
            is PossibleAnswer.MultipleChoice -> MultipleChoiceQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.MultipleChoice?,
                onAnswerSelected = { newAnswer, selected ->
                    // create the answer if it doesn't exist or
                    // update it based on the user's selection
                    if (answer == null) {
                        onAnswer(Answer.MultipleChoice(setOf(newAnswer)))
                    } else {
                        onAnswer(answer.withAnswerSelected(newAnswer, selected))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            is PossibleAnswer.Action -> ActionQuestion(
                questionId = question.id,
                possibleAnswer = question.answer,
                answer = answer as Answer.Action?,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth()
            )
            is PossibleAnswer.Slider -> SliderQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.Slider?,
                onAnswerSelected = { onAnswer(Answer.Slider(it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SingleChoiceQuestion(
    possibleAnswer: PossibleAnswer.SingleChoice,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }

    val radioOptions = options.keys.toList()

    val selected = if (answer != null) {
        stringResource(id = answer.answer)
    } else {
        null
    }
    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    Column(modifier = modifier) {
        radioOptions.forEach { text ->
            val onClickHandle = {
                onOptionSelected(text)
                options[text]?.let { onAnswerSelected(it) }
                Unit
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = onClickHandle
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = onClickHandle,
                    selectedColor = MaterialTheme.colors.primary
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun MultipleChoiceQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }
    Column(modifier = modifier) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringRes?.contains(option.value)
                mutableStateOf(selectedOption ?: false)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(
                        onClick = {
                            checkedState = !checkedState
                            onAnswerSelected(option.value, checkedState)
                        }
                    )
            ) {
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = { selected ->
                        checkedState = selected
                        onAnswerSelected(option.value, selected)
                    },
                    checkedColor = MaterialTheme.colors.primary
                )
                Text(
                    text = option.key,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionQuestion(
    questionId: Int,
    possibleAnswer: PossibleAnswer.Action,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onAction(questionId, possibleAnswer.actionType) },
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        Text(text = stringResource(id = possibleAnswer.label))
    }
    if (answer != null) {
        when (answer.result) {
            is SurveyActionResult.Date -> {
                Text(
                    text = stringResource(R.string.selected_date, answer.result.date),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
            is SurveyActionResult.Photo -> TODO()
            is SurveyActionResult.Contact -> TODO()
        }
    }
}

@Composable
private fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider?,
    onAnswerSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableStateOf(answer?.answerValue ?: possibleAnswer.defaultValue)
    }
    Row(modifier = modifier) {
        Text(
            text = stringResource(id = possibleAnswer.startText),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onAnswerSelected(it)
            },
            valueRange = possibleAnswer.range,
            steps = possibleAnswer.steps,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
        )
        Text(
            text = stringResource(id = possibleAnswer.endText),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
