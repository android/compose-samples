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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.SimpleUserInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun FlySearchContent(datesSelected: String, searchUpdates: FlySearchContentUpdates) {
    CraneSearch {
        PeopleUserInput(
            titleSuffix = stringResource(id = R.string.home_flight_type_economy),
            onPeopleChanged = searchUpdates.onPeopleChanged
        )
        Spacer(Modifier.height(8.dp))
        FromDestination()
        Spacer(Modifier.height(8.dp))
        ToDestinationUserInput(onToDestinationChanged = searchUpdates.onToDestinationChanged)
        Spacer(Modifier.height(8.dp))
        DatesUserInput(datesSelected, onDateSelectionClicked = searchUpdates.onDateSelectionClicked)
    }
}

@Composable
fun SleepSearchContent(datesSelected: String, sleepUpdates: SleepSearchContentUpdates) {
    CraneSearch {
        PeopleUserInput(onPeopleChanged = { sleepUpdates.onPeopleChanged })
        Spacer(Modifier.height(8.dp))
        DatesUserInput(datesSelected, onDateSelectionClicked = sleepUpdates.onDateSelectionClicked)
        Spacer(Modifier.height(8.dp))
        SimpleUserInput(caption = stringResource(id = R.string.home_select_location), vectorImageId = R.drawable.ic_hotel)
    }
}

@Composable
fun EatSearchContent(datesSelected: String, eatUpdates: EatSearchContentUpdates) {
    CraneSearch {
        PeopleUserInput(onPeopleChanged = { eatUpdates.onPeopleChanged })
        Spacer(Modifier.height(8.dp))
        DatesUserInput(datesSelected, onDateSelectionClicked = eatUpdates.onDateSelectionClicked)
        Spacer(Modifier.height(8.dp))
        SimpleUserInput(caption = stringResource(id = R.string.home_select_time), vectorImageId = R.drawable.ic_time)
        Spacer(Modifier.height(8.dp))
        SimpleUserInput(caption = stringResource(id = R.string.home_select_location), vectorImageId = R.drawable.ic_restaurant)
    }
}

@Composable
private fun CraneSearch(content: @Composable () -> Unit) {
    Column(Modifier.padding(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 12.dp)) {
        content()
    }
}
