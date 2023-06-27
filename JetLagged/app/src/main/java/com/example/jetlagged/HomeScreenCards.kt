package com.example.jetlagged

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
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
        Box {
            content()
        }
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
    BasicInformationalCard(borderColor = borderColor,
        modifier = modifier.size(200.dp)) {
        BubbleBackground(
            modifier = Modifier.fillMaxSize(),
            numberBubbles = 3, bubbleColor = borderColor.copy(0.25f))
        BoxWithConstraints(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            if (maxWidth > 400.dp) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(CenterStart)
                ) {
                    Icon(
                        icon, contentDescription = null, modifier = Modifier
                            .size(50.dp)
                            .align(CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier
                        .align(CenterVertically)
                        .wrapContentSize()) {
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
                Column(modifier = Modifier
                    .wrapContentSize()
                    .align(Center)) {
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
        firstLineText = stringResource(R.string.ave_time_in_bed_heading),
        secondLineText = "8h42min",
        icon = Icons.Default.Watch,
        modifier = modifier.animateBounds(
            Modifier
                .wrapContentWidth()
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
        firstLineText = stringResource(R.string.ave_time_sleep_heading),
        secondLineText = "7h42min",
        icon = Icons.Default.SingleBed,
        modifier = modifier.animateBounds(
            Modifier
                .wrapContentWidth()
                .heightIn(min = 156.dp))
    )
}

@Composable
fun BubbleBackground(
    modifier: Modifier = Modifier,
    numberBubbles: Int,
    bubbleColor: Color) {
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Preview
@Composable
fun WellnessCard(wellnessData: WellnessData = WellnessData(0, 0, 0)) {
    BasicInformationalCard(
        borderColor = LightBlue, modifier = Modifier.animateBounds(
            Modifier
                .widthIn(max = 400.dp)
                .heightIn(min = 200.dp)
        )
    ) {
        BubbleBackground(numberBubbles = 10, bubbleColor = LightBlue.copy(alpha = 0.25f))
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HomeScreenCardHeading(text = stringResource(R.string.wellness_heading))
            FlowRow(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                WellnessBubble(titleText = stringResource(R.string.snoring_heading), countText = wellnessData.snoring.toString(), metric = "min")
                WellnessBubble(titleText = stringResource(R.string.coughing_heading), countText = wellnessData.coughing.toString(), metric = "times")
                WellnessBubble(titleText = stringResource(R.string.respiration_heading), countText = wellnessData.respiration.toString(), metric = "rpm")
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
fun HeartRateCard(heartRateData: HeartRateData = HeartRateData(0)) {
    BasicInformationalCard(
        borderColor = Coral,
        modifier = Modifier
            .height(260.dp)
            .widthIn(max = 400.dp, min = 200.dp)
    ) {
        // TODO
        HomeScreenCardHeading(text = stringResource(R.string.heart_rate_heading))
    }
}

data class BackgroundBubbleData(
    val startPosition: Offset = Offset.Zero,
    val endPosition: Offset = Offset.Zero,
    val durationMillis: Long = 2000,
    val easingFunction: Easing = EaseInOut,
    val radius: Dp = 0.dp
)

@Composable
fun HomeScreenCardHeading(text : String){
    Text(text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        textAlign = TextAlign.Center,
        style = HeadingStyle
    )
}

@Preview
@Composable
fun AmbienceCard() {
    BasicInformationalCard(borderColor = Lilac, modifier = Modifier.size(250.dp)) {
        HomeScreenCardHeading(text = stringResource(R.string.ambiance_heading))
    }
}