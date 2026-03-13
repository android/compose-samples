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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.ui.theme.JetsnackTheme

// todo introduce lint check for when style is provided but not used ?
@Composable
fun JetsnackGradientTintedIconButton(
    @DrawableRes iconResourceId: Int,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    style: Style = Style,
    // TODO we cannot migrate this out yet!!
    colors: List<Color> = JetsnackTheme.colors.interactiveSecondary,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val styleState = rememberUpdatedStyleState(interactionSource)
    Surface(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .styleable(styleState, JetsnackTheme.appStyles.gradientIconButtonStyle, style),
        color = Color.Transparent,
    ) {
        val blendMode = if (JetsnackTheme.colors.isDark) BlendMode.Darken else BlendMode.Plus
        // todo this cant be migrated yet due to no support for blendMode and drawWithContent in Styles
        // This should use a layer + srcIn but needs investigation
        val pressed = interactionSource.collectIsPressedAsState().value
        val modifierColor = if (pressed) {
            Modifier.contentTintDiagonalGradient(
                colors = listOf(
                    JetsnackTheme.colors.textSecondary,
                    JetsnackTheme.colors.textSecondary,
                ),
                blendMode = blendMode,
            )
        } else {
            Modifier.contentTintDiagonalGradient(
                colors = colors,
                blendMode = blendMode,
            )
        }
        Icon(
            painter = painterResource(id = iconResourceId),
            contentDescription = contentDescription,
            modifier = modifierColor,
        )
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientTintedIconButtonPreview() {
    JetsnackTheme {
        JetsnackGradientTintedIconButton(
            iconResourceId = R.drawable.ic_add,
            onClick = {},
            contentDescription = "Demo",
            modifier = Modifier.padding(4.dp),
        )
    }
}
