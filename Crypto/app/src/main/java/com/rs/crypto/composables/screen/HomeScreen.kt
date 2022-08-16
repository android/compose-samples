package com.rs.crypto.composables

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.R
import com.rs.crypto.models.Transaction
import com.rs.crypto.models.TrendingCurrency
import com.rs.crypto.ui.theme.*
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData

@Composable
fun HomeScreen(
    onCardClick: (String) -> Unit
) {
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
            HomeScreenContent(onCardClick = onCardClick)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreenContent(
    onCardClick: (String) -> Unit
) {
    Box {
        BannerImage()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.TopCenter)
                .padding(
                    top = 30.dp,
                )
        ) {
            Icon(
                Icons.Filled.HorizontalSplit,
                contentDescription = "More",
                tint = Color.White
            )

            BadgeBox(badgeContent = {
                Text("12")
            },
            ){
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }

//        Image(
//            painter = painterResource(id = R.drawable.notification_white),
//            contentDescription = "Notification",
//            modifier = Modifier
//                .padding(
//                    top = 40.dp,
//                    end = 25.dp
//                )
//                .align(alignment = Alignment.TopEnd)
//                .clickable {
//                    Log.d("HomeScreen", "Notification icon Pressed")
//                }
//        )
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            PortfolioBalanceSection()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 180.dp,
                )
        ) {
            Text(
                text = "Trending",
                color = Color.White,
                style = Typography.h2,
                modifier = Modifier
                    .padding(start = Constants.PADDING_SIDE_VALUE.dp)
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
            ) {
//                items(items = DummyData.trendingCurrencies) { currency ->
//                    Spacer(modifier = Modifier.width(Constants.PADDING_SIDE_VALUE.dp))
//                    CurrencyCard(
//                        currency = currency,
//                        onCardClick = onCardClick
//                    )
//                }

                DummyData.trendingCurrencies.forEachIndexed{ index, currency ->
                    if (index == 0) {
                        Spacer(modifier = Modifier.width(Constants.PADDING_SIDE_VALUE.dp))
                    }

                    CurrencyCard(
                        currency = currency,
                        onCardClick = onCardClick
                    )

                    Spacer(modifier = Modifier.width(Constants.PADDING_SIDE_VALUE.dp))
                }
            }

            Spacer(modifier = Modifier.height(Constants.ELEVATION_VALUE.dp))

            SetPriceAlertSection()

            InvestingSafetySection()

            TransactionHistorySection()
        }
    }
}

@Composable
fun TransactionHistorySection() {
    Card(
        modifier = Modifier
            .padding(Constants.PADDING_SIDE_VALUE.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.background(color = Secondary),
        ) {
            Row() {
                Text(
                    text = "Your Transaction History",
                    style = Typography.h2,
                    color = Color.White,
                    modifier = Modifier.padding(
                        top = Constants.PADDING_SIDE_VALUE.dp,
                        bottom = Constants.PADDING_SIDE_VALUE.dp
                    )
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                DummyData.transactionHistory.forEachIndexed { index, transaction ->
                    TransactionItem(transaction)
                    Divider(
                        modifier = Modifier
                            .padding(
                                top = 5.dp,
                                bottom = if (DummyData.transactionHistory.size - 1 > index) {
                                    5.dp
                                } else {
                                    0.dp
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.White)),
        elevation = 10.dp,
        backgroundColor = Secondary
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    vertical = 4.dp,
                    horizontal = 8.dp
                )
                .fillMaxWidth()

        ) {
            TransactionDescriptionSection(transaction)

            TransactionAmountSection(transaction)
        }
    }
}

@Composable
private fun TransactionAmountSection(
    transaction: Transaction,
) {
    val amountColor = if (transaction.transactionType == "S") {
        Color.White
    } else {
        Green
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${transaction.amount} ${transaction.currencyCode}",
            style = Typography.h2,
            color = amountColor
        )

        Image(
            painter = painterResource(id = R.drawable.right_arrow),
            contentDescription = null,
            modifier = Modifier
                .clipToBounds()
                .padding(start = (Constants.PADDING_SIDE_VALUE * 1.5).dp)
        )
    }
}

@Composable
private fun TransactionDescriptionSection(transaction: Transaction) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = Constants.PADDING_SIDE_VALUE.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.transaction),
            contentDescription = "Transaction image",
            modifier = Modifier
                .padding(end = (Constants.PADDING_SIDE_VALUE * 1.5).dp),
            colorFilter = ColorFilter.tint(color = Color.White)
        )

        Column {
            Text(
                text = transaction.description,
                style = Typography.h4,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = transaction.transactionDate,
                style = Typography.h5,
                color = BlueGrey50
            )
        }
    }
}

