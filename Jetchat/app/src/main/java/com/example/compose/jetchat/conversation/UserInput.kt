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

package com.example.compose.jetchat.conversation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.theme.KarlaFontFamily
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

enum class InputSelector {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE,
}

enum class EmojiStickerSelector {
    EMOJI,
    STICKER,
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 444, heightDp = 280)
@Composable
fun UserInputPreview() {
    JetchatTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = 84.dp, bottom = 20.dp, start = 12.dp, end = 12.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            UserInput(onMessageSent = {})
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
    onVideoMessageSent: (videoUri: String, caption: String) -> Unit = { _, _ -> },
    initialText: String = "@gemini can you setup an announcement for next meetup",
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }

    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackHandler(onBack = dismissKeyboard)
    }

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(initialText.length),
            ),
        )
    }

    var attachedVideoUri by rememberSaveable { mutableStateOf<String?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            attachedVideoUri = it.toString()
        }
    }

    val sendMessage = {
        val currentVideoUri = attachedVideoUri
        if (currentVideoUri != null) {
            onVideoMessageSent(currentVideoUri, textState.text.trim())
            attachedVideoUri = null
        } else if (textState.text.isNotBlank()) {
            onMessageSent(textState.text)
        }
        textState = TextFieldValue()
        resetScroll()
        dismissKeyboard()
    }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    val glowMeshPainter = rememberUserInputGlowMeshGradientPainter()

    val cardShape = RoundedCornerShape(
        topStart = 48.dp,
        topEnd = 48.dp,
        bottomEnd = 28.dp,
        bottomStart = 48.dp,
    )
    val surfaceColor = Color(0xFFEAE9FC)
    val sendMessageEnabled = textState.text.isNotBlank() || attachedVideoUri != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 4.dp, bottom = 8.dp, top = 6.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Animated 6x7 Mesh Gradient Glow behind UserInput (Figma node 188:23011 & 188:22823)
            // Extends 185.dp above, 68.dp below (covering the nav bar area), and 76.dp horizontally
            // beyond the card bounds without adding layout height to the parent Column.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .layout { measurable, constraints ->
                        val topExpand = 185.dp.roundToPx()
                        val bottomExpand = 68.dp.roundToPx()
                        val horizontalExpand = 76.dp.roundToPx()
                        val expandedWidth = constraints.maxWidth + horizontalExpand * 2
                        val expandedHeight = constraints.maxHeight + topExpand + bottomExpand
                        val placeable = measurable.measure(
                            Constraints.fixed(expandedWidth, expandedHeight),
                        )
                        layout(constraints.maxWidth, constraints.maxHeight) {
                            placeable.place(-horizontalExpand, -topExpand)
                        }
                    }
                    .blur(radius = 12.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .paint(glowMeshPainter, contentScale = ContentScale.FillBounds),
            )

            // Main input/design1 Card (396 x 160 dp on 412dp screen, end-padded by 4.dp)
            Surface(
                shape = cardShape,
                color = surfaceColor,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp)
                    .heightIn(min = 160.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AnimatedVisibility(
                            visible = attachedVideoUri != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            attachedVideoUri?.let { videoUri ->
                                AttachedVideoPreview(
                                    videoUri = videoUri,
                                    onRemove = { attachedVideoUri = null },
                                )
                            }
                        }

                        UserInputText(
                            textFieldValue = textState,
                            onTextChanged = { textState = it },
                            keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                            onTextFieldFocused = { focused ->
                                if (focused) {
                                    currentInputSelector = InputSelector.NONE
                                    resetScroll()
                                }
                                textFieldFocusState = focused
                            },
                            onMessageSent = { sendMessage() },
                            focusState = textFieldFocusState,
                        )
                    }

                    UserInputSelector(
                        onSelectorChange = { currentInputSelector = it },
                        currentInputSelector = currentInputSelector,
                        onVideoClick = {
                            currentInputSelector = InputSelector.NONE
                            videoPickerLauncher.launch("video/*")
                        },
                        onAddClick = {
                            currentInputSelector = InputSelector.MAP
                        },
                    )
                }
            }

            // Send button (Figma node 188:23028 'Icon button')
            // Size 72 x 56 dp, cr=[28, 28, 16, 28], extending 4.dp past right edge of card
            val sendDescription = stringResource(id = R.string.send)
            val sendButtonShape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp,
                bottomEnd = 16.dp,
                bottomStart = 28.dp,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(width = 72.dp, height = 56.dp)
                    .clip(sendButtonShape)
                    .background(Color(0xFFEAE9FC))
                    .clickable(
                        enabled = sendMessageEnabled,
                        onClick = sendMessage,
                    )
                    .semantics(mergeDescendants = true) {
                        contentDescription = sendDescription
                        text = AnnotatedString(sendDescription)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = null,
                    tint = Color(0xFF444746).copy(alpha = if (sendMessageEnabled) 0.85f else 0.54f),
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        SelectorExpanded(
            onCloseRequested = dismissKeyboard,
            onTextAdded = { textState = textState.addText(it) },
            currentSelector = currentInputSelector,
        )
    }
}

