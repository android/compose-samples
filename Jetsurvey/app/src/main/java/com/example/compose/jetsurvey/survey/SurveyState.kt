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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class QuestionState(
    val question: Question,
    val questionIndex: Int,
    val totalQuestionsCount: Int,
    val showPrevious: Boolean,
    val showDone: Boolean
) {
    var enableNext by mutableStateOf(false)
    var answer by mutableStateOf<Answer<*>?>(null)
}

sealed class SurveyState {
    data class Questions(
        @StringRes val surveyTitle: Int,
        val questionsState: List<QuestionState>
    ) : SurveyState() {
        var currentQuestionIndex by mutableStateOf(0)
    }

    data class Result(
        @StringRes val surveyTitle: Int,
        val surveyResult: SurveyResult
    ) : SurveyState()
}
