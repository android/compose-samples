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

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.material.Button
import androidx.ui.material.DrawerState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.TextButtonStyle
import com.example.jetnews.R

class Icons(resources: Resources) {
    val appLogo by lazy { imageFromResource(resources, R.drawable.jp_news_logo) }
    val home by lazy { imageFromResource(resources, R.drawable.baseline_home_black_24dp) }
    val menu by lazy { imageFromResource(resources, R.drawable.baseline_menu_white_24dp) }
    val more by lazy { imageFromResource(resources, R.drawable.baseline_more_vert_black_24dp) }
    val back by lazy { imageFromResource(resources, R.drawable.baseline_arrow_back_white_24dp) }
    val bookmarkOn by lazy { imageFromResource(resources, R.drawable.baseline_bookmark_black_24dp) }
    val bookmarkOff by lazy {
        imageFromResource(
            resources,
            R.drawable.baseline_bookmark_border_black_24dp
        )
    }
    val heartOn by lazy { imageFromResource(resources, R.drawable.baseline_favorite_black_24dp) }
    val heartOff by lazy {
        imageFromResource(
            resources,
            R.drawable.baseline_favorite_border_black_24dp
        )
    }
    val share by lazy { imageFromResource(resources, R.drawable.baseline_share_black_24dp) }
    val placeholder_1_1 by lazy { imageFromResource(resources, R.drawable.placeholder_1_1) }
    val placeholder_4_3 by lazy { imageFromResource(resources, R.drawable.placeholder_4_3) }
}

sealed class Screen {
    object Home : Screen()
    data class Article(val postId: String) : Screen()
    object Interests : Screen()
}

@Model
class JetNewsStatus {
    var shownScreen: Screen = Screen.Home
    var favorites = setOf<String>()
}

val jetNewsStatus = JetNewsStatus()

fun navigateTo(destination: Screen) {
    jetNewsStatus.shownScreen = destination
}

fun toggleBookmark(postId: String) {
    with(jetNewsStatus) {
        favorites = if (favorites.contains(postId)) {
            favorites - postId
        } else {
            favorites + postId
        }
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val icons = Icons(resources)
        posts = getPostsWithImagesLoaded(posts, resources)
        setContent {
            JetNewsApp(icons)
        }
    }
}

@Composable
fun JetNewsApp(icons: Icons) {

    val (drawerState, onDrawerStateChange) = +state { DrawerState.Closed }

    fun openDrawer() {
        onDrawerStateChange(DrawerState.Opened)
    }

    fun closeDrawer() {
        onDrawerStateChange(DrawerState.Closed)
    }

    @Composable
    fun AppContent() {
        val screen = jetNewsStatus.shownScreen
        when (screen) {
            is Screen.Home -> HomeScreen(icons = icons, openDrawer = { openDrawer() })
            is Screen.Interests -> TopicsScreen(icons = icons, openDrawer = { openDrawer() })
            is Screen.Article -> ArticleScreen(icons = icons, postId = screen.postId)
        }
    }

    @Composable
    fun AppDrawer() {
        @Composable
        fun DrawerButton(text: String, destination: Screen) {
            Button(onClick = {
                navigateTo(destination)
                closeDrawer()
            }, style = TextButtonStyle()) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.Start,
                    mainAxisSize = LayoutSize.Expand
                ) {
                    Text(text)
                }
            }
        }

        Column(
            crossAxisAlignment = CrossAxisAlignment.Start,
            crossAxisSize = LayoutSize.Expand,
            mainAxisAlignment = MainAxisAlignment.Start,
            mainAxisSize = LayoutSize.Expand
        ) {
            HeightSpacer(40.dp)
            Padding(8.dp) {
                DrawerButton("Home", Screen.Home)
            }

            Padding(8.dp) {
                DrawerButton("Interests", Screen.Interests)
            }
        }
    }

    MaterialTheme(
        colors = lightThemeColors,
        typography = typography
    ) {
        ModalDrawerLayout(
            drawerState = drawerState,
            onStateChange = onDrawerStateChange,
            drawerContent = { AppDrawer() },
            bodyContent = { AppContent() }
        )
    }
}
