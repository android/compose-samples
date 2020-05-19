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

package com.example.jetnews.ui

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Home
import androidx.ui.material.icons.filled.ListAlt
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.theme.JetnewsTheme

@Composable
fun JetnewsApp(appContainer: AppContainer) {
    JetnewsTheme {
        AppContent(
            interestsRepository = appContainer.interestsRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@Composable
private fun AppContent(
    postsRepository: PostsRepository,
    interestsRepository: InterestsRepository
) {
    Crossfade(JetnewsStatus.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                is Screen.Home -> HomeScreen(postsRepository = postsRepository)
                is Screen.Interests -> InterestsScreen(interestsRepository = interestsRepository)
                is Screen.Article -> ArticleScreen(
                    postId = screen.postId,
                    postsRepository = postsRepository
                )
            }
        }
    }
}

@Composable
fun AppDrawer(
    currentScreen: Screen,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.preferredHeight(24.dp))
        JetNewsLogo(Modifier.padding(16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = "Home",
            isSelected = currentScreen == Screen.Home,
            action = {
                navigateTo(Screen.Home)
                closeDrawer()
            }
        )

        DrawerButton(
            icon = Icons.Filled.ListAlt,
            label = "Interests",
            isSelected = currentScreen == Screen.Interests,
            action = {
                navigateTo(Screen.Interests)
                closeDrawer()
            }
        )
    }
}

@Composable
private fun JetNewsLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            asset = vectorResource(R.drawable.ic_jetnews_logo),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )
        Spacer(Modifier.preferredWidth(8.dp))
        Image(
            asset = vectorResource(R.drawable.ic_jetnews_wordmark),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
    }
}

@Composable
private fun DrawerButton(
    icon: VectorAsset,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
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

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalGravity = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Image(
                    asset = icon,
                    colorFilter = ColorFilter.tint(textIconColor),
                    alpha = imageAlpha
                )
                Spacer(Modifier.preferredWidth(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview("Drawer contents")
@Composable
fun PreviewJetnewsApp() {
    ThemedPreview {
        AppDrawer(
            currentScreen = JetnewsStatus.currentScreen,
            closeDrawer = { }
        )
    }
}

@Preview("Drawer contents dark theme")
@Composable
fun PreviewJetnewsAppDark() {
    ThemedPreview(darkTheme = true) {
        AppDrawer(
            currentScreen = JetnewsStatus.currentScreen,
            closeDrawer = { }
        )
    }
}
