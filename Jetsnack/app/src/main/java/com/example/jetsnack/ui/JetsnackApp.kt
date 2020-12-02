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

package com.example.jetsnack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetsnack.ui.home.Home
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.ProvideDisplayInsets

@Composable
fun JetsnackApp() {
    ProvideDisplayInsets {
        JetsnackTheme {
            val navController = rememberNavController()
            val actions = remember(navController) { Actions(navController) }
            NavHost(navController = navController, startDestination = Routes.Home) {
                composable(
                    route = Routes.Home,
                    deepLinks = listOf(Routes.deeplink(Routes.Home))
                ) {
                    Home(actions.selectSnack)
                }
                composable(
                    route = Routes.SnackDetail,
                    deepLinks = listOf(Routes.deeplink(Routes.SnackDetail))
                ) { backStackEntry ->
                    val snackId = requireNotNull(
                        backStackEntry.arguments?.getString(Args.SnackId)
                    )
                    SnackDetail(
                        snackId = snackId.toLong(),
                        upPress = actions.upPress
                    )
                }
            }
        }
    }
}
