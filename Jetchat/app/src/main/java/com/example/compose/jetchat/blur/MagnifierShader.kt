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
import androidx.compose.ui.geometry.Size

/**
 * AGSL shader simulating an optical magnifier lens.
 *
 * Performs:
 * 1. Geometric coordinate scaling relative to the element center (magnification).
 * 2. Convex spherical lens profile: zoom is highest across the central axis and tapers smoothly towards
 *    the outer perimeter, mimicking a physical glass loupe or reading bar magnifier.
 * 3. Chromatic dispersion (aberration): separates R and B channels radially towards the lens boundary
 *    to recreate natural optical glass prism dispersion.
 */
const val MAGNIFIER_LENS_SHADER = """
    uniform shader content;
    uniform float2 size;
    uniform float zoom;
    uniform float lensCurvature;
    uniform float chromaticAberration;

    half4 main(float2 fragCoord) {
        float2 center = size * 0.5;
        float2 delta = fragCoord - center;
        float dist = length(delta);

        // Compute normalized distance to boundary for pill, circle, or rounded rectangle
        float r = min(size.x, size.y) * 0.5;
        float halfSpanX = max(size.x * 0.5 - r, 0.0);
        float closestX = clamp(delta.x, -halfSpanX, halfSpanX);
        float distToAxis = length(delta - float2(closestX, 0.0));
        float normDist = clamp(distToAxis / max(r, 0.001), 0.0, 1.0);

        // Convex spherical lens profile:
        // Center has full zoom; curvature gently tapers zoom towards the edge
        float effectiveZoom = mix(zoom, 1.0, normDist * normDist * lensCurvature);
        effectiveZoom = max(effectiveZoom, 0.1);

        // Sample coordinate scaled relative to center
        float2 sampleCoord = center + delta / effectiveZoom;

        // Chromatic dispersion direction and offset (strongest near lens rim)
        float2 dir = dist > 0.001 ? delta / dist : float2(0.0);
        float dispersion = normDist * normDist * chromaticAberration;

        half rColor = content.eval(sampleCoord + dir * dispersion).r;
        half gColor = content.eval(sampleCoord).g;
        half bColor = content.eval(sampleCoord - dir * dispersion).b;
        half aColor = content.eval(sampleCoord).a;

        half4 result = half4(rColor, gColor, bColor, aColor);
        result.rgb = clamp(result.rgb, 0.0, result.a);
        return result;
    }
"""

/**
 * AGSL shader simulating glass surface finish, Fresnel rim reflection, and 3D convex specular sheen.
 */
const val GLASS_FINISH_SHADER = """
    uniform shader content;
    uniform float2 size;
    uniform float rimIntensity;
    uniform float specularIntensity;

    half4 main(float2 fragCoord) {
        half4 color = content.eval(fragCoord);

        float2 center = size * 0.5;
        float2 delta = fragCoord - center;

        float r = min(size.x, size.y) * 0.5;
        float halfSpanX = max(size.x * 0.5 - r, 0.0);
        float closestX = clamp(delta.x, -halfSpanX, halfSpanX);
        float2 axisOffset = delta - float2(closestX, 0.0);
        float distToAxis = length(axisOffset);
        float normDist = clamp(distToAxis / max(r, 0.001), 0.0, 1.0);

        // 1. Fresnel edge rim highlight (inner edge glow / bevel)
        float rim = smoothstep(0.72, 0.98, normDist) * rimIntensity;

        // 2. 3D convex glass dome surface normal & specular highlight
        float2 normalXY = distToAxis > 0.001 ? (axisOffset / distToAxis) * normDist : float2(0.0);
        float normalZ = sqrt(max(1.0 - normDist * normDist, 0.0));
        float3 normal = normalize(float3(normalXY, normalZ));
        float3 lightDir = normalize(float3(-0.5, -0.7, 0.8));

        float NdotL = max(dot(normal, lightDir), 0.0);
        float specular = pow(NdotL, 6.0) * specularIntensity;

        // Apply additive glass illumination modulated by content alpha
        half3 highlight = half3(rim + specular) * color.a;
        color.rgb = clamp(color.rgb + highlight, 0.0, color.a);

        return color;
    }
"""

/**
 * Creates a chained hardware [RenderEffect] implementing an optical glass magnifier.
 *
 * Chaining pipeline:
 * 1. Optional inner hardware blur filter: [RenderEffect.createBlurEffect] (for frosted/soft-focus magnifier).
 * 2. Lens magnification shader: [MAGNIFIER_LENS_SHADER] (scales backdrop coords, applies spherical curvature
 *    and chromatic dispersion).
 * 3. Glass surface finish shader: [GLASS_FINISH_SHADER] (adds Fresnel rim highlight and 3D convex specular sheen).
 *
 * Uses [RenderEffect.createChainEffect] to chain these stages together into a single hardware pass.
 */
fun createMagnifierEffect(
    size: Size,
    zoom: Float = 1.35f,
    blurRadiusPx: Float = 0f,
    lensCurvature: Float = 0.35f,
    chromaticAberrationPx: Float = 3f,
    rimIntensity: Float = 0.18f,
    specularIntensity: Float = 0.15f,
    tileMode: Shader.TileMode = Shader.TileMode.CLAMP,
): RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    if (size.width <= 0f || size.height <= 0f) return null

    val blurEffect = if (blurRadiusPx > 0f) {
        RenderEffect.createBlurEffect(
            blurRadiusPx.coerceAtLeast(0.01f),
            blurRadiusPx.coerceAtLeast(0.01f),
            tileMode,
        )
    } else null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        try {
            val lensShader = RuntimeShader(MAGNIFIER_LENS_SHADER).apply {
                setFloatUniform("size", size.width, size.height)
                setFloatUniform("zoom", zoom)
                setFloatUniform("lensCurvature", lensCurvature)
                setFloatUniform("chromaticAberration", chromaticAberrationPx)
            }
            val lensEffect = RenderEffect.createRuntimeShaderEffect(lensShader, "content")

            val stage1 = if (blurEffect != null) {
                // inner = blurEffect (blurs backdrop first)
                // outer = lensEffect (magnifies the blurred backdrop)
                RenderEffect.createChainEffect(lensEffect, blurEffect)
            } else {
                lensEffect
            }

            if (rimIntensity > 0f || specularIntensity > 0f) {
                val glassShader = RuntimeShader(GLASS_FINISH_SHADER).apply {
                    setFloatUniform("size", size.width, size.height)
                    setFloatUniform("rimIntensity", rimIntensity)
                    setFloatUniform("specularIntensity", specularIntensity)
                }
                val glassEffect = RenderEffect.createRuntimeShaderEffect(glassShader, "content")

                // inner = stage1 (magnified backdrop)
                // outer = glassEffect (adds lens rim highlight & specular sheen)
                return RenderEffect.createChainEffect(glassEffect, stage1)
            }

            return stage1
        } catch (t: Throwable) {
            Log.w("BackdropMagnifier", "Failed to create chained magnifier effect: ${t.message}")
        }
    }

    return blurEffect
}
