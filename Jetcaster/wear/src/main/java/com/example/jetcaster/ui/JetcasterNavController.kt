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

package com.example.jetcaster.ui

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.android.horologist.media.ui.navigation.NavigationScreens

/**
 * NavController extensions that links to the screens of the Jetcaster app.
 */
public object JetcasterNavController {

    public fun NavController.navigateToYourPodcast() {
        navigate(YourPodcasts.destination())
    }

    public fun NavController.navigateToLatestEpisode() {
        navigate(LatestEpisodes.destination())
    }

    public fun NavController.navigateToPodcastDetails(podcastUri: String) {
        navigate(PodcastDetails.destination(podcastUri))
    }

    public fun NavController.navigateToUpNext() {
        navigate(UpNext.destination())
    }
}

public object YourPodcasts : NavigationScreens("yourPodcasts") {
    public fun destination(): String = navRoute
}

public object LatestEpisodes : NavigationScreens("latestEpisodes") {
    public fun destination(): String = navRoute
}

public object PodcastDetails : NavigationScreens("podcast?podcastUri={podcastUri}") {
    public const val podcastUri: String = "podcastUri"
    public fun destination(podcastUriValue: String): String {
        val encodedUri = Uri.encode(podcastUriValue)
        return "podcast?$podcastUri=$encodedUri"
    }

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(podcastUri) {
                type = NavType.StringType
            },
        )
}

public object UpNext : NavigationScreens("upNext") {
    public fun destination(): String = navRoute
}
