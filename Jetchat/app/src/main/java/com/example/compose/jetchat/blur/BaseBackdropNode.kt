/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat.blur

import android.annotation.SuppressLint
import android.graphics.Outline as AndroidOutline
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.os.Build
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

abstract class BaseBackdropNode(
    var shape: Shape,
    var tint: Color,
    var elevation: Dp,
    var outerShadowOnly: Boolean,
    var fallbackColor: Color,
) : Modifier.Node(),
    DrawModifierNode {

    abstract fun resolveRenderEffect(density: Density): RenderEffect?

    private var renderNode: RenderNode? = null
    private val androidOutline = AndroidOutline()
    private val tintPaint = Paint()

    private var lastWidth = -1
    private var lastHeight = -1
    private var lastRenderEffect: RenderEffect? = null
    private var lastDensity = -1f
    private var lastShape: Shape? = null
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastElevationPx = -1f
    private var lastTint: Color = Color.Unspecified
    private var isDirty = true

    protected fun markDirty() {
        isDirty = true
        invalidateDraw()
    }

    override fun onDetach() {
        if (Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.CINNAMON_BUN) {
            renderNode?.discardDisplayList()
        }
        lastWidth = -1
        lastHeight = -1
        isDirty = true
    }

    override fun ContentDrawScope.draw() {
        val effect = resolveRenderEffect(this)
        if (Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.CINNAMON_BUN && effect != null) {
            val widthPx = size.width.roundToInt()
            val heightPx = size.height.roundToInt()

            if (widthPx <= 0 || heightPx <= 0) {
                drawContent()
                return
            }

            var node = renderNode
            if (node == null) {
                node = RenderNode("BackdropRenderEffectNode").apply {
                    clipToOutline = true
                }
                renderNode = node
                isDirty = true
            }

            val elevationPx = elevation.toPx()
            val densityVal = density

            if (isDirty ||
                widthPx != lastWidth ||
                heightPx != lastHeight ||
                effect != lastRenderEffect ||
                densityVal != lastDensity ||
                shape != lastShape ||
                layoutDirection != lastLayoutDirection ||
                elevationPx != lastElevationPx ||
                tint != lastTint
            ) {
                lastWidth = widthPx
                lastHeight = heightPx
                lastRenderEffect = effect
                lastDensity = densityVal
                lastShape = shape
                lastLayoutDirection = layoutDirection
                lastElevationPx = elevationPx
                lastTint = tint
                isDirty = false

                // Configure RenderNode geometry & effect
                node.setPosition(0, 0, widthPx, heightPx)
                try {
                    node.setBackdropRenderEffect(effect)
                } catch (t: Throwable) {
                    Log.w("BackdropBlur", "Failed to setBackdropRenderEffect: ${t.message}")
                }

                // Map Compose Shape to Android Outline
                val composeOutline = shape.createOutline(size, layoutDirection, this)
                updateAndroidOutline(androidOutline, composeOutline, widthPx, heightPx)

                if (elevationPx > 0f) {
                    node.elevation = elevationPx
                    try {
                        androidOutline.isOuterShadowOnly = outerShadowOnly
                    } catch (_: Throwable) {
                        // Ignore if setOuterShadowOnly is not available
                    }
                } else {
                    node.elevation = 0f
                }

                node.setOutline(androidOutline)
                node.clipToOutline = true

                // Record backdrop tint/wash inside RenderNode
                val recordingCanvas = node.beginRecording(widthPx, heightPx)
                if (tint.isSpecified && tint.alpha > 0f) {
                    tintPaint.color = tint.toArgb()
                    recordingCanvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), tintPaint)
                }
                node.endRecording()
            }

            // 1. Draw hardware backdrop RenderNode
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRenderNode(node)
            }

            // 2. Draw composable content on top
            drawContent()
        } else {
            // Fallback for pre-API 37 or null effect
            if (fallbackColor.isSpecified && fallbackColor.alpha > 0f) {
                drawOutline(
                    outline = shape.createOutline(size, layoutDirection, this),
                    color = fallbackColor,
                )
            }
            drawContent()
        }
    }
}

/**
 * Helper to populate an [AndroidOutline] from a Compose [Outline].
 */
private fun updateAndroidOutline(androidOutline: AndroidOutline, composeOutline: Outline, width: Int, height: Int) {
    androidOutline.alpha = 1.0f
    when (composeOutline) {
        is Outline.Rectangle -> {
            androidOutline.setRect(0, 0, width, height)
        }

        is Outline.Rounded -> {
            val rect = composeOutline.roundRect
            val radius = rect.topLeftCornerRadius.x
            val topLeft = rect.topLeftCornerRadius
            val topRight = rect.topRightCornerRadius
            val bottomLeft = rect.bottomLeftCornerRadius
            val bottomRight = rect.bottomRightCornerRadius

            if (topLeft == topRight && topLeft == bottomLeft && topLeft == bottomRight && topLeft.x == topLeft.y) {
                // Uniform corner radii: use setRoundRect with scalar radius
                androidOutline.setRoundRect(0, 0, width, height, radius)
            } else {
                // Complex corner radii: convert to Path
                val path = AndroidPath().apply {
                    addRoundRect(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        floatArrayOf(
                            rect.topLeftCornerRadius.x,
                            rect.topLeftCornerRadius.y,
                            rect.topRightCornerRadius.x,
                            rect.topRightCornerRadius.y,
                            rect.bottomRightCornerRadius.x,
                            rect.bottomRightCornerRadius.y,
                            rect.bottomLeftCornerRadius.x,
                            rect.bottomLeftCornerRadius.y,
                        ),
                        AndroidPath.Direction.CW,
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    androidOutline.setPath(path)
                } else {
                    @Suppress("DEPRECATION")
                    androidOutline.setConvexPath(path)
                }
            }
        }

        is Outline.Generic -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                androidOutline.setPath(composeOutline.path.asAndroidPath())
            } else {
                @Suppress("DEPRECATION")
                androidOutline.setConvexPath(composeOutline.path.asAndroidPath())
            }
        }
    }
}
