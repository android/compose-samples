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
import android.graphics.Shader
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
 * Specification for an optical magnifier backdrop effect using a chained hardware [RenderEffect].
 *
 * @param zoom The magnification factor applied to content behind this element (e.g. 1.35f = 135% scale).
 * @param blurRadius Optional blur radius applied to the backdrop before magnification (0.dp for crystal clear glass).
 * @param lensCurvature Curvature falloff factor simulating a convex spherical lens (higher values curve more towards 1.0 at edges).
 * @param chromaticAberration Radial color fringe offset at the lens perimeter simulating optical dispersion.
 * @param rimIntensity Intensity of the Fresnel inner edge rim highlight / glass bevel.
 * @param specularIntensity Intensity of the 3D convex glass dome specular sheen.
 * @param tileMode Edge handling mode if blur is applied.
 */
data class MagnifierSpec(
    val zoom: Float = 1.35f,
    val blurRadius: Dp = 0.dp,
    val lensCurvature: Float = 0.35f,
    val chromaticAberration: Dp = 1.5.dp,
    val rimIntensity: Float = 0.18f,
    val specularIntensity: Float = 0.15f,
    val tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
) {
    /**
     * Creates a chained hardware [RenderEffect] configured with this specification.
     */
    fun createRenderEffect(density: Density, size: Size): RenderEffect? {
        val blurPx = with(density) { blurRadius.toPx() }
        val chromaticPx = with(density) { chromaticAberration.toPx() }
        return createMagnifierEffect(
            size = size,
            zoom = zoom,
            blurRadiusPx = blurPx,
            lensCurvature = lensCurvature,
            chromaticAberrationPx = chromaticPx,
            rimIntensity = rimIntensity,
            specularIntensity = specularIntensity,
            tileMode = tileMode,
        )
    }
}

/**
 * Draws the content behind this composable magnified with an optical lens effect using
 * a chained [RenderEffect] (refraction + chromatic dispersion + Fresnel rim + specular sheen),
 * clipped to [shape], beneath this composable's content.
 *
 * @param zoom The magnification factor (e.g., 1.35f = 135% scale).
 * @param blurRadius Optional blur applied to the backdrop before magnification (0.dp for crystal clear glass).
 * @param lensCurvature Convex lens curvature factor (tapers magnification towards edges).
 * @param chromaticAberration Radial color dispersion at the lens rim.
 * @param rimIntensity Intensity of the inner rim bevel highlight.
 * @param specularIntensity Intensity of the convex glass specular sheen.
 * @param shape The shape of the magnifier lens region.
 * @param tint An optional translucent color wash drawn over the magnified backdrop.
 * @param elevation Optional elevation shadow cast by this component.
 * @param outerShadowOnly If true, clips out the shadow cast beneath the outline area.
 * @param fallbackColor An optional fallback background color for platforms earlier than Android 17.
 */
fun Modifier.backdropMagnifier(
    zoom: Float = 1.35f,
    blurRadius: Dp = 0.dp,
    lensCurvature: Float = 0.35f,
    chromaticAberration: Dp = 1.5.dp,
    rimIntensity: Float = 0.18f,
    specularIntensity: Float = 0.15f,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = backdropMagnifier(
    spec = MagnifierSpec(
        zoom = zoom,
        blurRadius = blurRadius,
        lensCurvature = lensCurvature,
        chromaticAberration = chromaticAberration,
        rimIntensity = rimIntensity,
        specularIntensity = specularIntensity,
    ),
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

/**
 * Overload of [backdropMagnifier] configured via a [MagnifierSpec].
 */
fun Modifier.backdropMagnifier(
    spec: MagnifierSpec,
    shape: Shape = RectangleShape,
    tint: Color = Color.Unspecified,
    elevation: Dp = 0.dp,
    outerShadowOnly: Boolean = true,
    fallbackColor: Color = if (tint.isSpecified) tint else Color.Transparent,
): Modifier = this then BackdropMagnifierElement(
    spec = spec,
    shape = shape,
    tint = tint,
    elevation = elevation,
    outerShadowOnly = outerShadowOnly,
    fallbackColor = fallbackColor,
)

private data class BackdropMagnifierElement(
    val spec: MagnifierSpec,
    val shape: Shape,
    val tint: Color,
    val elevation: Dp,
    val outerShadowOnly: Boolean,
    val fallbackColor: Color,
) : ModifierNodeElement<BackdropMagnifierNode>() {
    override fun create(): BackdropMagnifierNode = BackdropMagnifierNode(
        spec = spec,
        shape = shape,
        tint = tint,
        elevation = elevation,
        outerShadowOnly = outerShadowOnly,
        fallbackColor = fallbackColor,
    )

    override fun update(node: BackdropMagnifierNode) {
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
        name = "backdropMagnifier"
        properties["spec"] = spec
        properties["shape"] = shape
        properties["tint"] = tint
        properties["elevation"] = elevation
        properties["outerShadowOnly"] = outerShadowOnly
        properties["fallbackColor"] = fallbackColor
    }
}

private class BackdropMagnifierNode(
    var spec: MagnifierSpec,
    shape: Shape,
    tint: Color,
    elevation: Dp,
    outerShadowOnly: Boolean,
    fallbackColor: Color,
) : BaseBackdropNode(shape, tint, elevation, outerShadowOnly, fallbackColor) {

    private var cachedEffect: RenderEffect? = null
    private var cachedDensity: Float = -1f
    private var cachedSpec: MagnifierSpec? = null
    private var cachedSize: Size = Size.Unspecified

    override fun resolveRenderEffect(density: Density, size: Size): RenderEffect? {
        val currentDensity = density.density
        if (cachedEffect == null ||
            cachedDensity != currentDensity ||
            cachedSpec != spec ||
            cachedSize != size
        ) {
            cachedEffect = spec.createRenderEffect(density, size)
            cachedDensity = currentDensity
            cachedSpec = spec
            cachedSize = size
        }
        return cachedEffect
    }

    fun update(spec: MagnifierSpec, shape: Shape, tint: Color, elevation: Dp, outerShadowOnly: Boolean, fallbackColor: Color) {
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
