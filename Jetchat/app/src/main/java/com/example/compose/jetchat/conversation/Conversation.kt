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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.blur.backdropBlur
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.components.rememberHeartReactionMeshGradientPainter
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.video.FullScreenVideoPlayer
import com.example.compose.jetchat.video.VideoThumbnail
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.launch

/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 * @param onMessageLikeToggled Sends an event up when the user double taps a message to like it
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    onMessageLikeToggled: (messageId: String) -> Unit = { },
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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                ChannelNameBar(
                    channelName = uiState.channelName,
                    channelMembers = uiState.channelMembers,
                    onNavIconPressed = onNavIconPressed,
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
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
                    modifier = Modifier
                        .imePadding()
                        .backdropBlur(
                            fallbackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            elevation = 0.dp,
                            spec = BlurRadiusSpec.verticalGradient(
                                listOf(
                                    BlurStop(0f, 0.dp),
                                    BlurStop(0.5f, 32.dp),
                                ),
                            ),
                        )
                        .navigationBarsPadding(),
                )
            },
            // Exclude ime and navigation bar padding so this can be added by the UserInput composable
            contentWindowInsets = ScaffoldDefaults
                .contentWindowInsets
                .exclude(WindowInsets.navigationBars)
                .exclude(WindowInsets.ime),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = background)
                    .border(width = 2.dp, color = borderStroke)
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { event ->
                            event
                                .mimeTypes()
                                .contains(
                                    ClipDescription.MIMETYPE_TEXT_PLAIN,
                                )
                        },
                        target = dragAndDropCallback,
                    ),
            ) {
                Messages(
                    messages = uiState.messages,
                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    contentPadding = paddingValues,
                    onVideoClick = { videoUri -> activeVideoUri = videoUri },
                    onMessageLikeToggled = onMessageLikeToggled,
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
                fallbackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                elevation = 0.dp,
                spec = BlurRadiusSpec.verticalGradient(
                    listOf(
                        BlurStop(0.5f, 32.dp),
                        BlurStop(1f, 0.dp),
                    ),
                ),
            ),
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        navigationIcon = {},
        title = {
            val navDrawerDescription = stringResource(R.string.navigation_drawer_open)
            Surface(
                onClick = onNavIconPressed,
                modifier = Modifier.semantics {
                    contentDescription = navDrawerDescription
                },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = channelName.removePrefix("#"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ali),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        )
                        Image(
                            painter = painterResource(id = R.drawable.someone_else),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        )
                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        )
                    }
                }
            }
        },
        actions = {
            FilledIconButton(
                onClick = { functionalityNotAvailablePopupShown = true },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(id = R.string.search),
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = { functionalityNotAvailablePopupShown = true },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = stringResource(id = R.string.info),
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
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
    onMessageLikeToggled: (messageId: String) -> Unit = {},
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
                        onLikeToggled = onMessageLikeToggled,
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = contentPadding.calculateBottomPadding()),
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
    onLikeToggled: (messageId: String) -> Unit = {},
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthorsModifier = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Column(modifier = spaceBetweenAuthorsModifier.fillMaxWidth()) {
        if (!isUserMe && isLastMessageByAuthor) {
            AuthorNameTimestamp(
                msg = msg,
                onAuthorClick = onAuthorClick,
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            if (!isUserMe) {
                if (isLastMessageByAuthor) {
                    AuthorAvatar(
                        authorImage = msg.authorImage,
                        authorName = msg.author,
                        borderColor = borderColor,
                        onAuthorClick = onAuthorClick,
                    )
                } else {
                    Spacer(modifier = Modifier.width(74.dp))
                }
            }
            AuthorAndTextMessage(
                msg = msg,
                isUserMe = isUserMe,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                onVideoClick = onVideoClick,
                onLikeToggled = onLikeToggled,
                modifier = Modifier
                    .padding(
                        start = if (isUserMe) 16.dp else 0.dp,
                        end = if (isUserMe) 0.dp else 16.dp,
                    )
                    .weight(1f),
            )
            if (isUserMe) {
                if (isLastMessageByAuthor) {
                    AuthorAvatar(
                        authorImage = msg.authorImage,
                        authorName = msg.author,
                        borderColor = borderColor,
                        onAuthorClick = onAuthorClick,
                    )
                } else {
                    Spacer(modifier = Modifier.width(74.dp))
                }
            }
        }
    }
}

@Composable
private fun RowScope.AuthorAvatar(authorImage: Int, authorName: String, borderColor: Color, onAuthorClick: (String) -> Unit) {
    Image(
        modifier = Modifier
            .clickable(onClick = { onAuthorClick(authorName) })
            .padding(horizontal = 16.dp)
            .size(42.dp)
            .border(2.dp, borderColor, CircleShape)
            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .clip(CircleShape)
            .align(Alignment.Top),
        painter = painterResource(id = authorImage),
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
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
    onLikeToggled: (messageId: String) -> Unit = {},
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
            onLikeToggled = onLikeToggled,
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
private fun AuthorNameTimestamp(msg: Message, modifier: Modifier = Modifier, onAuthorClick: (String) -> Unit = {}) {
    // Author name + timestamp pill badge
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
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
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = msg.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

private val ChatBubbleShapeOthers = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeMe = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

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
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
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
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
    onVideoClick: (String) -> Unit = {},
    onLikeToggled: (messageId: String) -> Unit = {},
) {
    val isLiked = message.isLiked
    val haptic = LocalHapticFeedback.current
    val heartMeshPainter = if (isLiked) rememberHeartReactionMeshGradientPainter() else null

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.inversePrimary
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val bubbleShape = if (isUserMe) ChatBubbleShapeMe else ChatBubbleShapeOthers
    val bubbleBorder = if (isLiked) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    } else {
        null
    }

    val likeTransition = updateTransition(targetState = isLiked, label = "like")

    // Goes 0 -> 1 when the message is liked; the whole text bubble scales up and back down along
    // the way. graphicsLayer only affects drawing, so the LazyColumn layout doesn't move.
    val likeProgress by likeTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 350, easing = FastOutSlowInEasing) },
        label = "likeBounce",
    ) { liked ->
        if (liked) 1f else 0f
    }

    // pointerInput(Unit) below captures this once, so always call the latest callback.
    val toggleLiked by rememberUpdatedState {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onLikeToggled(message.id)
    }

    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
    ) {
        val hasText = message.content.isNotBlank() || (message.image == null && message.videoUri == null)
        if (hasText) {
            Surface(
                color = if (isLiked) Color.Transparent else backgroundBubbleColor,
                shape = bubbleShape,
                border = bubbleBorder,
                modifier = Modifier
                    .graphicsLayer {
                        val scale = if (isLiked) 1f + LikeBounceScale * sin(PI.toFloat() * likeProgress) else 1f
                        scaleX = scale
                        scaleY = scale
                        // Grow away from the avatar, anchored at the bubble's pointed corner.
                        transformOrigin = TransformOrigin(pivotFractionX = if (isUserMe) 1f else 0f, pivotFractionY = 0f)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { toggleLiked() })
                    },
            ) {
                Box(
                    modifier = if (heartMeshPainter != null) {
                        Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .paint(heartMeshPainter, contentScale = ContentScale.FillBounds)
                    } else {
                        Modifier
                    },
                ) {
                    ClickableMessage(
                        message = message,
                        authorClicked = authorClicked,
                    )
                }
            }
        }

        message.image?.let { imageRes ->
            if (hasText) {
                Spacer(modifier = Modifier.height(4.dp))
            }
            val painter = painterResource(imageRes)
            val intrinsicSize = painter.intrinsicSize
            val aspectRatio = if (intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
                intrinsicSize.width / intrinsicSize.height
            } else {
                1f
            }
            Surface(
                color = if (isLiked) Color.Transparent else backgroundBubbleColor,
                shape = bubbleShape,
                border = bubbleBorder,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { toggleLiked() })
                },
            ) {
                Box(
                    // Animates the 8dp frame that reveals the heart mesh around a liked image.
                    modifier = Modifier
                        .animateContentSize()
                        .then(
                            if (heartMeshPainter != null) {
                                Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .paint(heartMeshPainter, contentScale = ContentScale.FillBounds)
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(if (isLiked) 8.dp else 0.dp)
                            .sizeIn(maxWidth = 240.dp, maxHeight = 260.dp)
                            .aspectRatio(aspectRatio, matchHeightConstraintsFirst = aspectRatio < 1f)
                            .clip(bubbleShape),
                        contentDescription = stringResource(id = R.string.attached_image),
                    )
                }
            }
        }

        message.videoUri?.let { videoUri ->
            if (hasText || message.image != null) {
                Spacer(modifier = Modifier.height(4.dp))
            }
            Surface(
                color = if (isLiked) Color.Transparent else backgroundBubbleColor,
                shape = bubbleShape,
                border = bubbleBorder,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { toggleLiked() })
                },
            ) {
                Box(
                    // Animates the 8dp frame that reveals the heart mesh around a liked video.
                    modifier = Modifier
                        .animateContentSize()
                        .then(
                            if (heartMeshPainter != null) {
                                Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .paint(heartMeshPainter, contentScale = ContentScale.FillBounds)
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    VideoThumbnail(
                        videoUri = videoUri,
                        onClick = { onVideoClick(videoUri) },
                        shape = bubbleShape,
                        modifier = Modifier
                            .padding(if (isLiked) 8.dp else 0.dp)
                            .widthIn(max = 260.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(bubbleShape),
                    )
                }
            }
        }

        // Liked reaction badge attached to bubble corner
        this@Column.AnimatedVisibility(
            visible = isLiked,

            enter = expandVertically(expandFrom = Alignment.Top, clip = false) +
                scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) +
                fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top, clip = false) + scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.End)
                .offset(y = (-8).dp)
                .padding(end = 4.dp),
        ) {
            val badgeShape = RoundedCornerShape(12.dp)
            Text(
                text = "❤️",
                fontSize = 12.sp,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), badgeShape)
                    .background(MaterialTheme.colorScheme.surface, badgeShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
fun ClickableMessage(message: Message, authorClicked: (String) -> Unit) {
    // Links and @mentions are LinkAnnotations, so the Text handles their clicks and all other
    // taps (e.g. double tap to like) fall through to the bubble.
    val styledMessage = messageFormatter(
        text = message.content,
        primary = false,
        onPersonClick = authorClicked,
    )

    Text(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

private const val LikeBounceScale = 0.12f

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
