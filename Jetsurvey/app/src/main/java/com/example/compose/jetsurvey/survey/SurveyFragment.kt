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

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.google.android.material.datepicker.MaterialDatePicker

class SurveyFragment : Fragment() {

    private val viewModel: SurveyViewModel by viewModels {
        SurveyViewModelFactory(PhotoUriManager(requireContext().applicationContext))
    }

    private var selfieUri: Uri? = null
    private val takeSelfie = registerForActivityResult(TakePicture()) { photoSaved ->
        if (photoSaved) {
            viewModel.onSelfieResponse(selfieUri!!)
        }
        selfieUri = null
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.sign_in_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                JetsurveyTheme {

                    if (viewModel.isSurveyComplete) {
                        SurveyResultScreen {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    } else {
                        val surveyScreenData = viewModel.surveyScreenData
                            ?: return@JetsurveyTheme
                        var shouldInterceptBackPresses by remember { mutableStateOf(true) }

                        BackHandler {
                            if (!shouldInterceptBackPresses || !viewModel.onBackPressed()) {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                        }

                        SurveyQuestionsScreen(
                            surveyScreenData = surveyScreenData,
                            isNextEnabled = viewModel.isNextEnabled,
                            onClosePressed = {
                                shouldInterceptBackPresses = false
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            },
                            onPreviousPressed = { viewModel.onPreviousPressed() },
                            onNextPressed = { viewModel.onNextPressed() },
                            onDonePressed = { viewModel.onDonePressed() }
                        ) { paddingValues ->

                            val modifier = Modifier.padding(paddingValues)

                            AnimatedContent(
                                targetState = surveyScreenData,
                                transitionSpec = {
                                    val animationSpec: TweenSpec<IntOffset> =
                                        tween(CONTENT_ANIMATION_DURATION)
                                    val direction = getTransitionDirection(
                                        initialIndex = initialState.questionIndex,
                                        targetIndex = targetState.questionIndex,
                                    )
                                    slideIntoContainer(
                                        towards = direction,
                                        animationSpec = animationSpec,
                                    ) with slideOutOfContainer(
                                        towards = direction,
                                        animationSpec = animationSpec
                                    )
                                }
                            ) { targetState ->

                                when (targetState.surveyQuestion) {
                                    SurveyQuestion.FREE_TIME -> {
                                        FreeTimeQuestion(
                                            selectedAnswers = viewModel.freeTimeResponse,
                                            onOptionSelected = viewModel::onFreeTimeResponse,
                                            modifier = modifier,
                                        )
                                    }

                                    SurveyQuestion.SUPERHERO -> SuperheroQuestion(
                                        selectedAnswer = viewModel.superheroResponse,
                                        onOptionSelected = viewModel::onSuperheroResponse,
                                        modifier = modifier,
                                    )

                                    SurveyQuestion.LAST_TAKEAWAY -> TakeawayQuestion(
                                        dateInMillis = viewModel.takeawayResponse,
                                        onClick = ::showTakeawayDatePicker,
                                        modifier = modifier,
                                    )

                                    SurveyQuestion.FEELING_ABOUT_SELFIES ->
                                        FeelingAboutSelfiesQuestion(
                                            value = viewModel.feelingAboutSelfiesResponse,
                                            onValueChange =
                                            viewModel::onFeelingAboutSelfiesResponse,
                                            modifier = modifier,
                                        )

                                    SurveyQuestion.TAKE_SELFIE -> TakeSelfieQuestion(
                                        imageUri = viewModel.selfieUriResponse,
                                        onClick = ::takeSelfie,
                                        modifier = modifier,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun getTransitionDirection(
        initialIndex: Int,
        targetIndex: Int
    ): AnimatedContentScope.SlideDirection {
        return if (targetIndex > initialIndex) {
            // Going forwards in the survey: Set the initial offset to start
            // at the size of the content so it slides in from right to left, and
            // slides out from the left of the screen to -fullWidth
            AnimatedContentScope.SlideDirection.Left
        } else {
            // Going back to the previous question in the set, we do the same
            // transition as above, but with different offsets - the inverse of
            // above, negative fullWidth to enter, and fullWidth to exit.
            AnimatedContentScope.SlideDirection.Right
        }
    }

    private fun showTakeawayDatePicker() {
        val date = viewModel.takeawayResponse
        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(date)
            .build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            picker.selection?.let { selectedDate ->
                viewModel.onTakeawayResponse(selectedDate)
            }
        }
    }

    private fun takeSelfie() {
        selfieUri = viewModel.getUriToSaveImage()
        takeSelfie.launch(selfieUri)
    }

    companion object {
        private const val CONTENT_ANIMATION_DURATION = 300
    }
}
