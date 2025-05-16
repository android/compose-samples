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

package com.example.jetnews

import android.content.Context
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.example.jetnews.ui.JetnewsApp

/**
 * Launches the app from a test context
 */
fun ComposeContentTestRule.launchJetNewsApp(context: Context) {
    setContent {
        JetnewsApp(
            appContainer = TestAppContainer(context),
            widthSizeClass = WindowWidthSizeClass.Compact,
        )
    }
}
