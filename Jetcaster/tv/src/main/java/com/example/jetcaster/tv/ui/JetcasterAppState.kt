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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetcaster.core.player.model.PlayerEpisode
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class JetcasterAppState(
    val navHostController: NavHostController
) {

    val currentDestinationFlow = navHostController.currentBackStackEntryFlow.map {
        it.destination
    }

    fun navigateToDiscover() {
        navHostController.navigate(DiscoverRoute)
    }

    fun navigateToLibrary() {
        navHostController.navigate(LibraryRoute)
    }

    fun navigateToProfile() {
        navHostController.navigate(ProfileRoute)
    }

    fun navigateToSearch() {
        navHostController.navigate(SearchRoute)
    }

    fun navigateToSettings() {
        navHostController.navigate(SettingsRoute)
    }

    fun showPodcastDetails(podcastUri: String) {
        val encodedUrl = Uri.encode(podcastUri)
        navHostController.navigate(PodcastRoute(encodedUrl))
    }

    fun showEpisodeDetails(playerEpisode: PlayerEpisode) {
        val encodeUrl = Uri.encode(playerEpisode.uri)
        navHostController.navigate(EpisodeRoute(encodeUrl))
    }

    fun playEpisode() {
        navHostController.navigate(PlayerRoute)
    }

    fun backToHome() {
        navHostController.popBackStack()
        navigateToDiscover()
    }
}

// Todo: Null-safe not supported in library, remove when supported in future
inline fun <reified T : Any> NavDestination?.hasRoute() =
    this?.hasRoute(T::class) ?: false

@Composable
fun rememberJetcasterAppState(
    navHostController: NavHostController = rememberNavController()
) =
    remember(navHostController) {
        JetcasterAppState(navHostController)
    }

@Serializable internal object DiscoverRoute
@Serializable internal object LibraryRoute
@Serializable internal object SearchRoute
@Serializable internal object ProfileRoute
@Serializable internal object SettingsRoute
@Serializable internal object PlayerRoute

const val PODCAST_URI_KEY = "podcastUri"
@Serializable data class PodcastRoute(val podcastUri: String)

const val EPISODE_URI_KEY = "episodeUri"
@Serializable data class EpisodeRoute(val episodeUri: String)
