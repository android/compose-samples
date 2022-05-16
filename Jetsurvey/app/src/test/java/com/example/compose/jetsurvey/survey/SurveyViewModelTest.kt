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

package com.example.compose.jetsurvey.survey

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SurveyViewModelTest {

    private lateinit var viewModel: SurveyViewModel

    @Before
    fun setUp() {
        viewModel = SurveyViewModel(
            TestSurveyRepository(),
            PhotoUriManager(ApplicationProvider.getApplicationContext())
        )
    }

    @Test
    fun onDatePicked_storesValueCorrectly() {
        // Select a date
        val initialDateMilliseconds = 0L
        viewModel.onDatePicked(dateQuestionId, initialDateMilliseconds)

        // Get the stored date
        val newDateMilliseconds = viewModel.getCurrentDate(dateQuestionId)

        // Verify they're identical
        assertThat(newDateMilliseconds).isEqualTo(initialDateMilliseconds)
    }
}

const val dateQuestionId = 1
class TestSurveyRepository : SurveyRepository {

    private val testSurvey = Survey(
        title = -1,
        questions = listOf(
            Question(
                id = dateQuestionId,
                questionText = -1,
                answer = PossibleAnswer.Action(label = -1, SurveyActionType.PICK_DATE)
            )
        )
    )

    override fun getSurvey() = testSurvey

    override fun getSurveyResult(answers: List<Answer<*>>): SurveyResult {
        TODO("Not yet implemented")
    }
}
