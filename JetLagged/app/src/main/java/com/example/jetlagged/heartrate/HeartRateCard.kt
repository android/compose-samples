package com.example.jetlagged.heartrate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.R
import com.example.jetlagged.data.HeartRateOverallData
import com.example.jetlagged.ui.theme.Coral
import com.example.jetlagged.ui.theme.LegendHeadingStyle
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.TitleStyle

@Preview
@Composable
fun HeartRateCard(heartRateData: HeartRateOverallData = HeartRateOverallData()) {
    BasicInformationalCard(
        borderColor = Coral,
        modifier = Modifier
            .height(260.dp)
            .widthIn(max = 400.dp, min = 200.dp)
    ) {
        Column {
            HomeScreenCardHeading(text = stringResource(R.string.heart_rate_heading))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(heartRateData.averageBpm.toString(),
                    style = TitleStyle,
                    modifier = Modifier.alignByBaseline(),
                    textAlign = TextAlign.Center
                )
                Text("bpm",
                    modifier = Modifier.alignByBaseline(),
                    style = SmallHeadingStyle)
            }

            HeartRateGraph(heartRateData.listData)
        }
    }
}
