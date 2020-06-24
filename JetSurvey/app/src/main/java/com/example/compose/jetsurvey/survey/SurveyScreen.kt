/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey.survey

import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope.gravity
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ChevronLeft
import androidx.ui.material.icons.filled.FiberManualRecord
import androidx.ui.material.icons.outlined.FiberManualRecord
import androidx.ui.res.stringResource
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme

data class QuestionUiModel(
    val question: Question,
    val questionIndex: Int,
    val totalQuestionsCount: Int,
    val enablePrevious: Boolean,
    val enableNext: Boolean
)

private fun Survey.getNextQuestionUiModel(index: Int): QuestionUiModel {
    val enablePrevious = index > 0
    val enableNext = index < questions.size - 1
    val question = questions[index]
    return QuestionUiModel(question, index, questions.size, enablePrevious, enableNext)
}

@Composable
fun SurveyScreen(
    survey: Survey,
    onBackPressed: () -> Unit
) {
    var currentQuestionIndex by savedInstanceState { 0 }
    val questionUiModel =
        remember(currentQuestionIndex) { survey.getNextQuestionUiModel(currentQuestionIndex) }

    Column(modifier = Modifier.fillMaxSize()) {
        SurveyTopAppBar(survey, onBackPressed)
        Question(
            question = questionUiModel.question.questionText,
            modifier = Modifier.fillMaxSize().weight(1f).padding(horizontal = 20.dp)
        )
        SurveyBottomBar(
            questionUiModel = questionUiModel,
            onPreviousPressed = { currentQuestionIndex-- },
            onNextPressed = { currentQuestionIndex++ }
        )
    }
}

@Composable
private fun SurveyTopAppBar(
    survey: Survey,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = survey.title),
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
    questionUiModel: QuestionUiModel,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        TextButton(
            modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.Start),
            onClick = onPreviousPressed,
            enabled = questionUiModel.enablePrevious
        ) {
            Text(text = stringResource(id = R.string.previous))
        }
        PageIndicator(
            pagesCount = questionUiModel.totalQuestionsCount,
            currentPageIndex = questionUiModel.questionIndex
        )
        TextButton(
            modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.End),
            onClick = onNextPressed,
            enabled = questionUiModel.enableNext
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
}

@Composable
private fun PageIndicator(pagesCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .gravity(align = Alignment.CenterVertically)
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

@Composable
private fun Question(@StringRes question: Int, modifier: Modifier = Modifier) {
    VerticalScroller(modifier = modifier) {
        Spacer(modifier = Modifier.preferredHeight(44.dp))
        Text(
            text = stringResource(id = question),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
    }
}

@Preview(name = "Survey light theme")
@Composable
fun SurveyScreenPreview() {
    JetsurveyTheme {
        SurveyScreen(SurveyRepository.getSurvey()) {}
    }
}

@Preview(name = "Survey dark theme")
@Composable
fun SurveyScreenPreviewDark() {
    JetsurveyTheme(darkTheme = true) {
        SurveyScreen(SurveyRepository.getSurvey()) {}
    }
}
