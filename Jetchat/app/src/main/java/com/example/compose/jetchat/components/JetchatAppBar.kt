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

package com.example.compose.jetchat.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.clickable
import androidx.ui.layout.RowScope
import androidx.ui.layout.RowScope.gravity
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import com.example.compose.jetchat.R

@Composable
fun JetchatAppBar(
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    title: @Composable() () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colors.onSurface,
        actions = actions,
        title = title,
        navigationIcon = {
            Image(
                asset = vectorResource(id = R.drawable.ic_jetchat),
                modifier = Modifier
                    .gravity(Alignment.CenterVertically)
                    .clickable(onClick = onNavIconPressed)
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight()
            )
        }
    )
}
