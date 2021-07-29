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

import androidx.compose.samples.crane.data.DatesRepository
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.di.DefaultDispatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

const val MAX_PEOPLE = 4

@HiltViewModel
class MainViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    datesRepository: DatesRepository
) : ViewModel() {

    private val _state = MutableLiveData(CraneHomeState(datesSelected = datesRepository.datesSelected))
    val state: LiveData<CraneHomeState>
        get() = _state

    init {
        getDestinations(CraneScreen.Fly)
    }

    fun getDestinations(crane: CraneScreen) {
        setState {
            val items = when (crane) {
                CraneScreen.Fly -> destinationsRepository.destinations
                CraneScreen.Sleep -> destinationsRepository.hotels
                CraneScreen.Eat -> destinationsRepository.restaurants
            }

            it.copy(
                items = items,
                currentScreen = crane
            )
        }
    }

    fun updatePeople(people: Int) {
        viewModelScope.launch {
            val newDestinations = if (people > MAX_PEOPLE) {
                emptyList()
            } else {
                withContext(defaultDispatcher) {
                    destinationsRepository.destinations
                        .shuffled(Random(people * (1..100).shuffled().first()))
                }
            }

            setState {
                it.copy(items = newDestinations)
            }
        }
    }

    fun toDestinationChanged(newDestination: String) {
        viewModelScope.launch {
            val newDestinations = withContext(defaultDispatcher) {
                destinationsRepository.destinations
                    .filter { it.city.nameToDisplay.contains(newDestination) }
            }

            setState {
                it.copy(items = newDestinations)
            }
        }
    }

    private fun setState(modifier: (CraneHomeState) -> CraneHomeState) {
        val currentState = _state.value
        val newState = modifier.invoke(currentState!!)
        _state.value = newState
    }
}
