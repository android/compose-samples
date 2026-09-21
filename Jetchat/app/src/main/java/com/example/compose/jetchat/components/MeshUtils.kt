/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A gently animated mesh gradient used as the glow behind the chat [UserInput] bar.
 *
 * The softness comes entirely from the mesh:
 *  - [MeshGradientPainter.hasBicubicColor] gives smooth (non-linear) colour blending, and
 *  - the outer ring of vertices is fully transparent (alpha 0x00), so the glow feathers to
 *    nothing at its edges.
 *
 * That means no `.blur()` and no linear gradient are needed to reproduce the effect.
 */
@Composable
fun rememberUserInputGlowMeshGradientPainter(): MeshGradientPainter {
    val transition = rememberInfiniteTransition(label = "userInputGlowMesh")
    val phase1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "glowPhase1",
    )
    val phase2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "glowPhase2",
    )

    // Helper for smooth organic orbital displacement per interior vertex.
    fun orbit(colIdx: Int, rowIdx: Int, ampX: Float, ampY: Float): Offset {
        val angle1 = phase1 + colIdx * 0.85f + rowIdx * 0.65f
        val angle2 = phase2 - colIdx * 0.55f + rowIdx * 0.90f
        val dx = ampX * (0.72f * cos(angle1) + 0.28f * sin(angle2))
        val dy = ampY * (0.72f * sin(angle1) + 0.28f * cos(angle2))
        return Offset(dx, dy)
    }

    return remember(phase1, phase2) {
        val uCols = floatArrayOf(0.00f, 0.12f, 0.27f, 0.56f, 0.78f, 1.00f)
        val vRows = floatArrayOf(0.00f, 0.20f, 0.33f, 0.43f, 0.64f, 0.85f, 1.00f)

        val gridColors = arrayOf(
            // Row 0: transparent royal-blue boundary (top of the glow).
            arrayOf(
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
            ),
            // Row 1: high royal-blue aura.
            arrayOf(
                Color(0x00125CCF),
                Color(0x3C1F62D8),
                Color(0x5C2365D7),
                Color(0x66125CCF),
                Color(0x451D63D1),
                Color(0x001D63D1),
            ),
            // Row 2: mid-upper sky-cerulean aura.
            arrayOf(
                Color(0x006B88EE),
                Color(0x80829CF2),
                Color(0xA86E8DF0),
                Color(0xB46488EE),
                Color(0x825C86EC),
                Color(0x005C86EC),
            ),
            // Row 3: lavender left, periwinkle centre, cobalt right shoulder.
            arrayOf(
                Color(0x00B6ABFB),
                Color(0xEEB6ABFB),
                Color(0xF4A39BFC),
                Color(0xF59D98FC),
                Color(0xF25C7CF5),
                Color(0x18728CF5),
            ),
            // Row 4: vibrant orchid-lavender left, electric cobalt right (card height).
            arrayOf(
                Color(0x00C2A7FF),
                Color(0xFFC2A7FF),
                Color(0xFFC1A6FF),
                Color(0xFF687DF9),
                Color(0xF04E6EFA),
                Color(0x205875FA),
            ),
            // Row 5: crisp indigo-cobalt shadow along the bottom.
            arrayOf(
                Color(0x007E8AF4),
                Color(0xE27E8AF4),
                Color(0xF45A6DFB),
                Color(0xF6556AFC),
                Color(0xFA3C5CFC),
                Color(0x204D6AFB),
            ),
            // Row 6: transparent cobalt boundary (bottom of the glow).
            arrayOf(
                Color(0x005A6DFB),
                Color(0x005A6DFB),
                Color(0x005A6DFB),
                Color(0x00556AFC),
                Color(0x003C5CFC),
                Color(0x003C5CFC),
            ),
        )

        MeshGradientPainter(
            rows = 6,
            columns = 5,
            hasBicubicColor = true,
        ) {
            for (r in 0..6) {
                for (c in 0..5) {
                    val baseU = uCols[c]
                    val baseV = vRows[r]
                    val offset = if (r in 1..5 && c in 1..4) {
                        val ampX = when (r) {
                            1, 2 -> 0.048f
                            3 -> 0.042f
                            4 -> 0.028f
                            else -> 0.036f
                        }
                        val ampY = when (r) {
                            1, 2 -> 0.028f
                            3 -> 0.020f
                            4 -> 0.022f
                            else -> 0.016f
                        }
                        val d = orbit(c, r, ampX, ampY)
                        Offset(
                            x = (baseU + d.x).coerceIn(0.04f, 0.96f),
                            y = (baseV + d.y).coerceIn(0.05f, 0.95f),
                        )
                    } else {
                        Offset(baseU, baseV)
                    }
                    setVertex(r, c, offset, gridColors[r][c])
                }
            }
        }
    }
}

/**
 * A small static mesh gradient for the Gemini spark button: a pink → purple → blue diagonal
 * built from a 2x2-cell mesh (3x3 vertices) with bicubic colour blending. This reproduces the
 * Figma diagonal fill without a linear gradient, keeping the screen mesh-only.
 */
@Composable
fun rememberUserInputSparkMeshGradientPainter(): MeshGradientPainter {
    val pink = Color(0xFFF96BD6)
    val pinkPurple = Color(0xFFC072EA)
    val purple = Color(0xFF9378FF)
    val purpleBlue = Color(0xFF585CFF)
    val blue = Color(0xFF1E40FF)

    return remember {
        val positions = floatArrayOf(0f, 0.5f, 1f)
        val colors = arrayOf(
            arrayOf(pink, pinkPurple, purple),
            arrayOf(pinkPurple, purple, purpleBlue),
            arrayOf(purple, purpleBlue, blue),
        )
        MeshGradientPainter(
            rows = 2,
            columns = 2,
            hasBicubicColor = true,
        ) {
            for (r in 0..2) {
                for (c in 0..2) {
                    setVertex(r, c, Offset(positions[c], positions[r]), colors[r][c])
                }
            }
        }
    }
}
