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
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.compose.jetchat.components.JetchatDrawer
import kotlinx.coroutines.launch
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import com.example.compose.jetchat.navigation.DEST_ROUTE_CONVERSATION
import com.example.compose.jetchat.navigation.DEST_ROUTE_PROFILE
import com.example.compose.jetchat.navigation.mobileNavigationNavHostCont

/**
 * Main activity for the app.
 */
class NavActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var navHostCont: NavHostController? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        setContent {

            val drawerState = rememberDrawerState(initialValue = Closed)
            val drawerOpen by viewModel.drawerShouldBeOpened
                .collectAsStateWithLifecycle()

            var selectedMenu by remember { mutableStateOf("composers") }
            if (drawerOpen) {
                // Open drawer and reset state in VM.
                LaunchedEffect(Unit) {
                    // wrap in try-finally to handle interruption whiles opening drawer
                    try {
                        drawerState.open()
                    } finally {
                        viewModel.resetOpenDrawerAction()
                    }
                }
            }

            val scope = rememberCoroutineScope()

            JetchatDrawer(
                drawerState = drawerState,
                selectedMenu = selectedMenu,
                onChatClicked = {
                    navHostCont?.popBackStack(DEST_ROUTE_CONVERSATION, false)

                    scope.launch {
                        drawerState.close()
                    }
                    selectedMenu = it
                },
                onProfileClicked = { user ->
                    navHostCont?.navigate("$DEST_ROUTE_PROFILE/$user")

                    scope.launch {
                        drawerState.close()
                    }
                    selectedMenu = user
                },
            ) {
                navHostCont = mobileNavigationNavHostCont(viewModel)
            }
        }
    }
}
