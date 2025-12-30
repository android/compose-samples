package com.baidaidai.animora.components.StartScreen

import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.baidaidai.animora.InfoActivity
import com.baidaidai.animora.components.StartScreen.home.homeScreenComtainer
import com.baidaidai.animora.components.StartScreen.list.animationListContainer
import com.baidaidai.animora.components.StartScreen.model.homeScreenBlurViewModel
import com.baidaidai.animora.components.StartScreen.components.NecessaryComponents

/**
 * The main container composable for the start screen, managing the overall layout and navigation.
 *
 * This composable sets up a [Scaffold] with a top app bar and a bottom navigation bar.
 * It hosts a [NavHost] to switch between the "Home" and "List" screens.
 * It also handles a blur effect that can be triggered by a [homeScreenBlurViewModel].
 *
 * @param context The Android [Context] used for creating intents.
 * @param homeViewNavController The [NavHostController] for the inner navigation between Home and List screens.
 * @param totalNavigationController The main [NavHostController] for navigating to other parts of the app.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun startScreenContainer(
    context: Context,
    homeViewNavController: NavHostController,
    totalNavigationController: NavHostController,
){
    val intent = Intent(context,InfoActivity::class.java)

    val homeScreenBlurViewModel = viewModel<homeScreenBlurViewModel>()
    val blurStatus by homeScreenBlurViewModel.blurStatus.collectAsState()
    val blurValue by animateDpAsState(
        targetValue = if (blurStatus) 10.dp else 0.dp
    )

    Scaffold(
        topBar = {
            NecessaryComponents.homeTopAppBar {
                context.startActivity(intent)
            }
        },
        bottomBar = {
            NecessaryComponents.homeButtomBar(
                controller = homeViewNavController
            )
        },
        modifier = Modifier
            .blur(blurValue)
    ) { contentPadding ->
        NavHost(
            navController = homeViewNavController,
            startDestination = "Home",
        ) {
            composable(
                route = "Home"
            ){
                homeScreenComtainer(
                    contentPadding = contentPadding,
                    onlySpringSpecOnClick = {
                        totalNavigationController.navigate("springStudio")
                    }
                )
            }
            composable (
                route = "List"
            ) {
                animationListContainer(
                    contentPaddingValues = contentPadding,
                    navController = totalNavigationController,
                    viewModel = homeScreenBlurViewModel,
                )
            }
        }

    }
}