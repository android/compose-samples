package com.rs.crypto.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rs.crypto.models.TrendingCurrency
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData
import com.rs.crypto.R
import com.rs.crypto.ui.theme.*

@Composable
fun CryptoDetailScreen(
    currencyCode: String,
    onBackArrowPressed: () -> Unit,
    onButtonClick: (String) -> Unit
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
            TopNavigationRow(onBackArrowPressed = onBackArrowPressed)

            LineChartCardSection(currency = currency)

            BuyCryptoCard(
                currency = currency,
                onButtonClick = onButtonClick
            )

            CurrencyDescriptionCard(currency = currency)

            SetPriceAlertSection()
        }
    }
}

@Composable
private fun CurrencyDescriptionCard(currency: TrendingCurrency) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = Constants.ELEVATION_VALUE.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_SIDE_VALUE.dp)
        ) {
            Text(
                text = "About ${currency.currencyName}",
                style = Typography.h2
            )
            
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${currency.description}",
                style = Typography.subtitle2
            )
        }
    }
}

@Composable
private fun BuyCryptoCard(
    currency: TrendingCurrency,
    onButtonClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(Constants.PADDING_SIDE_VALUE.dp)
            .fillMaxWidth(),
        elevation = Constants.ELEVATION_VALUE.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column() {
            CurrencyInfoBuyRow(currency = currency)

            StandardButton(
                onButtonClick = onButtonClick,
                currency = currency,
                buttonText = "Buy"
            )
        }
    }
}

@Composable
fun StandardButton(
    onButtonClick: (String) -> Unit,
    currency: TrendingCurrency,
    buttonText: String
) {
    Button(
        onClick = {
            onButtonClick(currency.currencyCode)
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Secondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = buttonText,
            color = Color.White
        )
    }
}

@Composable
private fun CurrencyInfoBuyRow(currency: TrendingCurrency) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp)
    ) {
        CurrencyItem(currency = currency)

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${DummyData.portfolio.balance}",
                    style = Typography.h2,
                )

                Text(
                    text = "${currency.wallet.crypto} ${currency.currencyCode}",
                    style = Typography.subtitle2,
                    color = BlueGrey700
                )
            }

            Image(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = null,
                modifier = Modifier
                    .clipToBounds()
                    .padding(start = (Constants.PADDING_SIDE_VALUE * 1.5).dp)
            )
        }
    }
}

@Composable
private fun LineChartCardSection(currency: TrendingCurrency) {
    Card(
        modifier = Modifier
            .padding(Constants.PADDING_SIDE_VALUE.dp)
            .fillMaxWidth(),
        elevation = Constants.ELEVATION_VALUE.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CardCurrencyInfoSection(currency = currency)

//                    Insert line chart later
            Image(
                painter = painterResource(id = R.drawable.sample_line_chart_image),
                contentDescription = "Line chart image",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun CardCurrencyInfoSection(
    currency: TrendingCurrency
) {
    Column() {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_SIDE_VALUE.dp)
        ) {
            CurrencyItem(currency = currency)

            Column() {
                ValuesItem(
                    currency = currency,
                    priceModifier = Modifier,
                    currencyPriceStyle = Typography.h2
                )
            }
        }
    }
}

@Composable
fun TopNavigationRow(
    onBackArrowPressed: () -> Unit,
    isStarNeeded: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .padding(
                top = (Constants.ELEVATION_VALUE + Constants.PADDING_SIDE_VALUE).dp,
                start = (2 * Constants.ELEVATION_VALUE).dp,
                end = Constants.PADDING_SIDE_VALUE.dp
            )
            .fillMaxWidth()
    ) {
        BackRowItem(onBackArrowPressed = onBackArrowPressed)

        FavouritesStarItem(isStarNeeded)
    }
}

@Composable
private fun FavouritesStarItem(
    isStarNeeded: Boolean = true
) {
    if (isStarNeeded) {
        Row() {
            Image(
                painter = painterResource(id = R.drawable.star),
                contentDescription = "favourites start",
                modifier = Modifier
                    .size(25.dp)
            )
        }
    }
}

@Composable
private fun BackRowItem(onBackArrowPressed: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Go back",
            tint = Color.White,
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    onBackArrowPressed()
                }
        )

        Text(
            text = "Back",
            modifier = Modifier
                .padding(start = 8.dp),
            style = Typography.h2,
            color = Color.White
        )
    }
}

@Preview
@Composable
fun CyptoDetailScreenPreview() {
    CryptoDetailScreen(
        currencyCode = "ETH",
        onBackArrowPressed = {

        },
        onButtonClick = {

        }
    )
}