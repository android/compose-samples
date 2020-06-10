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
import androidx.compose.MutableState
import androidx.compose.getValue
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Face
import androidx.ui.material.icons.filled.Phone
import androidx.ui.material.icons.filled.Place
import androidx.ui.material.icons.filled.Send
import androidx.ui.material.icons.filled.ThumbUp
import androidx.ui.material.ripple.ripple
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup

enum class InputSelector {
    NONE,
    MAP,
    THUMBS_UP,
    EMOJI,
    PHONE,
    NOT_IMPLEMENTED
}

@Preview
@Composable
fun UserInputPreview() {
    UserInput()
}

@Composable
fun UserInput(
    // TODO: Default non-empty modifier = bad
    modifier: Modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically),
    onMessageSent: (String) -> Unit = { }
) {
    var currentInputSelector by savedInstanceState { InputSelector.NONE }
    val onBackPressed = remember { { currentInputSelector = InputSelector.NONE } }
    backPressHandler(
        enabled = currentInputSelector != InputSelector.NONE,
        onBackPressed = onBackPressed
    )

    val textState = state { TextFieldValue() }

    Column(modifier = modifier) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f))
        UserInputSelector(
            currentSelector = currentInputSelector,
            onSelectorChange = { currentInputSelector = it }
        )
        SelectorExpanded(
            onCloseRequested = onBackPressed,
            onTextAdded = { textState.addText(it) },
            currentSelector = currentInputSelector
        )
        UserInputText(
            textFieldValue = textState.value,
            onTextChanged = { textState.value = it },
            onCloseSelectorRequested = onBackPressed,
            onMessageSent = onMessageSent
        )
    }
}

private fun MutableState<TextFieldValue>.addText(newString: String) {
    val newText = this.value.text.replaceRange(
        this.value.selection.start,
        this.value.selection.end,
        newString
    )
    val newSelection = this.value.selection.copy(
        start = newText.length,
        end = newText.length
    )

    this.value = this.value.copy(text = newText, selection = newSelection)
}

@Composable
private fun SelectorExpanded(
    currentSelector: InputSelector,
    onCloseRequested: () -> Unit,
    onTextAdded: (String) -> Unit
) {
    when (currentSelector) {
        InputSelector.MAP -> MapSelector()
        InputSelector.THUMBS_UP -> StickerSelector()
        InputSelector.EMOJI -> EmojiSelector(onTextAdded)
        InputSelector.PHONE -> StartCall()
        InputSelector.NOT_IMPLEMENTED -> NotAvailablePopup(onCloseRequested)
        else -> {}
    }
}

@Composable
fun MapSelector() {
    Surface(modifier = Modifier.fillMaxWidth().preferredHeight(128.dp), color = Color.LightGray) {
        Stack {
            Text(modifier = Modifier.gravity(Alignment.Center), text = "I'm here!")
        }
    }
}

@Composable
fun StickerSelector() {
    Text("Stickers…")
}

@Composable
fun StartCall() {
    Text("Calling…")
}

@Composable
private fun UserInputSelector(
    currentSelector: InputSelector,
    onSelectorChange: (InputSelector) -> Unit
) {

    if (currentSelector == InputSelector.NONE) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.MAP) },
                icon = Icons.Filled.Place
            )
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.THUMBS_UP) },
                icon = Icons.Filled.ThumbUp
            )
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.EMOJI) },
                icon = Icons.Filled.Face
            )
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.PHONE) },
                icon = Icons.Filled.Phone
            )
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.NOT_IMPLEMENTED) },
                icon = Icons.Filled.Add
            )
        }
    } else {
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.NONE) },
            icon = Icons.Filled.ArrowBack
        )
    }
}

@Composable
private fun InputSelectorButton(
    onClick: () -> Unit,
    icon: VectorAsset
) {
    IconButton(onClick = onClick) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Icon(icon)
        }
    }
}

@Composable
private fun NotAvailablePopup(onDismissed: () -> Unit) {
    FunctionalityNotAvailablePopup(onDismissed)
}

@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onMessageSent: (String) -> Unit,
    onCloseSelectorRequested: () -> Unit
) {
    val focusState = state { false }

    Row(
        modifier = Modifier.fillMaxWidth().preferredHeight(48.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Stack(
            modifier = Modifier
                .preferredHeight(48.dp)
                .weight(1f)
                .gravity(Alignment.Bottom)
        ) {
            TextField(
                value = textFieldValue,
                onValueChange = { onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .gravity(Alignment.CenterStart),
                keyboardType = keyboardType,
                imeAction = ImeAction.Send,
                onFocusChange = { focusState.value = it }
            )

            // FilledTextField has a placeholder but it shows a bottom indicator: b/155943102
            if (textFieldValue.text.isEmpty() && !focusState.value) {
                Text(
                    modifier = Modifier
                        .gravity(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    text = "Message #composers",
                    style = MaterialTheme.typography.body1.copy(color = Color.Gray)
                )
            }
        }

        IconButton(
            modifier = Modifier
                .preferredSize(48.dp)
                .padding(8.dp)
                .gravity(Alignment.CenterVertically),
            onClick = {
                onMessageSent(textFieldValue.text)
                // Reset
                onTextChanged(TextFieldValue())
                onCloseSelectorRequested()
            }
        ) {
            Icon(Icons.Filled.Send, tint = MaterialTheme.colors.secondary)
        }
    }
}

@Composable
fun EmojiSelector(
    onTextAdded: (String) -> Unit
) {
    val columns = 10
    HorizontalScroller {
        Column(Modifier.fillMaxWidth()) {
            repeat(4) { x ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(columns) { y ->
                        val emoji = emojis[x * columns + y]
                        Clickable(
                            onClick = { onTextAdded(emoji) },
                            modifier = Modifier
                                .preferredSize(42.dp, 42.dp)
                                .ripple()
                        ) {
                            Text(
                                text = emoji,
                                style = currentTextStyle().copy(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

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
    "\ud83d\udc8f" // Kiss
)
