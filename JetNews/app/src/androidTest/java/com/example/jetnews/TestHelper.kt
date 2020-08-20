/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews

import android.content.Context
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.ui.test.ComposeTestRule
import com.example.jetnews.ui.JetnewsApp
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.NavigationViewModel

/**
 * Launches the app from a test context
 */
fun ComposeTestRule.launchJetNewsApp(context: Context) {
    setContent {
        JetnewsStatus.resetState()
        JetnewsApp(
            TestAppContainer(context),
            remember { NavigationViewModel(SavedStateHandle()) }
        )
    }
}

/**
 * Resets the state of the app. Needs to be executed in Compose code (within a frame)
 */
fun JetnewsStatus.resetState() {
    favorites.clear()
    selectedTopics.clear()
}
