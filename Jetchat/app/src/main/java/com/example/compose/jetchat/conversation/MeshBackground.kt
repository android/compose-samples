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
//    val (painter, baseColor) = when (backgroundType) {
//        ChatBackgroundType.MESH_BG -> rememberMeshBackgroundGradientPainter() to Color(0xFFEAFFCE)
//        ChatBackgroundType.ANIMATED_BG -> rememberAnimatedMeshGradientPainter() to Color(0xFFF1EEFC)
//    }

    val (painter, baseColor) = rememberAnimatedMeshGradientPainter() to Color(0xFFF1EEFC)


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEAE9FC))
//            .paint(painter),
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

///**
// * Animated 5x5 Bicubic Mesh Gradient Glow behind the UserInput bar (Figma node 188:23011).
// * Fluidly animates the purple (#C1A6FF), periwinkle (#97A5FF), deep royal blue (#0B57D0),
// * and electric cobalt (#1E40FF) aura behind the chat input surface.
// */
//@Composable
//fun rememberUserInputGlowMeshGradientPainter(): MeshGradientPainter {
//    val transition = rememberInfiniteTransition(label = "userInputGlowMesh")
//    val phase1 by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = (2 * PI).toFloat(),
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 2400, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart,
//        ),
//        label = "glowPhase1",
//    )
//    val phase2 by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = (2 * PI).toFloat(),
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 3800, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart,
//        ),
//        label = "glowPhase2",
//    )
//
//    // Dynamic orbital & Gerstner wave displacements so the aura swirls and breathes visibly behind the input box
//    val d11 = Offset(0.085f * cos(phase1) + 0.030f * sin(phase2), 0.065f * sin(phase1))
//    val d12 = Offset(0.075f * sin(phase2), 0.070f * cos(phase1) + 0.025f * sin(phase2))
//    val d13 = Offset(0.085f * cos(phase2) - 0.030f * sin(phase1), 0.065f * sin(phase2))
//
//    val d21 = Offset(0.095f * sin(phase1), 0.075f * cos(phase2))
//    val d22 = Offset(0.080f * cos(phase1 + 1.2f), 0.070f * sin(phase2 + 0.8f))
//    val d23 = Offset(0.095f * sin(phase2), 0.075f * cos(phase1))
//
//    val d31 = Offset(0.065f * cos(phase2), 0.045f * sin(phase1))
//    val d32 = Offset(0.075f * sin(phase1), 0.050f * cos(phase2))
//    val d33 = Offset(0.065f * cos(phase1), 0.045f * sin(phase2))
//
//    return remember(phase1, phase2) {
//        MeshGradientPainter(
//            rows = 4,
//            columns = 4,
//            hasBicubicColor = true,
//        ) {
//            // Row 0: Transparent outer top boundary (matching RGB hues for clean premultiplied alpha)
//            setVertex(0, 0, Offset(0.00f, 0.00f), Color(0x00C1A6FF))
//            setVertex(0, 1, Offset(0.28f, 0.00f), Color(0x00C1A6FF))
//            setVertex(0, 2, Offset(0.52f, 0.00f), Color(0x005C85FF))
//            setVertex(0, 3, Offset(0.76f, 0.00f), Color(0x000B57D0))
//            setVertex(0, 4, Offset(1.00f, 0.00f), Color(0x000B57D0))
//
//            // Row 1: Upper aura above the input card (-55dp deep cobalt & upper-left purple cloud)
//            setVertex(1, 0, Offset(0.00f, 0.22f), Color(0x00C1A6FF))
//            setVertex(1, 1, Offset((0.28f + d11.x).coerceIn(0.08f, 0.48f), (0.24f + d11.y).coerceIn(0.08f, 0.42f)), Color(0xB3C1A6FF))
//            setVertex(1, 2, Offset((0.52f + d12.x).coerceIn(0.32f, 0.72f), (0.19f + d12.y).coerceIn(0.06f, 0.38f)), Color(0xCC5C85FF))
//            setVertex(1, 3, Offset((0.76f + d13.x).coerceIn(0.54f, 0.92f), (0.22f + d13.y).coerceIn(0.08f, 0.42f)), Color(0xD90B57D0))
//            setVertex(1, 4, Offset(1.00f, 0.22f), Color(0x000B57D0))
//
//            // Row 2: Mid-upper aura behind upper half of card (-81dp purple #C1A6FF, #97A5FF, #0B57D0)
//            setVertex(2, 0, Offset(0.00f, 0.48f), Color(0x00C1A6FF))
//            setVertex(2, 1, Offset((0.24f + d21.x).coerceIn(0.06f, 0.46f), (0.48f + d21.y).coerceIn(0.32f, 0.68f)), Color(0xFFC1A6FF))
//            setVertex(2, 2, Offset((0.50f + d22.x).coerceIn(0.30f, 0.70f), (0.46f + d22.y).coerceIn(0.30f, 0.66f)), Color(0xFF97A5FF))
//            setVertex(2, 3, Offset((0.78f + d23.x).coerceIn(0.56f, 0.94f), (0.48f + d23.y).coerceIn(0.32f, 0.68f)), Color(0xFF0B57D0))
//            setVertex(2, 4, Offset(1.00f, 0.48f), Color(0x001E40FF))
//
//            // Row 3: Lower body & bottom contact shadow (+4dp/+5dp electric blue #1E40FF)
//            setVertex(3, 0, Offset(0.00f, 0.84f), Color(0x0097A5FF))
//            setVertex(3, 1, Offset((0.26f + d31.x).coerceIn(0.08f, 0.46f), (0.84f + d31.y).coerceIn(0.72f, 0.94f)), Color(0xE68B95FF))
//            setVertex(3, 2, Offset((0.52f + d32.x).coerceIn(0.32f, 0.72f), (0.86f + d32.y).coerceIn(0.74f, 0.95f)), Color(0xFF1E40FF))
//            setVertex(3, 3, Offset((0.78f + d33.x).coerceIn(0.58f, 0.94f), (0.85f + d33.y).coerceIn(0.72f, 0.94f)), Color(0xFF1E40FF))
//            setVertex(3, 4, Offset(1.00f, 0.84f), Color(0x001E40FF))
//
//            // Row 4: Transparent outer bottom boundary
//            setVertex(4, 0, Offset(0.00f, 1.00f), Color(0x0097A5FF))
//            setVertex(4, 1, Offset(0.26f, 1.00f), Color(0x008B95FF))
//            setVertex(4, 2, Offset(0.52f, 1.00f), Color(0x001E40FF))
//            setVertex(4, 3, Offset(0.78f, 1.00f), Color(0x001E40FF))
//            setVertex(4, 4, Offset(1.00f, 1.00f), Color(0x001E40FF))
//        }
//    }
//}

