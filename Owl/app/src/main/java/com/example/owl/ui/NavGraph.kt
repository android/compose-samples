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

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.MainDestinations.COURSE_DETAIL_ID_KEY
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.CourseTabs
import com.example.owl.ui.courses.courses
import com.example.owl.ui.onboarding.Onboarding

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
    modifier: Modifier = Modifier,
    finishActivity: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.COURSES_ROUTE,
    showOnboardingInitially: Boolean = true
) {
    // Onboarding could be read from shared preferences.
    val onboardingComplete = remember(showOnboardingInitially) {
        mutableStateOf(!showOnboardingInitially)
    }

    val actions = remember(navController) { MainActions(navController) }

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
                    onboardingComplete.value = true
                    actions.onboardingComplete()
                }
            )
        }
        navigation(
            route = MainDestinations.COURSES_ROUTE,
            startDestination = CourseTabs.FEATURED.route
        ) {
            courses(
                onCourseSelected = { newCourseId: Long -> actions.openCourse(newCourseId) },
                onboardingComplete = onboardingComplete,
                navController = navController,
                modifier = modifier
            )
        }
        composable(
            "${MainDestinations.COURSE_DETAIL_ROUTE}/{$COURSE_DETAIL_ID_KEY}",
            arguments = listOf(
                navArgument(COURSE_DETAIL_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val currentCourseId = arguments.getLong(COURSE_DETAIL_ID_KEY)
            CourseDetails(
                courseId = currentCourseId,
                selectCourse = { newCourseId ->
                    actions.relatedCourse(newCourseId, currentCourseId)
                },
                upPress = actions.upPress
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val onboardingComplete: () -> Unit = {
        navController.popBackStack()
    }

    // Used from COURSES_ROUTE
    val openCourse: (Long) -> Unit = { newCourseId: Long ->
        // In order to discard duplicated navigation events, check the current route
        val currentRoute = navController.currentBackStackEntry?.arguments?.getString(KEY_ROUTE)
        if (currentRoute?.startsWith(MainDestinations.COURSES_ROUTE) == true) {
            navController.navigate("${MainDestinations.COURSE_DETAIL_ROUTE}/$newCourseId")
        }
    }

    // Used from COURSE_DETAIL_ROUTE
    val relatedCourse: (Long, Long) -> Unit = { newCourseId: Long, currentCourseId: Long ->
        // In order to discard duplicated navigation events, check the current courseId
        val navControllerCurrentCourseId = navController.currentBackStackEntry?.arguments
            ?.getLong(COURSE_DETAIL_ID_KEY)
        if (navControllerCurrentCourseId == currentCourseId) {
            navController.navigate("${MainDestinations.COURSE_DETAIL_ROUTE}/$newCourseId")
        }
    }

    // Used from COURSE_DETAIL_ROUTE
    val upPress: () -> Unit = {
        // In order to discard duplicated navigation events, check the current route
        val currentRoute = navController.currentBackStackEntry?.arguments?.getString(KEY_ROUTE)
        if (currentRoute?.startsWith("${MainDestinations.COURSE_DETAIL_ROUTE}/") == true) {
            navController.navigateUp()
        }
    }
}
