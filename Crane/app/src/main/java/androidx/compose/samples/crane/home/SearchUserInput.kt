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

package androidx.compose.samples.crane.home

import androidx.compose.animation.ColorPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneUserInput
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Invalid
import androidx.compose.samples.crane.home.PeopleUserInputAnimationState.Valid
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class PeopleUserInputState {
    var people by mutableStateOf(1)
        private set

    var animationState: PeopleUserInputAnimationState = Valid
        private set

    fun addPerson() {
        people = (people % (MAX_PEOPLE + 1)) + 1
        updateAnimationState()
    }

    private fun updateAnimationState() {
        val newState =
            if (people > MAX_PEOPLE) Invalid
            else Valid

        if (animationState != newState) animationState = newState
    }
}

@Composable
fun PeopleUserInput(
    titleSuffix: String? = "",
    onPeopleChanged: (Int) -> Unit,
    peopleState: PeopleUserInputState = remember { PeopleUserInputState() }
) {
    Column {
        val validColor = MaterialTheme.colors.onSurface
        val invalidColor = MaterialTheme.colors.secondary
        val transitionDefinition =
            remember(validColor, invalidColor) {
                generateTransitionDefinition(
                    validColor,
                    invalidColor
                )
            }

        val transition = transition(transitionDefinition, peopleState.animationState)
        val people = peopleState.people
        CraneUserInput(
            modifier = Modifier.clickable {
                peopleState.addPerson()
                onPeopleChanged(peopleState.people)
            },
            text = if (people == 1) "$people Adult$titleSuffix" else "$people Adults$titleSuffix",
            vectorImageId = R.drawable.ic_person,
            tint = transition[tintKey]
        )
        if (peopleState.animationState == Invalid) {
            Text(
                text = "Error: We don't support more than $MAX_PEOPLE people",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondary)
            )
        }
    }
}

@Composable
fun FromDestination() {
    CraneUserInput(text = "Seoul, South Korea", vectorImageId = R.drawable.ic_location)
}

@Composable
fun ToDestinationUserInput(onToDestinationChanged: (String) -> Unit) {
    CraneEditableUserInput(
        hint = "Choose Destination",
        caption = "To",
        vectorImageId = R.drawable.ic_plane,
        onInputChanged = onToDestinationChanged
    )
}

@Composable
fun DatesUserInput(datesSelected: String, onDateSelectionClicked: () -> Unit) {
    CraneUserInput(
        modifier = Modifier.clickable(onClick = onDateSelectionClicked),
        caption = if (datesSelected.isEmpty()) "Select Dates" else null,
        text = datesSelected,
        vectorImageId = R.drawable.ic_calendar
    )
}

@Preview
@Composable
fun PeopleUserInputPreview() {
    CraneTheme {
        PeopleUserInput(onPeopleChanged = {})
    }
}

private val tintKey = ColorPropKey(label = "tint")

enum class PeopleUserInputAnimationState { Valid, Invalid }

private fun generateTransitionDefinition(
    validColor: Color,
    invalidColor: Color
) = transitionDefinition<PeopleUserInputAnimationState> {
    state(Valid) {
        this[tintKey] = validColor
    }
    state(Invalid) {
        this[tintKey] = invalidColor
    }
    transition(fromState = Valid) {
        tintKey using tween(
            durationMillis = 300
        )
    }
    transition(fromState = Invalid) {
        tintKey using tween(
            durationMillis = 300
        )
    }
}
