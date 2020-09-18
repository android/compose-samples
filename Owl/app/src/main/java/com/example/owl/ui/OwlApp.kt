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
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.Courses
import com.example.owl.ui.onboarding.Onboarding
import com.example.owl.ui.utils.BackDispatcherAmbient
import com.example.owl.ui.utils.Navigator
import com.example.owl.ui.utils.ProvideDisplayInsets
import com.example.owl.ui.utils.ProvideImageLoader

@Composable
fun OwlApp(backDispatcher: OnBackPressedDispatcher) {
    @Suppress("RemoveExplicitTypeArguments")
    val navigator: Navigator<Destination> = rememberSavedInstanceState(
        saver = Navigator.saver<Destination>(backDispatcher)
    ) {
        Navigator(Destination.Onboarding, backDispatcher)
    }
    val actions = remember(navigator) { Actions(navigator) }

    Providers(BackDispatcherAmbient provides backDispatcher) {
        ProvideDisplayInsets {
            ProvideImageLoader {
                Crossfade(navigator.current) { destination ->
                    when (destination) {
                        Destination.Onboarding -> Onboarding(actions.onboardingComplete)
                        Destination.Courses -> Courses(actions.selectCourse)
                        is Destination.Course -> CourseDetails(
                            destination.courseId,
                            actions.selectCourse,
                            actions.upPress
                        )
                    }
                }
            }
        }
    }
}
