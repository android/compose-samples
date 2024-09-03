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
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import com.example.jetlagged.ui.theme.Yellow
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

/**
 * Background modifier that displays a custom shader for Android T and above and a linear gradient
 * for older versions of Android
 */
fun Modifier.solarFlareShaderBackground(): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.then(SolarFlareShaderBackgroundElement)
    } else {
        this.then(Modifier.simpleGradient())
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private data object SolarFlareShaderBackgroundElement :
    ModifierNodeElement<SolarFlairShaderBackgroundNode>() {
    override fun create() = SolarFlairShaderBackgroundNode()
    override fun update(node: SolarFlairShaderBackgroundNode) {
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class SolarFlairShaderBackgroundNode : DrawModifierNode, Modifier.Node() {
    private val shader = RuntimeShader(SHADER)
    private val shaderBrush = ShaderBrush(shader)
    private val time = mutableFloatStateOf(0f)

    init {
        shader.setColorUniform(
            "baseColor",
            android.graphics.Color.valueOf(Yellow.red, Yellow.green, Yellow.blue, Yellow.alpha)
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
        float alpha = 1.0 - (uv.y * uv.y);
        // Mix calculated color with the incoming base color
        float4 color = float4(colorPart * baseColor.r, colorPart * baseColor.g, colorPart * baseColor.b, alpha);
        // Keep all channels within valid bounds of 0.0 and 1.0
        return clamp(color, 0.0, 1.0);
    }
""".trimIndent()
