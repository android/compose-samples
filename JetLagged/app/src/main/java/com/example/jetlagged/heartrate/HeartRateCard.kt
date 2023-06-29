package com.example.jetlagged.heartrate

import android.graphics.PointF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.R
import com.example.jetlagged.data.HeartRateData
import com.example.jetlagged.data.HeartRateOverallData
import com.example.jetlagged.data.generateFakeHeartRateData
import com.example.jetlagged.ui.theme.Coral
import kotlinx.coroutines.launch

@Preview
@Composable
fun HeartRateCard(heartRateData: HeartRateOverallData = HeartRateOverallData()) {
    BasicInformationalCard(
        borderColor = Coral,
        modifier = Modifier
            .height(260.dp)
            .widthIn(max = 400.dp, min = 200.dp)
    ) {
        // TODO
        HomeScreenCardHeading(text = stringResource(R.string.heart_rate_heading))
      // TODO  HeartRateLineGraph(heartRateData = heartRateData.listData)
    }
}
