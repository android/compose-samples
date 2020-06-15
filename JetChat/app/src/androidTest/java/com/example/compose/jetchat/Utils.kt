/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.Providers
import androidx.ui.test.SemanticsMatcher
import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.dumpToString
import androidx.ui.test.findAll
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme

fun AndroidComposeTestRule<out ComponentActivity>.getString(@StringRes resId: Int): String {
    return this.activityTestRule.activity.resources.getString(resId)
}

/**
 * Used to debug the semantic tree.
 */
fun dumpSemanticNodes() {
    Log.e("JetchatLog", findAll(SemanticsMatcher.any).dumpToString())
}

fun AndroidComposeTestRule<out ComponentActivity>.startConversationScreen() {
    setContent {
        Providers(
            BackPressedDispatcherAmbient provides this.activityTestRule.activity
        ) {
            JetchatTheme {
                ConversationContent(
                    uiState = exampleUiState,
                    navigateToProfile = { },
                    onNavIconPressed = { }
                )
            }
        }
    }
}
