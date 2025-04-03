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

import com.google.android.horologist.media.ui.navigation.NavigationScreen
import kotlinx.serialization.Serializable

@Serializable
data object YourPodcasts : NavigationScreen

@Serializable
data object LatestEpisodes : NavigationScreen

@Serializable
data class PodcastDetails(val podcastUri: String) : NavigationScreen

@Serializable
data class Episode(val episodeUri: String) : NavigationScreen

@Serializable
data object UpNext : NavigationScreen
