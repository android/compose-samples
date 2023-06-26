package com.example.jetlagged

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetlagged.ui.theme.Coral
import com.example.jetlagged.ui.theme.HeadingStyle
import com.example.jetlagged.ui.theme.LightBlue
import com.example.jetlagged.ui.theme.Lilac
import com.example.jetlagged.ui.theme.MintGreen
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import kotlin.random.Random

@Composable
fun BasicInformationalCard(
    modifier: Modifier = Modifier,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .padding(8.dp)
            .border(2.dp, borderColor, shape)
    ) {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TwoLineInfoCard(
    borderColor: Color,
    firstLineText: String,
    secondLineText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    BasicInformationalCard(borderColor = borderColor) {
        BubbleBackground(numberBubbles = 3, bubbleColor = borderColor.copy(0.25f))
        BoxWithConstraints(
            modifier = modifier.padding(16.dp),
        ) {
            if (maxWidth > 400.dp) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(CenterStart)
                ) {
                    Icon(
                        icon, contentDescription = null, modifier = Modifier
                            .size(50.dp)
                            .align(CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.align(CenterVertically)) {
                        Text(
                            firstLineText,
                            style = SmallHeadingStyle
                        )
                        Text(
                            secondLineText,
                            style = HeadingStyle,
                        )
                    }
                }
            } else {
                Column {
                    Icon(
                        icon, contentDescription = null, modifier = Modifier
                            .size(50.dp)
                            .align(CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.align(CenterHorizontally)) {
                        Text(
                            firstLineText,
                            style = SmallHeadingStyle,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        Text(
                            secondLineText,
                            style = HeadingStyle,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Preview(widthDp = 500, name = "larger screen")
@Composable
fun AverageTimeInBedCard(modifier: Modifier = Modifier) {

    TwoLineInfoCard(borderColor = Lilac,
        firstLineText = "AVE TIME IN BED",
        secondLineText = "8h42min",
        icon = Icons.Default.Watch,
        modifier = modifier.animateBounds(
                Modifier.wrapContentWidth()
            .heightIn(min = 156.dp))

    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Preview(widthDp = 500, name = "larger screen")
@Composable
fun AverageTimeAsleepCard(modifier: Modifier = Modifier) {
    TwoLineInfoCard(
        borderColor = MintGreen,
        firstLineText = "AVE TIME SLEEP",
        secondLineText = "7h42min",
        icon = Icons.Default.SingleBed,
        modifier = modifier.animateBounds(
            Modifier.wrapContentWidth()
            .heightIn(min = 156.dp))
    )
}

@Composable
fun BubbleBackground(numberBubbles: Int, bubbleColor: Color) {
    val infiniteAnimation = rememberInfiniteTransition(label = "bubble position")

    BoxWithConstraints(modifier = Modifier) {
        val bubbles = remember {
            List(numberBubbles) {
                BackgroundBubbleData(
                    startPosition = Offset(
                        x = Random.nextFloat() * 400 ,
                        y = Random.nextFloat() * 400
                    ),
                    endPosition = Offset(
                        x = Random.nextFloat() * 400,
                        y = Random.nextFloat() * 400
                    ),
                    durationMillis = Random.nextLong(3000L, 10000L),
                    easingFunction = EaseInOut,
                    radius = Random.nextFloat() * 50f + 50f
                )
            }
        }
        for (bubble in bubbles) {
            val xValue by infiniteAnimation.animateFloat(
                initialValue = bubble.startPosition.x,
                targetValue = bubble.endPosition.x,
                animationSpec = infiniteRepeatable(
                    animation = tween(bubble.durationMillis.toInt(), easing = bubble.easingFunction),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            val yValue by infiniteAnimation.animateFloat(
                initialValue = bubble.startPosition.y,
                targetValue = bubble.endPosition.y,
                animationSpec = infiniteRepeatable(
                    animation = tween(bubble.durationMillis.toInt(), easing = bubble.easingFunction),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Canvas(modifier = Modifier) {
                drawCircle(
                    bubbleColor,
                    radius = bubble.radius,
                    center = Offset(xValue, yValue)
                )
            }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Preview
@Composable
fun WellnessCard() {
    BasicInformationalCard(
        borderColor = LightBlue, modifier = Modifier.animateBounds(
            Modifier.widthIn(max = 400.dp)
                .heightIn(min = 200.dp)
        )
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                "Wellness",
                modifier = Modifier
                    .align(CenterHorizontally)
            )
            FlowRow(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                WellnessBubble(titleText = "Snoring", countText = "5", metric = "min")
                WellnessBubble(titleText = "Coughing", countText = "0", metric = "times")
                WellnessBubble(titleText = "Respiration", countText = "15", metric = "rpm")
            }
        }
    }
}

@Composable
fun WellnessBubble(
    titleText: String,
    countText: String,
    metric: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .sizeIn(maxHeight = 100.dp)
            .aspectRatio(1f)
            .drawBehind {
                drawCircle(LightBlue)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(titleText, fontSize = 12.sp)
        Text(countText, fontSize = 36.sp)
        Text(metric, fontSize = 12.sp)
    }
}

@Preview
@Composable
fun HeartRateCard() {
    BasicInformationalCard(
        borderColor = Coral,
        modifier = Modifier
            .height(160.dp)
            .widthIn(max = 400.dp)
    ) {
        // TODO
        Text("Heart Rate")
    }
}

data class BackgroundBubbleData(
    val startPosition: Offset = Offset.Zero,
    val endPosition: Offset = Offset.Zero,
    val durationMillis: Long = 2000,
    val easingFunction: Easing = EaseInOut,
    val radius: Float = 5f
)