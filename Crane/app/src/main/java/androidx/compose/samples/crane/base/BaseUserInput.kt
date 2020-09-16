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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

@Composable
fun SimpleUserInput(
    text: String? = null,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null
) {
    CraneUserInput(
        caption = if (text == null) caption else null,
        text = text ?: "",
        vectorImageId = vectorImageId
    )
}

@Composable
fun CraneUserInput(
    text: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    tint: Color = contentColor()
) {
    CraneBaseUserInput(
        modifier = modifier,
        caption = caption,
        vectorImageId = vectorImageId,
        tintIcon = { text.isNotEmpty() },
        tint = tint
    ) {
        Text(text = text, style = MaterialTheme.typography.body1.copy(color = tint))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CraneEditableUserInput(
    hint: String,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    onInputChanged: (String) -> Unit
) {
    var textFieldState by remember { mutableStateOf(TextFieldValue(text = hint)) }
    val isHint = { textFieldState.text == hint }

    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !isHint() },
        showCaption = { !isHint() },
        vectorImageId = vectorImageId
    ) {
        BaseTextField(
            value = textFieldState,
            onValueChange = {
                textFieldState = it
                if (!isHint()) onInputChanged(textFieldState.text)
            },
            textStyle = if (isHint()) captionTextStyle else MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun CraneBaseUserInput(
    modifier: Modifier = Modifier,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    showCaption: () -> Boolean = { true },
    tintIcon: () -> Boolean,
    tint: Color = contentColor(),
    children: @Composable () -> Unit
) {
    Surface(modifier = modifier, color = MaterialTheme.colors.primaryVariant) {
        Row(Modifier.padding(all = 12.dp)) {
            if (vectorImageId != null) {
                Icon(
                    modifier = Modifier.preferredSize(24.dp, 24.dp),
                    asset = vectorResource(id = vectorImageId),
                    tint = if (tintIcon()) tint else Color(0x80FFFFFF)
                )
                Spacer(Modifier.preferredWidth(8.dp))
            }
            if (caption != null && showCaption()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = caption,
                    style = (captionTextStyle).copy(color = tint)
                )
                Spacer(Modifier.preferredWidth(8.dp))
            }
            Row(Modifier.weight(1f).align(Alignment.CenterVertically)) {
                children()
            }
        }
    }
}

@Preview
@Composable
fun previewInput() {
    CraneScaffold {
        CraneBaseUserInput(
            tintIcon = { true },
            vectorImageId = R.drawable.ic_plane,
            caption = "Caption",
            showCaption = { true }
        ) {
            Text(text = "text", style = MaterialTheme.typography.body1)
        }
    }
}
