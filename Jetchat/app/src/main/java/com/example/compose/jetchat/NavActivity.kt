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
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.backPressHandler
import com.example.compose.jetchat.theme.JetchatTheme

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity() {

    // Used for navigation events between fragments.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Providers(BackPressedDispatcherAmbient provides this) {
                val scaffoldState = rememberScaffoldState()

                val openDrawerEvent = viewModel.drawerShouldBeOpened.observeAsState()
                if (openDrawerEvent.value == true) {
                    scaffoldState.drawerState.open {
                        viewModel.resetOpenDrawer()
                    }
                }

                backPressHandler(
                    enabled = scaffoldState.drawerState.isOpen,
                    onBackPressed = { scaffoldState.drawerState.close() },
                    highPriority = true
                )

                JetchatScaffold(scaffoldState)
            }
        }
    }

    @Composable
    private fun JetchatScaffold(
        scaffoldState: ScaffoldState,
    ) {
        JetchatTheme {
            Scaffold(
                scaffoldState = scaffoldState,
                drawerContent = {
                    JetchatDrawer(
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
                    )
                }
            ) {
                val context = ContextAmbient.current

                AndroidView({
                    LayoutInflater.from(context)
                        .inflate(R.layout.content_main, FrameLayout(context), false)
                })
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}