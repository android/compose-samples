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

package com.example.compose.jetchat.profile

import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.mutableStateOf
import androidx.compose.state
import androidx.ui.tooling.preview.Preview
import com.example.compose.jetchat.JetChatProfileTheme


@Preview(widthDp = 340, name = "340 width - Me")
@Composable
fun ProfilePreview340() {
    JetChatProfileTheme(userIsMe = true) {
        ProfileScreen(
            aliConnors
        )
    }
}

@Preview(widthDp = 480, name = "480 width - Me")
@Composable
fun ProfilePreview480Me() {
    JetChatProfileTheme(userIsMe = true) {
        ProfileScreen(
            aliConnors
        )
    }
}

@Preview(widthDp = 480, name = "480 width - Other")
@Composable
fun ProfilePreview480Other() {
    JetChatProfileTheme(userIsMe = false) {
        ProfileScreen(
            someOneElse
        )
    }
}
@Preview(widthDp = 340, name = "340 width - Me - Dark")
@Composable
fun ProfilePreview340MeDark() {
    JetChatProfileTheme(isDarkTheme = true, userIsMe = true) {
        ProfileScreen(
            aliConnors
        )
    }
}

@Preview(widthDp = 480, name = "480 width - Me - Dark")
@Composable
fun ProfilePreview480MeDark() {
    JetChatProfileTheme(isDarkTheme = true, userIsMe = true) {
        ProfileScreen(
            aliConnors
        )
    }
}

@Preview(widthDp = 480, name = "480 width - Other - Dark")
@Composable
fun ProfilePreview480OtherDark() {
    JetChatProfileTheme(isDarkTheme = true, userIsMe = false) {
        ProfileScreen(
            someOneElse
        )
    }
}
