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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.spatial.RelativeLayoutBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import java.util.WeakHashMap

/**
 * Data specification for a blur region placed over a SurfaceView.
 */
@Immutable
data class BlurRegionSpec(
    val id: String,
    val boundsInSurface: Rect = Rect.Zero,
    val boundsInWindow: IntRect = IntRect.Zero,
    val cornerRadiusPx: Float = 0f,
    val blurRadiusPx: Float = 50f,
    val alpha: Float = 1.0f,
)

/**
 * Helper to manage SurfaceView#setBlurRegions platform API.
 */
object SurfaceViewBlurHelper {
    private const val TAG = "SurfaceViewBlurHelper"
    private val lastAppliedRegions = WeakHashMap<SurfaceView, List<BlurRegionSpec>>()

    /**
     * Applies a collection of blur regions to the given SurfaceView.
     */
    fun applyBlurRegions(surfaceView: SurfaceView?, regions: Collection<BlurRegionSpec>) {
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
    private fun applyBlurRegionsInternal(surfaceView: SurfaceView, regions: Collection<BlurRegionSpec>) {
        val regionList = if (regions.isEmpty()) emptyList() else regions.toList()
        if (lastAppliedRegions[surfaceView] == regionList) {
            return
        }
        lastAppliedRegions[surfaceView] = regionList

        if (regionList.isEmpty()) {
            surfaceView.setBlurRegions(emptyList())
            return
        }

        val blurRegions = ArrayList<BlurRegion>(regionList.size)
        for (spec in regionList) {
            if (spec.boundsInSurface.width <= 0f || spec.boundsInSurface.height <= 0f) continue
            val roundedRectRegion = RoundedRectBlurRegion().apply {
                bounds = RectF(
                    spec.boundsInSurface.left,
                    spec.boundsInSurface.top,
                    spec.boundsInSurface.right,
                    spec.boundsInSurface.bottom,
                )
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
fun Modifier.registerBlurRegion(
    id: String,
    cornerRadius: Dp,
    blurRadius: Dp = 20.dp,
    alpha: Float = 1.0f,
    onUpdateRegion: (BlurRegionSpec) -> Unit,
    onRemoveRegion: (String) -> Unit,
): Modifier = this
    .then(
        RegisterBlurRegionLifecycleElement(
            id = id,
            onRemoveRegion = onRemoveRegion,
        ),
    )
    .onLayoutRectChanged(throttleMillis = 0, debounceMillis = 0) { bounds: RelativeLayoutBounds ->
        val boxInWindow = bounds.boundsInWindow
        if (boxInWindow.width > 0 && boxInWindow.height > 0) {
            onUpdateRegion(
                BlurRegionSpec(
                    id = id,
                    boundsInWindow = boxInWindow,
                    cornerRadiusPx = cornerRadius.value,
                    blurRadiusPx = blurRadius.value,
                    alpha = alpha,
                ),
            )
        }
    }

private data class RegisterBlurRegionLifecycleElement(val id: String, val onRemoveRegion: (String) -> Unit) :
    ModifierNodeElement<RegisterBlurRegionLifecycleNode>() {
    override fun create(): RegisterBlurRegionLifecycleNode = RegisterBlurRegionLifecycleNode(id, onRemoveRegion)

    override fun update(node: RegisterBlurRegionLifecycleNode) {
        if (node.id != id) {
            node.onRemoveRegion(node.id)
            node.id = id
        }
        node.onRemoveRegion = onRemoveRegion
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "registerBlurRegion"
        properties["id"] = id
    }
}

private class RegisterBlurRegionLifecycleNode(var id: String, var onRemoveRegion: (String) -> Unit) : Modifier.Node() {
    override fun onDetach() {
        onRemoveRegion(id)
    }
}
