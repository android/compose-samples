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

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val FILEPATH_XML_KEY = "files-path"

class SurveyViewModel(private val surveyRepository: SurveyRepository) : ViewModel() {

    private val _uiState = MutableLiveData<SurveyState>()
    val uiState: LiveData<SurveyState>
        get() = _uiState

    private lateinit var surveyInitialState: SurveyState

    init {
        viewModelScope.launch {
            val survey = surveyRepository.getSurvey()

            // Create the default questions state based on the survey questions
            val questions: List<QuestionState> = survey.questions.mapIndexed { index, question ->
                val showPrevious = index > 0
                val showDone = index == survey.questions.size - 1
                QuestionState(
                    question = question,
                    questionIndex = index,
                    totalQuestionsCount = survey.questions.size,
                    showPrevious = showPrevious,
                    showDone = showDone
                )
            }
            surveyInitialState = SurveyState.Questions(survey.title, questions)
            _uiState.value = surveyInitialState
        }
    }

    fun computeResult(surveyQuestions: SurveyState.Questions) {
        val answers = surveyQuestions.questionsState.mapNotNull { it.answer }
        val result = surveyRepository.getSurveyResult(answers)
        _uiState.value = SurveyState.Result(surveyQuestions.surveyTitle, result)
    }

    fun onDatePicked(questionId: Int, date: String) {
        updateStateWithActionResult(questionId, SurveyActionResult.Date(date))
    }

    private fun updateStateWithActionResult(questionId: Int, result: SurveyActionResult) {
        val latestState = _uiState.value
        if (latestState != null && latestState is SurveyState.Questions) {
            val question =
                latestState.questionsState.first { questionState ->
                    questionState.question.id == questionId
                }
            question.answer = Answer.Action(result)
            question.enableNext = true
        }
    }


    fun saveImageFromCamera(bitmap: Bitmap, imagesFolder: File) {
        val imageFile = File(imagesFolder, generateFilename("CAMERA"))
        val imageStream = FileOutputStream(imageFile)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
                    imageStream.flush()
                    imageStream.close()

                    Log.d("flo", "Camera image saved")

                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                }
            }
        }
        updateStateWithActionResult(975, SurveyActionResult.Photo(bitmap))
    }
}

fun getImagesFolder(context: Context): File {
    return File(context.filesDir, "images/").also {
        if (!it.exists()) {
            it.mkdir()
        }
    }
}

fun generateFilename(source: String) = "$source-${System.currentTimeMillis()}.jpg"

suspend fun applyGrayscaleFilter(original: Bitmap): Bitmap = withContext(Dispatchers.Default) {
    val height = original.height
    val width = original.width

    val modifiedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val canvas = Canvas(modifiedBitmap)
    val paint = Paint()
    canvas.drawBitmap(original, null, Rect(0, 0, original.width, original.height), paint)

    modifiedBitmap
}


class SurveyViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            return SurveyViewModel(SurveyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