@Composable
private fun AttachedVideoPreview(videoUri: String, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
    ) {
        VideoPlayer(
            videoUri = videoUri,
            autoPlay = false,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = CircleShape,
                ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.remove_attached_video),
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText = this.text.replaceRange(
        this.selection.start,
        this.selection.end,
        newString,
    )
    val newSelection = TextRange(
        start = newText.length,
        end = newText.length,
    )

    return this.copy(text = newText, selection = newSelection)
}

@Composable
private fun SelectorExpanded(currentSelector: InputSelector, onCloseRequested: () -> Unit, onTextAdded: (String) -> Unit) {
    if (currentSelector == InputSelector.NONE) return

    // Request focus to force the TextField to lose it
    val focusRequester = remember { FocusRequester() }
    // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
    SideEffect {
        if (currentSelector == InputSelector.EMOJI) {
            focusRequester.requestFocus()
        }
    }

    Surface(tonalElevation = 8.dp) {
        when (currentSelector) {
            InputSelector.EMOJI -> EmojiSelector(onTextAdded, focusRequester)
            InputSelector.DM -> NotAvailablePopup(onCloseRequested)
            InputSelector.PICTURE -> FunctionalityNotAvailablePanel()
            InputSelector.MAP -> FunctionalityNotAvailablePanel()
            InputSelector.PHONE -> FunctionalityNotAvailablePanel()
            InputSelector.NONE -> Unit
        }
    }
}

@Composable
fun FunctionalityNotAvailablePanel() {
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.not_available),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(id = R.string.not_available_subtitle),
                modifier = Modifier.paddingFrom(FirstBaseline, before = 32.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun UserInputSelector(
    onSelectorChange: (InputSelector) -> Unit,
    currentInputSelector: InputSelector,
    modifier: Modifier = Modifier,
    onVideoClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    val iconTint = Color(0xFF3E41F4)
    val sparkButtonSizePx = with(LocalDensity.current) { 48.dp.toPx() }
    val sparkGradientBrush = remember(sparkButtonSizePx) {
        Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFFF96BD6),
                0.49f to Color(0xFF9378FF),
                1.00f to Color(0xFF1E40FF),
            ),
            start = Offset(sparkButtonSizePx * 0.09722f, 0f),
            end = Offset(sparkButtonSizePx * 0.92361f, sparkButtonSizePx),
        )
    }

    Row(
        modifier = modifier
            .height(56.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 1. Emoji button (x = 16.dp, size = 48x48 dp)
        IconButton(
            onClick = { onSelectorChange(InputSelector.EMOJI) },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mood),
                contentDescription = stringResource(id = R.string.emoji_selector_bt_desc),
                tint = iconTint,
                modifier = Modifier.size(24.dp),
            )
        }

        // 2. Photo button (x = 72.dp, size = 48x48 dp)
        IconButton(
            onClick = { onSelectorChange(InputSelector.PICTURE) },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_insert_photo),
                contentDescription = stringResource(id = R.string.attach_photo_desc),
                tint = iconTint,
                modifier = Modifier.size(24.dp),
            )
        }

        // 3. Video / Duo button (x = 128.dp, size = 48x48 dp)
        IconButton(
            onClick = onVideoClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_duo),
                contentDescription = stringResource(id = R.string.videochat_desc),
                tint = iconTint,
                modifier = Modifier.size(24.dp),
            )
        }

        // 4. Gemini Sparkle button (x = 184.dp, size = 48x48 dp, gradient circle)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF0B57D0))
                .background(sparkGradientBrush)
                .clickable { onSelectorChange(InputSelector.DM) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_spark),
                contentDescription = "AI Spark",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        // 5. Plus / Add button (x = 240.dp, size = 48x48 dp)
        IconButton(
            onClick = onAddClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add attachment",
                tint = Color(0xFF0B57D0),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun InputSelectorButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.painter.Painter,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundModifier = if (selected) {
        Modifier.background(
            color = LocalContentColor.current,
            shape = RoundedCornerShape(14.dp),
        )
    } else {
        Modifier
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.then(backgroundModifier),
    ) {
        val tint = if (selected) {
            contentColorFor(backgroundColor = LocalContentColor.current)
        } else {
            LocalContentColor.current
        }
        Icon(
            icon,
            tint = tint,
            modifier = Modifier
                .padding(8.dp)
                .size(56.dp),
            contentDescription = description,
        )
    }
}

