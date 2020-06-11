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

package com.example.compose.jetchat.conversation

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.rawDragGestureFilter
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.size
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.Info
import androidx.ui.material.icons.outlined.Search
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.text.LastBaseline
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.PxPosition
import androidx.ui.unit.dp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.theme.elevatedSurface

/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 */
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    val authorMe = stringResource(R.string.author_me)
    val timeNow = stringResource(id = R.string.now)
    Surface(modifier = modifier) {
        Stack(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Messages(
                    messages = uiState.messages,
                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f)
                )
                UserInput(
                    onMessageSent = { content ->
                        uiState.addMessage(
                            Message(authorMe, content, timeNow)
                        )
                    }
                )
            }
            // Channel name bar floats above the messages
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                modifier = Modifier.gravity(Alignment.TopCenter),
                onNavIconPressed = onNavIconPressed
            )
        }
    }
}

@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.95f)
    ) {
        Column {
            Row(modifier = Modifier.preferredHeight(56.dp)) {
                // Navigation icon
                Image(
                    asset = vectorResource(id = R.drawable.ic_jetchat),
                    modifier = Modifier
                        .gravity(Alignment.CenterVertically)
                        .clickable(onClick = onNavIconPressed)
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .gravity(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    horizontalGravity = Alignment.CenterHorizontally
                ) {
                    // Channel name
                    ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                        Text(
                            text = channelName,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    // Number of members
                    ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                        Text(
                            text = stringResource(R.string.members, channelMembers),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
                ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                    // Search icon
                    Icon(
                        asset = Icons.Outlined.Search,
                        modifier = Modifier
                            .clickable(onClick = {}) // TODO: Show not implemented dialog.
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .preferredHeight(24.dp)
                    )
                    // Info icon
                    Icon(
                        asset = Icons.Outlined.Info,
                        modifier = Modifier
                            .clickable(onClick = {}) // TODO: Show not implemented dialog.
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .preferredHeight(24.dp)
                    )
                }
            }
            Divider()
        }
    }
}

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollerPosition = ScrollerPosition()
    var userScrolled by state { false }
    // only runs once
    onActive {
        scrollerPosition.scrollTo(scrollerPosition.maxPosition)
    }

    // Scroll to last message
    if (!userScrolled && // Don't scroll if the user triggered the scrolling
        scrollerPosition.atBottom() // Don't scroll if already at the bottom
    ) {
        // Scroll smoothly after the first scroll
        scrollerPosition.smoothScrollTo(scrollerPosition.maxPosition)
    }

    Stack(modifier = modifier) {
        // Not remembering this is fine as it's cheaper to recreate
        val dragObserver = object : DragObserver {
            override fun onStart(downPosition: PxPosition) {
                userScrolled = true
            }
        }
        VerticalScroller(
            scrollerPosition = scrollerPosition,
            // Using [rawDragGestureFilter] so [DragObserver.onStart] is called immediately,
            modifier = Modifier.rawDragGestureFilter(dragObserver = dragObserver).fillMaxWidth()
        ) {
            val authorMe = stringResource(id = R.string.author_me)
            Column {
                messages.forEach { content ->
                    Message(
                        onAuthorClick = { navigateToProfile(content.author) },
                        msg = content,
                        isUserMe = content.author == authorMe
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(DensityAmbient.current) {
            JumpToBottomThreshold.toPx()
        }.value

        // Apply the threshold:
        val jumpToBottomButtonEnabled = (
            scrollerPosition.value < scrollerPosition.maxPosition - jumpThreshold
            )

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                // Reset the userScrolled flag, which is preventing the auto scroll
                userScrolled = false
            },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Message(onAuthorClick: () -> Unit, msg: Message, isUserMe: Boolean) {
    val image = if (isUserMe) {
        imageResource(id = R.drawable.ali)
    } else {
        imageResource(id = R.drawable.someone_else)
    }
    val borderColor = if (isUserMe) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.secondary
    }

    Row(modifier = Modifier.preferredHeightIn(minHeight = 64.dp)) {
        Image(
            modifier = Modifier
                .clickable(onClick = onAuthorClick)
                .padding(horizontal = 16.dp)
                // TODO: border behavior will change in https://b.corp.google.com/issues/158160576
                .drawBorder(1.5.dp, borderColor, CircleShape)
                .drawBorder(3.dp, MaterialTheme.colors.surface, CircleShape)
                .clip(CircleShape)
                .size(42.dp)
                .gravity(Alignment.Top),
            asset = image,
            contentScale = ContentScale.Crop
        )

        AuthorAndTextMessage(
            msg = msg,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                Text(
                    text = msg.author,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.alignWithSiblings(LastBaseline)
                )
            }
            Spacer(modifier = Modifier.preferredWidth(8.dp))
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(
                    text = msg.timestamp,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.alignWithSiblings(LastBaseline)
                )
            }
        }
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        ChatItemBubble(msg)
    }
}

private val ChatBubbleShape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 8.dp)

@Composable
fun ChatItemBubble(message: Message) {
    // TODO: Get these colors from a custom theme.
    val backgroundBubbleColor = remember(MaterialTheme.colors) {
        if (MaterialTheme.colors.isLight) {
            Color(0xFFF5F5F5)
        } else {
            MaterialTheme.colors.elevatedSurface(2.dp)
        }
    }
    Column {
        Surface(color = backgroundBubbleColor, shape = ChatBubbleShape) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        message.image?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = backgroundBubbleColor, shape = ChatBubbleShape) {
                Image(
                    asset = imageResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.preferredSize(160.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ConversationPreview() {
    JetchatTheme {
        ConversationContent(
            uiState = exampleUiState,
            navigateToProfile = { }
        )
    }
}

@Preview
@Composable
fun channelBarPrev() {
    JetchatTheme {
        ChannelNameBar(channelName = "composers", channelMembers = 52)
    }
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollerPosition.atBottom(): Boolean = maxPosition != Float.POSITIVE_INFINITY
