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

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Providers
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSubstring
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithSubstring
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.owl.R
import com.example.owl.model.courses
import com.example.owl.ui.fakes.ProvideTestImageLoader
import com.example.owl.ui.utils.AmbientBackDispatcher
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    /**
     * Using an empty activity to have control of the content that is set.
     *
     * This activity must be declared in the manifest (see src/debug/AndroidManifest.xml)
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    lateinit var activity: ComponentActivity

    private fun startActivity(startDestination: String? = null) {
        composeTestRule.activityRule.scenario.onActivity {
            activity = it
            composeTestRule.setContent {
                Providers(AmbientBackDispatcher provides activity.onBackPressedDispatcher) {
                    ProvideWindowInsets {
                        ProvideTestImageLoader {
                            if (startDestination == null) {
                                NavGraph()
                            } else {
                                NavGraph(startDestination)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun firstScreenIsOnboarding() {
        // When the app is open
        startActivity()
        // The first screen should be the onboarding screen.
        // Assert that the FAB label for the onboarding screen exists:
        composeTestRule.onNodeWithContentDescription(getOnboardingFabLabel()).assertExists()
    }

    @Test
    fun onboardingToCourses() {
        // Given the app in the onboarding screen
        startActivity()

        // Navigate to the next screen by clicking on the FAB
        val fabLabel = getOnboardingFabLabel()
        composeTestRule.onNodeWithContentDescription(fabLabel).performClick()

        // The first course should be shown
        composeTestRule.onNodeWithSubstring(courses.first().name).assertExists()
    }

    @Test
    fun coursesToDetail() {
        // Given the app in the courses screen
        startActivity(MainDestinations.COURSES_ROUTE)

        // Navigate to the first course
        composeTestRule.onNode(
            hasContentDescription(getFeaturedCourseLabel()).and(hasSubstring(courses.first().name))
        ).performClick()

        // Assert navigated to the course details
        composeTestRule.onNodeWithSubstring(getCourseDesc().take(15)).assertExists()
    }

    @Test
    fun coursesToDetailAndBack() {
        coursesToDetail()
        composeTestRule.runOnUiThread {
            activity.onBackPressed()
        }

        // The first course should be shown
        composeTestRule.onNodeWithSubstring(courses.first().name).assertExists()
    }

    private fun getOnboardingFabLabel(): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources
            .getString(R.string.continue_to_courses)
    }

    private fun getFeaturedCourseLabel(): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources
            .getString(R.string.featured)
    }

    private fun getCourseDesc(): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources
            .getString(R.string.course_desc)
    }
}
