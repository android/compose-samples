package com.rs.crypto.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.RepeatOneOn
import com.rs.crypto.R
import com.rs.crypto.models.*

object DummyData {

    val repeatOptions = listOf<RepeatOptions>(
        RepeatOptions(
            iconImageVector = Icons.Default.RepeatOneOn,
            optionName = "Repeats",
            frequency = "Never"
        ),
        RepeatOptions(
            iconImageVector = Icons.Default.DateRange,
            optionName = "Starts",
            frequency = "Today"
        ),
        RepeatOptions(
            iconImageVector = Icons.Default.DateRange,
            optionName = "Ends",
            frequency = "Never"
        ),
        RepeatOptions(
            iconImageVector = Icons.Default.Label,
            optionName = "Label",
            frequency = "Edit label"
        )
    )

    val tradeScreenSections = listOf<String>(
        "Transact",
        "Repeat",
        "Limit"
    )

    val topChipsName = listOf<String>(
        "All    4 ",
        "Following    2 ",
        "Crypto    4 ",
        "Utility Tokens    1 ",
        "Stable Coins    2 "
    )

    val user = User(
        userName = "DeveloperRahnuma",
        firstName = "Rahnuma",
        lastName = "Sharib"
    )

    val portfolio = Portfolio(
        balance = 12347.33F,
        changes = 8.39F,
        changeType = "I"
    )

