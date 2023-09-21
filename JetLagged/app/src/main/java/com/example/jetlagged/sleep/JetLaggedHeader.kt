/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged.sleep

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.R
import com.example.jetlagged.ui.theme.TitleBarStyle

@Preview
@Composable
fun JetLaggedHeader(
    onDrawerClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier.height(150.dp)
    ) {
        Row(modifier = Modifier.windowInsetsPadding(insets = WindowInsets.systemBars)) {
            IconButton(
                onClick = onDrawerClicked,
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.not_implemented)
                )
            }

            Text(
                stringResource(R.string.jetlagged_app_heading),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = TitleBarStyle,
                textAlign = TextAlign.Start
            )
        }
    }
}
