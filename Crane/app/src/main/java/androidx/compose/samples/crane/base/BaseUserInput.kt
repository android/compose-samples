/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.base

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.setValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

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
    modifier: Modifier = Modifier,
    text: String,
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

@Composable
fun CraneEditableUserInput(
    hint: String,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    onInputChanged: (String) -> Unit
) {
    var textFieldState by mutableStateOf(TextFieldValue(text = hint))
    val isHint = { textFieldState.text == hint }

    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !isHint() },
        showCaption = { !isHint() },
        vectorImageId = vectorImageId
    ) {
        TextField(
            value = textFieldState,
            onValueChange = {
                textFieldState = it
                if (!isHint()) onInputChanged(textFieldState.text)
            },
            textStyle = if (isHint()) captionTextStyle else MaterialTheme.typography.body1
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
    tint: Color = MaterialTheme.colors.onSurface,
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
                    modifier = Modifier.gravity(Alignment.CenterVertically),
                    text = caption,
                    style = (captionTextStyle).copy(color = tint)
                )
                Spacer(Modifier.preferredWidth(8.dp))
            }
            Row(Modifier.weight(1f).gravity(Alignment.CenterVertically)) {
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
            showCaption = { true }) {
            Text(text = "text", style = MaterialTheme.typography.body1)
        }
    }
}
