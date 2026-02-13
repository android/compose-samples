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

@file:OptIn(
    ExperimentalSharedTransitionApi::class,
)

package com.example.jetsnack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.components.rememberJetsnackScaffoldState
import com.example.jetsnack.ui.home.Feed
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.Profile
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.home.search.Search
import com.example.jetsnack.ui.navigation.CartKey
import com.example.jetsnack.ui.navigation.FeedKey
import com.example.jetsnack.ui.navigation.ProfileKey
import com.example.jetsnack.ui.navigation.SearchKey
import com.example.jetsnack.ui.navigation.SnackDetailKey
import com.example.jetsnack.ui.navigation.addHomeSection
import com.example.jetsnack.ui.navigation.addSnackDetail
import com.example.jetsnack.ui.navigation.currentHomeSectionKey
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.snackdetail.nonSpatialExpressiveSpring
import com.example.jetsnack.ui.snackdetail.spatialExpressiveSpring
import com.example.jetsnack.ui.theme.JetsnackTheme

@Preview
@Composable
fun JetsnackApp() {
    JetsnackTheme {

        val backStack = rememberNavBackStack(FeedKey)
        val jetsnackScaffoldState = rememberJetsnackScaffoldState()

        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
            ) {
                JetsnackScaffold(
                    bottomBar = {
                        val showBottomBar = backStack.last() !is SnackDetailKey

                        AnimatedVisibility(visible = showBottomBar) {
                            JetsnackBottomBar(
                                tabs = HomeSections.entries.toTypedArray(),
                                currentKey = backStack.currentHomeSectionKey(),
                                onItemClick = { navKey -> backStack.addHomeSection(navKey) },
                                modifier = Modifier
                                    .renderInSharedTransitionScopeOverlay(
                                        zIndexInOverlay = 1f,
                                    )
                                    .animateEnterExit(
                                        enter = fadeIn(nonSpatialExpressiveSpring()) + slideInVertically(
                                            spatialExpressiveSpring(),
                                        ) {
                                            it
                                        },
                                        exit = fadeOut(nonSpatialExpressiveSpring()) + slideOutVertically(
                                            spatialExpressiveSpring(),
                                        ) {
                                            it
                                        },
                                    ),
                            )
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = it,
                            modifier = Modifier.systemBarsPadding(),
                            snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) },
                        )
                    },
                    snackBarHostState = jetsnackScaffoldState.snackBarHostState,
                ) { padding ->

                    val modifier = Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)

                    val transitionSpec = fadeIn(nonSpatialExpressiveSpring()) togetherWith
                            fadeOut(nonSpatialExpressiveSpring())

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        entryProvider = entryProvider {
                            entry<FeedKey> {
                                Feed(
                                    onSnackClick = backStack.addSnackDetail(),
                                    modifier = modifier
                                )
                            }
                            entry<CartKey> {
                                Cart(
                                    onSnackClick = backStack.addSnackDetail(),
                                    modifier = modifier
                                )
                            }
                            entry<SearchKey> {
                                Search(
                                    onSnackClick = backStack.addSnackDetail(),
                                    modifier = modifier
                                )
                            }
                            entry<ProfileKey> {
                                Profile(modifier)
                            }
                            entry<SnackDetailKey> { key ->
                                SnackDetail(
                                    key.snackId,
                                    origin = key.origin,
                                    upPress = { backStack.removeLastOrNull() },
                                )
                            }
                        },
                        transitionSpec = { transitionSpec },
                        popTransitionSpec = { transitionSpec },
                        predictivePopTransitionSpec = { transitionSpec },
                    )
                }
            }
        }
    }
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
