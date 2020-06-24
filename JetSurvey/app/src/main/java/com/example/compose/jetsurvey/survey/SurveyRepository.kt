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

import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.PossibleAnswer.SingleChoice

// Static data of questions
private val jetpackQuestions = listOf(
    Question(
        id = 1,
        questionText = R.string.in_my_free_time,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games
            )
        )
    ),
    Question(
        id = 2,
        questionText = R.string.in_my_free_time2,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games
            )
        )
    ),
    Question(
        id = 3,
        questionText = R.string.in_my_free_time3,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games
            )
        )
    ),
    Question(
        id = 4,
        questionText = R.string.in_my_free_time4,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games
            )
        )
    )
)
private val jetpackSurvey = Survey(
    title = R.string.which_jetpack_library,
    questions = jetpackQuestions
)

object SurveyRepository {

    fun getSurvey() = jetpackSurvey

    fun getSurveyResult(survey: Survey, answers: List<UserAnswer<PossibleAnswer>>): SurveyResult {
        return SurveyResult(
            result = R.string.room,
            description = R.string.room_description
        )
    }
}
