/*
 * Copyright 2024 The Android Open Source Project
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
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

/**
 * Background modifier that displays a custom shader for Android T and above and a linear gradient
 * for older versions of Android
 */
fun Modifier.solarFlareShaderBackground(
    baseColor: Color,
    backgroundColor: Color,
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.then(SolarFlareShaderBackgroundElement(baseColor, backgroundColor))
    } else {
        this.then(Modifier.simpleGradient())
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private data class SolarFlareShaderBackgroundElement(
    val baseColor: Color,
    val backgroundColor: Color,
) :
    ModifierNodeElement<SolarFlairShaderBackgroundNode>() {
    override fun create() = SolarFlairShaderBackgroundNode(baseColor, backgroundColor)
    override fun update(node: SolarFlairShaderBackgroundNode) {
        node.updateColors(baseColor, backgroundColor)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class SolarFlairShaderBackgroundNode(
    baseColor: Color,
    backgroundColor: Color,
) : DrawModifierNode, Modifier.Node() {
    private val shader = RuntimeShader(SHADER)
    private val shaderBrush = ShaderBrush(shader)
    private val time = mutableFloatStateOf(0f)

    init {
        updateColors(baseColor, backgroundColor)
    }

    fun updateColors(baseColor: Color, backgroundColor: Color) {
        shader.setColorUniform(
            "baseColor",
            android.graphics.Color.valueOf(
                baseColor.red,
                baseColor.green,
                baseColor.blue,
                baseColor.alpha
            )
        )
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
            while (isAttached) {
                withInfiniteAnimationFrameMillis {
                    time.floatValue = it / 1000f
                }
            }
        }
    }
}

@Language("AGSL")
private val SHADER = """
    uniform float2 resolution;
    uniform float time;
    layout(color) uniform half4 baseColor;
    layout(color) uniform half4 backgroundColor;
    
    const int ITERATIONS = 2;
    const float INTENSITY = 100.0;
    const float TIME_MULTIPLIER = 0.25;
    
    float4 main(in float2 fragCoord) {
        // Slow down the animation to be more soothing
        float calculatedTime = time * TIME_MULTIPLIER;
        
        // Coords
        float2 uv = fragCoord / resolution.xy;
        float2 uvCalc = (uv * 5.0) - (INTENSITY * 2.0);
        
        // Values to adjust per iteration
        float2 iterationChange = float2(uvCalc);
        float colorPart = 1.0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            iterationChange = uvCalc + float2(
                cos(calculatedTime + iterationChange.x) +
                sin(calculatedTime - iterationChange.y), 
                cos(calculatedTime - iterationChange.x) +
                sin(calculatedTime + iterationChange.y) 
            );
            colorPart += 0.8 / length(
                float2(uvCalc.x / (cos(iterationChange.x + calculatedTime) * INTENSITY),
                    uvCalc.y / (sin(iterationChange.y + calculatedTime) * INTENSITY)
                )
            );
        }
        colorPart = 1.6 - (colorPart / float(ITERATIONS));
        
        // Fade out the bottom on a curve
        float mixRatio = 1.0 - (uv.y * uv.y);
        // Mix calculated color with the incoming base color
        float4 color = float4(colorPart * baseColor.r, colorPart * baseColor.g, colorPart * baseColor.b, 1.0);
        // Mix color with the background
        color = float4(
            mix(backgroundColor.r, color.r, mixRatio),
            mix(backgroundColor.g, color.g, mixRatio),
            mix(backgroundColor.b, color.b, mixRatio),
            1.0
        );
        // Keep all channels within valid bounds of 0.0 and 1.0
        return clamp(color, 0.0, 1.0);
    }
""".trimIndent()
