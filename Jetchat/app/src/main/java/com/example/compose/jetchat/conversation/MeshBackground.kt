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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Calculates a 2D wave displacement vector for a vertex at ([baseX], [baseY]).
 * Produces a traveling Gerstner-like orbital wave flowing diagonally across the mesh,
 * combined with a gentle harmonic cross-wave for a natural fluid surface feel.
 */
private fun calculateWaveDisplacement(baseX: Float, baseY: Float, phase1: Float, phase2: Float, scale: Float = 1f): Offset {
    // Primary diagonal wave (rolling from top-left to bottom-right)
    val wave1 = phase1 - (baseX * 2.0f + baseY * 2.4f)
    // Secondary cross-undulation to prevent mechanical repetition
    val wave2 = phase2 - (baseX * 1.4f - baseY * 1.8f)

    val dx = (0.065f * sin(wave1) + 0.025f * cos(wave2)) * scale
    val dy = (0.070f * cos(wave1) + 0.025f * sin(wave2)) * scale

    return Offset(dx, dy)
}

/**
 * 4x4 Mesh Gradient for #composers (Figma "mesh bg").
 * Vibrant lime, teal, and forest green tones with a subtle undulating wave effect.
 */
@Composable
fun rememberMeshBackgroundGradientPainter(): MeshGradientPainter {
    val transition = rememberInfiniteTransition(label = "composersMeshWave")
    val phase1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wavePhase1",
    )
    val phase2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wavePhase2",
    )

    // Interior wave displacements
    val d11 = calculateWaveDisplacement(0.3154f, 0.2157f, phase1, phase2, scale = 1.15f)
    val d12 = calculateWaveDisplacement(0.6773f, 0.4324f, phase1, phase2, scale = 1.10f)
    val d21 = calculateWaveDisplacement(0.2936f, 0.6260f, phase1, phase2, scale = 1.15f)
    val d22 = calculateWaveDisplacement(0.6603f, 0.8200f, phase1, phase2, scale = 0.90f)

    // Edge wave displacements (constrained to slide along outer boundaries)
    val d02 = calculateWaveDisplacement(0.6700f, 0.0000f, phase1, phase2, scale = 0.85f)
    val d10 = calculateWaveDisplacement(0.0000f, 0.3300f, phase1, phase2, scale = 0.85f)
    val d20 = calculateWaveDisplacement(0.0000f, 0.6700f, phase1, phase2, scale = 0.85f)
    val d13 = calculateWaveDisplacement(1.0000f, 0.2919f, phase1, phase2, scale = 0.75f)
    val d23 = calculateWaveDisplacement(1.0000f, 0.6736f, phase1, phase2, scale = 0.75f)
    val d31 = calculateWaveDisplacement(0.3300f, 1.0000f, phase1, phase2, scale = 0.70f)
    val d32 = calculateWaveDisplacement(0.6700f, 1.0000f, phase1, phase2, scale = 0.70f)

    return remember(phase1, phase2) {
        MeshGradientPainter(
            rows = 3,
            columns = 3,
            hasBicubicColor = true,
        ) {
            // Row 0 (top edge, y = 0.0f)
            setVertex(0, 0, Offset(0.0000f, 0.0000f), Color(0xFFF0FCB0))
            setVertex(0, 1, Offset(0.3300f, 0.0000f), Color(0xFFF0FCB0))
            setVertex(0, 2, Offset(0.6700f + d02.x, 0.0000f), Color(0xFFDCFA51))
            setVertex(0, 3, Offset(1.0000f, 0.0000f), Color(0xFFF0FCB0))

            // Row 1 (upper-mid, y ~ 0.21f - 0.43f)
            setVertex(1, 0, Offset(0.0000f, 0.3300f + d10.y), Color(0xFFDCFA51))
            setVertex(1, 1, Offset(0.3154f + d11.x, 0.2157f + d11.y), Color(0xFF63CEAD))
            setVertex(1, 2, Offset(0.6773f + d12.x, 0.4324f + d12.y), Color(0xFF80B259))
            setVertex(1, 3, Offset(1.0534f, 0.2919f + d13.y), Color(0xFF80B259))

            // Row 2 (lower-mid, y ~ 0.62f - 0.87f)
            setVertex(2, 0, Offset(0.0000f, 0.6700f + d20.y), Color(0xFF63CEAD))
            setVertex(2, 1, Offset(0.2936f + d21.x, 0.6260f + d21.y), Color(0xFF63CEAD))
            setVertex(2, 2, Offset(0.6603f + d22.x, 0.8200f + d22.y), Color(0xFF43B55F))
            setVertex(2, 3, Offset(1.1238f, 0.6736f + d23.y), Color(0xFF43B55F))

            // Row 3 (bottom edge, y = 1.0f)
            setVertex(3, 0, Offset(0.0000f, 1.0000f), Color(0xFF05D6A1))
            setVertex(3, 1, Offset(0.3300f + d31.x, 1.0000f), Color(0xFF1AB2A6))
            setVertex(3, 2, Offset(0.6700f + d32.x, 1.0000f), Color(0xFF43B55F))
            setVertex(3, 3, Offset(1.0000f, 1.0000f), Color(0xFF43B55F))
        }
    }
}

/**
 * Heart reaction mesh gradient for chat speech bubbles (Figma node 268:29966).
 * Vibrant coral, blush, hot pink, and lavender tones.
 */
