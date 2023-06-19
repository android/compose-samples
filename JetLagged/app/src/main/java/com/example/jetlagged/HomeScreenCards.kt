package com.example.jetlagged

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.jetlagged.ui.theme.Yellow
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import kotlin.math.min

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

@Preview
@Preview(widthDp = 500, name = "larger screen")
@Composable
fun AverageTimeInBedCard(modifier: Modifier = Modifier) {
    TwoLineInfoCard(borderColor = Lilac,
        firstLineText = "AVE TIME IN BED",
        secondLineText = "8h42min",
        icon = Icons.Default.Watch,
        modifier = modifier
            .wrapContentWidth()
            .heightIn(min = 156.dp)
            .drawBehind {
                drawCircle(
                    Lilac.copy(alpha = 0.25f),
                    center = Offset(0f, size.minDimension / 4f),
                    radius = size.minDimension / 1.5f
                )
            }
    )
}

@Preview
@Preview(widthDp = 500, name = "larger screen")
@Composable
fun AverageTimeAsleepCard(modifier: Modifier = Modifier) {
    TwoLineInfoCard(borderColor = MintGreen,
        firstLineText = "AVE TIME SLEEP",
        secondLineText = "7h42min",
        icon = Icons.Default.SingleBed,
        modifier = modifier
            .wrapContentWidth()
            .heightIn(min = 156.dp)
            .drawBehind {
                drawCircle(
                    MintGreen.copy(alpha = 0.25f),
                    center = Offset(size.minDimension / 2f, size.minDimension / 1.2f),
                    radius = size.minDimension / 1.5f
                )
            }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun WellnessCard() {
    BasicInformationalCard(
        borderColor = LightBlue, modifier = Modifier
            .widthIn(max = 400.dp)
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
            FlowRow(horizontalArrangement = Arrangement.Center) {
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
    metric: String
) {
    Column(
        modifier = Modifier
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