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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.question.DateQuestion
import com.example.compose.jetsurvey.survey.question.MultipleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.PhotoQuestion
import com.example.compose.jetsurvey.survey.question.SingleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.SliderQuestion
import com.example.compose.jetsurvey.survey.question.Superhero

@Composable
fun FreeTimeQuestion(
    modifier: Modifier = Modifier,
    selectedAnswers: List<Int>,
    onOptionSelected: (selected: Boolean, answer: Int) -> Unit,
) {
    MultipleChoiceQuestion(
        modifier = modifier,
        titleResourceId = R.string.in_my_free_time,
        directionResourceId = R.string.select_all,
        possibleAnswers = listOf(
            R.string.read,
            R.string.work_out,
            R.string.draw,
            R.string.play_games,
            R.string.dance,
            R.string.watch_movies,
        ),
        selectedAnswers = selectedAnswers,
        onOptionSelected = onOptionSelected,
    )
}

@Composable
fun SuperheroQuestion(
    modifier: Modifier = Modifier,
    selectedAnswer: Superhero?,
    onOptionSelected: (Superhero) -> Unit,
) {
    SingleChoiceQuestion(
        modifier = modifier,
        titleResourceId = R.string.pick_superhero,
        directionResourceId = R.string.select_one,
        possibleAnswers = listOf(
            Superhero(R.string.spark, R.drawable.spark),
            Superhero(R.string.lenz, R.drawable.lenz),
            Superhero(R.string.bugchaos, R.drawable.bug_of_chaos),
            Superhero(R.string.frag, R.drawable.frag),
        ),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
    )
}

@Composable
fun TakeawayQuestion(
    modifier: Modifier = Modifier,
    dateInMillis: Long?,
    onClick: () -> Unit,
) {
    DateQuestion(
        modifier = modifier,
        titleResourceId = R.string.takeaway,
        directionResourceId = R.string.select_date,
        dateInMillis = dateInMillis,
        onClick = onClick,
    )
}

@Composable
fun FeelingAboutSelfiesQuestion(
    modifier: Modifier = Modifier,
    value: Float?,
    onValueChange: (Float) -> Unit,
) {
    SliderQuestion(
        modifier = modifier,
        titleResourceId = R.string.selfies,
        value = value,
        onValueChange = onValueChange,
        startTextResource = R.string.strongly_dislike,
        neutralTextResource = R.string.neutral,
        endTextResource = R.string.strongly_like,
    )
}

@Composable
fun TakeSelfieQuestion(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onClick: () -> Unit,
) {
    PhotoQuestion(
        modifier = modifier,
        titleResourceId = R.string.selfie_skills,
        imageUri = imageUri,
        onClick = onClick,
    )
}