@Composable
fun rememberHeartReactionMeshGradientPainter(): MeshGradientPainter {
    return remember {
        MeshGradientPainter(
            rows = 2,
            columns = 3,
            hasBicubicColor = true,
        ) {
            // Row 0 (top edge, y = 0.0f)
            setVertex(0, 0, Offset(0.0000f, 0.0000f), Color(0xFFFF6B6B))
            setVertex(0, 1, Offset(0.3300f, 0.0000f), Color(0xFFFFD6D6))
            setVertex(0, 2, Offset(0.6700f, 0.0000f), Color(0xFFE8C3FF))
            setVertex(0, 3, Offset(1.0000f, 0.0000f), Color(0xFFE396FF))

            // Row 1 (mid, y ~ 0.33f - 0.50f)
            setVertex(1, 0, Offset(0.0000f, 0.3300f), Color(0xFFFFAAEA))
            setVertex(1, 1, Offset(0.3300f, 0.3300f), Color(0xFFFF6060))
            setVertex(1, 2, Offset(0.6700f, 0.3300f), Color(0xFFFFBCBC))
            setVertex(1, 3, Offset(1.0000f, 0.5000f), Color(0xFFFF2088))

            // Row 2 (bottom edge, y = 1.0f)
            setVertex(2, 0, Offset(0.0000f, 1.0000f), Color(0xFFFFFFFF))
            setVertex(2, 1, Offset(0.3300f, 1.0000f), Color(0xFFD0BEF9))
            setVertex(2, 2, Offset(0.6637f, 1.0000f), Color(0xFFFFA298))
            setVertex(2, 3, Offset(1.0000f, 1.0000f), Color(0xFFFF9BEB))
        }
    }
}

/**
 * Animated 4x4 Mesh Gradient for the other channel (Figma "animated bg").
 * Smoothly drifting pastel lavender, violet, peach, and soft sky nodes.
 */
@Composable
fun rememberAnimatedMeshGradientPainter(): MeshGradientPainter {
    val transition = rememberInfiniteTransition(label = "animatedMesh")
    val phase1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase1",
    )
    val phase2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase2",
    )

    val p11x = 0.33f + 0.08f * cos(phase1)
    val p11y = 0.28f + 0.07f * sin(phase1)

    val p21x = 0.67f + 0.07f * sin(phase2)
    val p21y = 0.38f + 0.06f * cos(phase2)

    val p12x = 0.30f + 0.07f * sin(phase2)
    val p12y = 0.68f + 0.08f * cos(phase1)

    val p22x = 0.68f + 0.06f * cos(phase1)
    val p22y = 0.72f + 0.07f * sin(phase2)

    return remember(p11x, p11y, p21x, p21y, p12x, p12y, p22x, p22y) {
        MeshGradientPainter(
            rows = 3,
            columns = 3,
            hasBicubicColor = true,
        ) {
            // Row 0
            setVertex(0, 0, Offset(0.0000f, 0.0000f), Color(0xFFF7F1FB))
            setVertex(0, 1, Offset(0.3300f, 0.0000f), Color(0xFFFDE8C7))
            setVertex(0, 2, Offset(0.6700f, 0.0000f), Color(0xFFE5D4F5))
            setVertex(0, 3, Offset(1.0000f, 0.0000f), Color(0xFFD6E5FA))

            // Row 1
            setVertex(1, 0, Offset(0.0000f, 0.3300f), Color(0xFFF1E6FA))
            setVertex(1, 1, Offset(p11x, p11y), Color(0xFFD0BEF9))
            setVertex(1, 2, Offset(p21x, p21y), Color(0xFFFFD5B8))
            setVertex(1, 3, Offset(1.0000f, 0.3300f), Color(0xFFC7DCF8))

            // Row 2
            setVertex(2, 0, Offset(0.0000f, 0.6700f), Color(0xFFDCC8F7))
            setVertex(2, 1, Offset(p12x, p12y), Color(0xFFC1F0DC))
            setVertex(2, 2, Offset(p22x, p22y), Color(0xFFE2C4F5))
            setVertex(2, 3, Offset(1.0000f, 0.6700f), Color(0xFFB8CFF7))

            // Row 3
            setVertex(3, 0, Offset(0.0000f, 1.0000f), Color(0xFFBFAFF2))
            setVertex(3, 1, Offset(0.3300f, 1.0000f), Color(0xFFB4C8F5))
            setVertex(3, 2, Offset(0.6700f, 1.0000f), Color(0xFFD4B8F3))
            setVertex(3, 3, Offset(1.0000f, 1.0000f), Color(0xFFA592EE))
        }
    }
}

/**
 * Fullscreen container applying the appropriate chat background according to [ChatBackgroundType].
 */
@Composable
fun ChatBackground(backgroundType: ChatBackgroundType, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val (painter, baseColor) = when (backgroundType) {
        ChatBackgroundType.MESH_BG -> rememberMeshBackgroundGradientPainter() to Color(0xFFEAFFCE)
        ChatBackgroundType.ANIMATED_BG -> rememberAnimatedMeshGradientPainter() to Color(0xFFF1EEFC)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
            .paint(painter),
    ) {
        content()
    }
}

/**
 * Backwards-compatible fullscreen container using the default mesh background.
 */
@Composable
fun MeshBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ChatBackground(
        backgroundType = ChatBackgroundType.MESH_BG,
        modifier = modifier,
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
fun MeshBgPreview() {
    ChatBackground(backgroundType = ChatBackgroundType.MESH_BG) {}
}

@Preview(showBackground = true)
@Composable
fun AnimatedBgPreview() {
    ChatBackground(backgroundType = ChatBackgroundType.ANIMATED_BG) {}
}
