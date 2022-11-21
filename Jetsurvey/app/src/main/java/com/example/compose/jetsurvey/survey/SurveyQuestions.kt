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

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.question.ActionQuestion
import com.example.compose.jetsurvey.survey.question.MultipleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.SingleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.SliderQuestion
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.slightlyDeemphasizedAlpha
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Question(
    question: Question,
    answer: Answer<*>?,
    shouldAskPermissions: Boolean,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, SurveyActionType) -> Unit,
    onDoNotAskForPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (question.permissionsRequired.isEmpty()) {
        QuestionContent(question, answer, onAnswer, onAction, modifier)
    } else {
        val multiplePermissionsState = rememberMultiplePermissionsState(
            question.permissionsRequired
        )

        if (multiplePermissionsState.allPermissionsGranted) {
            QuestionContent(question, answer, onAnswer, onAction, modifier)
        } else {
            PermissionsRationale(
                question,
                multiplePermissionsState,
                onDoNotAskForPermissions,
                modifier.padding(horizontal = 20.dp)
            )
        }

        // If we cannot ask for permissions, inform the caller that can move to the next question
        if (!shouldAskPermissions) {
            LaunchedEffect(true) {
                onAnswer(Answer.PermissionsDenied)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsRationale(
    question: Question,
    multiplePermissionsState: MultiplePermissionsState,
    onDoNotAskForPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(32.dp))
        QuestionTitle(question.questionText)
        Spacer(modifier = Modifier.height(32.dp))
        val rationaleId =
            question.permissionsRationaleText ?: R.string.permissions_rationale
        Text(stringResource(id = rationaleId))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        ) {
            Text(stringResource(R.string.request_permissions))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onDoNotAskForPermissions) {
            Text(stringResource(R.string.do_not_ask_permissions))
        }
    }
}

@Composable
private fun QuestionContent(
    question: Question,
    answer: Answer<*>?,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            QuestionTitle(question.questionText)
            Spacer(modifier = Modifier.height(24.dp))
            if (question.description != null) {
                Text(
                    text = stringResource(id = question.description),
                    color = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = stronglyDeemphasizedAlpha),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(bottom = 18.dp, start = 8.dp, end = 8.dp)
                )
            }
            when (question.answer) {
                is PossibleAnswer.SingleChoice -> SingleChoiceQuestion(
                    options = question.answer.options,
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
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.Slider -> SliderQuestion(
                    possibleAnswer = question.answer,
                    answer = answer as Answer.Slider?,
                    onAnswerSelected = { onAnswer(Answer.Slider(it)) },
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QuestionTitle(@StringRes title: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
fun QuestionPreview() {
    val question = Question(
        id = 2,
        questionText = R.string.pick_superhero,
        answer = PossibleAnswer.SingleChoice(
            options = listOf(
                AnswerOption(R.string.spark),
                AnswerOption(R.string.lenz),
                AnswerOption(R.string.bugchaos),
                AnswerOption(R.string.frag)
            )
        ),
        description = R.string.select_one
    )
    JetsurveyTheme {
        Surface {
            Question(
                question = question,
                shouldAskPermissions = true,
                answer = null,
                onAnswer = {},
                onAction = { _, _ -> },
                onDoNotAskForPermissions = {}
            )
        }
    }
}
