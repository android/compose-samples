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

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackGradientTintedIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colors: List<Color> = JetsnackTheme.colors.interactiveSecondary
) {
    val interactionSource = remember { MutableInteractionSource() }

    // This should use a layer + srcIn but needs investigation
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = true,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = CircleShape
    )
    val pressed by interactionSource.collectIsPressedAsState()
    val background = if (pressed) {
        Modifier.offsetGradientBackground(colors, 200f, 0f)
    } else {
        Modifier.background(JetsnackTheme.colors.uiBackground)
    }
    val blendMode = if (JetsnackTheme.colors.isDark) BlendMode.Darken else BlendMode.Plus
    val modifierColor = if (pressed) {
        Modifier.diagonalGradientTint(
            colors = listOf(
                JetsnackTheme.colors.textSecondary,
                JetsnackTheme.colors.textSecondary
            ),
            blendMode = blendMode
        )
    } else {
        Modifier.diagonalGradientTint(
            colors = colors,
            blendMode = blendMode
        )
    }
    Surface(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .clip(CircleShape)
            .then(border)
            .then(background),
        color = Color.Transparent
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifierColor
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientTintedIconButtonPreview() {
    JetsnackTheme {
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Add,
            onClick = {},
            contentDescription = "Demo",
            modifier = Modifier.padding(4.dp)
        )
    }
}
