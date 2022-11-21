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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import kotlin.math.roundToInt

@Composable
fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider?,
    onAnswerSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableStateOf(answer?.answerValue ?: possibleAnswer.defaultValue)
    }
    Column(modifier = modifier.fillMaxSize()) {
        DiscreteSlider(
            value = sliderPosition.toInt(),
            onValueChange = { new ->
                sliderPosition = new.toFloat()
                onAnswerSelected(new.toFloat())
            },
            startText = stringResource(possibleAnswer.startText),
            neutralText = stringResource(possibleAnswer.neutralText),
            endText = stringResource(possibleAnswer.endText)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscreteSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    startText: String,
    neutralText: String,
    endText: String,
    modifier: Modifier = Modifier,
    ticks: Int = 5,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    require(ticks >= 3) { "ticks should be >= 3" }
    Column {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            modifier = modifier.padding(horizontal = 21.dp),
            steps = ticks - 2,
            valueRange = 1f..(ticks.toFloat()),
            onValueChangeFinished = onValueChangeFinished,
            interactionSource = interactionSource,
            track = { sliderPositions -> Track(sliderPositions) }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center)
            ) {
                Text(
                    text = startText,
                    modifier = Modifier.widthIn(max = 60.dp)
                )
                Text(
                    text = neutralText,
                    modifier = Modifier.widthIn(max = 60.dp)
                )
                Text(
                    text = endText,
                    modifier = Modifier.widthIn(max = 60.dp)
                )
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
private fun Track(
    sliderPositions: SliderPositions,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    val tickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val trackStrokeWidth = 6.dp
    val tickStrokeWidth = 2.dp
    val tickSize = 6.dp
    val tickTrackGap = 3.dp

    Canvas(
        modifier
            .fillMaxWidth()
            .height(trackStrokeWidth + tickTrackGap + tickSize)
    ) {
        val trackExtraSpace = 28.dp.toPx()
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val tickLeft = Offset(0f, center.y)
        val tickRight = Offset(size.width, center.y)
        val sliderLeft = tickLeft.minus(Offset(x = trackExtraSpace, y = 0f))
        val sliderRight = tickRight.plus(Offset(x = trackExtraSpace, y = 0f))
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight

        drawLine(
            trackColor,
            sliderStart,
            sliderEnd,
            trackStrokeWidth.toPx(),
            StrokeCap.Round
        )

        sliderPositions.tickFractions.forEach { tickFraction ->
            drawLine(
                color = tickColor,
                start = Offset(
                    lerp(tickLeft, tickRight, tickFraction).x,
                    center.y + (trackStrokeWidth.div(2) + tickTrackGap).toPx()
                ),
                end = Offset(
                    lerp(tickLeft, tickRight, tickFraction).x,
                    center.y + (trackStrokeWidth.div(2) + tickTrackGap + tickSize).toPx()
                ),
                strokeWidth = tickStrokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
private fun SliderQuestionPreview() {
    JetsurveyTheme {
        Surface {
            SliderQuestion(
                possibleAnswer = PossibleAnswer.Slider(
                    startText = R.string.strongly_dislike,
                    endText = R.string.strongly_like,
                    neutralText = R.string.neutral
                ),
                answer = Answer.Slider(5f),
                onAnswerSelected = {}
            )
        }
    }
}

@Preview(name = "default")
@Preview(name = "large font", fontScale = 2f)
@Preview(name = "dark", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "wide", widthDp = 600)
@Preview(name = "small", widthDp = 300)
@Composable
private fun DiscreteSliderPreview() {
    var sliderValue by remember { mutableStateOf(3) }
    JetsurveyTheme {
        Surface {
            DiscreteSlider(
                value = sliderValue,
                onValueChange = { sliderValue = sliderValue },
                startText = "start label",
                neutralText = "neutral label",
                endText = "end label"
            )
        }
    }
}
