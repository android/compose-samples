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

package com.example.compose.jetchat.conversation

import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.BlurRegion
import android.view.RoundedRectBlurRegion
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.spatial.RelativeLayoutBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data specification for a blur region placed over a SurfaceView.
 */
data class BlurRegionSpec(
    val id: String,
    val boundsInSurface: RectF,
    val cornerRadiusPx: Float,
    val blurRadiusPx: Float = 50f,
    val alpha: Float = 1.0f,
)

/**
 * Helper to manage SurfaceView#setBlurRegions platform API.
 */
object SurfaceViewBlurHelper {
    private const val TAG = "SurfaceViewBlurHelper"

    /**
     * Applies a list of blur regions to the given SurfaceView.
     */
    fun applyBlurRegions(surfaceView: SurfaceView?, regions: List<BlurRegionSpec>) {
        if (surfaceView == null) return
        try {
            if (Build.VERSION.SDK_INT >= 37) {
                applyBlurRegionsInternal(surfaceView, regions)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to apply blur regions to SurfaceView", e)
        }
    }

    /**
     * Clears all blur regions on the given SurfaceView.
     */
    fun clearBlurRegions(surfaceView: SurfaceView?) {
        applyBlurRegions(surfaceView, emptyList())
    }

    @RequiresApi(37)
    private fun applyBlurRegionsInternal(surfaceView: SurfaceView, regions: List<BlurRegionSpec>) {
        if (regions.isEmpty()) {
            surfaceView.setBlurRegions(emptyList())
            return
        }

        val blurRegions = ArrayList<BlurRegion>(regions.size)
        for (spec in regions) {
            if (spec.boundsInSurface.width() <= 0f || spec.boundsInSurface.height() <= 0f) continue
            val roundedRectRegion = RoundedRectBlurRegion().apply {
                bounds = spec.boundsInSurface
                setCornerRadii(spec.cornerRadiusPx.coerceAtLeast(0f))
                alpha = spec.alpha.coerceIn(0f, 1f)
                blurRadius = spec.blurRadiusPx.coerceAtLeast(0f)
            }
            blurRegions.add(roundedRectRegion)
        }
        surfaceView.setBlurRegions(blurRegions)
    }
}

/**
 * Modifier to register and track a control's bounding box relative to the underlying SurfaceView,
 * enabling SurfaceView#setBlurRegions to blur the region underneath this control.
 * This modifier doesn't do blurring itself.
 */
@Composable
fun Modifier.registerBlurRegion(
    id: String,
    surfaceCoordinates: LayoutCoordinates?,
    cornerRadius: Dp,
    blurRadius: Dp = 20.dp,
    alpha: Float = 1.0f,
    onUpdateRegion: (BlurRegionSpec) -> Unit,
    onRemoveRegion: (String) -> Unit,
): Modifier {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
    val blurRadiusPx = with(density) { blurRadius.toPx() }

    DisposableEffect(id) {
        onDispose {
            onRemoveRegion(id)
        }
    }

    return this.onLayoutRectChanged(throttleMillis = 0, debounceMillis = 0) { bounds: RelativeLayoutBounds ->
        if (surfaceCoordinates != null && surfaceCoordinates.isAttached) {
            val surfacePosInWindow = surfaceCoordinates.positionInWindow()
            val surfaceW = surfaceCoordinates.size.width.toFloat()
            val surfaceH = surfaceCoordinates.size.height.toFloat()

            val boxInWindow = bounds.boundsInWindow
            val left = (boxInWindow.left - surfacePosInWindow.x).coerceIn(0f, surfaceW)
            val top = (boxInWindow.top - surfacePosInWindow.y).coerceIn(0f, surfaceH)
            val right = (boxInWindow.right - surfacePosInWindow.x).coerceIn(0f, surfaceW)
            val bottom = (boxInWindow.bottom - surfacePosInWindow.y).coerceIn(0f, surfaceH)

            if (right > left && bottom > top) {
                onUpdateRegion(
                    BlurRegionSpec(
                        id = id,
                        boundsInSurface = RectF(left, top, right, bottom),
                        cornerRadiusPx = cornerRadiusPx,
                        blurRadiusPx = blurRadiusPx,
                        alpha = alpha,
                    ),
                )
            }
        }
    }
}
