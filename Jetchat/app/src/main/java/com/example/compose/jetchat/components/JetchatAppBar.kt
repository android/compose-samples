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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose.jetchat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
/**
 * JetchatAppBar.kt - Custom top app bar component for Jetchat.
 *
 * This file provides a customized top app bar following Material Design 3 specifications,
 * tailored specifically for the Jetchat messaging application.
 *
 * Key Components:
 * - [JetchatAppBar]: Main composable for the app bar with custom navigation icon
 * - [JetchatAppBarPreview]: Light theme preview
 * - [JetchatAppBarPreviewDark]: Dark theme preview
 *
 * Features:
 * - Center-aligned title text
 * - Custom Jetchat icon as navigation button
 * - Support for action buttons on the right
 * - Scroll behavior support for collapsing/pinning
 * - Full accessibility support
 *
 * The app bar uses [CenterAlignedTopAppBar] from Material 3 and customizes the
 * navigation icon with the Jetchat-specific icon component.
 *
 * @see androidx.compose.material3.CenterAlignedTopAppBar
 * @see JetchatIcon
 * @see JetchatTheme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JetchatAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        actions = actions,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            JetchatIcon(
                contentDescription = stringResource(id = R.string.navigation_drawer_open),
                modifier = Modifier
                    .size(64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(16.dp),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun JetchatAppBarPreview() {
    JetchatTheme {
        JetchatAppBar(title = { Text("Preview!") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun JetchatAppBarPreviewDark() {
    JetchatTheme(isDarkTheme = true) {
        JetchatAppBar(title = { Text("Preview!") })
    }
}