@Composable
private fun InvestingSafetySection() {
    Card(
        modifier = Modifier
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = Constants.ELEVATION_VALUE.dp,
        backgroundColor = BannerColor
    ) {
        Column(
            modifier = Modifier
                .padding(Constants.PADDING_SIDE_VALUE.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.PADDING_SIDE_VALUE.dp)
        ) {
            InvestingSafetyTextItems()
        }
    }
}

@Composable
private fun InvestingSafetyTextItems() {
    Text(
        text = "Investing Safety",
        color = Color.White,
        style = Typography.h3
    )

    Text(
        text = "It's very difficult to time an investement especially when the market is volatile. Learn how to use dollar cost averaging to you advantage.",
        color = Color.White,
        style = Typography.subtitle2,
        lineHeight = 18.sp
    )

    Text(
        text = "Learn More",
        color = Green,
        textDecoration = TextDecoration.Underline,
        style = Typography.h3,
        modifier = Modifier
            .clickable {
                Log.d("HomeScreen", "Learn More clicked")
            }
    )
}

@Composable
fun SetPriceAlertSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = Constants.ELEVATION_VALUE.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(Constants.PADDING_SIDE_VALUE.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.notification_color),
                contentDescription = "Price Alert Icon"
            )

            SetPriceAlertTextColumn()

            Image(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = null
            )
        }
    }
}

@Composable
fun SetPriceAlertTextColumn() {
    Column() {
        Text(
            text = "Set Price Alert",
            style = Typography.h3
        )
        Text(
            text = "Get notified when your coins are moving",
            style = Typography.subtitle2,
            color = Gray
        )
    }
}

@Composable
private fun CurrencyCard(
    currency: TrendingCurrency,
    onCardClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(top = Constants.PADDING_SIDE_VALUE.dp)
            .clickable {
                onCardClick(currency.currencyCode)
            },
        shape = MaterialTheme.shapes.medium,
        elevation = Constants.ELEVATION_VALUE.dp
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 20.dp,
                    start = 20.dp,
                    bottom = 20.dp
                )
        ) {
            CurrencyItem(currency = currency)

            ValuesItem(currency = currency)
        }
    }
}

@Composable
fun ValuesItem(
    currency: TrendingCurrency,
    priceModifier: Modifier = Modifier
        .padding(top = 20.dp),
    changesModifier: Modifier = Modifier
        .padding(top = 5.dp),
    currencyPriceStyle: TextStyle = Typography.h3,
    currencyChangesStyle: TextStyle = Typography.h3
    ) {
    var changeColor by remember {
        mutableStateOf(
            if(currency.changeType == "I") {
                Green
            } else {
                Red
            }
        )
    }

    var changeOperator by remember {
        mutableStateOf(
            if(currency.changeType == "I") {
                "+"
            } else {
                "-"
            }
        )
    }

    Text(
        text = "£${currency.currentPrice}",
        style = currencyPriceStyle,
        modifier = priceModifier
    )

    Text(
        text = "$changeOperator${currency.changes}%",
        style = currencyChangesStyle,
        color = changeColor,
        modifier = changesModifier
    )
}

@Composable
fun CurrencyItem(
    currency: TrendingCurrency
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = currency.imageRes),
            contentDescription = currency.currencyName,
            modifier = Modifier
                .size(25.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = Constants.PADDING_SIDE_VALUE.dp)
        ) {
            Text(
                text = currency.currencyName,
                style = Typography.h2,
                color = Color.Black
            )

            Text(
                text = currency.currencyCode,
                style = Typography.subtitle1,
                color = Gray
            )
        }
    }
}

@Composable
public fun PortfolioBalanceSection() {
    var operator: String = if (DummyData.portfolio.changeType == "I") {
        "+"
    } else {
        "-"
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Your Portfolio Balance",
            style = Typography.h3,
            color = Color.White
        )
        Text(
            text = "£${DummyData.portfolio.balance}",
            style = Typography.h1,
            color = Color.White
        )
        Text(
            text = "$operator${DummyData.portfolio.changes}   Last 24 hours",
            style = Typography.h5,
            color = Color.White
        )
    }
}

@Composable
private fun BannerImage() {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
fun HomeScreenPrev() {
    HomeScreen() {

    }
}