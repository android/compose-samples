package com.example.jetnews.ui.splash

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    splashVm: SplashScreenViewModel,
) {
//    val isUserAuth = splashVm.isUserAuthenticated.collectAsState()
//
//    LaunchedEffect(isUserAuth.value) {
//        splashVm.checkUserAuthenticated()
//        when (isUserAuth.value) {
//            true -> {
//                // si esta logueado nos vamos a la pantalla de mapScreen
//                navHostController.navigate(JetnewsDestinations.HOME_ROUTE) {
//                    popUpTo(JetnewsDestinations.SPLASH_ROUTE) { inclusive = true }
//                }
//            }
//
//            false -> {
//                navHostController.navigate(JetnewsDestinations.SIGNIN_ROUTE) {
//                    popUpTo(JetnewsDestinations.SPLASH_ROUTE) { inclusive = true }
//                }
//            }
//
//            else -> {
//                navHostController.navigate(JetnewsDestinations.SIGNUP_ROUTE) {
//                    popUpTo(JetnewsDestinations.SPLASH_ROUTE) { inclusive = true }
//                }
//            }
//        }
//    }
}
