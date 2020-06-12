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
import androidx.compose.key
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.IntrinsicSize
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.heightIn
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.widthIn
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.material.icons.outlined.Chat
import androidx.ui.material.icons.outlined.Create
import androidx.ui.material.icons.outlined.MoreVert
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.px
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.theme.JetchatTheme

@Composable
fun ProfileScreen(userData: ProfileScreenState, onNavIconPressed: () -> Unit = { }) {

    val scrollerPosition = ScrollerPosition()
    val fabExpandScrollThreshold = with(DensityAmbient.current) { 5.dp.toPx() }.value

    Column(modifier = Modifier.fillMaxSize()) {
        JetchatAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavIconPressed = onNavIconPressed,
            title = { },
            actions = {
                ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                    // More icon
                    Icon(
                        asset = Icons.Outlined.MoreVert,
                        modifier = Modifier
                            .clickable(onClick = {}) // TODO: Show not implemented dialog.
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .preferredHeight(24.dp)
                    )
                }
            }
        )
        Stack(modifier = Modifier.weight(1f)) {
            VerticalScroller(
                modifier = Modifier.fillMaxSize(),
                scrollerPosition = scrollerPosition
            ) {
                Surface {
                    Column {
                        ProfileHeader(
                            scrollerPosition,
                            userData
                        )
                        UserInfoFields(userData)
                    }
                }
            }
            ProfileFab(
                extended = scrollerPosition.value < fabExpandScrollThreshold,
                userIsMe = userData.isMe(),
                modifier = Modifier.gravity(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState) {

    Column {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = userData.name,
                style = MaterialTheme.typography.h4
            )
        }
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                text = userData.position,
                style = MaterialTheme.typography.body1
            )
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        ProfileProperty(
            stringResource(R.string.display_name), userData.displayName
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        ProfileProperty(
            stringResource(R.string.status), userData.status
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        ProfileProperty(
            stringResource(R.string.twitter), userData.twitter, isLink = true
        )

        userData.timeZone?.let {
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileProperty(
                stringResource(R.string.timezone), userData.timeZone
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    scrollerPosition: ScrollerPosition,
    data: ProfileScreenState
) {
    val offset = (scrollerPosition.value.px / 2)
    val offsetDp = with(DensityAmbient.current) { offset.value.toDp() }

    data.photo?.let {
        val asset = imageResource(id = it)
        val ratioAsset = asset.width / asset.height.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Allow for landscape and portrait ratios
                .heightIn(maxHeight = 320.dp)
                .aspectRatio(ratioAsset),
            backgroundColor = Color.LightGray

        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = offsetDp),
                asset = asset,
                contentScale = ContentScale.Crop
            )
        }
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
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {

    Column(modifier = Modifier.padding(16.dp)) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(label, style = MaterialTheme.typography.caption)
        }
        val style = if (isLink) {
            MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary)
        } else {
            MaterialTheme.typography.body1
        }
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(value, style = style)
        }
    }
}

@Composable
fun ProfileError() {
    Text(stringResource(R.string.profile_error))
}

@Composable
fun ProfileFab(extended: Boolean, userIsMe: Boolean, modifier: Modifier = Modifier) {
    key(userIsMe) { // Prevent multiple invocations to execute during composition
        val aspectRatioModifier = if (!extended) Modifier.aspectRatio(1f) else Modifier
        FloatingActionButton(
            onClick = { /* TODO */ },
            modifier = modifier
                .padding(16.dp)
                .preferredHeight(48.dp)
                .widthIn(minWidth = 48.dp)
                .plus(aspectRatioModifier),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            Row(modifier = Modifier.wrapContentHeight()) {
                Icon(
                    asset = if (userIsMe) Icons.Outlined.Create else Icons.Outlined.Chat,
                    modifier = Modifier.padding(14.dp)
                )
                // TODO: Animate
                if (extended) {
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.message
                        ),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .gravity(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 480, name = "480 width - Me")
@Composable
fun ConvPreview480MeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

@Preview(widthDp = 480, name = "480 width - Other")
@Composable
fun ConvPreview480OtherDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}
