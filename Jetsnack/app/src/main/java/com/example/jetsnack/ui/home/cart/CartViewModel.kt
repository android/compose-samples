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

package com.example.jetsnack.ui.home.cart

import androidx.lifecycle.ViewModel
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.SnackRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class CartViewModel : ViewModel() {
    private val _orderLines: MutableStateFlow<List<OrderLine>> =
        MutableStateFlow(SnackRepo.getCart())
    val orderLines: StateFlow<List<OrderLine>> get() = _orderLines

    fun removeSnack(snackId: Long) {
        _orderLines.value = _orderLines.value.filter { it.snack.id != snackId }
    }

    fun increaseSnackCount(snackId: Long) {
        val currentCount = _orderLines.value.first { it.snack.id == snackId }.count
        updateSnackCount(snackId, currentCount + 1)
    }

    fun decreaseSnackCount(snackId: Long) {
        val currentCount = _orderLines.value.first { it.snack.id == snackId }.count
        if (currentCount == 1) {
            // remove snack from cart
            removeSnack(snackId)
        } else {
            // update quantity in cart
            updateSnackCount(snackId, currentCount - 1)
        }
    }

    private fun updateSnackCount(snackId: Long, count: Int) {
        _orderLines.value = _orderLines.value.map {
            if (it.snack.id == snackId) {
                it.copy(count = count)
            } else {
                it
            }
        }
    }
}
