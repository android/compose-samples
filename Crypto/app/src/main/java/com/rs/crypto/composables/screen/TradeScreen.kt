package com.rs.crypto.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.composables.trade_components.LimitSection
import com.rs.crypto.composables.trade_components.RepeatSection
import com.rs.crypto.composables.trade_components.TransactSection
import com.rs.crypto.ui.theme.BannerColor
import com.rs.crypto.ui.theme.RobotoBold
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun TradeScreen(
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        var selectedIndex = mutableStateOf(1)

        selectedIndex.value = SectionSelectorRow()

        when(selectedIndex.value) {
            0 -> TransactSection()
            1 -> RepeatSection()
            2 -> LimitSection()
        }
        
        Button(
            onClick = {
                onButtonClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding((2 * Constants.PADDING_SIDE_VALUE).dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = BannerColor
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "Trade",
                color = Color.White
            )
        }
    }
}

@Composable
private fun SectionSelectorRow(): Int {
    var selectedSectionIndex by remember {
        mutableStateOf(0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp)
            .clip(RoundedCornerShape((2 * Constants.ELEVATION_VALUE).dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DummyData.tradeScreenSections.forEachIndexed { index, sectionName ->
            SectionCard(
                isSelected = selectedSectionIndex == index,
                text = sectionName,
                modifier = Modifier
                    .weight(1f),
                onCardClick = {
                    selectedSectionIndex = index
                }
            )
        }
    }

    return selectedSectionIndex
}

@Composable
private fun SectionCard(
    isSelected: Boolean = false,
    text: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit
) {
    val cardColor = if(isSelected) {
        BannerColor
    } else {
        Color.White
    }

    val cardTextColor = if(isSelected) {
        Color.White
    } else {
        Color.Black
    }

    Card(
        backgroundColor = cardColor,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((2 * Constants.ELEVATION_VALUE).dp))
            .clickable(onClick = onCardClick),
        elevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(
                    vertical = (Constants.PADDING_SIDE_VALUE / 2).dp,
                ),
            color = cardTextColor,
            fontSize = 14.sp,
            fontFamily = RobotoBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun TradeScreenPreview() {
    Surface() {
        TradeScreen(
            onButtonClick = {

            }
        )
    }
}