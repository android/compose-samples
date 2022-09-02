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

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.slightlyDeemphasizedAlpha
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.text.SimpleDateFormat
import java.util.*

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
                    possibleAnswer = question.answer,
                    answer = answer as Answer.SingleChoice?,
                    onAnswerSelected = { answer -> onAnswer(Answer.SingleChoice(answer)) },
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.SingleChoiceIcon -> SingleChoiceIconQuestion(
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
                    modifier = Modifier.fillParentMaxWidth()
                )
                is PossibleAnswer.MultipleChoiceIcon -> MultipleChoiceIconQuestion(
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
            val optionSelected = text == selectedOption

            val answerBorderColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = text
                    )

                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceIconQuestion(
    possibleAnswer: PossibleAnswer.SingleChoiceIcon,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringIconRes.associateBy { stringResource(id = it.second) }

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
                options[text]?.let { onAnswerSelected(it.second) }
                Unit
            }
            val optionSelected = text == selectedOption
            val answerBorderColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    options[text]?.let {
                        Image(
                            painter = painterResource(
                                id = it.first
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .width(56.dp)
                                .height(56.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    Text(
                        text = text
                    )

                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
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
            val answerBorderColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(answerBackgroundColor)
                        .clickable(
                            onClick = {
                                checkedState = !checkedState
                                onAnswerSelected(option.value, checkedState)
                            }
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = option.key)

                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option.value, selected)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceIconQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoiceIcon,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringIconRes.associateBy { stringResource(id = it.second) }
    Column(modifier = modifier) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringRes?.contains(option.value.second)
                mutableStateOf(selectedOption ?: false)
            }
            val answerBorderColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                checkedState = !checkedState
                                onAnswerSelected(option.value.second, checkedState)
                            }
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = option.value.first),
                        contentDescription = null,
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    Text(text = option.key)

                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option.value.second, selected)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        ),
                    )
                }
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
    when (possibleAnswer.actionType) {
        SurveyActionType.PICK_DATE -> {
            DateQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
        SurveyActionType.TAKE_PHOTO -> {
            PhotoQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
        SurveyActionType.SELECT_CONTACT -> TODO()
    }
}

@Composable
private fun PhotoQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val resource = if (answer != null) {
        Icons.Filled.SwapHoriz
    } else {
        Icons.Filled.AddAPhoto
    }
    OutlinedButton(
        onClick = { onAction(questionId, SurveyActionType.TAKE_PHOTO) },
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues()
    ) {
        Column {
            if (answer != null && answer.result is SurveyActionResult.Photo) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(answer.result.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(96.dp)
                        .aspectRatio(4 / 3f)
                )
            } else {
                PhotoDefaultImage(modifier = Modifier.padding(horizontal = 86.dp, vertical = 74.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .padding(vertical = 26.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = resource, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        id = if (answer != null) {
                            R.string.retake_photo
                        } else {
                            R.string.add_photo
                        }
                    )
                )
            }
        }
    }
}

/**
 * Returns the start of today in milliseconds
 */
fun getDefaultDateInMillis(): Long {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val date = cal.get(Calendar.DATE)
    cal.clear()
    cal.set(year, month, date)
    return cal.timeInMillis
}

@Composable
private fun DateQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val timestamp = if (answer != null && answer.result is SurveyActionResult.Date) {
        answer.result.dateMillis
    } else {
        getDefaultDateInMillis()
    }

    // All times are stored in UTC, so generate the display from UTC also
    val dateFormat = SimpleDateFormat(simpleDateFormatPattern, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateString = dateFormat.format(timestamp)

    Button(
        onClick = { onAction(questionId, SurveyActionType.PICK_DATE) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = slightlyDeemphasizedAlpha),
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .padding(vertical = 20.dp)
            .height(54.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

    ) {
        Text(
            text = dateString,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )
    }
}

@Composable
private fun PhotoDefaultImage(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_selfie_light
    } else {
        R.drawable.ic_selfie_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
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

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onAnswerSelected(it)
            },
            valueRange = possibleAnswer.range,
            steps = possibleAnswer.steps,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
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

@Preview
@Composable
fun QuestionPreview() {
    val question = Question(
        id = 2,
        questionText = R.string.pick_superhero,
        answer = PossibleAnswer.SingleChoice(
            optionsStringRes = listOf(
                R.string.spark,
                R.string.lenz,
                R.string.bugchaos,
                R.string.frag
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

@Preview(name = "Photo Question Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Photo Question Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PhotoQuestionPreview() {
    JetsurveyTheme {
        Surface {
            PhotoQuestion(questionId = 1, answer = null, onAction = { _, _ -> })
        }
    }
}

@Preview(name = "Photo Question Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Photo Question Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DateQuestionPreview() {
    JetsurveyTheme {
        Surface {
            DateQuestion(questionId = 1, answer = null, onAction = { _, _ -> })
        }
    }
}
