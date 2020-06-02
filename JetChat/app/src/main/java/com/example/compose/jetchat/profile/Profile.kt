/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.profile

import androidx.compose.Composable
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.graphics.VerticalGradient
import androidx.ui.layout.Column
import androidx.ui.layout.IntrinsicSize
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Px
import androidx.ui.unit.dp
import androidx.ui.unit.px
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme

@Composable
fun ProfileScreen(userData: ProfileScreenState) {

    WithConstraints { constraints, _ ->
        val height = constraints.maxHeight.value.toFloat()
        val position = animatedFloat(initVal = 0f).apply { setBounds(0f, height) }

        val profilePicHeight: Px = with(DensityAmbient.current) { 320.dp.toPx() }
        val scrollerPosition = ScrollerPosition()
        VerticalScroller(modifier = Modifier.fillMaxSize(), scrollerPosition = scrollerPosition) {
            Surface {
                Column(
                    modifier = Modifier.drawLayer(translationY = position.value)
                ) {
                    ProfileHeader(
                        scrollerPosition,
                        profilePicHeight,
                        userData
                    )
                    ProfileButtonsRow()
                    UserInfoFields(userData)

                }
            }
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState) {
    mapOf(
        stringResource(R.string.bio) to userData.position,
        stringResource(R.string.status) to userData.status,
        stringResource(R.string.timezone) to userData.timeZone,
        stringResource(R.string.common_channels) to userData.commonChannels,
        stringResource(R.string.lorem) to "Lorem",
        stringResource(R.string.lorem) to "Ipsum",
        stringResource(R.string.lorem) to "Lorem",
        stringResource(R.string.lorem) to "Lorem"
    ).forEach { (label, value) ->
        value?.let {
            ProfileProperty(
                label,
                value
            )
            Divider(color = Color.LightGray)
        }
    }
}

@Composable
private fun ProfileHeader(
    scrollerPosition: ScrollerPosition,
    profilePicHeight: Px,
    data: ProfileScreenState
) {
    val offset = (scrollerPosition.value.px / 2)
    val offsetDp = with(DensityAmbient.current) { offset.value.toDp() }
    Stack(
        modifier = Modifier
            .fillMaxWidth()
            .preferredHeight(320.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.LightGray

        ) {
            data.photo?.let {
                // TODO: Adapt height using image ratio
                val asset = imageResource(id = it)
                Image(
                    modifier = Modifier
                        .gravity(Alignment.Center)
                        .fillMaxSize()
                        .padding(top = offsetDp),
                    asset = asset,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().drawBackground(
                brush = VerticalGradient(
                    0.0f to Color.Transparent,
                    0.8f to Color.Transparent,
                    1.0f to Color(0xAA000000),
                    startY = Px.Zero,
                    endY = Px(profilePicHeight.value)
                )
            )
        )
        Text(
            modifier = Modifier.gravity(Alignment.BottomStart).padding(8.dp),
            text = data.name,
            style = MaterialTheme.typography.h4.copy(color = Color.White)
        )
    }
}

@Composable
private fun ProfileButtonsRow() {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .preferredHeight(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .gravity(Alignment.CenterVertically)
                    .wrapContentHeight(Alignment.CenterVertically),
                text = stringResource(R.string.message)
            )
        }
        Button(
            onClick = {},
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight(Alignment.CenterVertically),
                text = stringResource(R.string.edit_profile)
            )
        }
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .preferredHeight(48.dp)
                .padding(8.dp)
        ) {
            Icon(
                asset = Icons.Filled.MoreVert
            )
        }
    }
}

@Composable
fun ProfileProperty(label: String, value: String) {
    Row(modifier = Modifier.padding(12.dp)) {
        Column {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(value)
            }
            Text(label)
        }
    }
}

@Composable
fun ProfileError() {
    Text(stringResource(R.string.profile_error))
}

@Preview(widthDp = 480, name = "480 width - Me")
@Composable
fun ConvPreview480MeDefault() {
    JetchatTheme {
        ProfileScreen(aliConnors)
    }
}

@Preview(widthDp = 480, name = "480 width - Other")
@Composable
fun ConvPreview480OtherDefault() {
    JetchatTheme {
        ProfileScreen(aliConnors)
    }
}
