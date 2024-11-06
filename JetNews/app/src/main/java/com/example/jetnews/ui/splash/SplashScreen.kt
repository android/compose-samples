package com.example.jetnews.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetnews.R
import com.example.jetnews.ui.JetnewsDestinations

@Suppress("ktlint:standard:function-naming")
@Composable
fun SplashScreen(
    navHostController: NavHostController,
    splashVm: SplashScreenViewModel,
) {
    val authState by splashVm.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            true ->
                navHostController.navigate(JetnewsDestinations.HOME_ROUTE) {
                    popUpTo("splash") { inclusive = true }
                }
            false ->
                navHostController.navigate(JetnewsDestinations.SIGNIN_ROUTE) {
                    popUpTo("splash") { inclusive = true }
                }
            null -> {}
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        when (authState) {
            null -> CircularProgressIndicator()
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.ic_jetnews_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp),
                )
            }
        }
    }
}
