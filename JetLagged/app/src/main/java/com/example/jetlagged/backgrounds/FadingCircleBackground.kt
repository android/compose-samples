package com.example.jetlagged.backgrounds

import android.graphics.BlendMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun FadingCircleBackground(bubbleSize: Dp, color: Color) {
    val alphaAnimation = remember {
        Animatable(0.5f)
    }
    LaunchedEffect(Unit) {
        alphaAnimation.animateTo(
            1f, animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
            val bubbleSizePx = bubbleSize.toPx()
            val paddingPx = 8.dp.toPx()
            val numberCols = size.width / bubbleSizePx
            val numberRows = size.height / bubbleSizePx

            onDrawBehind {
                repeat(ceil(numberRows).toInt()) { row ->
                    repeat(ceil(numberCols).toInt()) { col ->
                        val offset = if (row.mod(2) == 0)
                                (bubbleSizePx + paddingPx) / 2f else 0f
                        drawCircle(
                            color.copy(
                                alpha = color.alpha * ((row) / numberRows * alphaAnimation.value)),
                            radius = bubbleSizePx / 2f,
                            center = Offset(
                                (bubbleSizePx + paddingPx) * col + offset,
                                (bubbleSizePx + paddingPx) * row
                            )
                        )
                    }
                }
            }
        })
}

@Preview
@Composable
fun FadingCirclePreview() {
    FadingCircleBackground(bubbleSize = 30.dp, color = Color.Red)
}