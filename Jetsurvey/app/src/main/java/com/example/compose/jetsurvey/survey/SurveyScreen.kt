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
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.align
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.outlined.FiberManualRecord
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R

@Composable
fun SurveyQuestionsScreen(
    questions: SurveyState.Questions,
    onAction: (Int, SurveyActionType) -> Unit,
    onDonePressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    var currentQuestionIndex by savedInstanceState { 0 }
    val questionState =
        remember(currentQuestionIndex) { questions.questionsState[currentQuestionIndex] }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { SurveyTopAppBar(questions.surveyTitle, onBackPressed) },
            bodyContent = { innerPadding ->
                Question(
                    question = questionState.question,
                    answer = questionState.answer,
                    onAnswer = { questionState.answer = it },
                    onAction = onAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            },
            bottomBar = {
                SurveyBottomBar(
                    questionState = questionState,
                    onPreviousPressed = { currentQuestionIndex-- },
                    onNextPressed = { currentQuestionIndex++ },
                    onDonePressed = onDonePressed
                )
            }
        )
    }
}

@Composable
fun SurveyResultScreen(
    result: SurveyState.Result,
    onDonePressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { SurveyTopAppBar(result.surveyTitle, onBackPressed) },
            bodyContent = { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                SurveyResult(result = result, modifier = modifier)
            },
            bottomBar = {
                OutlinedButton(
                    onClick = { onDonePressed() },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        )
    }
}

@Composable
private fun SurveyResult(result: SurveyState.Result, modifier: Modifier = Modifier) {
    ScrollableColumn(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.preferredHeight(44.dp))
        Text(
            text = result.surveyResult.library,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = stringResource(
                result.surveyResult.result,
                result.surveyResult.library
            ),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = stringResource(result.surveyResult.description),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun SurveyTopAppBar(
    @StringRes surveyTitle: Int,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = surveyTitle),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ChevronLeft)
            }
        },
        // We need to balance the navigation icon, so we add a spacer.
        actions = {
            Spacer(modifier = Modifier.preferredWidth(68.dp))
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}

@Composable
private fun SurveyBottomBar(
    questionState: QuestionState,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        TextButton(
            modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.Start),
            onClick = onPreviousPressed,
            enabled = questionState.enablePrevious
        ) {
            Text(text = stringResource(id = R.string.previous))
        }
        PageIndicator(
            pagesCount = questionState.totalQuestionsCount,
            currentPageIndex = questionState.questionIndex
        )
        if (questionState.showDone) {
            TextButton(
                modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.End),
                onClick = onDonePressed
            ) {
                Text(text = stringResource(id = R.string.done))
            }
        } else {
            TextButton(
                modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.End),
                onClick = onNextPressed
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }
    }
}

@Composable
private fun PageIndicator(pagesCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .align(Alignment.CenterVertically)
    ) {
        for (pageIndex in 0 until pagesCount) {
            val asset = if (currentPageIndex == pageIndex) {
                Icons.Filled.FiberManualRecord
            } else {
                Icons.Outlined.FiberManualRecord
            }
            Icon(
                asset = asset,
                tint = MaterialTheme.colors.primary
            )
        }
    }
}
