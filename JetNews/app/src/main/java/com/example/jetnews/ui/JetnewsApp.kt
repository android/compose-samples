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
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.material.surface.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen

@Composable
fun JetnewsApp() {

    MaterialTheme(
        colors = lightThemeColors,
        typography = themeTypography
    ) {
       AppContent()
    }
}

@Composable
private fun AppContent() {
    Crossfade(JetnewsStatus.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors().background) {
            when (screen) {
                is Screen.Home -> HomeScreen()
                is Screen.Interests -> InterestsScreen()
                is Screen.Article -> ArticleScreen(postId = screen.postId)
            }
        }
    }
}

@Composable
fun AppDrawer(
    currentScreen: Screen,
    closeDrawer: () -> Unit
) {
    Column(modifier = LayoutSize.Fill) {
        Spacer(LayoutHeight(24.dp))
        Row(modifier = LayoutPadding(16.dp)) {
            VectorImage(
                id = R.drawable.ic_jetnews_logo,
                tint = (MaterialTheme.colors()).primary
            )
            Spacer(LayoutWidth(8.dp))
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
    val colors = MaterialTheme.colors()
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
        modifier = modifier + LayoutPadding(
            left = 8.dp,
            top = 8.dp,
            right = 8.dp,
            bottom = 0.dp
        ),
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        TextButton(onClick = action) {
            Row {
                VectorImage(
                    modifier = LayoutGravity.Center,
                    id = icon,
                    tint = textIconColor
                )
                Spacer(LayoutWidth(16.dp))
                Text(
                    text = label,
                    style = (MaterialTheme.typography()).body2.copy(
                        color = textIconColor
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewJetnewsApp() {
    AppDrawer(
        currentScreen = JetnewsStatus.currentScreen,
        closeDrawer = { }
    )
}
