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

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.Courses
import com.example.owl.ui.onboarding.Onboarding

open class Destination(open val route: String)
open class DestinationSingleArg(
    private val rootRoute: String,
    private val argName: String,
    argType: NavType<*>
) {
    val args = listOf(navArgument(argName) { type = argType })
    val route = "$rootRoute/{$argName}"
    fun getRouteWithArg(courseId: Long) = "$rootRoute/$courseId"
    fun getArgFromBundle(args: Bundle) = args.getLong(argName)
}

/**
 * Destinations used in the main screen ([OwlApp]).
 */
object MainDestinations {
    val Onboarding = Destination("onboarding")
    val Courses = Destination("courses")
    val CourseDetail = DestinationSingleArg("courses", "courseId", NavType.LongType)
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val onboardingComplete: () -> Unit = {
        navController.navigate(MainDestinations.Courses.route)
    }
    val selectCourse: (Long) -> Unit = { courseId: Long ->
        navController.navigate(MainDestinations.CourseDetail.getRouteWithArg(courseId))
    }
    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.Onboarding.route) {
    val navController = rememberNavController()

    val actions = remember(navController) { MainActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.Onboarding.route) {
            Onboarding(onboardingComplete = actions.onboardingComplete)
        }
        composable(MainDestinations.Courses.route) {
            Courses(selectCourse = actions.selectCourse)
        }
        composable(
            MainDestinations.CourseDetail.route,
            arguments = MainDestinations.CourseDetail.args
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            CourseDetails(
                courseId = MainDestinations.CourseDetail.getArgFromBundle(arguments),
                selectCourse = actions.selectCourse,
                upPress = actions.upPress
            )
        }
    }
}
