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

package com.example.jetnews.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.theme.JetnewsTheme

@Composable
fun JetnewsApp(
    appContainer: AppContainer,
    navigationViewModel: NavigationViewModel
) {
    JetnewsTheme {
        AppContent(
            navigationViewModel = navigationViewModel,
            interestsRepository = appContainer.interestsRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@Composable
private fun AppContent(
    navigationViewModel: NavigationViewModel,
    postsRepository: PostsRepository,
    interestsRepository: InterestsRepository
) {
    Crossfade(navigationViewModel.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                is Screen.Home -> HomeScreen(
                    navigateTo = navigationViewModel::navigateTo,
                    postsRepository = postsRepository
                )
                is Screen.Interests -> InterestsScreen(
                    navigateTo = navigationViewModel::navigateTo,
                    interestsRepository = interestsRepository
                )
                is Screen.Article -> ArticleScreen(
                    postId = screen.postId,
                    postsRepository = postsRepository,
                    onBack = { navigationViewModel.onBack() }
                )
            }
        }
    }
}

@Composable
fun AppDrawer(
    navigateTo: (Screen) -> Unit,
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
        Color.Transparent
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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
            navigateTo = { },
            currentScreen = Screen.Home,
            closeDrawer = { }
        )
    }
}

@Preview("Drawer contents dark theme")
@Composable
fun PreviewJetnewsAppDark() {
    ThemedPreview(darkTheme = true) {
        AppDrawer(
            navigateTo = { },
            currentScreen = Screen.Home,
            closeDrawer = { }
        )
    }
}
