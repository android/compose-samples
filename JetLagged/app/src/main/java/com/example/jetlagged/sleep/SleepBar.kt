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

package com.example.jetlagged.sleep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.jetlagged.data.sleepData
import com.example.jetlagged.ui.theme.JetLaggedTheme
import com.example.jetlagged.ui.theme.LegendHeadingStyle

@Composable
fun SleepBar(
    sleepData: SleepDayData,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val transition = updateTransition(targetState = isExpanded, label = "expanded")

    Column(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isExpanded = !isExpanded
            }
    ) {
        SleepRoundedBar(
            sleepData,
            transition
        )

        transition.AnimatedVisibility(
            enter = fadeIn(animationSpec = tween(animationDuration)) + expandVertically(
                animationSpec = tween(animationDuration)
            ),
            exit = fadeOut(animationSpec = tween(animationDuration)) + shrinkVertically(
                animationSpec = tween(animationDuration)
            ),
            content = {
                DetailLegend()
            },
            visible = { it }
        )
    }
}

@Composable
private fun SleepRoundedBar(
    sleepData: SleepDayData,
    transition: Transition<Boolean>,
) {
    val textMeasurer = rememberTextMeasurer()

    val height by transition.animateDp(label = "height", transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness =
            Spring.StiffnessLow
        )
    }) { targetExpanded ->
        if (targetExpanded) 100.dp else 24.dp
    }
    val animationProgress by transition.animateFloat(label = "progress", transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness =
            Spring.StiffnessLow
        )
    }) { target ->
        if (target) 1f else 0f
    }

    val sleepGradientBarColorStops = sleepGradientBarColorStops()
    Spacer(
        modifier = Modifier
            .drawWithCache {
                val width = this.size.width
                val cornerRadiusStartPx = 2.dp.toPx()
                val collapsedCornerRadiusPx = 10.dp.toPx()
                val animatedCornerRadius = CornerRadius(
                    lerp(cornerRadiusStartPx, collapsedCornerRadiusPx, (1 - animationProgress))
                )

                val lineThicknessPx = lineThickness.toPx()
                val roundedRectPath = Path()
                roundedRectPath.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            Offset(x = 0f, y = -lineThicknessPx / 2f),
                            Size(
                                this.size.width + lineThicknessPx * 2,
                                this.size.height + lineThicknessPx
                            )
                        ),
                        cornerRadius = animatedCornerRadius
                    )
                )
                val roundedCornerStroke = Stroke(
                    lineThicknessPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(
                        cornerRadiusStartPx * animationProgress
                    )
                )
                val barHeightPx = barHeight.toPx()

                val sleepGraphPath = generateSleepPath(
                    this.size,
                    sleepData, width, barHeightPx, animationProgress,
                    lineThickness.toPx() / 2f
                )
                val gradientBrush =
                    Brush.verticalGradient(
                        colorStops = sleepGradientBarColorStops.toTypedArray(),
                        startY = 0f,
                        endY = SleepType.entries.size * barHeightPx
                    )
                val textResult = textMeasurer.measure(AnnotatedString(sleepData.sleepScoreEmoji))

                onDrawBehind {
                    drawSleepBar(
                        roundedRectPath,
                        sleepGraphPath,
                        gradientBrush,
                        roundedCornerStroke,
                        animationProgress,
                        textResult,
                        cornerRadiusStartPx
                    )
                }
            }
            .height(height)
            .fillMaxWidth()
    )
}

private fun DrawScope.drawSleepBar(
    roundedRectPath: Path,
    sleepGraphPath: Path,
    gradientBrush: Brush,
    roundedCornerStroke: Stroke,
    animationProgress: Float,
    textResult: TextLayoutResult,
    cornerRadiusStartPx: Float,
) {
    clipPath(roundedRectPath) {
        drawPath(sleepGraphPath, brush = gradientBrush)
        drawPath(
            sleepGraphPath,
            style = roundedCornerStroke,
            brush = gradientBrush
        )
    }

    translate(left = -animationProgress * (textResult.size.width + textPadding.toPx())) {
        drawText(
            textResult,
            topLeft = Offset(textPadding.toPx(), cornerRadiusStartPx)
        )
    }
}

/**
 * Generate the path for the different sleep periods.
 */
private fun generateSleepPath(
    canvasSize: Size,
    sleepData: SleepDayData,
    width: Float,
    barHeightPx: Float,
    heightAnimation: Float,
    lineThicknessPx: Float,
): Path {
    val path = Path()

    var previousPeriod: SleepPeriod? = null

    path.moveTo(0f, 0f)

    sleepData.sleepPeriods.forEach { period ->
        val percentageOfTotal = sleepData.fractionOfTotalTime(period)
        val periodWidth = percentageOfTotal * width
        val startOffsetPercentage = sleepData.minutesAfterSleepStart(period) /
            sleepData.totalTimeInBed.toMinutes().toFloat()
        val halfBarHeight = canvasSize.height / SleepType.entries.size / 2f

        val offset = if (previousPeriod == null) {
            0f
        } else {
            halfBarHeight
        }

        val offsetY = lerp(
            0f,
            period.type.heightSleepType() * canvasSize.height, heightAnimation
        )
        // step 1 - draw a line from previous sleep period to current
        if (previousPeriod != null) {
            path.lineTo(
                x = startOffsetPercentage * width + lineThicknessPx,
                y = offsetY + offset
            )
        }

        // step 2 - add the current sleep period as rectangle to path
        path.addRect(
            rect = Rect(
                offset = Offset(x = startOffsetPercentage * width + lineThicknessPx, y = offsetY),
                size = canvasSize.copy(width = periodWidth, height = barHeightPx)
            )
        )
        // step 3 - move to the middle of the current sleep period
        path.moveTo(
            x = startOffsetPercentage * width + periodWidth + lineThicknessPx,
            y = offsetY + halfBarHeight
        )

        previousPeriod = period
    }
    return path
}

@Preview
@Composable
private fun DetailLegend() {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SleepType.entries.forEach {
            LegendItem(it)
        }
    }
}

@Composable
private fun LegendItem(sleepType: SleepType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(colorForSleepType(sleepType))
        )
        Text(
            stringResource(id = sleepType.title),
            style = LegendHeadingStyle,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Preview
@Composable
fun SleepBarPreview() {
    SleepBar(sleepData = sleepData.sleepDayData.first())
}

private val lineThickness = 2.dp
private val barHeight = 24.dp
private const val animationDuration = 500
private val textPadding = 4.dp

@Composable
fun sleepGradientBarColorStops(): List<Pair<Float, Color>> =
    SleepType.entries.map {
        Pair(
            when (it) {
                SleepType.Awake -> 0f
                SleepType.REM -> 0.33f
                SleepType.Light -> 0.66f
                SleepType.Deep -> 1f
            },
            colorForSleepType(it)
        )
    }

private fun SleepType.heightSleepType(): Float {
    return when (this) {
        SleepType.Awake -> 0f
        SleepType.REM -> 0.25f
        SleepType.Light -> 0.5f
        SleepType.Deep -> 0.75f
    }
}

@Composable
fun colorForSleepType(sleepType: SleepType): Color =
    when (sleepType) {
        SleepType.Awake -> JetLaggedTheme.extraColors.sleepAwake
        SleepType.REM -> JetLaggedTheme.extraColors.sleepRem
        SleepType.Light -> JetLaggedTheme.extraColors.sleepLight
        SleepType.Deep -> JetLaggedTheme.extraColors.sleepDeep
    }
