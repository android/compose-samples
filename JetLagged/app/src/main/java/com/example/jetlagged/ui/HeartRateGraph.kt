package com.example.jetlagged.ui

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toSize
import com.example.jetlagged.ui.theme.DarkGreen
import com.example.jetlagged.ui.theme.Green
import com.example.jetlagged.ui.theme.MintGreen
import com.example.jetlagged.ui.theme.Pink
import com.example.jetlagged.ui.theme.Purple
import com.example.jetlagged.ui.theme.Yellow
import java.time.LocalTime
import kotlin.math.roundToInt

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
fun HeartRateGraph() {
  Box(Modifier.size(width = 400.dp, height = 100.dp)) {
//    Background()
    Graph()
    Highlight()
  }
}

@Composable
private fun Background() {
  Spacer(Modifier.fillMaxSize().drawBehind {
    drawLine(
      Yellow,
      start = Offset(0f, 0f),
      end = Offset(size.width, 0f),
      strokeWidth = 2f
    )
    drawLine(
      Yellow,
      start = Offset(0f, size.height),
      end = Offset(size.width, size.height),
      strokeWidth = 2f
    )
    drawLine(
      Yellow,
      start = Offset(0f, size.height / 2),
      end = Offset(size.width, size.height / 2),
      strokeWidth = 2f
    )
  })
}

@Composable
private fun Graph() {
  Spacer(
    Modifier
      .fillMaxSize()
      .drawWithCache {
        val paths = generateSmoothPath(graphData, size)
        onDrawBehind {
          drawPath(
            paths.second,
            Brush.verticalGradient(listOf(Pink.copy(0.25f), Purple.copy(0.25f), Green.copy(0.25f))),
            style = Fill
          )
          drawPath(
            paths.first,
            DarkGreen,
            style = Stroke(2.dp.toPx())
          )
        }
      })
}

