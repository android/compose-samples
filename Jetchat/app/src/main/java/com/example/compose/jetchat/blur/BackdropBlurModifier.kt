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
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies an in-window backdrop [RenderEffect] to content drawn behind this composable in the window.
 *
 * On supported platforms (Android 17 / SDK 37+), this leverages [RenderNode.setBackdropRenderEffect]
 * to apply hardware-accelerated visual effects (like blur) to the backdrop before this composable
 * is drawn, enabling translucent floating navigation bars, top app bars, and frosted-glass cards.
 *
 * @param renderEffect The [RenderEffect] to apply to the backdrop behind this composable.
 * @param shape The shape used to clip the backdrop effect and outline.
 * @param tint An optional translucent color overlay drawn on top of the backdrop effect.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area so the shadow
 *                        does not darken the translucent frosted glass interior.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropRenderEffect(
    renderEffect: RenderEffect?,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = this then BackdropRenderEffectElement(
    renderEffect = renderEffect,
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Overload of [backdropRenderEffect] accepting Compose's [androidx.compose.ui.graphics.RenderEffect].
 */
fun Modifier.backdropRenderEffect(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = backdropRenderEffect(
    renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        renderEffect?.asAndroidRenderEffect()
    } else {
        null
    },
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Draws the content behind this composable blurred according to the provided [spec],
 * clipped to [shape], beneath this composable's own content.
 *
 * Uses Compose's built-in [BlurRadiusSpec] to configure uniform or spatially-varying blur
 * radii (such as [BlurRadiusSpec.uniform], [BlurRadiusSpec.verticalGradient], etc.).
 *
 * @param spec The [BlurRadiusSpec] defining the blur radius or gradient.
 * @param shape The shape of the frosted-glass region.
 * @param tint An optional translucent color overlay drawn over the blurred backdrop.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropBlur(
    spec: BlurRadiusSpec,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = this then BackdropBlurElement(
    spec = spec,
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Draws the content behind this composable blurred with the specified uniform [radius],
 * clipped to [shape], beneath this composable's own content.
 *
 * @param radius The blur radius to apply to the backdrop.
 * @param shape The shape of the frosted-glass region.
 * @param tint An optional translucent color overlay drawn over the blurred backdrop.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropBlur(
    radius: Dp,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = backdropBlur(
    spec = BlurRadiusSpec.uniform(radius),
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Overload of [backdropBlur] allowing independent horizontal and vertical blur radii.
 *
 * @param radiusX The horizontal blur radius.
 * @param radiusY The vertical blur radius.
 * @param shape The shape of the frosted-glass region.
 * @param tint An optional translucent color overlay drawn over the blurred backdrop.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropBlur(
    radiusX: Dp,
    radiusY: Dp,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = if (radiusX == radiusY) {
    backdropBlur(
        spec = BlurRadiusSpec.uniform(radiusX),
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )
} else {
    this then BackdropEllipticalBlurElement(
        radiusX = radiusX,
        radiusY = radiusY,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )
}

private data class BackdropRenderEffectElement(
    val renderEffect: RenderEffect?,
    val shape: Shape,
    val tint: Color,
    val elevation: Dp,
    val outerShadowOnly: Boolean,
    val fallbackColor: Color,
) : ModifierNodeElement<BackdropRenderEffectNode>() {
    override fun create(): BackdropRenderEffectNode = BackdropRenderEffectNode(
        renderEffect = renderEffect,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )

    override fun update(node: BackdropRenderEffectNode) {
        node.update(
            renderEffect = renderEffect,
            shape = shape,
            tint = tint,
            elevation = elevation,
            outerShadowOnly = outerShadowOnly,
            fallbackColor = fallbackColor,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "backdropRenderEffect"
        properties["renderEffect"] = renderEffect
        properties["shape"] = shape
        properties["tint"] = tint
        properties["elevation"] = elevation
        properties["outerShadowOnly"] = outerShadowOnly
        properties["fallbackColor"] = fallbackColor
    }
}

private data class BackdropBlurElement(
    val spec: BlurRadiusSpec,
    val shape: Shape,
    val tint: Color,
    val elevation: Dp,
    val outerShadowOnly: Boolean,
    val fallbackColor: Color,
) : ModifierNodeElement<BackdropBlurNode>() {
    override fun create(): BackdropBlurNode = BackdropBlurNode(
        spec = spec,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )

    override fun update(node: BackdropBlurNode) {
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
        name = "backdropBlur"
        properties["spec"] = spec
        properties["shape"] = shape
        properties["tint"] = tint
        properties["elevation"] = elevation
        properties["outerShadowOnly"] = outerShadowOnly
        properties["fallbackColor"] = fallbackColor
    }
}

private data class BackdropEllipticalBlurElement(
    val radiusX: Dp,
    val radiusY: Dp,
    val shape: Shape,
    val tint: Color,
    val elevation: Dp,
    val outerShadowOnly: Boolean,
    val fallbackColor: Color,
) : ModifierNodeElement<BackdropEllipticalBlurNode>() {
    override fun create(): BackdropEllipticalBlurNode = BackdropEllipticalBlurNode(
        radiusX = radiusX,
        radiusY = radiusY,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )

    override fun update(node: BackdropEllipticalBlurNode) {
        node.update(
            radiusX = radiusX,
            radiusY = radiusY,
            shape = shape,
            tint = tint,
            elevation = elevation,
            outerShadowOnly = outerShadowOnly,
            fallbackColor = fallbackColor,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "backdropBlur"
        properties["radiusX"] = radiusX
        properties["radiusY"] = radiusY
        properties["shape"] = shape
        properties["tint"] = tint
        properties["elevation"] = elevation
        properties["outerShadowOnly"] = outerShadowOnly
        properties["fallbackColor"] = fallbackColor
    }
}

private class BackdropRenderEffectNode(
    var renderEffect: RenderEffect?,
    shape: Shape,
    tint: Color,
    elevation: Dp,
    outerShadowOnly: Boolean,
    fallbackColor: Color,
) : BaseBackdropNode(shape, tint, elevation, outerShadowOnly, fallbackColor) {

    override fun resolveRenderEffect(density: Density): RenderEffect? = renderEffect

    fun update(renderEffect: RenderEffect?, shape: Shape, tint: Color, elevation: Dp, outerShadowOnly: Boolean, fallbackColor: Color) {
        var changed = false

        if (this.renderEffect != renderEffect) {
            this.renderEffect = renderEffect
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

private class BackdropBlurNode(
    var spec: BlurRadiusSpec,
    shape: Shape,
    tint: Color,
    elevation: Dp,
    outerShadowOnly: Boolean,
    fallbackColor: Color,
) : BaseBackdropNode(shape, tint, elevation, outerShadowOnly, fallbackColor) {

    private var cachedEffect: RenderEffect? = null
    private var cachedDensity: Float = -1f
    private var cachedSize: Size = Size.Unspecified
    private var cachedSpec: BlurRadiusSpec? = null

    override fun resolveRenderEffect(density: Density, size: Size): RenderEffect? {
        val currentDensity = density.density
        if (cachedEffect == null || cachedDensity != currentDensity || cachedSize != size || cachedSpec != spec) {
            cachedEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && size.width > 0f && size.height > 0f) {
                try {
                    spec.createRenderEffect(size, density).asAndroidRenderEffect()
                } catch (_: Throwable) {
                    null
                }
            } else {
                null
            }
            cachedDensity = currentDensity
            cachedSize = size
            cachedSpec = spec
        }
        return cachedEffect
    }

    fun update(spec: BlurRadiusSpec, shape: Shape, tint: Color, elevation: Dp, outerShadowOnly: Boolean, fallbackColor: Color) {
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

private class BackdropEllipticalBlurNode(
    var radiusX: Dp,
    var radiusY: Dp,
    shape: Shape,
    tint: Color,
    elevation: Dp,
    outerShadowOnly: Boolean,
    fallbackColor: Color,
) : BaseBackdropNode(shape, tint, elevation, outerShadowOnly, fallbackColor) {

    private var cachedEffect: RenderEffect? = null
    private var cachedDensity: Float = -1f
    private var cachedRadiusX: Dp = 0.dp
    private var cachedRadiusY: Dp = 0.dp

    override fun resolveRenderEffect(density: Density): RenderEffect? {
        val currentDensity = density.density
        if (cachedEffect == null || cachedDensity != currentDensity || cachedRadiusX != radiusX || cachedRadiusY != radiusY) {
            val rxPx = with(density) { radiusX.toPx() }
            val ryPx = with(density) { radiusY.toPx() }
            cachedEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (rxPx > 0f || ryPx > 0f)) {
                RenderEffect.createBlurEffect(
                    rxPx.coerceAtLeast(0.01f),
                    ryPx.coerceAtLeast(0.01f),
                    Shader.TileMode.CLAMP,
                )
            } else {
                null
            }
            cachedDensity = currentDensity
            cachedRadiusX = radiusX
            cachedRadiusY = radiusY
        }
        return cachedEffect
    }

    fun update(radiusX: Dp, radiusY: Dp, shape: Shape, tint: Color, elevation: Dp, outerShadowOnly: Boolean, fallbackColor: Color) {
        var changed = false
        if (this.radiusX != radiusX) {
            this.radiusX = radiusX
            cachedEffect = null
            changed = true
        }
        if (this.radiusY != radiusY) {
            this.radiusY = radiusY
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
