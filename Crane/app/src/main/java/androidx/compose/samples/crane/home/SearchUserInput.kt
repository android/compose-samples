/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.home

import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneUserInput
import androidx.compose.samples.crane.base.ServiceLocator
import androidx.compose.setValue
import androidx.ui.animation.ColorPropKey
import androidx.ui.animation.Transition
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme

class PeopleUserInputState {
    var people by mutableStateOf(1)
        private set

    var animationState: PeopleUserInputAnimationState = PeopleUserInputAnimationState.Valid
        private set

    fun addPerson() {
        people = (people % (MAX_PEOPLE + 1)) + 1
        updateAnimationState()
    }

    private fun updateAnimationState() {
        val newState =
            if (people > MAX_PEOPLE) PeopleUserInputAnimationState.Invalid
            else PeopleUserInputAnimationState.Valid

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

        Transition(
            definition = transitionDefinition,
            toState = peopleState.animationState
        ) { state ->
            val people = peopleState.people
            CraneUserInput(
                modifier = Modifier.clickable(onClick = {
                    peopleState.addPerson()
                    onPeopleChanged(peopleState.people)
                }),
                text = if (people == 1) "$people Adult$titleSuffix" else "$people Adults$titleSuffix",
                vectorImageId = R.drawable.ic_person,
                tint = state[tintKey]
            )
            if (peopleState.animationState == PeopleUserInputAnimationState.Invalid) {
                Text(
                    text = "Error: We don't support more than $MAX_PEOPLE people",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondary)
                )
            }
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
fun DatesUserInput(onDateSelectionClicked: () -> Unit) {
    val datesSelectedText = ServiceLocator.datesSelected.toString()
    CraneUserInput(
        modifier = Modifier.clickable(onClick = onDateSelectionClicked),
        caption = if (datesSelectedText.isEmpty()) "Select Dates" else null,
        text = datesSelectedText,
        vectorImageId = R.drawable.ic_calendar
    )
}

private val tintKey = ColorPropKey()

enum class PeopleUserInputAnimationState { Valid, Invalid }

private fun generateTransitionDefinition(
    validColor: Color,
    invalidColor: Color
) = transitionDefinition {
    state(PeopleUserInputAnimationState.Valid) {
        this[tintKey] = validColor
    }
    state(PeopleUserInputAnimationState.Invalid) {
        this[tintKey] = invalidColor
    }
    transition(fromState = PeopleUserInputAnimationState.Valid to PeopleUserInputAnimationState.Invalid) {
        tintKey using tween<Color> {
            duration = 300
        }
    }
    transition(fromState = PeopleUserInputAnimationState.Invalid to PeopleUserInputAnimationState.Valid) {
        tintKey using tween<Color> {
            duration = 300
        }
    }
}
