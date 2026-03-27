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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.R
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.navigation.NavigationItem
import com.example.jetnews.ui.navigation.TOP_LEVEL_ROUTES
import com.example.jetnews.ui.theme.JetnewsTheme

@Composable
fun AppNavRail(
    currentTopLevelRoute: NavKey,
    navigate: (NavKey) -> Unit,
    navigationItems: List<NavigationItem>,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        header = {
            Icon(
                painterResource(R.drawable.ic_jetnews_logo),
                null,
                Modifier.padding(vertical = 12.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        modifier = modifier,
    ) {
        Spacer(Modifier.weight(1f))

        navigationItems.forEach { topLevelRoute ->
            key(topLevelRoute.navKey) {
                NavigationRailItem(
                    selected = currentTopLevelRoute == topLevelRoute.navKey,
                    onClick = { navigate(topLevelRoute.navKey) },
                    icon = {
                        Icon(
                            painterResource(id = topLevelRoute.iconResourceId),
                            stringResource(topLevelRoute.iconContentDescriptionResourceId),
                        )
                    },
                    label = { Text(stringResource(topLevelRoute.labelResourceId)) },
                    alwaysShowLabel = false,
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    JetnewsTheme {
        AppNavRail(
            currentTopLevelRoute = HomeKey,
            navigationItems = TOP_LEVEL_ROUTES,
            navigate = {},
        )
    }
}
