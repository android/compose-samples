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
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.heightIn
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.widthIn
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.Chat
import androidx.ui.material.icons.outlined.Create
import androidx.ui.material.icons.outlined.MoreVert
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.AnimatingFabContent
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.components.baselineHeight
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.theme.JetchatTheme

@Composable
fun ProfileScreen(userData: ProfileScreenState, onNavIconPressed: () -> Unit = { }) {

    val scrollerPosition = ScrollerPosition()

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
                extended = scrollerPosition.value == 0f,
                userIsMe = userData.isMe(),
                modifier = Modifier.gravity(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState) {

    Column {
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        NameAndPosition(userData)

        ProfileProperty(stringResource(R.string.display_name), userData.displayName)

        ProfileProperty(stringResource(R.string.status), userData.status)

        ProfileProperty(stringResource(R.string.twitter), userData.twitter, isLink = true)

        userData.timeZone?.let {
            ProfileProperty(stringResource(R.string.timezone), userData.timeZone)
        }
    }
}

@Composable
private fun NameAndPosition(
    userData: ProfileScreenState
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData,
            modifier = Modifier.baselineHeight(32.dp)
        )
        Position(
            userData,
            modifier = Modifier.padding(bottom = 20.dp).baselineHeight(24.dp)
        )
    }
}

@Composable
private fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
        Text(
            text = userData.name,
            modifier = modifier,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
private fun Position(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
        Text(
            text = userData.position,
            modifier = modifier,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ProfileHeader(
    scrollerPosition: ScrollerPosition,
    data: ProfileScreenState
) {
    val offset = (scrollerPosition.value / 2)
    val offsetDp = with(DensityAmbient.current) { offset.toDp() }

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
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                text = label,
                modifier = Modifier.baselineHeight(24.dp),
                style = MaterialTheme.typography.caption
            )
        }
        val style = if (isLink) {
            MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary)
        } else {
            MaterialTheme.typography.body1
        }
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                text = value,
                modifier = Modifier.baselineHeight(24.dp),
                style = style
            )
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
        FloatingActionButton(
            onClick = { /* TODO */ },
            modifier = modifier
                .padding(16.dp)
                .preferredHeight(48.dp)
                .widthIn(minWidth = 48.dp),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        asset = if (userIsMe) Icons.Outlined.Create else Icons.Outlined.Chat
                    )
                },
                text = { opacity ->
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.message
                        ),
                        modifier = Modifier.drawOpacity(opacity)
                    )
                },
                extended = extended

            )
        }
    }
}

@Preview
@Composable
fun ConvPreview480MeDefault() {
    JetchatTheme {
        ProfileScreen(colleagueProfile)
    }
}
