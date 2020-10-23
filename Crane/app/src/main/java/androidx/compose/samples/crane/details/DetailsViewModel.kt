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

package androidx.compose.samples.crane.details

import androidx.compose.samples.crane.base.Result
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import java.lang.IllegalArgumentException

class DetailsViewModel @AssistedInject constructor(
    private val destinationsRepository: DestinationsRepository,
    @Assisted private val cityName: String
) : ViewModel() {

    val cityDetails: Result<ExploreModel>
        get() {
            val destination = destinationsRepository.getDestination(cityName)
            return if (destination != null) {
                Result.Success(destination)
            } else {
                Result.Error(IllegalArgumentException("City doesn't exist"))
            }
        }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(cityName: String): DetailsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            cityName: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(cityName) as T
            }
        }
    }
}

@InstallIn(ActivityRetainedComponent::class)
@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectModule::class])
interface AssistedInjectModule
