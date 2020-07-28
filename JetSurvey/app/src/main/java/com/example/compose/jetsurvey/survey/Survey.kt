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

data class SurveyResult(
    @StringRes val result: Int,
    @StringRes val description: Int
)

data class Survey(
    @StringRes val title: Int,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    @StringRes val questionText: Int,
    val answer: PossibleAnswer
)

data class UserAnswer<T : PossibleAnswer>(
    val questionId: Int,
    val answer: SelectedAnswer<T>
)

sealed class PossibleAnswer {
    data class SingleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    data class MultipleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    object FreeText : PossibleAnswer()
    data class Slider(
        val startValue: Int,
        @StringRes val startText: Int,
        val endValue: Int,
        @StringRes val endText: Int
    ) : PossibleAnswer()
}

sealed class SelectedAnswer<T : PossibleAnswer> {
    data class SingleChoice(val answerIndex: Int) : SelectedAnswer<PossibleAnswer.SingleChoice>()
    data class MultipleChoice(val answerIndices: List<Int>) :
        SelectedAnswer<PossibleAnswer.MultipleChoice>()

    data class FreeText(val answer: String) : SelectedAnswer<PossibleAnswer.FreeText>()
    data class Slider(val answerValue: Int) : SelectedAnswer<PossibleAnswer.Slider>()
}
