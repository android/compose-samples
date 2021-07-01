/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetsnack.ui.snackdetail

import androidx.lifecycle.ViewModel
import com.example.jetsnack.model.SnackRepo

/**
 * This is a basic contrived example of a ViewModel that is scoped to the navigation graph.
 * When navigating back from the SnackDetail screen this ViewModel would be cleared.
 *
 * In a real world app you would probably load the data from the repo and expose it via LiveData or
 * Flow. For this example we just hold on to it in an immutable variable for simplicity.
 **/
class SnackDetailViewModel(
    snackId: Long
) : ViewModel() {
    val snack = SnackRepo.getSnack(snackId)
    val related = SnackRepo.getRelated(snackId)
}
