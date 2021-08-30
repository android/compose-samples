/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.MainDestinations
import com.example.jetnews.ui.theme.JetnewsTheme
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun JetnewsNavRail(
    topIcon: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxHeight().padding(horizontal = 16.dp)) {
        topIcon(
            Modifier
                .systemBarsPadding()
                .align(alignment = Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(alignment = Alignment.Center),
            verticalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    modifier: Modifier = Modifier
) {
    JetnewsNavRail(
        topIcon = { topIconInnerModifier ->
            JetnewsIcon(topIconInnerModifier.padding(top = 16.dp))
        },
        modifier = modifier
    ) {
        NavRailButton(
            icon = Icons.Filled.Home,
            contentDescription = stringResource(id = R.string.cd_navigate_home),
            isSelected = currentRoute == MainDestinations.HOME_ROUTE,
            action = navigateToHome
        )
        NavRailButton(
            icon = Icons.Filled.ListAlt,
            contentDescription = stringResource(id = R.string.cd_navigate_interests),
            isSelected = currentRoute == MainDestinations.INTERESTS_ROUTE,
            action = navigateToInterests
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NavRailButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    Surface(
        color = backgroundColor,
        onClick = action,
        shape = CircleShape,
        modifier = modifier.size(48.dp)
    ) {
        NavigationIcon(
            icon = icon,
            isSelected = isSelected,
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    JetnewsTheme {
        Surface {
            AppNavRail(
                currentRoute = MainDestinations.HOME_ROUTE,
                navigateToHome = {},
                navigateToInterests = {},
            )
        }
    }
}
