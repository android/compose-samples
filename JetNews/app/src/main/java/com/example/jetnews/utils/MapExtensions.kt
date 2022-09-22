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

package com.example.jetnews.utils

import android.util.Log

internal fun <E> MutableSet<E>.addOrRemove(element: E) {
    if (!add(element)) {
        remove(element)
    }
}


internal suspend fun <E> MutableSet<E>.suspendAddOrRemove(element: E,
                                                          onAdded: suspend (E) -> Unit,
                                                          onRemoved: suspend (E) -> Unit) {

    val isAdded = add(element)
    if (!isAdded) {
        if (remove(element)) {
            onRemoved.invoke(element)
        }
    }else{
        onAdded(element)
    }
}