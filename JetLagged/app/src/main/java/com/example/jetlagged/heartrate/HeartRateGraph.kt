/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetlagged.heartrate

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toSize
import com.example.jetlagged.data.HeartRateData
import com.example.jetlagged.data.bracketInSeconds
import com.example.jetlagged.data.heartRateGraphData
import com.example.jetlagged.data.numberEntries
import com.example.jetlagged.ui.theme.JetLaggedTheme
import kotlin.math.roundToInt

@Composable
fun HeartRateGraph(listData: List<HeartRateData>) {
    Box(Modifier.size(width = 400.dp, height = 100.dp)) {
        Graph(
            listData = listData,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun Graph(
    listData: List<HeartRateData>,
    modifier: Modifier = Modifier,
    waveLineColors: List<Color> = JetLaggedTheme.extraColors.heartWave,
    pathBackground: Color = JetLaggedTheme.extraColors.heartWaveBackground,
) {
    if (waveLineColors.size < 2) {
        throw IllegalArgumentException("waveLineColors requires 2+ colors; $waveLineColors")
    }
    Box(
        modifier
            .fillMaxSize()
            .drawWithCache {
                val paths = generateSmoothPath(listData, size)
                val lineBrush = Brush.verticalGradient(waveLineColors)
                onDrawBehind {
                    drawPath(
                        paths.second,
                        pathBackground,
                        style = Fill
                    )
                    drawPath(
                        paths.first,
                        lineBrush,
                        style = Stroke(2.dp.toPx())
                    )
                }
            }
    )
}

sealed class DataPoint {
    object NoMeasurement : DataPoint()
    data class Measurement(
        val averageMeasurementTime: Int,
        val minHeartRate: Int,
        val maxHeartRate: Int,
        val averageHeartRate: Int,
    ) : DataPoint()
}

fun generateSmoothPath(data: List<HeartRateData>, size: Size): Pair<Path, Path> {
    val path = Path()
    val variancePath = Path()

    val totalSeconds = 60 * 60 * 24 // total seconds in a day
    val widthPerSecond = size.width / totalSeconds
    val maxValue = data.maxBy { it.amount }.amount
    val minValue = data.minBy { it.amount }.amount
    val graphTop = ((maxValue + 5) / 10f).roundToInt() * 10
    val graphBottom = (minValue / 10f).toInt() * 10
    val range = graphTop - graphBottom
    val heightPxPerAmount = size.height / range.toFloat()

    var previousX = 0f
    var previousY = size.height
    var previousMaxX = 0f
    var previousMaxY = size.height
    val groupedMeasurements = (0..numberEntries).map { bracketStart ->
        heartRateGraphData.filter {
            (bracketStart * bracketInSeconds..(bracketStart + 1) * bracketInSeconds)
                .contains(it.date.toSecondOfDay())
        }
    }.map { heartRates ->
        if (heartRates.isEmpty()) DataPoint.NoMeasurement else
            DataPoint.Measurement(
                averageMeasurementTime = heartRates.map { it.date.toSecondOfDay() }.average()
                    .roundToInt(),
                minHeartRate = heartRates.minBy { it.amount }.amount,
                maxHeartRate = heartRates.maxBy { it.amount }.amount,
                averageHeartRate = heartRates.map { it.amount }.average().roundToInt()
            )
    }
    groupedMeasurements.forEachIndexed { i, dataPoint ->
        if (i == 0 && dataPoint is DataPoint.Measurement) {
            path.moveTo(
                0f,
                size.height - (dataPoint.averageHeartRate - graphBottom).toFloat() *
                    heightPxPerAmount
            )
            variancePath.moveTo(
                0f,
                size.height - (dataPoint.maxHeartRate - graphBottom).toFloat() *
                    heightPxPerAmount
            )
        }

        if (dataPoint is DataPoint.Measurement) {
            val x = dataPoint.averageMeasurementTime * widthPerSecond
            val y = size.height - (dataPoint.averageHeartRate - graphBottom).toFloat() *
                heightPxPerAmount

            // to do smooth curve graph - we use cubicTo, uncomment section below for non-curve
            val controlPoint1 = PointF((x + previousX) / 2f, previousY)
            val controlPoint2 = PointF((x + previousX) / 2f, y)
            path.cubicTo(
                controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                x, y
            )
            previousX = x
            previousY = y

            val maxX = dataPoint.averageMeasurementTime * widthPerSecond
            val maxY = size.height - (dataPoint.maxHeartRate - graphBottom).toFloat() *
                heightPxPerAmount
            val maxControlPoint1 = PointF((maxX + previousMaxX) / 2f, previousMaxY)
            val maxControlPoint2 = PointF((maxX + previousMaxX) / 2f, maxY)
            variancePath.cubicTo(
                maxControlPoint1.x, maxControlPoint1.y, maxControlPoint2.x, maxControlPoint2.y,
                maxX, maxY
            )

            previousMaxX = maxX
            previousMaxY = maxY
        }
    }

    var previousMinX = size.width
    var previousMinY = size.height
    groupedMeasurements.reversed().forEachIndexed { index, dataPoint ->
        val i = 47 - index
        if (i == 47 && dataPoint is DataPoint.Measurement) {
            variancePath.moveTo(
                size.width,
                size.height - (dataPoint.minHeartRate - graphBottom).toFloat() *
                    heightPxPerAmount
            )
        }

        if (dataPoint is DataPoint.Measurement) {
            val minX = dataPoint.averageMeasurementTime * widthPerSecond
            val minY = size.height - (dataPoint.minHeartRate - graphBottom).toFloat() *
                heightPxPerAmount
            val minControlPoint1 = PointF((minX + previousMinX) / 2f, previousMinY)
            val minControlPoint2 = PointF((minX + previousMinX) / 2f, minY)
            variancePath.cubicTo(
                minControlPoint1.x, minControlPoint1.y, minControlPoint2.x, minControlPoint2.y,
                minX, minY
            )

            previousMinX = minX
            previousMinY = minY
        }
    }
    return path to variancePath
}

fun DrawScope.drawHighlight(
    highlightedWeek: Int,
    graphData: List<HeartRateData>,
    textMeasurer: TextMeasurer,
    labelTextStyle: TextStyle
) {
    val amount = graphData[highlightedWeek].amount
    val minAmount = graphData.minBy { it.amount }.amount
    val range = graphData.maxBy { it.amount }.amount - minAmount
    val percentageHeight = ((amount - minAmount).toFloat() / range.toFloat())
    val pointY = size.height - (size.height * percentageHeight)
    // draw vertical line on week
    val x = highlightedWeek * (size.width / (graphData.size - 1))
    drawLine(
        HighlightColor,
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )

    // draw hit circle on graph
    drawCircle(
        Color.Green,
        radius = 4.dp.toPx(),
        center = Offset(x, pointY)
    )

    // draw info box
    val textLayoutResult = textMeasurer.measure("$amount", style = labelTextStyle)
    val highlightContainerSize = (textLayoutResult.size).toIntRect().inflate(4.dp.roundToPx()).size
    val boxTopLeft = (x - (highlightContainerSize.width / 2f))
        .coerceIn(0f, size.width - highlightContainerSize.width)
    drawRoundRect(
        Color.White,
        topLeft = Offset(boxTopLeft, 0f),
        size = highlightContainerSize.toSize(),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
    drawText(
        textLayoutResult,
        color = Color.Black,
        topLeft = Offset(boxTopLeft + 4.dp.toPx(), 4.dp.toPx())
    )
}

val BarColor = Color.White.copy(alpha = 0.3f)
val HighlightColor = Color.White.copy(alpha = 0.7f)
