package com.rs.crypto.composables.trade_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rs.crypto.ui.theme.LightGray1
import com.rs.crypto.utils.Constants

@Composable
fun LimitSection() {
    TransactSection()

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp)
            .clip(shape = RoundedCornerShape(4.dp))
            .background(color = LightGray1)
        ) {
            RepeatOptionsItem(
                iconImageVector = Icons.Default.DateRange,
                optionName = "Duration",
                frequency = "Good 'til canceled"
            )
        }
    }
}


@Preview
@Composable
fun LimSectionPreview() {
    Surface() {
        Column() {
            LimitSection()
        }
    }
}