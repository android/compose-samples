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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.jetsnack.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleState
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * An alternative to [androidx.compose.material3.Surface] utilizing
 * [com.example.jetsnack.ui.theme.JetsnackColors]
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    style: Style = Style,
    // todo confirm patten is acceptable
    styleState: StyleState = rememberUpdatedStyleState(null),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .styleable(styleState, JetsnackTheme.styles.surfaceStyle, style),
    ) {
        // todo double check CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
        content()
    }
}
