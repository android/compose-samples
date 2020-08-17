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

import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.craneDestinations
import androidx.compose.samples.crane.data.craneHotels
import androidx.compose.samples.crane.data.craneRestaurants
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

const val MAX_PEOPLE = 4

class MainViewModel : ViewModel() {

    val hotels = craneHotels
    val restaurants = craneRestaurants

    private val _suggestedDestinations = MutableLiveData<List<ExploreModel>>()
    val suggestedDestinations: LiveData<List<ExploreModel>>
        get() = _suggestedDestinations

    init {
        _suggestedDestinations.value = craneDestinations
    }

    fun updatePeople(people: Int) {
        if (people > MAX_PEOPLE) {
            _suggestedDestinations.value = emptyList()
        } else {
            // Making Random more random
            _suggestedDestinations.value =
                craneDestinations.shuffled(Random(people * (1..100).shuffled().first()))
        }
    }

    fun toDestinationChanged(newDestination: String) {
        _suggestedDestinations.value =
            craneDestinations.filter { it.city.nameToDisplay.contains(newDestination) }
    }
}
