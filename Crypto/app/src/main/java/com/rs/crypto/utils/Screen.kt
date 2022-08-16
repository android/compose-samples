package com.rs.crypto.utils

sealed class Screen(val route: String) {
    object SettingsScreen: Screen("settings_screen")
    object HomeScreen: Screen("home_screen")
    object CryptoDetailScreen: Screen("crypto_detail_screen")
    object TransactionScreen: Screen("transaction_screen")
    object PortfolioScreen: Screen("portfolio_screen")
    object PricesScreen: Screen("prices_screen")
    object TradeScreen: Screen("trade_screen")
}
