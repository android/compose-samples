package com.rs.crypto

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.rs.crypto.composables.*
import com.rs.crypto.utils.Screen


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen() { currencyCode ->
                navController.navigate(Screen.CryptoDetailScreen.route + "/$currencyCode")
            }
        }
        composable(
            route = Screen.CryptoDetailScreen.route + "/{currencyCode}",
            arguments = listOf(
                navArgument(name = "currencyCode") {
                    type = NavType.StringType
                }
            )
        ) {
            val currencyCode = it.arguments?.getString("currencyCode")!!
            CryptoDetailScreen(
                currencyCode = currencyCode,
                onBackArrowPressed = {
                    navController.popBackStack()
                },
                onButtonClick = {
                    navController.navigate(route = Screen.TransactionScreen.route + "/$currencyCode")
                }
            )
        }
        composable(
            route = Screen.TransactionScreen.route + "/{currencyCode}",
            arguments = listOf(
                navArgument(name = "currencyCode") {
                    type = NavType.StringType
                }
            )
        ) {
            val currencyCode = it.arguments?.getString("currencyCode")!!
            TransactionScreen(
                onBackArrowPressed = {
                    navController.popBackStack()
                },
                currencyCode = currencyCode,
                onTradeButtonClick = {
                    Log.d("TransactionScreen", "Trade section coming soon...")
                }
            )
        }
        composable(
            route = Screen.SettingsScreen.route
        ) {
            SettingsScreen(
                onBackArrowPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.PortfolioScreen.route
        ) {
            PortfolioScreen(
                onBackArrowPressed = {
                    navController.popBackStack()
                },
                onCoinSearch = {

                }
            )
        }
        composable(
            route = Screen.PricesScreen.route
        ) {
            PricesScreen(
                onBackArrowPressed = {

                },
                onCoinSearch = {

                },
                onItemClick = { currencyCode ->
                    navController.navigate(Screen.CryptoDetailScreen.route + "/$currencyCode")
                }
            )
        }
    }
}