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

package com.example.jetnews.ui

import androidx.compose.Composable
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.Crossfade
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.material.Button
import androidx.ui.material.DrawerState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor

@Composable
fun JetnewsApp(icons: Icons) {

    val (drawerState, onDrawerStateChange) = +state { DrawerState.Closed }

    MaterialTheme(
        colors = lightThemeColors,
        typography = typography
    ) {
        ModalDrawerLayout(
            drawerState = drawerState,
            onStateChange = onDrawerStateChange,
            drawerContent = { AppDrawer(closeDrawer = { onDrawerStateChange(DrawerState.Closed) }) },
            bodyContent = {
                AppContent(
                    icons = icons,
                    openDrawer = { onDrawerStateChange(DrawerState.Opened) })
            }
        )
    }
}

@Composable
private fun DrawerButton(drawerButtonText: String, action: () -> Unit) {
    Button(onClick = { action() }, style = TextButtonStyle()) {
        Row(mainAxisSize = LayoutSize.Expand) {
            Text(drawerButtonText)
        }
    }
}

@Composable
private fun AppContent(icons: Icons, openDrawer: () -> Unit) {
    Crossfade(JetnewsStatus.currentScreen) { screen ->
        Surface(color = +themeColor { background }) {
            when (screen) {
                is Screen.Home -> HomeScreen(icons = icons, openDrawer = { openDrawer() })
                is Screen.Interests -> InterestsScreen(icons = icons, openDrawer = { openDrawer() })
                is Screen.Article -> ArticleScreen(icons = icons, postId = screen.postId)
            }
        }
    }
}

@Composable
private fun AppDrawer(closeDrawer: () -> Unit) {
    Column(
        crossAxisSize = LayoutSize.Expand,
        mainAxisSize = LayoutSize.Expand
    ) {
        HeightSpacer(40.dp)
        Padding(8.dp) {
            DrawerButton("Home") {
                navigateTo(Screen.Home)
                closeDrawer()
            }
        }

        Padding(8.dp) {
            DrawerButton("Interests") {
                navigateTo(Screen.Interests)
                closeDrawer()
            }
        }
    }
}
