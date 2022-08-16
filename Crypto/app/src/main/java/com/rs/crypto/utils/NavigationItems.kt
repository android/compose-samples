package com.rs.crypto.utils

import com.rs.crypto.R

sealed class NavigationItems(val route: String, val iconRes: Int, val name: String) {
    object Home: NavigationItems(route = "home_nav_item", iconRes = R.drawable.home, name = "HOME")
    object Portfolio: NavigationItems(route = "portfolio_nav_item", iconRes = R.drawable.pie_chart, name = "PORTFOLIO")
    object Transaction: NavigationItems(route = "transaction_nav_item", iconRes = R.drawable.transaction, name = "")
    object Prices: NavigationItems(route = "prices_nav_item", iconRes = R.drawable.line_graph, name = "PRICES")
    object Settings: NavigationItems(route = "settings_nav_item", iconRes = R.drawable.settings, name = "SETTINGS")
}
