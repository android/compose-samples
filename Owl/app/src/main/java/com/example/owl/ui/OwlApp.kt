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

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.Courses
import com.example.owl.ui.onboarding.Onboarding
import com.example.owl.ui.utils.AmbientBackDispatcher
import com.example.owl.ui.utils.ProvideDisplayInsets
import com.example.owl.ui.utils.ProvideImageLoader

@Composable
fun OwlApp(backDispatcher: OnBackPressedDispatcher) {

    Providers(AmbientBackDispatcher provides backDispatcher) {
        ProvideDisplayInsets {
            ProvideImageLoader {
                val navController = rememberNavController()

                val actions = remember(navController) { Actions(navController) }
                NavHost(navController = navController, startDestination = Destinations.Onboarding) {
                    composable(Destinations.Onboarding) {
                        Onboarding(actions.onboardingComplete)
                    }
                    composable(Destinations.Courses) {
                        Courses(actions.selectCourse)
                    }
                    composable(Destinations.CourseDetails + "/{courseId}") { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getLong("courseId")
                            ?: throw IllegalArgumentException("No course ID provided")
                        CourseDetails(
                            courseId,
                            actions.selectCourse,
                            actions.upPress
                        )
                    }
                }
            }
        }
    }
}
