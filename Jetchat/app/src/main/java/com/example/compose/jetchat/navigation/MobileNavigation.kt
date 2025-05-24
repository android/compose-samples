/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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