    val trendingCurrencies = listOf<TrendingCurrency>(
        TrendingCurrency(
            id = 3,
            currencyName = "Litecoin",
            currencyCode = "LTC",
            imageRes = R.drawable.litecoin,
            currentPrice = 98.33F,
            changes = 1.73F,
            changeType = "I",
            description = "Litecoin is a peer-to-peer cryptocurrency and open-source software project released under the MIT/X11 license. Litecoin was an early bitcoin spinoff or altcoin, starting in October 2011. In technical details, Litecoin is nearly identical to Bitcoin.",
            chartData = listOf(
                Pair(1F, 2.6F),
                Pair(1.5F, 2.2F),
                Pair(2F, 2F),
                Pair(2.5F, 2.2F),
                Pair(3F, 1.6F),
                Pair(3.5F, 2.1F),
                Pair(4F, 2.5F)
            ),
            wallet = Wallet(
                value = 13139.23F,
                crypto = 100.2F
            ),
            transactionHistory = listOf(
                Transaction(
                    transactionID = 1,
                    description = "Sold Litecoin",
                    amount = -2.0034F,
                    currencyCode = "LTC",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 2,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 3,
                    description = "Sold Litecoin",
                    amount = -2.0034F,
                    currencyCode = "LTC",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 4,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 5,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 6,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 7,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 8,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 9,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "LTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
            )
        ),
        TrendingCurrency(
            id = 4,
            currencyName = "Ripple",
            currencyCode = "XRP",
            imageRes = R.drawable.ripple,
            currentPrice = 1.49F,
            changes = 2.23F,
            changeType = "D",
            description = "Ripple is a real-time gross settlement system, currency exchange and remittance network created by Ripple Labs Inc., a US-based technology company.",
            chartData = listOf(
                Pair(1F, 2.3F),
                Pair(1.5F, 2.3F),
                Pair(2F, 2.5F),
                Pair(2.5F, 2.1F),
                Pair(3F, 2.2F),
                Pair(3.5F, 1.8F),
                Pair(4F, 2.5F)
            ),
            wallet = Wallet(
                value = 2000.0F,
                crypto = 6000.0F
            ),
            transactionHistory = listOf(
                Transaction(
                    transactionID = 1,
                    description = "Sold Ripple",
                    amount = -2.0034F,
                    currencyCode = "XRP",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 2,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 3,
                    description = "Sold Ripple",
                    amount = -2.0034F,
                    currencyCode = "XRP",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 4,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 5,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 6,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 7,
                    description = "Bought Litecoin",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 8,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 9,
                    description = "Bought Ripple",
                    amount = 2.0034F,
                    currencyCode = "XRP",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
            )
        ),
        TrendingCurrency(
            id = 1,
            currencyName = "Bitcoin",
            currencyCode = "BTC",
            imageRes = R.drawable.bitcoin,
            currentPrice = 19598.74F,
            changes = 18.24F,
            changeType = "I",
            description = "Bitcoin is a cryptocurrency invented in 2008 by an unknown person or group of people using the name Satoshi Nakamoto. The currency began use in 2009 when its implementation was released as open-source software.",
            chartData = listOf(
                Pair(1F, 2.5F),
                Pair(1.5F, 2F),
                Pair(2F, 2.3F),
                Pair(2.5F, 1.4F),
                Pair(3F, 1.5F),
                Pair(3.5F, 2.3F),
                Pair(4F, 2.8F)
            ),
            wallet = Wallet(
                value = 170435.86F,
                crypto = 5.1F
            ),
            transactionHistory = listOf(
                Transaction(
                    transactionID = 1,
                    description = "Sold Bitcoin",
                    amount = -2.0034F,
                    currencyCode = "BTC",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 2,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 3,
                    description = "Sold Bitcoin",
                    amount = -2.0034F,
                    currencyCode = "BTC",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 4,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 5,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 6,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 7,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 8,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 9,
                    description = "Bought Bitcoin",
                    amount = 2.0034F,
                    currencyCode = "BTC",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
            )
        ),
        TrendingCurrency(
            id = 2,
            currencyName = "Ethereum",
            currencyCode = "ETH",
            imageRes = R.drawable.ethereum,
            currentPrice = 512.03F,
            changes = 5.12F,
            changeType = "D",
            description = "Ethereum is a decentralized, open-source blockchain featuring smart contract functionality. Ether is the native cryptocurrency of the platform. It is the second-largest cryptocurrency by market capitalization, after Bitcoin. Ethereum is the most actively used blockchain.",
            chartData = listOf(
                Pair(1F, 2F),
                Pair(1.5F, 2.3F),
                Pair(2F, 2F),
                Pair(2.5F, 2.2F),
                Pair(3F, 1.5F),
                Pair(3.5F, 2.1F),
                Pair(4F, 2.5F)
            ),
            wallet = Wallet(
                value = 18409.24F,
                crypto = 13.7F
            ),
            transactionHistory = listOf(
                Transaction(
                    transactionID = 1,
                    description = "Sold Ethereum",
                    amount = -2.0034F,
                    currencyCode = "ETH",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 2,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 3,
                    description = "Sold Ethereum",
                    amount = -2.0034F,
                    currencyCode = "ETH",
                    transactionType = "S",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 4,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 5,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 6,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 7,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 8,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
                Transaction(
                    transactionID = 9,
                    description = "Bought Ethereum",
                    amount = 2.0034F,
                    currencyCode = "ETH",
                    transactionType = "B",
                    transactionDate = "14:20 12 Apr"
                ),
            )
        )
    )

    val followingTokens = listOf<TrendingCurrency>(
        trendingCurrencies[0],
        trendingCurrencies[1]
        )

    val utilityTokens = listOf<TrendingCurrency>(
        trendingCurrencies[3]
    )

    val stableCoins = listOf<TrendingCurrency>(
        trendingCurrencies[1],
        trendingCurrencies[0]
    )

    val transactionHistory = listOf(
        Transaction(
            transactionID = 1,
            description = "Sold Bitcoin",
            amount = -1.3174F,
            currencyCode = "ETH",
            transactionType = "S",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 2,
            description = "Bought Ripple",
            amount = 2.0034F,
            currencyCode = "ETH",
            transactionType = "B",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 3,
            description = "Sold Bitcoin",
            amount = -2.0034F,
            currencyCode = "ETH",
            transactionType = "S",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 4,
            description = "Bought Ripple",
            amount = 2.0034F,
            currencyCode = "ETH",
            transactionType = "B",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 5,
            description = "Sold Litecoin",
            amount = -2.0034F,
            currencyCode = "ETH",
            transactionType = "S",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 6,
            description = "Bought Litecoin",
            amount = 2.0034F,
            currencyCode = "ETH",
            transactionType = "B",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 7,
            description = "Sold Ripple",
            amount = -2.0034F,
            currencyCode = "ETH",
            transactionType = "S",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 8,
            description = "Sold Bitcoin",
            amount = -2.0034F,
            currencyCode = "ETH",
            transactionType = "S",
            transactionDate = "14:20 12 Apr"
        ),
        Transaction(
            transactionID = 9,
            description = "Bought Bitcoin",
            amount = 2.0034F,
            currencyCode = "ETH",
            transactionType = "B",
            transactionDate = "14:20 12 Apr"
        ),
    )

    val chartOptions = listOf(
        ChartOption(
            optionId = 1,
            label = "1 hr"
        ),
        ChartOption(
            optionId = 2,
            label = "3 Days"
        ),
        ChartOption(
            optionId = 3,
            label = "1 Week"
        ),
        ChartOption(
            optionId = 4,
            label = "1 Month"
        ),
        ChartOption(
            optionId = 5,
            label = "3 Months"
        )
    )
}