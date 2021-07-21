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

package com.example.jetsnack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navigation
import com.example.jetsnack.ui.MainDestinations.SNACK_ID_KEY
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.addHomeGraph
import com.example.jetsnack.ui.snackdetail.SnackDetail

/**
 * Destinations used in the ([JetsnackApp]).
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val SNACK_DETAIL_ROUTE = "snack"
    const val SNACK_ID_KEY = "snackId"
}

@Composable
fun JetsnackNavGraph(
    scaffoldStateHolder: ScaffoldStateHolder,
    modifier: Modifier = Modifier,
    startDestination: String = MainDestinations.HOME_ROUTE,
) {
    NavHost(
        navController = scaffoldStateHolder.navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            route = MainDestinations.HOME_ROUTE,
            startDestination = HomeSections.FEED.route
        ) {
            addHomeGraph(
                onSnackSelected = { snackId: Long, from: NavBackStackEntry ->
                    scaffoldStateHolder.navigateToSnackDetail(from, snackId)
                },
                modifier = modifier
            )
        }
        composable(
            "${MainDestinations.SNACK_DETAIL_ROUTE}/{$SNACK_ID_KEY}",
            arguments = listOf(navArgument(SNACK_ID_KEY) { type = NavType.LongType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val snackId = arguments.getLong(SNACK_ID_KEY)
            SnackDetail(
                snackId = snackId,
                upPress = {
                    scaffoldStateHolder.onBackPress()
                }
            )
        }
    }
}
