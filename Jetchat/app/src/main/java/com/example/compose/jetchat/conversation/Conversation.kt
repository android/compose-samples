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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose.jetchat.conversation

import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.components.JetchatIcon
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.theme.MontserratFontFamily
import kotlinx.coroutines.launch

/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
) {
    val authorMe = stringResource(R.string.author_me)
    val timeNow = stringResource(id = R.string.now)

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    var background by remember {
        mutableStateOf(Color.Transparent)
    }

    var borderStroke by remember {
        mutableStateOf(Color.Transparent)
    }

    val dragAndDropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val clipData = event.toAndroidDragEvent().clipData

                if (clipData.itemCount < 1) {
                    return false
                }

                uiState.addMessage(
                    Message(authorMe, clipData.getItemAt(0).text.toString(), timeNow),
                )

                return true
            }

            override fun onStarted(event: DragAndDropEvent) {
                super.onStarted(event)
                borderStroke = Color.Red
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                background = Color.Red.copy(alpha = .3f)
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                background = Color.Transparent
            }

            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                background = Color.Transparent
                borderStroke = Color.Transparent
            }
        }
    }

    var activeVideoUri by rememberSaveable { mutableStateOf<String?>(null) }

    MeshBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                ChannelNameBar(
                    channelName = uiState.channelName,
                    channelMembers = uiState.channelMembers,
                    onNavIconPressed = onNavIconPressed,
                    scrollBehavior = scrollBehavior,
                )
            },
            // Exclude ime and navigation bar padding so this can be added by the UserInput composable
            contentWindowInsets = ScaffoldDefaults
                .contentWindowInsets
                .exclude(WindowInsets.navigationBars)
                .exclude(WindowInsets.ime),
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { paddingValues ->
            Column(
                Modifier.fillMaxSize().padding(paddingValues)
                    .background(color = background)
                    .border(width = 2.dp, color = borderStroke)
                    .dragAndDropTarget(shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains(
                                ClipDescription.MIMETYPE_TEXT_PLAIN,
                            )
                    }, target = dragAndDropCallback),
            ) {
                Messages(
                    messages = uiState.messages,
                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    onVideoClick = { videoUri -> activeVideoUri = videoUri },
                )
                UserInput(
                    onMessageSent = { content ->
                        uiState.addMessage(
                            Message(authorMe, content, timeNow),
                        )
                    },
                    onVideoMessageSent = { videoUri, content ->
                        uiState.addMessage(
                            Message(
                                author = authorMe,
                                content = content,
                                timestamp = timeNow,
                                videoUri = videoUri,
                            ),
                        )
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    // let this element handle the padding so that the elevation is shown behind the
                    // navigation bar
                    modifier = Modifier.navigationBarsPadding().imePadding(),
                )
            }
        }

        AnimatedVisibility(
            visible = activeVideoUri != null,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)),
        ) {
            activeVideoUri?.let { uri ->
                FullScreenVideoPlayer(
                    videoUri = uri,
                    onDismiss = { activeVideoUri = null },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }
    JetchatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            JetchatIcon(
                contentDescription = stringResource(id = R.string.navigation_drawer_open),
                modifier = Modifier
                    .size(64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(16.dp),
            )
            /*Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEAE9FC).copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                    .clickable(onClick = onNavIconPressed),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "composers",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1D1B1F),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Image(
                        painter = painterResource(id = R.drawable.composers_avatars),
                        contentDescription = null,
                        modifier = Modifier
                            .width(76.dp)
                            .height(18.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }*/
        },
        actions = {
            Row(
                modifier = Modifier.padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Search icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color = Color(0xFF1E40FF), shape = CircleShape)
                        .clickable { functionalityNotAvailablePopupShown = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                        contentDescription = stringResource(id = R.string.search),
                    )
                }
                // Info icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color = Color(0xFF1E40FF), shape = CircleShape)
                        .clickable { functionalityNotAvailablePopupShown = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                        contentDescription = stringResource(id = R.string.info),
                    )
                }
            }
        },
    )
}

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onVideoClick: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {

        val authorMe = stringResource(id = R.string.author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize(),
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author

                // Hardcode day divider for Today
                if (index == 1) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    Message(
                        onAuthorClick = { name -> navigateToProfile(name) },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                        onVideoClick = onVideoClick,
                    )
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                    scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    onVideoClick: (String) -> Unit = {},
) {
    val horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isUserMe) {
            Image(
                painter = painterResource(id = R.drawable.android_profile_pic),
                contentDescription = msg.author,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 4.dp)
                    .size(width = 44.dp, height = 62.dp)
                    .clickable { onAuthorClick(msg.author) },
                contentScale = ContentScale.Fit,
            )
            Column(modifier = Modifier.weight(1f, fill = false)) {
                if (isLastMessageByAuthor) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFE4E7FF),
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = -12f
                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                            }
                            .padding(bottom = 2.dp)
                            .clickable { onAuthorClick(msg.author) },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = msg.author,
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E40FF),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = msg.timestamp,
                                fontFamily = MontserratFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF1E40FF).copy(alpha = 0.65f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Surface(
                    color = Color(0xFFD5DAFF),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomEnd = 24.dp,
                        bottomStart = 4.dp,
                    ),
                ) {
                    ChatItemBubble(
                        message = msg,
                        isUserMe = false,
                        authorClicked = onAuthorClick,
                        onVideoClick = onVideoClick,
                    )
                }
            }
        } else {
            Surface(
                color = Color(0xFF97A5FF),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 4.dp,
                    bottomEnd = 24.dp,
                    bottomStart = 24.dp,
                ),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                ChatItemBubble(
                    message = msg,
                    isUserMe = true,
                    authorClicked = onAuthorClick,
                    onVideoClick = onVideoClick,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.android_profile_pic),
                contentDescription = msg.author,
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 4.dp)
                    .size(width = 54.dp, height = 62.dp)
                    .clickable { onAuthorClick(msg.author) },
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun DayHeader(dayString: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = dayString,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        /*Image(
            painter = painterResource(id = R.drawable.wavy_divider),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(6.dp),
            contentScale = ContentScale.FillWidth,
        )*/
    }
}

@Composable
fun ChatItemBubble(message: Message, isUserMe: Boolean, authorClicked: (String) -> Unit, onVideoClick: (String) -> Unit = {}) {

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {
        val hasText = message.content.isNotBlank() || (message.image == null && message.videoUri == null)
        if (hasText) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked,
            )
        }

        message.image?.let {
            Image(
                painter = painterResource(it),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp),
                contentDescription = stringResource(id = R.string.attached_image),
            )
        }

        message.videoUri?.let { videoUri ->
            VideoThumbnail(
                videoUri = videoUri,
                onClick = { onVideoClick(videoUri) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
    }
}

@Composable
fun ClickableMessage(message: Message, isUserMe: Boolean, authorClicked: (String) -> Unit) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe,
    )

    ClickableText(
        text = styledMessage,
        style = TextStyle(
            fontFamily = MontserratFontFamily,
            fontSize = if (isUserMe) 18.sp else 16.sp,
            color = Color.Black,
            lineHeight = if (isUserMe) 24.sp else 22.sp,
        ),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        },
    )
}

@Preview
@Composable
fun ConversationPreview() {
    JetchatTheme {
        ConversationContent(
            uiState = exampleUiState,
            navigateToProfile = { },
        )
    }
}

@Preview
@Composable
fun ChannelBarPrev() {
    JetchatTheme {
        ChannelNameBar(channelName = "composers", channelMembers = 52)
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}

private val JumpToBottomThreshold = 56.dp
