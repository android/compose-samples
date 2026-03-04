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

package com.example.jetnews.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.deeplink.handleDeepLink
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.navigation.rememberInitialBackStack
import com.example.jetnews.ui.utils.NewIntentEffect

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as JetnewsApplication).container
        setContent {
            val initialDeepLinkBackStack = remember { intent.data?.handleDeepLink() }
            var isOpenedByDeepLink by rememberSaveable { mutableStateOf(initialDeepLinkBackStack != null) }
            var initialBackStack by rememberInitialBackStack(initialDeepLinkBackStack ?: listOf(HomeKey))

            LaunchedEffect(initialBackStack) {
                Log.d("MainActivity", "initialBackStack: $initialBackStack")
            }

            NewIntentEffect { newIntent ->
                Log.d("MainActivity", "NewIntentEffect:newIntent: $newIntent")
                newIntent.data?.handleDeepLink()?.let {
                    isOpenedByDeepLink = true
                    initialBackStack = it
                }
            }

            JetnewsApp(
                appContainer,
                isOpenedByDeepLink,
                initialBackStack,
            )
        }
    }
}
