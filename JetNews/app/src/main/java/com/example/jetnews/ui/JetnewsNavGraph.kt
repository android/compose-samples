/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.article.ArticleViewModel
import com.example.jetnews.ui.article.ArticleViewModel.Companion.ARTICLE_ID_KEY
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.interests.InterestsViewModel
import kotlinx.coroutines.launch

/**
 * Destinations used in the ([JetnewsApp]).
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val INTERESTS_ROUTE = "interests"
    const val ARTICLE_ROUTE = "post"
}

@Composable
fun JetnewsNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.postsRepository)
            )
            HomeScreen(
                homeViewModel = homeViewModel,
                navigateToArticle = actions.navigateToArticle,
                openDrawer = openDrawer
            )
        }
        composable(MainDestinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.provideFactory(appContainer.interestsRepository)
            )
            InterestsScreen(
                interestsViewModel = interestsViewModel,
                openDrawer = openDrawer
            )
        }
        composable(
            route = "${MainDestinations.ARTICLE_ROUTE}/{$ARTICLE_ID_KEY}",
            arguments = listOf(navArgument(ARTICLE_ID_KEY) { type = NavType.StringType })
        ) { backStackEntry ->
            // ArticleVM obtains the articleId via backStackEntry.arguments from SavedStateHandle
            val articleViewModel: ArticleViewModel = viewModel(
                factory = ArticleViewModel.provideFactory(
                    postsRepository = appContainer.postsRepository,
                    owner = backStackEntry,
                    defaultArgs = backStackEntry.arguments
                )
            )
            ArticleScreen(
                articleViewModel = articleViewModel,
                onBack = actions.upPress
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToArticle: (String) -> Unit = { postId: String ->
        navController.navigate("${MainDestinations.ARTICLE_ROUTE}/$postId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
