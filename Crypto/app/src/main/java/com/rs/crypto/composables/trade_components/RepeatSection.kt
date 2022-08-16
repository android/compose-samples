package com.rs.crypto.composables.trade_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rs.crypto.ui.theme.LightGray1
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun RepeatSection() {
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
            DummyData.repeatOptions.forEachIndexed{ index, repeatOptions ->
                RepeatOptionsItem(
                    iconImageVector = repeatOptions.iconImageVector,
                    optionName = repeatOptions.optionName,
                    frequency = repeatOptions.frequency
                )
            }
        }
    }
}

@Preview
@Composable
fun RepeatSectionPreview() {
    Surface() {
        Column() {
            RepeatSection()
        }
    }
}