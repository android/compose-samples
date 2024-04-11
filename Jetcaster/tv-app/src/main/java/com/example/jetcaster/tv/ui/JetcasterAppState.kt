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
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetcaster.core.model.PlayerEpisode

class JetcasterAppState(
    val navHostController: NavHostController
) {
    fun navigateToDiscover() {
        navHostController.navigate(Screen.Discover.route)
    }

    fun navigateToLibrary() {
        navHostController.navigate(Screen.Library.route)
    }

    fun navigateToProfile() {
        navHostController.navigate(Screen.Profile.route)
    }

    fun navigateToSearch() {
        navHostController.navigate(Screen.Search.route)
    }

    fun navigateToSettings() {
        navHostController.navigate(Screen.Settings.route)
    }

    fun showPodcastDetails(podcastUri: String) {
        val encodedUrL = Uri.encode(podcastUri)
        val screen = Screen.Podcast(encodedUrL)
        navHostController.navigate(screen.route)
    }

    fun showEpisodeDetails(episodeUri: String) {
        val encodeUrl = Uri.encode(episodeUri)
        val screen = Screen.Episode(encodeUrl)
        navHostController.navigate(screen.route)
    }

    fun showEpisodeDetails(playerEpisode: PlayerEpisode) {
        showEpisodeDetails(playerEpisode.uri)
    }

    fun playEpisode() {
        navHostController.navigate(Screen.Player.route)
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

    data object Discover : Screen {
        override val route = "/discover"
    }

    data object Library : Screen {
        override val route = "library"
    }

    data object Search : Screen {
        override val route = "search"
    }

    data object Profile : Screen {
        override val route = "profile"
    }

    data object Settings : Screen {
        override val route: String = "settings"
    }

    data class Podcast(private val podcastUri: String) : Screen {
        override val route = "$ROOT/$podcastUri"

        companion object : Screen {
            private const val ROOT = "podcast"
            const val PARAMETER_NAME = "podcastUri"
            override val route = "$ROOT/{$PARAMETER_NAME}"
        }
    }

    data class Episode(private val episodeUri: String) : Screen {

        override val route: String = "$ROOT/$episodeUri"
        companion object : Screen {
            private const val ROOT = "episode"
            const val PARAMETER_NAME = "episodeUri"
            override val route = "$ROOT/{$PARAMETER_NAME}"
        }
    }

    data object Player : Screen {
        override val route = "player"
    }
}
