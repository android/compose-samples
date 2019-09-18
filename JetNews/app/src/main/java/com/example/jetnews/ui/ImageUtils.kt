/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui

import androidx.compose.composer

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Draw
import androidx.ui.core.toRect
import androidx.ui.engine.geometry.Rect
import androidx.ui.painting.Image
import androidx.ui.painting.Paint


/**
 * Fits an image into the parent container. If the image is larger than the parent,
 * it is centered and clipped.
 * If the image is smaller than the container, it is centered.
 */
@Composable
fun ClippedImage(image: Image) {
    val paint = +memo { Paint() }
    Draw { canvas, parentSize ->
        //
        val imHeight = image.height.toFloat()
        val imWidth = image.width.toFloat()

        val pHeight = parentSize.height.value
        val pWidth = parentSize.width.value
        val drawWidth = kotlin.math.min(pWidth, imWidth)
        val drawHeight = kotlin.math.min(pHeight, imHeight)

        val srcRect = Rect(
            top = imHeight / 2 - drawHeight / 2,
            left = imWidth / 2 - drawWidth / 2,
            right = imWidth / 2 + drawWidth / 2,
            bottom = imHeight / 2 + drawHeight / 2
        )

        val dstRect = Rect(
            top = pHeight / 2 - drawHeight / 2,
            left = pWidth / 2 - drawWidth / 2,
            right = pWidth / 2 + drawWidth / 2,
            bottom = pHeight / 2 + drawHeight / 2
        )
        canvas.drawImageRect(image, srcRect, dstRect, paint)
    }
}


/**
 * Fits an image in its parent, stretching or squeezing it as needed.
 */
@Composable
fun FittedImage(image: Image) {
    val paint = +memo { Paint() }
    Draw { canvas, parentSize ->
        val fullImageRect = Rect(0f, 0f, image.width.toFloat(), image.height.toFloat())
        canvas.drawImageRect(image, fullImageRect, parentSize.toRect(), paint)
    }
}