@Composable
private fun NotAvailablePopup(onDismissed: () -> Unit) {
    FunctionalityNotAvailablePopup(onDismissed)
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    onMessageSent: (String) -> Unit,
    focusState: Boolean,
) {
    val a11ylabel = stringResource(id = R.string.textfield_desc)
    // Figma node 188:23032 'Chat' positioned at x=32dp, y=20dp, width=324dp (end=40dp), height=72dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 20.dp, end = 40.dp)
            .heightIn(min = 72.dp),
    ) {
        UserInputTextField(
            textFieldValue,
            onTextChanged,
            onTextFieldFocused,
            keyboardType,
            focusState,
            onMessageSent,
            Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = a11ylabel
                    keyboardShownProperty = keyboardShown
                },
        )
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var lastFocusState by remember { mutableStateOf(false) }
    val unfocusedCursorTransformation = remember(focusState) {
        if (!focusState) {
            VisualTransformation { annotatedString ->
                if (annotatedString.text.isNotEmpty()) {
                    TransformedText(
                        text = AnnotatedString(annotatedString.text + "|"),
                        offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int = offset
                            override fun transformedToOriginal(offset: Int): Int = offset.coerceAtMost(annotatedString.text.length)
                        },
                    )
                } else {
                    TransformedText(annotatedString, OffsetMapping.Identity)
                }
            }
        } else {
            VisualTransformation.None
        }
    }

    val textStyle = TextStyle(
        fontFamily = KarlaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = Color(0xFF001CBA),
    )

    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .align(Alignment.TopStart)
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send,
        ),
        keyboardActions = KeyboardActions {
            if (textFieldValue.text.isNotBlank()) onMessageSent(textFieldValue.text)
        },
        maxLines = 4,
        visualTransformation = unfocusedCursorTransformation,
        cursorBrush = SolidColor(Color(0xFF001CBA)),
        textStyle = textStyle,
    )

    if (textFieldValue.text.isEmpty() && !focusState) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = "Send a message",
                color = Color(0xFF49454F),
                fontSize = 18.sp,
            )
    }
}

@Composable
private fun RecordingIndicator(swipeOffset: () -> Float) {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000.milliseconds)
            duration += 1.seconds
        }
    }
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")

        val animatedPulse = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                tween(2000),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulse",
        )
        Box(
            Modifier
                .size(56.dp)
                .padding(24.dp)
                .graphicsLayer {
                    scaleX = animatedPulse.value
                    scaleY = animatedPulse.value
                }
                .clip(CircleShape)
                .background(Color.Red),
        )
        Text(
            duration.toComponents { minutes, seconds, _ ->
                val min = minutes.toString().padStart(2, '0')
                val sec = seconds.toString().padStart(2, '0')
                "$min:$sec"
            },
            Modifier.alignByBaseline(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .alignByBaseline()
                .clipToBounds(),
        ) {
            val swipeThreshold = with(LocalDensity.current) { 200.dp.toPx() }
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = swipeOffset() / 2
                        alpha = 1 - (swipeOffset().absoluteValue / swipeThreshold)
                    },
                textAlign = TextAlign.Center,
                text = stringResource(R.string.swipe_to_cancel_recording),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun EmojiSelector(onTextAdded: (String) -> Unit, focusRequester: FocusRequester) {
    var selected by remember { mutableStateOf(EmojiStickerSelector.EMOJI) }

    val a11yLabel = stringResource(id = R.string.emoji_selector_desc)
    Column(
        modifier = Modifier
            .focusRequester(focusRequester) // Requests focus when the Emoji selector is displayed
            // Make the emoji selector focusable so it can steal focus from TextField
            .focusTarget()
            .semantics { contentDescription = a11yLabel },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            ExtendedSelectorInnerButton(
                text = stringResource(id = R.string.emojis_label),
                onClick = { selected = EmojiStickerSelector.EMOJI },
                selected = true,
                modifier = Modifier.weight(1f),
            )
            ExtendedSelectorInnerButton(
                text = stringResource(id = R.string.stickers_label),
                onClick = { selected = EmojiStickerSelector.STICKER },
                selected = false,
                modifier = Modifier.weight(1f),
            )
        }
        Row(modifier = Modifier.verticalScroll(rememberScrollState())) {
            EmojiTable(onTextAdded, modifier = Modifier.padding(8.dp))
        }
    }
    if (selected == EmojiStickerSelector.STICKER) {
        NotAvailablePopup(onDismissed = { selected = EmojiStickerSelector.EMOJI })
    }
}

