/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged.backgrounds

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private data class MovingStripesBackgroundElement(
    val stripeColor: Color,
    val backgroundColor: Color
) : ModifierNodeElement<MovingStripesBackgroundNode>() {
    override fun create(): MovingStripesBackgroundNode =
        MovingStripesBackgroundNode(stripeColor, backgroundColor)
    override fun update(node: MovingStripesBackgroundNode) {
        node.updateColors(stripeColor, backgroundColor)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class MovingStripesBackgroundNode(
    stripeColor: Color,
    backgroundColor: Color,
) : DrawModifierNode, Modifier.Node() {

    private val shader = RuntimeShader(SHADER)
    private val shaderBrush = ShaderBrush(shader)
    private val time = mutableFloatStateOf(0f)

    init {
        updateColors(stripeColor, backgroundColor)
    }

    fun updateColors(stripeColor: Color, backgroundColor: Color) {
        shader.setColorUniform(
            "stripeColor",
            android.graphics.Color.valueOf(
                stripeColor.red,
                stripeColor.green,
                stripeColor.blue,
                stripeColor.alpha
            )
        )
        shader.setFloatUniform("backgroundLuminance", backgroundColor.luminance())
        shader.setColorUniform(
            "backgroundColor",
            android.graphics.Color.valueOf(
                backgroundColor.red,
                backgroundColor.green,
                backgroundColor.blue,
                backgroundColor.alpha
            )
        )
    }

    override fun ContentDrawScope.draw() {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time.floatValue)

        drawRect(shaderBrush)

        drawContent()
    }

    override fun onAttach() {
        coroutineScope.launch {
            while (true) {
                withInfiniteAnimationFrameMillis {
                    time.floatValue = it / 1000f
                }
            }
        }
    }
}

fun Modifier.movingStripesBackground(
    stripeColor: Color,
    backgroundColor: Color,
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.then(MovingStripesBackgroundElement(stripeColor, backgroundColor))
    } else {
        this.then(Modifier.simpleGradient())
    }

@Language("AGSL")
private val SHADER = """
    uniform float2 resolution;
    uniform float time;
    uniform float backgroundLuminance;
    layout(color) uniform half4 backgroundColor;
    layout(color) uniform half4 stripeColor;
    
    float calculateColorMultiplier(float yCoord, float factor, bool fadeToDark) {
        float result = step(yCoord, 1.0 + factor * 2.0) - step(yCoord, factor - 0.1);
        if (fadeToDark) {
            result *= -2.4;
        }
        return result;
    }

    float4 main(in float2 fragCoord) {
        // Config values
        const float speedMultiplier = 1.5;
        const float waveDensity = 1.0;
        const float waves = 7.0;
        const float waveCurveMultiplier = 4.3;
        const float energyMultiplier = 0.1;
        const float backgroundTolerance = 0.1;
        
        // Calculated values
        float2 uv = fragCoord / resolution.xy;
        float energy = waves * energyMultiplier;
        float timeOffset = time * speedMultiplier;
        float3 rgbColor = stripeColor.rgb;
        float hAdjustment = uv.x * waveCurveMultiplier;
        float loopMultiplier = 0.7 / waves;
        float3 loopColor = vec3(1.0 - rgbColor.r, 1.0 - rgbColor.g, 1.0 - rgbColor.b) / waves;
        bool fadeToDark = false;
        if (backgroundLuminance < 0.5) {
            fadeToDark = true;
        }
        float channelOffset = 0.0;
        
        for (float i = 1.0; i <= waves; i += 1.0) {
            float loopFactor = i * loopMultiplier;
            float sinInput = (timeOffset + hAdjustment) * energy;
            float curve = sin(sinInput) * (1.0 - loopFactor) * 0.05;
            float colorMultiplier = calculateColorMultiplier(uv.y, loopFactor, fadeToDark);
            rgbColor += loopColor * colorMultiplier;
            channelOffset += colorMultiplier;
            
            // Offset for next loop
            uv.y += curve;
        }
        
        // Clipped values are overridden to the passed in backgroundColor
        if (fadeToDark) {
            if (rgbColor.r <= backgroundTolerance && rgbColor.g <= backgroundTolerance && rgbColor.b <= backgroundTolerance) {
                rgbColor = backgroundColor.rgb;
            }
        } else {
            if (rgbColor.r >= (1.0 - backgroundTolerance) && rgbColor.g >= (1.0 - backgroundTolerance) && rgbColor.b >= (1.0 - backgroundTolerance)) {
                rgbColor = backgroundColor.rgb;
            }
        }
        return float4(rgbColor, 1.0);
    }
""".trimIndent()
