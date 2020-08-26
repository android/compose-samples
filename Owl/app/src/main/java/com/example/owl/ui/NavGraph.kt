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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.example.owl.ui.utils.Navigator
import kotlinx.android.parcel.Parcelize

/**
 * Models the screens in the app and any arguments they require.
 */
sealed class Destination : Parcelable {
    @Parcelize
    object Onboarding : Destination()

    @Parcelize
    object Courses : Destination()

    @Immutable
    @Parcelize
    data class Course(val courseId: Long) : Destination()
}

/**
 * Models the navigation actions in the app.
 */
class Actions(navigator: Navigator<Destination>) {
    val onboardingComplete: () -> Unit = {
        navigator.navigate(Destination.Courses)
    }
    val selectCourse: (Long) -> Unit = { courseId: Long ->
        navigator.navigate(Destination.Course(courseId))
    }
    val upPress: () -> Unit = {
        navigator.back()
    }
}
