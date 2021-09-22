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

package com.example.jetnews.utils

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowSizeTest {

    @Test
    fun getWindowSize_Compact() {
        assertEquals(WindowSize.Compact, getWindowSize(599.5.dp))
    }

    @Test
    fun getWindowSize_Medium_lowEnd() {
        assertEquals(WindowSize.Medium, getWindowSize(800.dp))
    }

    @Test
    fun getWindowSize_Medium_highEnd() {
        assertEquals(WindowSize.Medium, getWindowSize(839.5.dp))
    }

    @Test
    fun getWindowSize_Expanded() {
        assertEquals(WindowSize.Expanded, getWindowSize(840.dp))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getWindowSize_Negative() {
        getWindowSize((-1).dp)
    }
}
