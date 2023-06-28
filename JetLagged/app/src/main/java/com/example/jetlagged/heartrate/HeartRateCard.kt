package com.example.jetlagged.heartrate

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.R
import com.example.jetlagged.data.HeartRateData
import com.example.jetlagged.ui.theme.Coral

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