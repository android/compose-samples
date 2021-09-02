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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.article.ArticleRoute
import com.example.jetnews.ui.article.ArticleViewModel
import com.example.jetnews.ui.article.ArticleViewModel.Companion.ARTICLE_ID_KEY
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.interests.InterestsViewModel
import kotlinx.coroutines.launch

@Composable
fun JetnewsNavGraph(
    appContainer: AppContainer,
    showNavRail: Boolean,
    navigationActions: JetnewsNavigationActions,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = JetnewsDestinations.HOME_ROUTE
) {
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(JetnewsDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.postsRepository)
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                showNavRail = showNavRail,
                navigateToArticle = navigationActions.navigateToArticle,
                navigateToInterests = navigationActions.navigateToInterests,
                openDrawer = openDrawer
            )
        }
        composable(JetnewsDestinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.provideFactory(appContainer.interestsRepository)
            )
            InterestsRoute(
                interestsViewModel = interestsViewModel,
                showNavRail = showNavRail,
                navigateToHome = navigationActions.navigateToHome,
                openDrawer = openDrawer
            )
        }
        composable(
            route = "${JetnewsDestinations.ARTICLE_ROUTE}/{$ARTICLE_ID_KEY}",
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
            ArticleRoute(
                articleViewModel = articleViewModel,
                showNavRail = showNavRail,
                onBack = navigationActions.upPress
            )
        }
    }
}
