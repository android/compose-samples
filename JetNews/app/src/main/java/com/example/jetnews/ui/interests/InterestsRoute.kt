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

package com.example.jetnews.ui.interests

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.jetnews.utils.WindowSize

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param interestsViewModel ViewModel that handles the business logic of this screen
 * @param windowSize (state) the current window size class
 * @param isDrawerActive (state) true if the drawer is active
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for screen Scaffold
 */
@Composable
fun InterestsRoute(
    interestsViewModel: InterestsViewModel,
    windowSize: WindowSize,
    isDrawerActive: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val tabContent = rememberTabContent(windowSize, interestsViewModel)
    val (currentSection, updateSection) = rememberSaveable {
        mutableStateOf(tabContent.first().section)
    }

    InterestsScreen(
        tabContent = tabContent,
        currentSection = currentSection,
        windowSize = windowSize,
        isDrawerActive = isDrawerActive,
        onTabChange = updateSection,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}
