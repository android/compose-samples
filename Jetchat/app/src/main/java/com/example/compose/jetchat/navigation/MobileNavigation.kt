package com.example.compose.jetchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.conversation.ConversationScreen
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.profile.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

const val NAV_GRAPH_ROUTE_MAIN = "route_main"
const val DEST_ROUTE_CONVERSATION = "route_conversation"
const val DEST_ROUTE_PROFILE = "route_profile"

@Composable
fun mobileNavigationNavHostCont(activityViewModel: MainViewModel): NavHostController {
    val navHostCont: NavHostController = rememberNavController()

    NavHost(
        navController = navHostCont,
        route = NAV_GRAPH_ROUTE_MAIN,
        startDestination = DEST_ROUTE_CONVERSATION
    ) {
        composable(route = DEST_ROUTE_CONVERSATION) {
            ConversationScreen(activityViewModel, navHostCont)
        }
        composable(
            route = "$DEST_ROUTE_PROFILE/{userId}", arguments = listOf(
                navArgument(name = "userId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )) {

            // ProfileViewModel is tied to the ProfileScreen's lifecycle.
            // Compose automatically injects the argument "SavedStateHandle" in the
            // ProfileViewModel constructor as long as userId was defined in navArguments.
            val profileViewModel = viewModel<ProfileViewModel>(viewModelStoreOwner = it)
            ProfileScreen(activityViewModel, profileViewModel)
        }
    }

    return navHostCont
}