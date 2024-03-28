/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data object JetcasterAppDefaults {
    val overScanMargin = OverScanMarginSettings()
    val gapSettings = GapSettings()
    val cardWidth = CardWidth()
    val padding = PaddingSettings()
}

internal data class OverScanMarginSettings(
    val default: OverScanMargin = OverScanMargin(),
    val podcastDetails: OverScanMargin = OverScanMargin(top = 40.dp, bottom = 40.dp),
    val drawer: OverScanMargin = OverScanMargin(start = 0.dp, end = 0.dp),
    val catalog: OverScanMargin = OverScanMargin(start = 0.dp, end = 0.dp)
)

internal data class OverScanMargin(
    val top: Dp = 24.dp,
    val bottom: Dp = 24.dp,
    val start: Dp = 48.dp,
    val end: Dp = 48.dp,
) {
    fun intoPaddingValues(): PaddingValues {
        return PaddingValues(start, top, end, bottom)
    }
}

internal data class CardWidth(
    val large: Dp = 268.dp,
    val medium: Dp = 196.dp,
    val small: Dp = 124.dp
)

internal data class PaddingSettings(
    val tab: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    val sectionTitle: PaddingValues = PaddingValues(bottom = 16.dp)
)

internal data class GapSettings(
    val catalogItemGap: Dp = 20.dp,
    val catalogSectionGap: Dp = 40.dp,
    val itemGap: Dp = 16.dp,
    val chipGap: Dp = 8.dp
)
