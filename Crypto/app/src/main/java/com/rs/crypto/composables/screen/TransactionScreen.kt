package com.rs.crypto.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rs.crypto.models.TrendingCurrency
import com.rs.crypto.ui.theme.BlueGrey700
import com.rs.crypto.ui.theme.LightGray1
import com.rs.crypto.ui.theme.Secondary
import com.rs.crypto.ui.theme.Typography
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun TransactionScreen(
    currencyCode: String,
    onBackArrowPressed: () -> Unit,
    onTradeButtonClick: (String) -> Unit
) {
    val currency = DummyData.trendingCurrencies.find { it.currencyCode == currencyCode }!!

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Secondary
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 50.dp)
        ) {
            TopNavigationRow(
                onBackArrowPressed = onBackArrowPressed,
                isStarNeeded = false
            )

            Card(
                modifier = Modifier
                    .padding(Constants.PADDING_SIDE_VALUE.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = Constants.ELEVATION_VALUE.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = Constants.PADDING_SIDE_VALUE.dp,
                            start = Constants.PADDING_SIDE_VALUE.dp,
                            end = Constants.PADDING_SIDE_VALUE.dp
                        )
                ) {
                    CurrencyItem(currency = currency)

                    Spacer(modifier = Modifier.height(Constants.PADDING_SIDE_VALUE.dp))

                    TransactionCurrencyValueTextSection(currency)

                    Spacer(modifier = Modifier.height(Constants.PADDING_SIDE_VALUE.dp))

                    StandardButton(
                        onButtonClick = onTradeButtonClick,
                        currency = currency,
                        buttonText = "Trade"
                    )
                }
            }

            TransactionHistorySection()
        }
    }
}

@Composable
private fun TransactionCurrencyValueTextSection(currency: TrendingCurrency) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%.3f ${currency.currencyCode}".format(currency.wallet.crypto),
            style = Typography.h2
        )

        Text(
            text = "£${DummyData.portfolio.balance}",
            style = Typography.subtitle2,
            color = BlueGrey700
        )
    }
}

@Preview
@Composable
fun TransactionScreenPreview() {
    TransactionScreen(
        onBackArrowPressed = {

        },
        currencyCode = "ETH",
        onTradeButtonClick = {

        }
    )
}