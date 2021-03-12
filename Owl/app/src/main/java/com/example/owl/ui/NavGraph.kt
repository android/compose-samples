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

package com.example.owl.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.MainDestinations.COURSE_DETAIL_ID_KEY
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.CourseTabs
import com.example.owl.ui.courses.courses
import com.example.owl.ui.onboarding.Onboarding
import com.example.owl.ui.theme.BlueTheme
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

/**
 * Destinations used in the ([OwlApp]).
 */
object MainDestinations {
    const val ONBOARDING_ROUTE = "onboarding"
    const val COURSES_ROUTE = "courses"
    const val COURSE_DETAIL_ROUTE = "course"
    const val COURSE_DETAIL_ID_KEY = "courseId"
}

@Composable
fun NavGraph(
    finishActivity: () -> Unit,
    startDestination: String = MainDestinations.COURSES_ROUTE,
    showOnboardingInitially: Boolean = true
) {
    val navController = rememberNavController()

    // Onboarding could be read from shared preferences.
    var onboardingComplete by remember { mutableStateOf(!showOnboardingInitially) }

    // This is false in the beginning, true after FAB click
    Log.d("jalc", "onboardingComplete: ${onboardingComplete.toString()}")
    val tabs = CourseTabs.values()

    val actions = remember(navController) { MainActions(navController) }

    BlueTheme {
        OwlScaffold(
            bottomBar = { OwlBottomBar(navController = navController, tabs) }
        ) { innerPaddingModifier ->
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(MainDestinations.ONBOARDING_ROUTE) {
                    // Intercept back in Onboarding: make it finish the activity
                    BackHandler {
                        finishActivity()
                    }

                    Onboarding(
                        onboardingComplete = {
                            // Set the flag so that onboarding is not shown next time.
                            onboardingComplete = true
                            actions.onboardingComplete()
                        }
                    )
                }
                navigation(
                    route = MainDestinations.COURSES_ROUTE,
                    startDestination = CourseTabs.FEATURED.route
                ) {
                    courses(
                        onCourseSelected = actions.selectCourse,
                        onboardingComplete = onboardingComplete,
                        navController = navController,
                        modifier = innerPaddingModifier
                    )
                }
                composable(
                    "${MainDestinations.COURSE_DETAIL_ROUTE}/{$COURSE_DETAIL_ID_KEY}",
                    arguments = listOf(
                        navArgument(COURSE_DETAIL_ID_KEY) { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val arguments = requireNotNull(backStackEntry.arguments)
                    CourseDetails(
                        courseId = arguments.getLong(COURSE_DETAIL_ID_KEY),
                        selectCourse = actions.selectCourse,
                        upPress = actions.upPress
                    )
                }
            }
        }
    }
}

@Composable
fun OwlBottomBar(navController: NavController, tabs: Array<CourseTabs>) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        ?: CourseTabs.FEATURED.route

    if (currentRoute in CourseTabs.values().map { it.route }) {

        BottomNavigation(
            Modifier.navigationBarsHeight(additional = 56.dp)
        ) {
            tabs.forEach { tab ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                    label = {
                        Text(stringResource(tab.title).toUpperCase())
                    },
                    selected = currentRoute == tab.route,
                    onClick = {
                        navController.popBackStack(navController.graph.startDestination, false)
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route)
                        }
                    },
                    alwaysShowLabel = false,
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = LocalContentColor.current,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
fun OwlScaffold(
    bottomBar: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        bottomBar = bottomBar
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        content(modifier)
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val onboardingComplete: () -> Unit = {
//        navController.navigateUp()
        navController.navigate(MainDestinations.COURSES_ROUTE)
    }
    val selectCourse: (Long) -> Unit = { courseId: Long ->
        navController.navigate("${MainDestinations.COURSE_DETAIL_ROUTE}/$courseId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}