@Composable
fun ExtendedSelectorInnerButton(text: String, onClick: () -> Unit, selected: Boolean, modifier: Modifier = Modifier) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        else Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.74f),
    )
    TextButton(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(36.dp),
        colors = colors,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun EmojiTable(onTextAdded: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        repeat(4) { x ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(EMOJI_COLUMNS) { y ->
                    val emoji = emojis[x * EMOJI_COLUMNS + y]
                    Text(
                        modifier = Modifier
                            .clickable(onClick = { onTextAdded(emoji) })
                            .sizeIn(minWidth = 42.dp, minHeight = 42.dp)
                            .padding(8.dp),
                        text = emoji,
                        style = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
            }
        }
    }
}

private const val EMOJI_COLUMNS = 10

private val emojis = listOf(
    "\ud83d\ude00", // Grinning Face
    "\ud83d\ude01", // Grinning Face With Smiling Eyes
    "\ud83d\ude02", // Face With Tears of Joy
    "\ud83d\ude03", // Smiling Face With Open Mouth
    "\ud83d\ude04", // Smiling Face With Open Mouth and Smiling Eyes
    "\ud83d\ude05", // Smiling Face With Open Mouth and Cold Sweat
    "\ud83d\ude06", // Smiling Face With Open Mouth and Tightly-Closed Eyes
    "\ud83d\ude09", // Winking Face
    "\ud83d\ude0a", // Smiling Face With Smiling Eyes
    "\ud83d\ude0b", // Face Savouring Delicious Food
    "\ud83d\ude0e", // Smiling Face With Sunglasses
    "\ud83d\ude0d", // Smiling Face With Heart-Shaped Eyes
    "\ud83d\ude18", // Face Throwing a Kiss
    "\ud83d\ude17", // Kissing Face
    "\ud83d\ude19", // Kissing Face With Smiling Eyes
    "\ud83d\ude1a", // Kissing Face With Closed Eyes
    "\u263a", // White Smiling Face
    "\ud83d\ude42", // Slightly Smiling Face
    "\ud83e\udd17", // Hugging Face
    "\ud83d\ude07", // Smiling Face With Halo
    "\ud83e\udd13", // Nerd Face
    "\ud83e\udd14", // Thinking Face
    "\ud83d\ude10", // Neutral Face
    "\ud83d\ude11", // Expressionless Face
    "\ud83d\ude36", // Face Without Mouth
    "\ud83d\ude44", // Face With Rolling Eyes
    "\ud83d\ude0f", // Smirking Face
    "\ud83d\ude23", // Persevering Face
    "\ud83d\ude25", // Disappointed but Relieved Face
    "\ud83d\ude2e", // Face With Open Mouth
    "\ud83e\udd10", // Zipper-Mouth Face
    "\ud83d\ude2f", // Hushed Face
    "\ud83d\ude2a", // Sleepy Face
    "\ud83d\ude2b", // Tired Face
    "\ud83d\ude34", // Sleeping Face
    "\ud83d\ude0c", // Relieved Face
    "\ud83d\ude1b", // Face With Stuck-Out Tongue
    "\ud83d\ude1c", // Face With Stuck-Out Tongue and Winking Eye
    "\ud83d\ude1d", // Face With Stuck-Out Tongue and Tightly-Closed Eyes
    "\ud83d\ude12", // Unamused Face
    "\ud83d\ude13", // Face With Cold Sweat
    "\ud83d\ude14", // Pensive Face
    "\ud83d\ude15", // Confused Face
    "\ud83d\ude43", // Upside-Down Face
    "\ud83e\udd11", // Money-Mouth Face
    "\ud83d\ude32", // Astonished Face
    "\ud83d\ude37", // Face With Medical Mask
    "\ud83e\udd12", // Face With Thermometer
    "\ud83e\udd15", // Face With Head-Bandage
    "\u2639", // White Frowning Face
    "\ud83d\ude41", // Slightly Frowning Face
    "\ud83d\ude16", // Confounded Face
    "\ud83d\ude1e", // Disappointed Face
    "\ud83d\ude1f", // Worried Face
    "\ud83d\ude24", // Face With Look of Triumph
    "\ud83d\ude22", // Crying Face
    "\ud83d\ude2d", // Loudly Crying Face
    "\ud83d\ude26", // Frowning Face With Open Mouth
    "\ud83d\ude27", // Anguished Face
    "\ud83d\ude28", // Fearful Face
    "\ud83d\ude29", // Weary Face
    "\ud83d\ude2c", // Grimacing Face
    "\ud83d\ude30", // Face With Open Mouth and Cold Sweat
    "\ud83d\ude31", // Face Screaming in Fear
    "\ud83d\ude33", // Flushed Face
    "\ud83d\ude35", // Dizzy Face
    "\ud83d\ude21", // Pouting Face
    "\ud83d\ude20", // Angry Face
    "\ud83d\ude08", // Smiling Face With Horns
    "\ud83d\udc7f", // Imp
    "\ud83d\udc79", // Japanese Ogre
    "\ud83d\udc7a", // Japanese Goblin
    "\ud83d\udc80", // Skull
    "\ud83d\udc7b", // Ghost
    "\ud83d\udc7d", // Extraterrestrial Alien
    "\ud83e\udd16", // Robot Face
    "\ud83d\udca9", // Pile of Poo
    "\ud83d\ude3a", // Smiling Cat Face With Open Mouth
    "\ud83d\ude38", // Grinning Cat Face With Smiling Eyes
    "\ud83d\ude39", // Cat Face With Tears of Joy
    "\ud83d\ude3b", // Smiling Cat Face With Heart-Shaped Eyes
    "\ud83d\ude3c", // Cat Face With Wry Smile
    "\ud83d\ude3d", // Kissing Cat Face With Closed Eyes
    "\ud83d\ude40", // Weary Cat Face
    "\ud83d\ude3f", // Crying Cat Face
    "\ud83d\ude3e", // Pouting Cat Face
    "\ud83d\udc66", // Boy
    "\ud83d\udc67", // Girl
    "\ud83d\udc68", // Man
    "\ud83d\udc69", // Woman
    "\ud83d\udc74", // Older Man
    "\ud83d\udc75", // Older Woman
    "\ud83d\udc76", // Baby
    "\ud83d\udc71", // Person With Blond Hair
    "\ud83d\udc6e", // Police Officer
    "\ud83d\udc72", // Man With Gua Pi Mao
    "\ud83d\udc73", // Man With Turban
    "\ud83d\udc77", // Construction Worker
    "\u26d1", // Helmet With White Cross
    "\ud83d\udc78", // Princess
    "\ud83d\udc82", // Guardsman
    "\ud83d\udd75", // Sleuth or Spy
    "\ud83c\udf85", // Father Christmas
    "\ud83d\udc70", // Bride With Veil
    "\ud83d\udc7c", // Baby Angel
    "\ud83d\udc86", // Face Massage
    "\ud83d\udc87", // Haircut
    "\ud83d\ude4d", // Person Frowning
    "\ud83d\ude4e", // Person With Pouting Face
    "\ud83d\ude45", // Face With No Good Gesture
    "\ud83d\ude46", // Face With OK Gesture
    "\ud83d\udc81", // Information Desk Person
    "\ud83d\ude4b", // Happy Person Raising One Hand
    "\ud83d\ude47", // Person Bowing Deeply
    "\ud83d\ude4c", // Person Raising Both Hands in Celebration
    "\ud83d\ude4f", // Person With Folded Hands
    "\ud83d\udde3", // Speaking Head in Silhouette
    "\ud83d\udc64", // Bust in Silhouette
    "\ud83d\udc65", // Busts in Silhouette
    "\ud83d\udeb6", // Pedestrian
    "\ud83c\udfc3", // Runner
    "\ud83d\udc6f", // Woman With Bunny Ears
    "\ud83d\udc83", // Dancer
    "\ud83d\udd74", // Man in Business Suit Levitating
    "\ud83d\udc6b", // Man and Woman Holding Hands
    "\ud83d\udc6c", // Two Men Holding Hands
    "\ud83d\udc6d", // Two Women Holding Hands
    "\ud83d\udc8f", // Kiss
)
