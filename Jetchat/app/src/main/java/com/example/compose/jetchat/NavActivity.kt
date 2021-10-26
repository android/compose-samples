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

package com.example.compose.jetchat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.jetchat.components.JetchatScaffold
import com.example.compose.jetchat.conversation.BackPressHandler
import com.example.compose.jetchat.conversation.LocalBackPressedDispatcher
import com.example.compose.jetchat.databinding.ContentMainBinding
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.launch

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Provide WindowInsets to our content. We don't want to consume them, so that
            // they keep being pass down the view hierarchy (since we're using fragments).
            ProvideWindowInsets(consumeWindowInsets = false) {
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                ) {
                    val scaffoldState = rememberScaffoldState()

                    val drawerOpen by viewModel.drawerShouldBeOpened.collectAsState()
                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            scaffoldState.drawerState.open()
                            viewModel.resetOpenDrawerAction()
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    val scope = rememberCoroutineScope()
                    if (scaffoldState.drawerState.isOpen) {
                        BackPressHandler {
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                    }

                    JetchatScaffold(
                        scaffoldState,
                        onChatClicked = {
                            findNavController().popBackStack(R.id.nav_home, false)
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        },
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                    ) {
                        AndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}
