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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

internal data object JetcasterAppDefaults {
    val overScanMargin = OverScanMarginSettings()
    val gap = GapSettings()
    val cardWidth = CardWidth()
    val padding = PaddingSettings()
    val thumbnailSize = ThumbnailSize()
    val iconButtonSize: IconButtonSize = IconButtonSize()
}

internal data class OverScanMarginSettings(
    val default: OverScanMargin = OverScanMargin(),
    val catalog: OverScanMargin = OverScanMargin(end = 0.dp),
    val episode: OverScanMargin = OverScanMargin(start = 80.dp, end = 80.dp),
    val drawer: OverScanMargin = OverScanMargin(start = 16.dp, end = 16.dp),
    val podcast: OverScanMargin = OverScanMargin(
        top = 40.dp,
        bottom = 40.dp,
        start = 80.dp,
        end = 80.dp
    ),
    val player: OverScanMargin = OverScanMargin(
        top = 40.dp,
        bottom = 40.dp,
        start = 80.dp,
        end = 80.dp
    ),
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

internal data class ThumbnailSize(
    val episodeDetails: DpSize = DpSize(266.dp, 266.dp),
    val podcast: DpSize = DpSize(196.dp, 196.dp),
    val episode: DpSize = DpSize(124.dp, 124.dp)
)

internal data class PaddingSettings(
    val tab: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    val sectionTitle: PaddingValues = PaddingValues(bottom = 16.dp),
    val podcastRowContentPadding: PaddingValues = PaddingValues(horizontal = 5.dp),
    val episodeRowContentPadding: PaddingValues = PaddingValues(horizontal = 5.dp),
)

internal data class GapSettings(
    val tiny: Dp = 4.dp,
    val small: Dp = tiny * 2,
    val default: Dp = small * 2,
    val medium: Dp = default + tiny,
    val large: Dp = medium * 2,

    val chip: Dp = small,
    val episodeRow: Dp = medium,
    val item: Dp = default,
    val paragraph: Dp = default,
    val podcastRow: Dp = medium,
    val section: Dp = large,
    val twoColumn: Dp = large,
)

internal data class IconButtonSize(
    val default: Radius = Radius(14.dp),
    val medium: Radius = Radius(20.dp),
    val large: Radius = Radius(28.dp)
)

internal data class Radius(private val value: Dp) {
    private fun diameter(): Dp {
        return value * 2
    }
    fun intoDpSize(): DpSize {
        val d = diameter()
        return DpSize(d, d)
    }
}
