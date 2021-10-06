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

package com.example.jetcaster.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.WindowInfo
import com.example.jetcaster.util.isBookPosture
import com.example.jetcaster.util.isTableTopPosture
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        /**
         * Flow of [WindowInfo] that emits every time there's a change in the windowLayoutInfo
         */
        val windowInfo = windowInfoRepository().windowLayoutInfo
            .flowWithLifecycle(this.lifecycle)
            .map { layoutInfo ->
                val foldingFeature: FoldingFeature? =
                    layoutInfo.displayFeatures.find { it is FoldingFeature } as? FoldingFeature
                WindowInfo(
                    isInTableTopPosture = isTableTopPosture(foldingFeature),
                    isInBookPosture = isBookPosture(foldingFeature),
                    hingePosition = foldingFeature?.bounds
                )
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = WindowInfo()
            )

        setContent {
            JetcasterTheme {
                ProvideWindowInsets {
                    JetcasterApp(windowInfo)
                }
            }
        }
    }
}
