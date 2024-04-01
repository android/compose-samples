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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.jetcaster.core.data.database.model.Podcast

@Composable
internal fun Background(
    podcast: Podcast,
    modifier: Modifier = Modifier,
    overlay: DrawScope.() -> Unit = {
        val brush = Brush.radialGradient(
            listOf(Color.Black, Color.Transparent),
            center = Offset(0f, size.height),
            radius = size.width * 1.5f
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    }
) {
    AsyncImage(
        model = podcast.imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}
