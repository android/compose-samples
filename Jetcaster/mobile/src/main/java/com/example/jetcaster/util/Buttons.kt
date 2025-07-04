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

package com.example.jetcaster.util

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToggleFollowPodcastIconButton(isFollowed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconToggleButton(
        checked = isFollowed,
        onCheckedChange = { onClick() },
        modifier = modifier,
        colors = IconToggleButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary,
            checkedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            checkedContentColor = MaterialTheme.colorScheme.secondary,
        ),
        shapes = IconToggleButtonShapes(
            shape = RoundedCornerShape(10.dp),
            pressedShape = if (isFollowed) RoundedCornerShape(10.dp) else CircleShape,
            checkedShape = CircleShape,
        ),
    ) {
        Icon(
            // TODO: think about animating these icons
            painter = when {
                isFollowed -> painterResource(id = R.drawable.ic_check)
                else -> painterResource(id = R.drawable.ic_add)
            },
            contentDescription = when {
                isFollowed -> stringResource(R.string.cd_following)
                else -> stringResource(R.string.cd_not_following)
            },
        )
    }
}
