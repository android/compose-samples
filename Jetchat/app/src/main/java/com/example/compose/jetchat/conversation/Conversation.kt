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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.blur.BlurRadiusSpec
import com.example.compose.jetchat.blur.backdropBlur
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.video.FullScreenVideoPlayer
import com.example.compose.jetchat.video.VideoThumbnail
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

    Box(modifier = modifier.fillMaxSize().background(color = Color(0xFFEAE9FC))) {
        Scaffold(
            containerColor = Color(0xFFEAE9FC),
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
//            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { paddingValues ->
            Column(
                Modifier.fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
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
                    contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
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
        modifier = modifier
            .backdropBlur(
                tint = Color(0xFFEAE9FC).copy(alpha = 0.5f),
                elevation = 0.dp,
                radius = 12.dp,
            ),
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF000965)
                )
                // Number of members
                Text(
                    text = stringResource(R.string.members, channelMembers),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF444746),
                )
            }
        },
        actions = {
            // Search icon
            Icon(
                painterResource(id = R.drawable.ic_search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.search),
            )
            // Info icon
            Icon(
                painterResource(id = R.drawable.ic_info),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info),
            )
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onVideoClick: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {

        val authorMe = stringResource(id = R.string.author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding = contentPadding,
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

                // Hardcode day dividers for simplicity
                if (index == messages.size - 1) {
                    item(key = "header_20_aug", contentType = "header") {
                        DayHeader("20 Aug")
                    }
                } else if (index == 2) {
                    item(key = "header_today", contentType = "header") {
                        DayHeader("Today")
                    }
                }

                item(key = content.id, contentType = "message") {
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
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 12.dp) else Modifier

    if (isUserMe) {
        // Self messages: right-aligned bubble (#97A5FF) with avatar on the right (Figma 191:25321, 191:25358)
        Row(
            modifier = spaceBetweenAuthors
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
            verticalAlignment = Alignment.Top,
        ) {
            AuthorAndTextMessage(
                msg = msg,
                isUserMe = true,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                onVideoClick = onVideoClick,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = 32.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (isLastMessageByAuthor) {
                Image(
                    modifier = Modifier
                        .clickable(onClick = { onAuthorClick(msg.author) })
                        .size(48.dp)
                        .border(1.5.dp, Color(0xFF97A5FF), CircleShape)
                        .clip(CircleShape),
                    painter = painterResource(id = msg.authorImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    } else {
        // Other user messages: left-aligned avatar + pill badge header + bubble (#D5DAFF) (Figma 191:25301, 191:25334)
        Column(
            modifier = spaceBetweenAuthors
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            if (isLastMessageByAuthor) {
                AuthorNameTimestamp(msg = msg, onAuthorClick = onAuthorClick)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                if (isLastMessageByAuthor) {
                    Image(
                        modifier = Modifier
                            .clickable(onClick = { onAuthorClick(msg.author) })
                            .size(48.dp)
                            .border(1.5.dp, Color(0xFF1E40FF).copy(alpha = 0.2f), CircleShape)
                            .clip(CircleShape),
                        painter = painterResource(id = msg.authorImage),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                AuthorAndTextMessage(
                    msg = msg,
                    isUserMe = false,
                    isFirstMessageByAuthor = isFirstMessageByAuthor,
                    isLastMessageByAuthor = isLastMessageByAuthor,
                    authorClicked = onAuthorClick,
                    onVideoClick = onVideoClick,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 32.dp),
                )
            }
        }
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    onVideoClick: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
    ) {
        ChatItemBubble(
            message = msg,
            isUserMe = isUserMe,
            authorClicked = authorClicked,
            onVideoClick = onVideoClick,
        )
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AuthorNameTimestamp(
    msg: Message,
    onAuthorClick: (String) -> Unit = {},
) {
    // Figma name+time pill badge (id=191:25314, 191:25347)
    // Fill: #EAE9FC + 20% #1E40FF (~#D4D9FC), cornerRadius = 24.dp
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFD4D9FC),
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onAuthorClick(msg.author) }
            .semantics(mergeDescendants = true) {},
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = msg.author,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1E40FF),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = msg.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF836FB2),
            )
        }
    }
}

// Figma 191:25313 / 191:25346: cr=[24.0, 24.0, 24.0, 4.0] for other users
private val OtherChatBubbleShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomEnd = 24.dp,
    bottomStart = 4.dp,
)

private val SelfChatBubbleShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 4.dp,
    bottomEnd = 24.dp,
    bottomStart = 24.dp,
)

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            ),
            color = Color(0xFF000000),
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = Color(0xFF79767F).copy(alpha = 0.45f),
    )
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
    onVideoClick: (String) -> Unit = {},
) {
    // Figma speech-bubble fills: #97A5FF for self, #D5DAFF for others
    val backgroundBubbleColor = if (isUserMe) {
        Color(0xFF97A5FF)
    } else {
        Color(0xFFD5DAFF)
    }
    val bubbleShape = if (isUserMe) SelfChatBubbleShape else OtherChatBubbleShape

    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
    ) {
        val hasText = message.content.isNotBlank() || (message.image == null && message.videoUri == null)
        if (hasText) {
            Surface(
                color = backgroundBubbleColor,
                shape = bubbleShape,
            ) {
                ClickableMessage(
                    message = message,
                    isUserMe = isUserMe,
                    authorClicked = authorClicked,
                )
            }
        }

        message.image?.let {
            if (hasText) {
                Spacer(modifier = Modifier.height(4.dp))
            }
            Surface(
                color = backgroundBubbleColor,
                shape = bubbleShape,
            ) {
                Image(
                    painter = painterResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp),
                    contentDescription = stringResource(id = R.string.attached_image),
                )
            }
        }

        message.videoUri?.let { videoUri ->
            if (hasText || message.image != null) {
                Spacer(modifier = Modifier.height(4.dp))
            }
            Surface(
                color = backgroundBubbleColor,
                shape = bubbleShape,
            ) {
                VideoThumbnail(
                    videoUri = videoUri,
                    onClick = { onVideoClick(videoUri) },
                    shape = bubbleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(bubbleShape),
                )
            }
        }
    }
}

@Composable
fun ClickableMessage(message: Message, isUserMe: Boolean, authorClicked: (String) -> Unit) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message.content,
        primary = false,
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color(0xFF000000),
            lineHeight = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
