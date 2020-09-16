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

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.theme.elevatedSurface

@Composable
fun JetchatAppBar(
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        // This bar needs to be translucent but, if the backgroundColor in TopAppBar is not
        // opaque, the elevation is ignored. We need to manually calculate the elevated surface
        // color for dark mode:
        val backgroundColor = MaterialTheme.colors.elevatedSurface(3.dp)
        TopAppBar(
            modifier = modifier,
            backgroundColor = backgroundColor.copy(alpha = 0.95f),
            elevation = 0.dp, // No shadow needed
            contentColor = MaterialTheme.colors.onSurface,
            actions = actions,
            title = title,
            navigationIcon = {
                Image(
                    asset = vectorResource(id = R.drawable.ic_jetchat),
                    modifier = Modifier
                        .clickable(onClick = onNavIconPressed)
                        .padding(horizontal = 16.dp)
                )
            }
        )
        Divider()
    }
}

@Preview
@Composable
fun JetchatAppBarPreview() {
    JetchatTheme {
        JetchatAppBar(title = { Text("Preview!") })
    }
}
