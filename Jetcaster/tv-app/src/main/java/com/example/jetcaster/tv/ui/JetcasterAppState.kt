/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetcaster.core.model.PlayerEpisode

class JetcasterAppState(
    val navHostController: NavHostController
) {

    private var _currentScreenState = mutableStateOf<Screen>(Screen.Discover)
    val currentScreenState = _currentScreenState
    private fun navigate(screen: Screen) {
        _currentScreenState.value = screen
        navHostController.navigate(screen.route)
    }

    fun navigateToDiscover() {
        navigate(Screen.Discover)
    }

    fun navigateToLibrary() {
        navigate(Screen.Library)
    }

    fun navigateToProfile() {
        navigate(Screen.Profile)
    }

    fun navigateToSearch() {
        navigate(Screen.Search)
    }

    fun navigateToSettings() {
        navigate(Screen.Settings)
    }

    fun showPodcastDetails(podcastUri: String) {
        val encodedUrL = Uri.encode(podcastUri)
        val screen = Screen.Podcast(encodedUrL)
        navigate(screen)
    }

    fun showEpisodeDetails(episodeUri: String) {
        val encodeUrl = Uri.encode(episodeUri)
        val screen = Screen.Episode(encodeUrl)
        navigate(screen)
    }

    fun showEpisodeDetails(playerEpisode: PlayerEpisode) {
        showEpisodeDetails(playerEpisode.uri)
    }

    fun playEpisode() {
        navigate(Screen.Player)
    }

    fun backToHome() {
        navHostController.popBackStack()
        navigateToDiscover()
    }
}

@Composable
fun rememberJetcasterAppState(
    navHostController: NavHostController = rememberNavController()
) =
    remember(navHostController) {
        JetcasterAppState(navHostController)
    }

sealed interface Screen {
    val route: String
    val index: Int

    data object Discover : Screen {
        override val route = "/discover"
        override val index = 0
    }

    data object Library : Screen {
        override val route = "library"
        override val index = 1
    }

    data object Search : Screen {
        override val route = "search"
        override val index = 2
    }

    data object Profile : Screen {
        override val route = "profile"
        override val index = 3
    }

    data object Settings : Screen {
        override val route: String = "settings"
        override val index = 4
    }

    data class Podcast(private val podcastUri: String) : Screen {
        override val route = "$ROOT/$podcastUri"
        override val index = Companion.index

        companion object : Screen {
            private const val ROOT = "podcast"
            const val PARAMETER_NAME = "podcastUri"
            override val route = "$ROOT/{$PARAMETER_NAME}"
            override val index = 5
        }
    }

    data class Episode(private val episodeUri: String) : Screen {

        override val route: String = "$ROOT/$episodeUri"
        override val index = Companion.index

        companion object : Screen {
            private const val ROOT = "episode"
            const val PARAMETER_NAME = "episodeUri"
            override val route = "$ROOT/{$PARAMETER_NAME}"
            override val index = 6
        }
    }

    data object Player : Screen {
        override val route = "player"
        override val index = 7
    }
}