/**
 * Animated 6x7 (rows = 6, columns = 5) Bicubic Mesh Gradient Glow behind the UserInput bar
 * (Figma node 188:23011 & full-screen context 188:22823).
 *
 * Calibrated directly against Figma's RGBA / white-composited color profile:
 * - Left margin & upper-left: Vibrant orchid-lavender (#C2A7FF / #B6ABFB)
 * - Top-center: Rich periwinkle-violet (#A199FC / #9D98FC)
 * - Top-right shoulder: Concentrated deep royal-cobalt hotspot (#5C7CF5 / #0B57D0)
 * - High upper aura (up to 185.dp above card): Soft cerulean & sky-royal blue (#869FEE -> #8AA9EC)
 * - Bottom edge & navigation bar area (down to 68.dp below card): Crisp electric indigo-cobalt
 *   drop shadow (#5D6FFA bottom-left, #596CFC center, #3C5CFC under Send button).
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

    // Helper for smooth organic orbital displacement per interior vertex
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
            // Row 0 (v=0.00, 185dp above card): transparent royal blue boundary
            arrayOf(
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
                Color(0x000B57D0),
            ),
            // Row 1 (v=0.20, ~102dp above card): high royal blue aura (#0B57D0 / #2365D7)
            arrayOf(
                Color(0x00125CCF),
                Color(0x3C1F62D8),
                Color(0x5C2365D7),
                Color(0x66125CCF),
                Color(0x451D63D1),
                Color(0x001D63D1),
            ),
            // Row 2 (v=0.33, ~48dp above card): mid-upper sky-cerulean aura (#869FEE center on white)
            arrayOf(
                Color(0x006B88EE),
                Color(0x80829CF2),
                Color(0xA86E8DF0),
                Color(0xB46488EE),
                Color(0x825C86EC),
                Color(0x005C86EC),
            ),
            // Row 3 (v=0.43, ~7dp above card top): lavender left (#B6ABFB), periwinkle center (#A199FC), royal-cobalt right shoulder (#5C7CF5)
            arrayOf(
                Color(0x00B6ABFB),
                Color(0xEEB6ABFB),
                Color(0xF4A39BFC),
                Color(0xF59D98FC),
                Color(0xF25C7CF5),
                Color(0x18728CF5),
            ),
            // Row 4 (v=0.64, mid-card height): vibrant orchid-lavender left (#C2A7FF), electric cobalt right (#4E6EFA)
            arrayOf(
                Color(0x00C2A7FF),
                Color(0xFFC2A7FF),
                Color(0xFFC1A6FF),
                Color(0xFF687DF9),
                Color(0xF04E6EFA),
                Color(0x205875FA),
            ),
            // Row 5 (v=0.85, ~6dp below card bottom & spanning across nav bar): crisp indigo-cobalt shadow
            arrayOf(
                Color(0x007E8AF4),
                Color(0xE27E8AF4),
                Color(0xF45A6DFB),
                Color(0xF6556AFC),
                Color(0xFA3C5CFC),
                Color(0x204D6AFB),
            ),
            // Row 6 (v=1.00, 68dp below card bottom): transparent electric cobalt boundary
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
