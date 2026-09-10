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

/**
 * AGSL shader generating procedural fractal noise (equivalent to SVG feTurbulence type="fractalNoise")
 * blended over the backdrop content without coordinate displacement.
 */
const val FRACTAL_NOISE_SHADER = """
    uniform shader content;
    uniform float frequency;
    uniform float noiseIntensity;

    float2 mod289(float2 x) {
        return x - floor(x * (1.0 / 289.0)) * 289.0;
    }

    float3 mod289(float3 x) {
        return x - floor(x * (1.0 / 289.0)) * 289.0;
    }

    float3 permute(float3 x) {
        return mod289(((x * 34.0) + 1.0) * x);
    }

    // Stefan Gustavson's deterministic 2D Simplex Noise
    float simplexNoise2D(float2 v) {
        const float4 C = float4(
            0.211324865405187,   // (3.0-sqrt(3.0))/6.0
            0.366025403784439,   // 0.5*(sqrt(3.0)-1.0)
            -0.577350269189626,  // -1.0 + 2.0 * C.x
            0.024390243902439    // 1.0 / 41.0
        );

        float2 i = floor(v + dot(v, C.yy));
        float2 x0 = v - i + dot(i, C.xx);

        float2 i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
        float4 x12 = x0.xyxy + C.xxzz;
        x12.xy -= i1;

        i = mod289(i);
        float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0)) + i.x + float3(0.0, i1.x, 1.0));

        float3 m = max(0.5 - float3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
        m = m * m;
        m = m * m;

        float3 x = 2.0 * fract(p * C.w) - 1.0;
        float3 h = abs(x) - 0.5;
        float3 ox = floor(x + 0.5);
        float3 a0 = x - ox;

        m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);

        float3 g;
        g.x = a0.x * x0.x + h.x * x0.y;
        g.yz = a0.yz * x12.xz + h.yz * x12.yw;
        return 130.0 * dot(m, g);
    }

    float fractalNoise(float2 p) {
        float n0 = simplexNoise2D(p);
        float n1 = simplexNoise2D(p * 2.0);
        float n2 = simplexNoise2D(p * 4.0);
        return (n0 + n1 * 0.5 + n2 * 0.25) / 1.75;
    }

    half4 main(float2 fragCoord) {
        // Snap to pixel center to eliminate sub-pixel floating-point jitter across redraws
        float2 pixelCoord = floor(fragCoord) + 0.5;
        float noise = fractalNoise(pixelCoord * frequency);
        half4 color = content.eval(fragCoord);
        // Add subtle frosted glass surface grain without displacing backdrop coordinates
        color.rgb = clamp(color.rgb + noise * (noiseIntensity * color.a), 0.0, color.a);
        return color;
    }
"""