@Composable
private fun Highlight() {

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

fun generateSmoothPath(data: List<HeartRate>, size: Size): Pair<Path, Path> {
  val path = Path()
  val variancePath = Path()

  val totalSeconds = 60 * 60 * 24 // total seconds in a day
  val widthPerSecond = size.width / totalSeconds

  val heightPxPerAmount = size.height / range.toFloat()

  var previousX = 0f
  var previousY = size.height
  var previousMaxX = 0f
  var previousMaxY = size.height

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

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawHighlight(
  highlightedWeek: Int,
  graphData: List<HeartRate>,
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

data class HeartRate(val date: LocalTime, val amount: Int)

val BarColor = Color.White.copy(alpha = 0.3f)
val HighlightColor = Color.White.copy(alpha = 0.7f)

private val graphData = listOf(
  HeartRate(LocalTime.of(0, 34), 55),
  HeartRate(LocalTime.of(0, 52), 145),
  HeartRate(LocalTime.of(0, 40), 99),
  HeartRate(LocalTime.of(0, 19), 72),
  HeartRate(LocalTime.of(0, 14), 150),
  HeartRate(LocalTime.of(1, 44), 95),
  HeartRate(LocalTime.of(1, 58), 105),
  HeartRate(LocalTime.of(1, 21), 170),
  HeartRate(LocalTime.of(1, 49), 152),
  HeartRate(LocalTime.of(1, 31), 55),
  HeartRate(LocalTime.of(1, 20), 158),
  HeartRate(LocalTime.of(1, 41), 67),
  HeartRate(LocalTime.of(1, 21), 65),
  HeartRate(LocalTime.of(2, 4), 159),
  HeartRate(LocalTime.of(2, 19), 174),
  HeartRate(LocalTime.of(2, 19), 117),
  HeartRate(LocalTime.of(2, 0), 84),
  HeartRate(LocalTime.of(2, 33), 152),
  HeartRate(LocalTime.of(2, 4), 162),
  HeartRate(LocalTime.of(3, 11), 55),
  HeartRate(LocalTime.of(3, 22), 93),
  HeartRate(LocalTime.of(3, 39), 133),
  HeartRate(LocalTime.of(3, 15), 173),
  HeartRate(LocalTime.of(3, 7), 172),
  HeartRate(LocalTime.of(4, 8), 93),
  HeartRate(LocalTime.of(4, 27), 148),
  HeartRate(LocalTime.of(4, 8), 153),
  HeartRate(LocalTime.of(4, 47), 170),
  HeartRate(LocalTime.of(4, 11), 60),
  HeartRate(LocalTime.of(4, 46), 100),
  HeartRate(LocalTime.of(4, 15), 175),
  HeartRate(LocalTime.of(5, 39), 133),
  HeartRate(LocalTime.of(5, 16), 98),
  HeartRate(LocalTime.of(5, 59), 80),
  HeartRate(LocalTime.of(5, 17), 122),
  HeartRate(LocalTime.of(5, 55), 144),
  HeartRate(LocalTime.of(5, 5), 101),
  HeartRate(LocalTime.of(5, 3), 141),
  HeartRate(LocalTime.of(5, 10), 153),
  HeartRate(LocalTime.of(5, 17), 135),
  HeartRate(LocalTime.of(6, 28), 117),
  HeartRate(LocalTime.of(6, 22), 153),
  HeartRate(LocalTime.of(6, 38), 103),
//  HeartRate(LocalTime.of(6, 9), 65),
//  HeartRate(LocalTime.of(6, 31), 172),
//  HeartRate(LocalTime.of(6, 19), 135),
//  HeartRate(LocalTime.of(6, 14), 103),
//  HeartRate(LocalTime.of(6, 23), 106),
//  HeartRate(LocalTime.of(7, 56), 70),
//  HeartRate(LocalTime.of(7, 50), 140),
//  HeartRate(LocalTime.of(7, 13), 124),
//  HeartRate(LocalTime.of(7, 42), 86),
//  HeartRate(LocalTime.of(7, 51), 163),
//  HeartRate(LocalTime.of(7, 15), 138),
//  HeartRate(LocalTime.of(7, 14), 77),
//  HeartRate(LocalTime.of(7, 52), 140),
//  HeartRate(LocalTime.of(8, 57), 173),
//  HeartRate(LocalTime.of(8, 9), 153),
//  HeartRate(LocalTime.of(8, 26), 102),
//  HeartRate(LocalTime.of(8, 30), 54),
//  HeartRate(LocalTime.of(8, 3), 155),
//  HeartRate(LocalTime.of(8, 43), 164),
//  HeartRate(LocalTime.of(8, 49), 98),
//  HeartRate(LocalTime.of(9, 26), 171),
//  HeartRate(LocalTime.of(9, 7), 142),
//  HeartRate(LocalTime.of(9, 45), 135),
//  HeartRate(LocalTime.of(9, 42), 68),
  HeartRate(LocalTime.of(9, 6), 92),
  HeartRate(LocalTime.of(9, 15), 141),
  HeartRate(LocalTime.of(9, 22), 120),
  HeartRate(LocalTime.of(10, 50), 125),
  HeartRate(LocalTime.of(10, 4), 109),
  HeartRate(LocalTime.of(10, 59), 174),
  HeartRate(LocalTime.of(10, 11), 115),
  HeartRate(LocalTime.of(10, 13), 92),
  HeartRate(LocalTime.of(10, 4), 127),
  HeartRate(LocalTime.of(10, 8), 62),
  HeartRate(LocalTime.of(10, 9), 129),
  HeartRate(LocalTime.of(11, 7), 128),
  HeartRate(LocalTime.of(11, 44), 67),
  HeartRate(LocalTime.of(11, 10), 130),
  HeartRate(LocalTime.of(11, 12), 153),
  HeartRate(LocalTime.of(11, 5), 133),
  HeartRate(LocalTime.of(11, 31), 174),
  HeartRate(LocalTime.of(11, 45), 91),
  HeartRate(LocalTime.of(11, 9), 95),
  HeartRate(LocalTime.of(11, 4), 102),
  HeartRate(LocalTime.of(11, 46), 147),
  HeartRate(LocalTime.of(11, 48), 145),
  HeartRate(LocalTime.of(11, 44), 131),
  HeartRate(LocalTime.of(12, 40), 159),
  HeartRate(LocalTime.of(12, 14), 150),
  HeartRate(LocalTime.of(12, 37), 118),
  HeartRate(LocalTime.of(12, 38), 134),
  HeartRate(LocalTime.of(12, 53), 168),
  HeartRate(LocalTime.of(12, 11), 143),
  HeartRate(LocalTime.of(12, 47), 110),
  HeartRate(LocalTime.of(12, 21), 116),
  HeartRate(LocalTime.of(12, 13), 145),
  HeartRate(LocalTime.of(13, 37), 56),
  HeartRate(LocalTime.of(13, 9), 132),
  HeartRate(LocalTime.of(13, 6), 98),
  HeartRate(LocalTime.of(13, 22), 134),
  HeartRate(LocalTime.of(13, 25), 125),
  HeartRate(LocalTime.of(13, 47), 101),
  HeartRate(LocalTime.of(13, 50), 138),
  HeartRate(LocalTime.of(13, 47), 59),
  HeartRate(LocalTime.of(13, 55), 105),
  HeartRate(LocalTime.of(14, 56), 73),
  HeartRate(LocalTime.of(14, 7), 67),
  HeartRate(LocalTime.of(14, 33), 118),
  HeartRate(LocalTime.of(14, 50), 169),
  HeartRate(LocalTime.of(14, 2), 125),
  HeartRate(LocalTime.of(14, 16), 93),
  HeartRate(LocalTime.of(14, 7), 80),
  HeartRate(LocalTime.of(14, 1), 129),
  HeartRate(LocalTime.of(14, 59), 142),
  HeartRate(LocalTime.of(15, 5), 62),
  HeartRate(LocalTime.of(15, 55), 132),
  HeartRate(LocalTime.of(15, 41), 145),
  HeartRate(LocalTime.of(15, 41), 107),
  HeartRate(LocalTime.of(15, 45), 110),
  HeartRate(LocalTime.of(16, 52), 97),
  HeartRate(LocalTime.of(16, 16), 127),
  HeartRate(LocalTime.of(16, 0), 155),
  HeartRate(LocalTime.of(16, 35), 75),
  HeartRate(LocalTime.of(16, 18), 170),
  HeartRate(LocalTime.of(16, 6), 68),
  HeartRate(LocalTime.of(16, 12), 63),
  HeartRate(LocalTime.of(16, 2), 162),
  HeartRate(LocalTime.of(16, 40), 146),
  HeartRate(LocalTime.of(16, 26), 70),
  HeartRate(LocalTime.of(16, 32), 121),
  HeartRate(LocalTime.of(17, 49), 87),
  HeartRate(LocalTime.of(17, 42), 54),
  HeartRate(LocalTime.of(17, 12), 169),
  HeartRate(LocalTime.of(17, 24), 154),
  HeartRate(LocalTime.of(17, 4), 75),
  HeartRate(LocalTime.of(17, 51), 104),
  HeartRate(LocalTime.of(17, 53), 114),
  HeartRate(LocalTime.of(17, 14), 93),
  HeartRate(LocalTime.of(17, 35), 146),
  HeartRate(LocalTime.of(17, 19), 101),
  HeartRate(LocalTime.of(17, 27), 130),
  HeartRate(LocalTime.of(17, 2), 56),
  HeartRate(LocalTime.of(17, 27), 55),
  HeartRate(LocalTime.of(17, 31), 73),
  HeartRate(LocalTime.of(18, 59), 103),
  HeartRate(LocalTime.of(18, 10), 95),
  HeartRate(LocalTime.of(18, 28), 120),
  HeartRate(LocalTime.of(18, 5), 88),
  HeartRate(LocalTime.of(18, 44), 63),
  HeartRate(LocalTime.of(18, 16), 124),
  HeartRate(LocalTime.of(18, 14), 120),
  HeartRate(LocalTime.of(18, 18), 121),
  HeartRate(LocalTime.of(18, 53), 167),
  HeartRate(LocalTime.of(18, 45), 110),
  HeartRate(LocalTime.of(19, 19), 170),
  HeartRate(LocalTime.of(19, 59), 85),
  HeartRate(LocalTime.of(19, 4), 84),
  HeartRate(LocalTime.of(19, 8), 111),
  HeartRate(LocalTime.of(19, 54), 75),
  HeartRate(LocalTime.of(20, 36), 122),
  HeartRate(LocalTime.of(20, 21), 153),
  HeartRate(LocalTime.of(20, 11), 82),
  HeartRate(LocalTime.of(20, 19), 152),
  HeartRate(LocalTime.of(20, 26), 56),
  HeartRate(LocalTime.of(20, 21), 63),
  HeartRate(LocalTime.of(20, 22), 90),
  HeartRate(LocalTime.of(20, 20), 172),
  HeartRate(LocalTime.of(20, 56), 78),
  HeartRate(LocalTime.of(21, 52), 65),
  HeartRate(LocalTime.of(21, 46), 106),
  HeartRate(LocalTime.of(21, 57), 129),
  HeartRate(LocalTime.of(21, 31), 105),
  HeartRate(LocalTime.of(21, 39), 138),
  HeartRate(LocalTime.of(21, 0), 93),
  HeartRate(LocalTime.of(21, 20), 67),
  HeartRate(LocalTime.of(21, 47), 166),
  HeartRate(LocalTime.of(21, 10), 136),
  HeartRate(LocalTime.of(21, 26), 90),
  HeartRate(LocalTime.of(21, 56), 83),
  HeartRate(LocalTime.of(21, 9), 72),
  HeartRate(LocalTime.of(21, 38), 87),
  HeartRate(LocalTime.of(22, 15), 149),
  HeartRate(LocalTime.of(22, 25), 176),
  HeartRate(LocalTime.of(22, 13), 77),
  HeartRate(LocalTime.of(22, 53), 159),
  HeartRate(LocalTime.of(22, 20), 81),
  HeartRate(LocalTime.of(22, 48), 150),
  HeartRate(LocalTime.of(22, 1), 123),
  HeartRate(LocalTime.of(22, 19), 130),
  HeartRate(LocalTime.of(23, 27), 147),
  HeartRate(LocalTime.of(23, 59), 126),
  HeartRate(LocalTime.of(23, 22), 142),
  HeartRate(LocalTime.of(23, 48), 114),
  HeartRate(LocalTime.of(23, 51), 93),
  HeartRate(LocalTime.of(23, 46), 65),
  HeartRate(LocalTime.of(23, 21), 63),
  HeartRate(LocalTime.of(23, 59), 95),
).sortedBy { it.date.toSecondOfDay() }

const val numberEntries = 48 // 48 blocks of 30 minutes
const val bracketInSeconds = 30 * 60 // 30 minutes time frame

val groupedMeasurements = (0..numberEntries).map { bracketStart ->
  graphData.filter {
    (bracketStart * bracketInSeconds..(bracketStart + 1) * bracketInSeconds)
      .contains(it.date.toSecondOfDay())
  }
}.map { heartRates ->
  if (heartRates.isEmpty()) DataPoint.NoMeasurement else
    DataPoint.Measurement(
      averageMeasurementTime = heartRates.map { it.date.toSecondOfDay() }.average().roundToInt(),
      minHeartRate = heartRates.minBy { it.amount }.amount,
      maxHeartRate = heartRates.maxBy { it.amount }.amount,
      averageHeartRate = heartRates.map { it.amount }.average().roundToInt()
    )
}

val maxValue = graphData.maxBy { it.amount }.amount
val minValue = graphData.minBy { it.amount }.amount
val graphTop = ((maxValue + 5) / 10f).roundToInt() * 10
val graphBottom = (minValue / 10f).toInt() * 10
val range = graphTop - graphBottom