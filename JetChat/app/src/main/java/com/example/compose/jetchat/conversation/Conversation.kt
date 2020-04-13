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

import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.rawDragGestureFilter
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.size
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Face
import androidx.ui.material.icons.outlined.Person
import androidx.ui.material.ripple.ripple
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.PxPosition
import androidx.ui.unit.dp
import com.example.compose.jetchat.JetChatTheme
import com.example.compose.jetchat.R

/**
 * Entry point for a conversation screen.
 *
 * @param modifier [Modifier] to apply to this layout node
 * @param uiState [ConversationUiState] that contains messages to display
 * @param backDispatcher A [OnBackPressedDispatcher] to intercept back actions
 * @param navigateToProfile User action when navigation to a profile is requested
 */
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    onSelectorStateChanged: (Boolean) -> Unit,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    backState: State<Boolean> = state { false }
) {
    val authorMe = stringResource(R.string.author_me)
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChannelNameBar()
            Messages(
                messages = uiState.messages,
                navigateToProfile = navigateToProfile,
                modifier = Modifier.weight(1f)
            )
            UserInput(
                onSelectorStateChanged = onSelectorStateChanged,
                onMessageSent = { content ->
                    uiState.addMessage(
                        Message(authorMe, content)
                    )
                },
                backState = backState
            )
        }
    }
}

@Composable
fun ChannelNameBar() {
    Column {
        Surface(color = MaterialTheme.colors.onSurface.copy(alpha = 0.04f)) {
            Row(
                modifier = Modifier
                    .padding(16.dp, 2.dp, 16.dp, 2.dp)
                    .fillMaxWidth(),
                verticalGravity = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "#composers"
                )
                Icon(
                    asset = Icons.Outlined.Person,
                    modifier = Modifier.preferredHeight(24.dp).padding(4.dp, 4.dp, 0.dp, 4.dp)
                )
                Text(
                    text = "42",
                    style = MaterialTheme.typography.overline
                )
            }
        }
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
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
    if (!userScrolled  // Don't scroll if the user triggered the scrolling
        && scrollerPosition.atBottom() // Don't scroll if already at the bottom
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
            Column {
                messages.forEach { content ->
                    Message(
                        onAuthorClick = { navigateToProfile(content.author) },
                        msg = content
                    )
                }
            }
        }
        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = scrollerPosition.value != scrollerPosition.maxPosition,
            onClicked = {
                // Reset the userScrolled flag, which is preventing the auto scroll
                userScrolled = false
            },
            modifier = Modifier.gravity(Alignment.BottomEnd)
        )
    }
}

@Composable
fun MessageImage(@DrawableRes img: Int) {
    Surface(modifier = Modifier.preferredHeight(96.dp).padding(8.dp), color = Color.LightGray) {
        Image(modifier = Modifier.padding(8.dp), asset = vectorResource(id = img))
    }
}

@Composable
fun Message(onAuthorClick: () -> Unit, msg: Message) {
    Row(modifier = Modifier.preferredHeightIn(minHeight = 64.dp)) {
        Clickable(
            onClick = onAuthorClick,
            modifier = Modifier.ripple()
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(48.dp)
                    .gravity(Alignment.Top),
                asset = Icons.Filled.Face,
                tint = Color.Gray
            )
        }

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
        Text(text = msg.author, style = MaterialTheme.typography.subtitle1)
        Text(text = msg.content, style = MaterialTheme.typography.body1)
        msg.image?.let { MessageImage(it) }
    }
}

@Preview()
@Composable
fun ConversationPreview() {
    JetChatTheme {
        ConversationContent(
            uiState = ConversationUiState(),
            onSelectorStateChanged = { },
            navigateToProfile = { }
        )
    }
}

private fun ScrollerPosition.atBottom(): Boolean = maxPosition != Float.POSITIVE_INFINITY