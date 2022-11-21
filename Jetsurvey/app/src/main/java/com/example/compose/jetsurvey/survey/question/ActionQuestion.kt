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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.survey.SurveyActionType

@Composable
fun ActionQuestion(
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
