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
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SurveyViewModelTest {

    private lateinit var viewModel: SurveyViewModel

    @Before
    fun setUp() {
        viewModel = SurveyViewModel(
            PhotoUriManager(ApplicationProvider.getApplicationContext())
        )
    }

    @Test
    fun onFreeTimeResponse() {
        val answerOne = 0
        val answerTwo = 99

        // Starts empty, next disabled
        Truth.assertThat(viewModel.freeTimeResponse).isEmpty()
        Truth.assertThat(viewModel.isNextEnabled).isFalse()

        // Add two arbitrary values
        viewModel.onFreeTimeResponse(true, answerOne)
        viewModel.onFreeTimeResponse(true, answerTwo)
        Truth.assertThat(viewModel.freeTimeResponse).containsExactly(answerOne, answerTwo)
        Truth.assertThat(viewModel.isNextEnabled).isTrue()

        // Remove one value
        viewModel.onFreeTimeResponse(false, answerTwo)
        Truth.assertThat(viewModel.freeTimeResponse).containsExactly(answerOne)
        Truth.assertThat(viewModel.isNextEnabled).isTrue()

        // Remove the last value
        viewModel.onFreeTimeResponse(false, answerOne)
        Truth.assertThat(viewModel.freeTimeResponse).isEmpty()
        Truth.assertThat(viewModel.isNextEnabled).isFalse()
    }
}
