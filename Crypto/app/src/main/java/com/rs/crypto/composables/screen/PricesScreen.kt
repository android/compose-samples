package com.rs.crypto.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.composables.prices_components.*
import com.rs.crypto.ui.theme.BannerColor
import com.rs.crypto.ui.theme.Primary
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun PricesScreen(
    onBackArrowPressed: () -> Unit,
    onCoinSearch: (String) -> Unit,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Primary
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 50.dp)
        ) {
            TopNavigationRow(
                onBackArrowPressed = onBackArrowPressed,
                isStarNeeded = false
            )

            SearchField(onCoinSearch = onCoinSearch)

            var selectedIndex = mutableStateOf(0)

            selectedIndex.value = ChipsRow()

            Spacer(modifier = Modifier.height(Constants.PADDING_SIDE_VALUE.dp))

            when(selectedIndex.value) {
                0 -> AllSection(
                    onItemClick = onItemClick
                )
                1 -> FollowingSection(
                    onItemClick = onItemClick
                )
                2 -> CryptoSection(
                    onItemClick = onItemClick
                )
                3 -> UtilityTokensSection(
                    onItemClick = onItemClick
                )
                4 -> StableCoinsSection(
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun ChipsRow(): Int {
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    LazyRow() {
        items(count = DummyData.topChipsName.size) { index ->
            if (index == 0) {
                Spacer(modifier = Modifier.width(Constants.PADDING_SIDE_VALUE.dp))
            }

            SectionChip(
                text = DummyData.topChipsName[index],
                isSelected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                }
            )
            Spacer(modifier = Modifier.width(Constants.ELEVATION_VALUE.dp))
        }
    }
    return selectedIndex
}

@Composable
private fun SectionChip(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
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
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(
                    horizontal = Constants.ELEVATION_VALUE.dp,
                    vertical = (Constants.PADDING_SIDE_VALUE / 2).dp,
                ),
            color = cardTextColor,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
fun PricesScreenPreview() {
    PricesScreen(
        onBackArrowPressed = {

        },
        onCoinSearch = {
            
        },
        onItemClick = {

        }
    )
}