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

package com.example.jetsnack.ui.components

import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.jetsnack.model.Filter
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun FilterBar(filters: List<Filter>) {
    ScrollableRow(modifier = Modifier.preferredHeightIn(min = 56.dp)) {
        Spacer(Modifier.preferredWidth(8.dp))
        IconButton(
            onClick = { /* todo */ },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Rounded.FilterList,
                tint = JetsnackTheme.colors.brand,
                modifier = Modifier.diagonalGradientBorder(
                    colors = JetsnackTheme.colors.interactiveSecondary,
                    shape = CircleShape
                )
            )
        }

        filters.forEach { filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.preferredWidth(8.dp))
        }
    }
}

@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected, setSelected) = filter.enabled
    val backgroundColor =
        animate(if (selected) JetsnackTheme.colors.brand else JetsnackTheme.colors.uiBackground)
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor = animate(
        if (selected) JetsnackTheme.colors.textInteractive else JetsnackTheme.colors.textSecondary
    )
    JetsnackSurface(
        modifier = modifier.preferredHeight(28.dp).then(border),
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        Box(
            modifier = Modifier.toggleable(
                value = selected,
                onValueChange = setSelected
            )
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}
