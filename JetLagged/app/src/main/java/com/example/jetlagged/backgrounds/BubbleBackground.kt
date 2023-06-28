package com.example.jetlagged.backgrounds

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlin.random.Random


@Composable
fun BubbleBackground(
    modifier: Modifier = Modifier,
    numberBubbles: Int,
    bubbleColor: Color
) {
    val infiniteAnimation = rememberInfiniteTransition(label = "bubble position")

    Box(modifier = modifier) {
        val bubbles = remember(numberBubbles) {
            List(numberBubbles) {
                BackgroundBubbleData(
                    startPosition = Offset(
                        x = Random.nextFloat(),
                        y = Random.nextFloat()
                    ),
                    endPosition = Offset(
                        x = Random.nextFloat(),
                        y = Random.nextFloat()
                    ),
                    durationMillis = Random.nextLong(3000L, 10000L),
                    easingFunction = EaseInOut,
                    radius = Random.nextFloat() * 30.dp + 20.dp
                )
            }
        }
        for (bubble in bubbles) {
            val xValue by infiniteAnimation.animateFloat(
                initialValue = bubble.startPosition.x,
                targetValue = bubble.endPosition.x,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        bubble.durationMillis.toInt(),
                        easing = bubble.easingFunction
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            val yValue by infiniteAnimation.animateFloat(
                initialValue = bubble.startPosition.y,
                targetValue = bubble.endPosition.y,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        bubble.durationMillis.toInt(),
                        easing = bubble.easingFunction
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    bubbleColor,
                    radius = bubble.radius.toPx(),
                    center = Offset(xValue * size.width, yValue * size.height)
                )
            }
        }
    }

}

data class BackgroundBubbleData(
    val startPosition: Offset = Offset.Zero,
    val endPosition: Offset = Offset.Zero,
    val durationMillis: Long = 2000,
    val easingFunction: Easing = EaseInOut,
    val radius: Dp = 0.dp
)