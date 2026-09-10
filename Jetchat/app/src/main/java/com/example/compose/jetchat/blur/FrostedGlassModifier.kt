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

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Specification for a frosted glass backdrop effect chaining hardware blur and fractal noise texture.
 *
 * @param blurRadius The radius of the blur applied to the backdrop.
 * @param noiseFrequency Spatial frequency of the fractal noise (controls grain scale).
 * @param noiseIntensity Intensity of the frosted glass noise texture blended over the blurred backdrop.
 * @param tileMode Edge handling mode for the blur effect.
 */
data class FrostedGlassSpec(
    val blurRadius: Dp = 16.dp,
    val noiseFrequency: Float = 0.05f,
    val noiseIntensity: Float = 0.05f,
    val tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
) {
    /**
     * Creates a chained hardware [android.graphics.RenderEffect] applying blur and frosted fractal noise texture.
     */
    fun createRenderEffect(density: Density): RenderEffect? {
        val blurPx = with(density) { blurRadius.toPx() }
        return createFrostedGlassEffect(
            blurRadiusPx = blurPx,
            noiseFrequency = noiseFrequency,
            noiseIntensity = noiseIntensity,
            tileMode = tileMode,
        )
    }

    companion object {
        /**
         * Creates a chained [RenderEffect] combining blur and frosted fractal noise texture.
         */
        fun createRenderEffect(
            blurRadius: Dp,
            density: Density,
            noiseFrequency: Float = 0.05f,
            noiseIntensity: Float = 0.05f,
            tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
        ): RenderEffect? {
            val blurPx = with(density) { blurRadius.toPx() }
            return createFrostedGlassEffect(
                blurRadiusPx = blurPx,
                noiseFrequency = noiseFrequency,
                noiseIntensity = noiseIntensity,
                tileMode = tileMode,
            )
        }
    }
}

/**
 * Draws the content behind this composable with a frosted glass effect chaining
 * hardware blur and fractal noise texture, clipped to [shape], beneath this composable's content.
 *
 * @param blurRadius The blur radius applied to the backdrop.
 * @param noiseFrequency Spatial frequency of the fractal noise (controls grain scale).
 * @param noiseIntensity Intensity of the frosted glass noise texture blended over the blurred backdrop.
 * @param shape The shape of the frosted-glass region.
 * @param tint An optional translucent color overlay drawn over the backdrop.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropFrostedGlass(
    blurRadius: Dp = 16.dp,
    noiseFrequency: Float = 0.05f,
    noiseIntensity: Float = 0.05f,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = backdropFrostedGlass(
    spec = FrostedGlassSpec(
        blurRadius = blurRadius,
        noiseFrequency = noiseFrequency,
        noiseIntensity = noiseIntensity,
    ),
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Overload of [backdropFrostedGlass] configured via a [FrostedGlassSpec].
 */
fun Modifier.backdropFrostedGlass(
    spec: FrostedGlassSpec,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = this then BackdropFrostedGlassElement(
    spec = spec,
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

private data class BackdropFrostedGlassElement(
    val spec: FrostedGlassSpec,
    val shape: Shape,
    val tint: Color,
    val elevation: Dp,
    val outerShadowOnly: Boolean,
    val fallbackColor: Color,
) : ModifierNodeElement<BackdropFrostedGlassNode>() {
    override fun create(): BackdropFrostedGlassNode = BackdropFrostedGlassNode(
        spec = spec,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )

    override fun update(node: BackdropFrostedGlassNode) {
        node.update(
            spec = spec,
            shape = shape,
            tint = tint,
            elevation = elevation,
            outerShadowOnly = outerShadowOnly,
            fallbackColor = fallbackColor,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "backdropFrostedGlass"
        properties["spec"] = spec
        properties["shape"] = shape
        properties["tint"] = tint
        properties["elevation"] = elevation
        properties["outerShadowOnly"] = outerShadowOnly
        properties["fallbackColor"] = fallbackColor
    }
}

private class BackdropFrostedGlassNode(
    var spec: FrostedGlassSpec,
    shape: Shape,
    tint: Color,
    elevation: Dp,
    outerShadowOnly: Boolean,
    fallbackColor: Color,
) : BaseBackdropNode(shape, tint, elevation, outerShadowOnly, fallbackColor) {

    private var cachedEffect: RenderEffect? = null
    private var cachedDensity: Float = -1f
    private var cachedSpec: FrostedGlassSpec? = null

    override fun resolveRenderEffect(density: Density): RenderEffect? {
        val currentDensity = density.density
        if (cachedEffect == null || cachedDensity != currentDensity || cachedSpec != spec) {
            cachedEffect = spec.createRenderEffect(density)
            cachedDensity = currentDensity
            cachedSpec = spec
        }
        return cachedEffect
    }

    fun update(spec: FrostedGlassSpec, shape: Shape, tint: Color, elevation: Dp, outerShadowOnly: Boolean, fallbackColor: Color) {
        var changed = false
        if (this.spec != spec) {
            this.spec = spec
            cachedEffect = null
            changed = true
        }
        if (this.shape != shape) {
            this.shape = shape
            changed = true
        }
        if (this.tint != tint) {
            this.tint = tint
            changed = true
        }
        if (this.elevation != elevation) {
            this.elevation = elevation
            changed = true
        }
        if (this.outerShadowOnly != outerShadowOnly) {
            this.outerShadowOnly = outerShadowOnly
            changed = true
        }
        if (this.fallbackColor != fallbackColor) {
            this.fallbackColor = fallbackColor
            changed = true
        }
        if (changed) {
            markDirty()
        }
    }
}

/**
 * Creates a chained hardware [RenderEffect] combining blur and frosted fractal noise texture.
 *
 * Chains:
 * 1. Inner effect: Hardware blur filter applied first to the backdrop.
 * 2. Outer effect: [RuntimeShader] applying frosted fractal noise grain on top of the blurred backdrop.
 */
fun createFrostedGlassEffect(
    blurRadiusPx: Float,
    noiseFrequency: Float = 0.05f,
    noiseIntensity: Float = 0.05f,
    tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
): RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null

    val blurEffect = if (blurRadiusPx > 0f) {
        RenderEffect.createBlurEffect(
            blurRadiusPx.coerceAtLeast(0.01f),
            blurRadiusPx.coerceAtLeast(0.01f),
            tileMode,
        )
    } else null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && noiseIntensity > 0f) {
        try {
            val shader = RuntimeShader(FRACTAL_NOISE_SHADER).apply {
                setFloatUniform("frequency", noiseFrequency)
                setFloatUniform("noiseIntensity", noiseIntensity)
            }

            val noiseEffect = RenderEffect.createRuntimeShaderEffect(shader, "content")

            return if (blurEffect != null) {
                // inner = blurEffect (blurs the backdrop first)
                // outer = noiseEffect (applies frosted noise grain on top of the blurred backdrop)
                RenderEffect.createChainEffect(noiseEffect, blurEffect)
            } else {
                noiseEffect
            }
        } catch (t: Throwable) {
            Log.w("BackdropBlur", "Failed to create RuntimeShader noise: ${t.message}")
        }
    }

    return blurEffect
}
