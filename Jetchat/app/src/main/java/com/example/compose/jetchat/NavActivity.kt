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
import androidx.compose.runtime.Providers
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.findViewTreeCompositionContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.example.compose.jetchat.components.JetchatScaffold
import com.example.compose.jetchat.conversation.BackPressHandler
import com.example.compose.jetchat.conversation.LocalBackPressedDispatcher
import com.example.compose.jetchat.databinding.ContentMainBinding
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

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
                Providers(LocalBackPressedDispatcher provides this.onBackPressedDispatcher) {
                    val scaffoldState = rememberScaffoldState()

                    val openDrawerEvent = viewModel.drawerShouldBeOpened.observeAsState()
                    if (openDrawerEvent.value == true) {
                        // Open drawer and reset state in VM.
                        scaffoldState.drawerState.open {
                            viewModel.resetOpenDrawerAction()
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    if (scaffoldState.drawerState.isOpen) {
                        BackPressHandler { scaffoldState.drawerState.close() }
                    }

                    JetchatScaffold(
                        scaffoldState,
                        onChatClicked = {
                            findNavController(R.id.nav_host_fragment)
                                .popBackStack(R.id.nav_home, true)
                            scaffoldState.drawerState.close()
                        },
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.nav_profile,
                                bundle
                            )
                            scaffoldState.drawerState.close()
                        }
                    ) {
                        // Workaround for b/178174718 and b/179181757
                        // Inflate the XML layout using View Binding:
                        val bindingRef = remember { Ref<ViewBinding>() }
                        val currentView = LocalView.current

                        AndroidViewBinding({ inflater, parent, attachToParent ->
                            if (bindingRef.value == null) {
                                val binding: ViewBinding =
                                    ContentMainBinding.inflate(inflater, parent, attachToParent)
                                bindingRef.value = binding
                                binding.root.compositionContext =
                                    currentView.findViewTreeCompositionContext()
                            }
                            bindingRef.value as ViewBinding
                        })
                        // End of workaround for b/178174718
                        // AndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
