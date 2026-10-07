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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.rememberRecordButtonMeshGradientPainter
import com.example.compose.jetchat.components.rememberUserInputGlowMeshGradientPainter
import com.example.compose.jetchat.video.VideoPlayer

enum class InputSelector {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE,
    RICHTEXTEDITOR,
}

enum class EmojiStickerSelector {
    EMOJI,
    STICKER,
}

@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    onMessageSent: (AnnotatedString) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
    onVideoMessageSent: (videoUri: String, caption: AnnotatedString) -> Unit = { _, _ -> },
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }

    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackHandler(onBack = dismissKeyboard)
    }

    val formattedTextState = rememberFormattedTextState()

    var attachedVideoUri by rememberSaveable { mutableStateOf<String?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            attachedVideoUri = it.toString()
        }
    }

    // Toggled when the user clicks the recording mic icon. Drives both the animated mesh-gradient
    // glow behind the input card and the recording button's gradient fill.
    var isRecordingActive by rememberSaveable { mutableStateOf(false) }

    val sendMessage = {
        val currentVideoUri = attachedVideoUri
        val content = formattedTextState.toAnnotatedString()
        if (currentVideoUri != null) {
            onVideoMessageSent(currentVideoUri, content.trimWhitespace())
            attachedVideoUri = null
        } else if (content.isNotBlank()) {
            onMessageSent(content)
        }
        formattedTextState.clear()
        isRecordingActive = false
        resetScroll()
        dismissKeyboard()
    }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val sendMessageEnabled = !formattedTextState.isBlank || attachedVideoUri != null

    // Animated mesh-gradient glow behind the card, shown while recording is active.
    val glowAlpha by animateFloatAsState(
        targetValue = if (isRecordingActive) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "glowFade",
    )

    val isGlowVisible by remember { derivedStateOf { glowAlpha > 0f } }
    val glowMeshPainter = if (isRecordingActive || isGlowVisible) {
        rememberUserInputGlowMeshGradientPainter()
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 4.dp, bottom = 8.dp, top = 6.dp),
    ) {
        val cardShape = RoundedCornerShape(32.dp)

        Surface(
            shape = cardShape,
            color = surfaceColor,
            modifier = Modifier
                .fillMaxWidth()
                // Draw the glow behind the card, scaled past its bounds.
                .then(
                    if (glowMeshPainter != null) {
                        Modifier.drawBehind {
                            translate(top = -58.dp.toPx()) {
                                scale(scaleX = 1.38f, scaleY = 2.85f) {
                                    with(glowMeshPainter) { draw(size, alpha = glowAlpha) }
                                }
                            }
                        }
                    } else {
                        Modifier
                    },
                )
                .padding(end = 4.dp)
                // Blue-tinted shadow while idle; the glow replaces it while recording.
                // Tinted shadows render on API 28+ (black on older versions).
                .then(
                    if (isRecordingActive) {
                        Modifier
                    } else {
                        Modifier.shadow(
                            elevation = 16.dp,
                            shape = cardShape,
                            clip = false,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
                .heightIn(min = 136.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                        formattedTextState = formattedTextState,
                        // Only show the keyboard if there's no extended input selector (or rich
                        // text editor is active) and text field has focus
                        keyboardShown =
                            (
                                currentInputSelector == InputSelector.NONE ||
                                    currentInputSelector == InputSelector.RICHTEXTEDITOR
                                ) && textFieldFocusState,
                        // Close extended selector if text field receives focus, keeping rich text
                        // editor open while editing
                        onTextFieldFocused = { focused ->
                            if (focused) {
                                if (currentInputSelector != InputSelector.RICHTEXTEDITOR) {
                                    currentInputSelector = InputSelector.NONE
                                }
                                resetScroll()
                            }
                            textFieldFocusState = focused
                        },
                        onMessageSent = { sendMessage() },
                        focusState = textFieldFocusState,
                        showCloseButton = currentInputSelector == InputSelector.RICHTEXTEDITOR,
                        onCloseRichTextEditor = dismissKeyboard,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserInputSelector(
                        onSelectorChange = { currentInputSelector = it },
                        currentInputSelector = currentInputSelector,
                        recordingActive = isRecordingActive,
                        onRecordingClick = { isRecordingActive = !isRecordingActive },
                        onVideoClick = {
                            currentInputSelector = InputSelector.NONE
                            videoPickerLauncher.launch("video/*")
                        },
                        onAddClick = { currentInputSelector = InputSelector.MAP },
                    )

                    IconButton(
                        onClick = sendMessage,
                        enabled = sendMessageEnabled,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = stringResource(id = R.string.send),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (sendMessageEnabled) 0.85f else 0.54f,
                            ),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }

        SelectorExpanded(
            onCloseRequested = dismissKeyboard,
            onTextAdded = { formattedTextState.addText(it) },
            currentSelector = currentInputSelector,
            spanStyleState = formattedTextState.spanStyleState,
        )
    }
}

/**
 * Trims leading and trailing whitespace while keeping [SpanStyle] ranges aligned with the trimmed
 * text ([AnnotatedString.subSequence] re-bases the ranges).
 */
private fun AnnotatedString.trimWhitespace(): AnnotatedString {
    val start = indexOfFirst { !it.isWhitespace() }
    if (start == -1) return AnnotatedString("")
    val end = indexOfLast { !it.isWhitespace() } + 1
    return if (start == 0 && end == length) this else subSequence(start, end)
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

@Composable
private fun SelectorExpanded(
    currentSelector: InputSelector,
    onCloseRequested: () -> Unit,
    onTextAdded: (String) -> Unit,
    spanStyleState: SpanStyleState,
) {
    if (currentSelector == InputSelector.NONE) return

    // Request focus to force the TextField to lose it
    val focusRequester = remember { FocusRequester() }
    // If the emoji selector is shown, request focus to trigger a TextField.onFocusChange.
    // Do not steal focus when RICHTEXTEDITOR is open so the user can keep typing/selecting text.
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
            InputSelector.RICHTEXTEDITOR -> RichTextToolbar(spanStyleState = spanStyleState)
            InputSelector.NONE -> Unit
        }
    }
}

@Composable
fun RichTextToolbar(spanStyleState: SpanStyleState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InlineStyleButton(
            spanStyleState = spanStyleState,
            style = InlineStyle.Bold,
            iconRes = R.drawable.ic_text_format_bold,
            contentDescriptionRes = R.string.format_bold,
        )
        InlineStyleButton(
            spanStyleState = spanStyleState,
            style = InlineStyle.Underline,
            iconRes = R.drawable.ic_text_format_underlined,
            contentDescriptionRes = R.string.format_underlined,
        )
        InlineStyleButton(
            spanStyleState = spanStyleState,
            style = InlineStyle.Strikethrough,
            iconRes = R.drawable.ic_text_strikethrough,
            contentDescriptionRes = R.string.format_strikethrough,
        )
        val activeColor = spanStyleState.activeColorStyle?.color
        RichTextToolbarButton(
            iconRes = R.drawable.ic_text_border_color,
            contentDescription = stringResource(id = R.string.format_color),
            selected = activeColor != null,
            onClick = { spanStyleState.cycleColor() },
            activeContainerColor = Color.White,
            iconTint = activeColor ?: MaterialTheme.colorScheme.onPrimary,
        )
        InlineStyleButton(
            spanStyleState = spanStyleState,
            style = InlineStyle.Italic,
            iconRes = R.drawable.ic_text_format_italic,
            contentDescriptionRes = R.string.format_italic,
        )
        InlineStyleButton(
            spanStyleState = spanStyleState,
            style = InlineStyle.LargeFont,
            iconRes = R.drawable.ic_text_format_size,
            contentDescriptionRes = R.string.format_size,
        )
    }
}

@Composable
private fun InlineStyleButton(
    spanStyleState: SpanStyleState,
    style: InlineStyle,
    @DrawableRes iconRes: Int,
    @StringRes contentDescriptionRes: Int,
) {
    RichTextToolbarButton(
        iconRes = iconRes,
        contentDescription = stringResource(id = contentDescriptionRes),
        selected = spanStyleState.isActive(style),
        onClick = { spanStyleState.toggle(style) },
    )
}

@Composable
private fun RichTextToolbarButton(
    iconRes: Int,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeContainerColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
    iconTint: Color = MaterialTheme.colorScheme.onPrimary,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .then(
                if (selected) {
                    Modifier.background(
                        color = activeContainerColor,
                        shape = RoundedCornerShape(12.dp),
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
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
private fun RowScope.UserInputSelector(
    onSelectorChange: (InputSelector) -> Unit,
    currentInputSelector: InputSelector,
    recordingActive: Boolean,
    onRecordingClick: () -> Unit,
    onVideoClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    val iconTint = MaterialTheme.colorScheme.primary
    val recordButtonPainter = if (recordingActive) rememberRecordButtonMeshGradientPainter() else null

    // Emoji
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

    // Photo
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

    // Video / Duo
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

    // Record (mic) button: filled with a mesh gradient while recording is active,
    // otherwise a plain primary-tinted mic icon.
    IconButton(
        onClick = onRecordingClick,
        modifier = Modifier
            .size(48.dp)
            .then(
                if (recordButtonPainter != null) {
                    Modifier
                        .clip(CircleShape)
                        .paint(recordButtonPainter, contentScale = ContentScale.FillBounds)
                } else {
                    Modifier
                },
            ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_mic),
            contentDescription = stringResource(id = R.string.record_message),
            tint = if (recordingActive) Color.White else iconTint,
            modifier = Modifier.size(24.dp),
        )
    }

    val isRichTextSelected = currentInputSelector == InputSelector.RICHTEXTEDITOR
    IconButton(
        onClick = {
            onSelectorChange(
                if (isRichTextSelected) {
                    InputSelector.NONE
                } else {
                    InputSelector.RICHTEXTEDITOR
                },
            )
        },
        modifier = Modifier.size(48.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_text_format),
            contentDescription = stringResource(id = R.string.format_text),
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
    }

    IconButton(
        onClick = onAddClick,
        modifier = Modifier.size(48.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = stringResource(id = R.string.add_attachment_desc),
            tint = iconTint,
            modifier = Modifier.size(24.dp),
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
    formattedTextState: FormattedTextState,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    onMessageSent: () -> Unit,
    focusState: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    showCloseButton: Boolean = false,
    onCloseRichTextEditor: () -> Unit = {},
) {
    val a11ylabel = stringResource(id = R.string.textfield_desc)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                top = 20.dp,
                end = if (showCloseButton) 16.dp else 32.dp,
            )
            .heightIn(min = 48.dp),
    ) {
        UserInputTextField(
            formattedTextState = formattedTextState,
            onTextFieldFocused = onTextFieldFocused,
            keyboardType = keyboardType,
            focusState = focusState,
            onMessageSent = onMessageSent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = if (showCloseButton) 36.dp else 0.dp)
                .semantics {
                    contentDescription = a11ylabel
                    keyboardShownProperty = keyboardShown
                },
        )

        if (showCloseButton) {
            IconButton(
                onClick = onCloseRichTextEditor,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close_rich_text_editor),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    formattedTextState: FormattedTextState,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
    onMessageSent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var lastFocusState by remember { mutableStateOf(false) }

    FormattedTextField(
        state = formattedTextState,
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
        onKeyboardAction = {
            if (formattedTextState.text.isNotBlank()) onMessageSent()
        },
        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 4),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.primary,
        ),
    )

    if (formattedTextState.isEmpty && !focusState) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = stringResource(R.string.textfield_hint),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
