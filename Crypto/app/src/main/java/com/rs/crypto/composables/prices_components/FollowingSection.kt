package com.rs.crypto.composables.prices_components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.models.TrendingCurrency
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun FollowingSection(
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
    ) {
        Text(
            text = "Following",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(Constants.PADDING_SIDE_VALUE.dp))

        LazyColumn {
            itemsIndexed(items = DummyData.followingTokens) { index: Int, item: TrendingCurrency ->
                CoinRateItem(
                    currency = item,
                    onItemClick = onItemClick
                )
            }
        }
    }
}