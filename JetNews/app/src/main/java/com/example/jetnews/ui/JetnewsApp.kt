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

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Expanded
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.surface.Surface
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen

@Composable
fun JetnewsApp() {

    val (drawerState, onDrawerStateChange) = +state { DrawerState.Closed }

    MaterialTheme(
        colors = lightThemeColors,
        typography = themeTypography
    ) {
        ModalDrawerLayout(
            drawerState = drawerState,
            onStateChange = onDrawerStateChange,
            gesturesEnabled = drawerState == DrawerState.Opened,
            drawerContent = {
                AppDrawer(
                    currentScreen = JetnewsStatus.currentScreen,
                    closeDrawer = { onDrawerStateChange(DrawerState.Closed) }
                )
            },
            bodyContent = { AppContent { onDrawerStateChange(DrawerState.Opened) } }
        )
    }
}

@Composable
private fun AppContent(openDrawer: () -> Unit) {
    Crossfade(JetnewsStatus.currentScreen) { screen ->
        Surface(color = (+MaterialTheme.colors()).background) {
            when (screen) {
                is Screen.Home -> HomeScreen { openDrawer() }
                is Screen.Interests -> InterestsScreen { openDrawer() }
                is Screen.Article -> ArticleScreen(postId = screen.postId)
            }
        }
    }
}

@Composable
private fun AppDrawer(
    currentScreen: Screen,
    closeDrawer: () -> Unit
) {
    Column(modifier = Expanded) {
        HeightSpacer(24.dp)
        Row(modifier = Spacing(16.dp)) {
            VectorImage(
                id = R.drawable.ic_jetnews_logo,
                tint = (+MaterialTheme.colors()).primary
            )
            WidthSpacer(8.dp)
            VectorImage(id = R.drawable.ic_jetnews_wordmark)
        }
        Divider(color = Color(0x14333333))
        DrawerButton(
            icon = R.drawable.ic_home,
            label = "Home",
            isSelected = currentScreen == Screen.Home
        ) {
            navigateTo(Screen.Home)
            closeDrawer()
        }

        DrawerButton(
            icon = R.drawable.ic_interests,
            label = "Interests",
            isSelected = currentScreen == Screen.Interests
        ) {
            navigateTo(Screen.Interests)
            closeDrawer()
        }
    }
}

@Composable
private fun DrawerButton(
    modifier: Modifier = Modifier.None,
    @DrawableRes icon: Int,
    label: String,
    isSelected: Boolean,
    action: () -> Unit
) {
    val colors = +MaterialTheme.colors()
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        colors.surface
    }

    Surface(
        modifier = modifier wraps Spacing(
            left = 8.dp,
            top = 8.dp,
            right = 8.dp
        ),
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Button(onClick = action, style = TextButtonStyle()) {
            Row(arrangement = Arrangement.Begin) {
                VectorImage(
                    modifier = Gravity.Center,
                    id = icon,
                    tint = textIconColor
                )
                WidthSpacer(16.dp)
                Text(
                    text = label,
                    style = (+MaterialTheme.typography()).body2.copy(
                        color = textIconColor
                    ),
                    modifier = ExpandedWidth
                )
            }
        }
    }
}

@Preview
@Composable
fun preview() {
    AppDrawer(
        currentScreen = JetnewsStatus.currentScreen,
        closeDrawer = { }
    )
}
