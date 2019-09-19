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
import androidx.ui.graphics.Image
import androidx.ui.graphics.Paint

/**
 * Fits an image into the parent container, clipping sides or top and bottom to match the aspect
 * ratio with no distortion
 */
@Composable
fun ZoomedClippedImage(image: Image) {
    val paint = +memo { Paint() }
    Draw { canvas, parentSize ->
        //
        val imHeight = image.height.toFloat()
        val imWidth = image.width.toFloat()

        val pHeight = parentSize.height.value
        val pWidth = parentSize.width.value

        val pAspectRatio = pWidth / pHeight
        val imAspectRatio = imWidth / imHeight

        val srcRect = if (pAspectRatio > imAspectRatio) {
            val drawHeight = imWidth / pAspectRatio
            val drawWidth = imWidth
            Rect(
                top = imHeight / 2 - drawHeight / 2,
                left = imWidth / 2 - drawWidth / 2,
                right = imWidth / 2 + drawWidth / 2,
                bottom = imHeight / 2 + drawHeight / 2
            )
        } else {
            val drawHeight = imHeight
            val drawWidth = imHeight * pAspectRatio
            Rect(
                top = imHeight / 2 - drawHeight / 2,
                left = imWidth / 2 - drawWidth / 2,
                right = imWidth / 2 + drawWidth / 2,
                bottom = imHeight / 2 + drawHeight / 2
            )
        }

        val dstRect = Rect(
            top = 0f,
            left = 0f,
            right = pWidth,
            bottom = pHeight
        )
        canvas.drawImageRect(image, srcRect, dstRect, paint)
    }
}
