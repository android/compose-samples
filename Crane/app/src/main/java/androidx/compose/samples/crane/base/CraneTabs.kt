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

package androidx.compose.samples.crane.base

import androidx.compose.foundation.Border
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.drawBorder
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat

@Composable
fun CraneTabBar(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit,
    children: @Composable (Modifier) -> Unit
) {
    Row(modifier) {
        // Separate Row as the children shouldn't have the padding
        Row(Modifier.padding(top = 8.dp)) {
            Image(
                modifier = Modifier.padding(top = 8.dp).clickable(onClick = onMenuClicked),
                asset = vectorResource(id = R.drawable.ic_menu)
            )
            Spacer(Modifier.preferredWidth(8.dp))
            Image(asset = vectorResource(id = R.drawable.ic_crane_logo))
        }
        children(Modifier.weight(1f).gravity(Alignment.CenterVertically))
    }
}

@Composable
fun CraneTabs(
    modifier: Modifier = Modifier,
    titles: List<String>,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit
) {
    val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
        TabRow.IndicatorContainer(tabPositions, tabSelected.ordinal) {}
    }

    TabRow(
        modifier = modifier,
        items = titles,
        selectedIndex = tabSelected.ordinal,
        contentColor = MaterialTheme.colors.onSurface,
        indicatorContainer = indicatorContainer,
        divider = {}
    ) { index, title ->
        val selected = index == tabSelected.ordinal
        val textModifier = if (!selected) {
            Modifier
        } else {
            Modifier.drawBorder(
                border = Border(2.dp, Color.White),
                shape = RoundedCornerShape(16.dp)
            ).padding(top = 8.dp, start = 16.dp, bottom = 8.dp, end = 16.dp)
        }

        Tab(
            text = {
                Text(
                    modifier = textModifier,
                    text = title.toUpperCase(
                        ConfigurationCompat.getLocales(ConfigurationAmbient.current)[0]
                    )
                )
            },
            selected = selected,
            onSelected = { onTabSelected(CraneScreen.values()[index]) }
        )
    }
}
