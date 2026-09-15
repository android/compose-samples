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

package com.example.compose.jetchat.effects

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * HDR colors exceeding 1.0f in [ColorSpaces.ExtendedSrgb] to achieve peak luminance
 * and bloom on HDR-capable displays (CL 4034508).
 */
private val HdrCoreWhite = Color(3.0f, 3.0f, 3.2f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrGlowCyan = Color(0.8f, 2.2f, 2.8f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrGlowMagenta = Color(2.5f, 0.4f, 1.8f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrSparkleGold = Color(2.8f, 2.4f, 0.6f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrTransparentCyan = Color(0.8f, 2.2f, 2.8f, 0.0f, ColorSpaces.ExtendedSrgb)

/**
 * Represents an individual sparkle particle radiating outward from the touch point.
 */
private class SparkleParticle(
    val id: Long = Random.nextLong(),
    var currentOffset: Offset,
    val velocity: Offset,
    val maxRadius: Float,
    val rotationSpeed: Float,
    val color: Color,
    var currentRadius: Float = maxRadius * 0.3f,
    var rotation: Float = Random.nextFloat() * 360f,
    var alpha: Float = 1.0f,
    var life: Float = 1.0f,
    val decayRate: Float = Random.nextFloat() * 0.035f + 0.02f,
)

/**
 * Tracks an active touch pointer state for the HDR light bloom and sparkles.
 */
private class ActiveTouchPointer(
    val id: PointerId,
    var position: Offset,
    var isPressed: Boolean = true,
    var bloomRadius: Float = 0f,
    var targetBloomRadius: Float = 180f,
    var energyWaveRadius: Float = 0f,
    var alpha: Float = 1.0f,
    val sparkles: MutableList<SparkleParticle> = mutableListOf(),
)

/**
 * Container composable that tracks all pointer inputs and renders an HDR light bloom
 * and sparkle effect that follows the user's touch.
 *
 * Uses [PointerEventPass.Initial] to observe gestures without consuming or intercepting
 * child click/scroll events.
 *
 * @param modifier Modifier applied to the container.
 * @param enabled Whether the HDR touch effect is active.
 * @param content The UI content over which the touch effect is drawn.
 */
@Composable
fun HdrSparkleTouchContainer(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val activePointers = remember { mutableStateListOf<ActiveTouchPointer>() }
    var frameTick by remember { mutableStateOf(0L) }

    // Particle & animation tick loop - only runs when there are active touches or lingering sparkles
    LaunchedEffect(enabled, activePointers.size) {
        if (!enabled) return@LaunchedEffect
        var lastTime = withFrameMillis { it }
        while (activePointers.isNotEmpty()) {
            withFrameMillis { now ->
                val dt = ((now - lastTime) / 1000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                val iterator = activePointers.iterator()
                while (iterator.hasNext()) {
                    val pointer = iterator.next()

                    // Expand bloom radius smoothly toward target
                    if (pointer.isPressed) {
                        pointer.bloomRadius += (pointer.targetBloomRadius - pointer.bloomRadius) * 0.25f
                        pointer.energyWaveRadius = (pointer.energyWaveRadius + 160f * dt).coerceAtMost(pointer.targetBloomRadius * 1.6f)
                    } else {
                        // Dissipate on release
                        pointer.bloomRadius += (pointer.targetBloomRadius * 1.3f - pointer.bloomRadius) * 0.15f
                        pointer.alpha -= 0.075f
                        pointer.energyWaveRadius += 220f * dt
                    }

                    // Update existing sparkles
                    val sparkleIter = pointer.sparkles.iterator()
                    while (sparkleIter.hasNext()) {
                        val spark = sparkleIter.next()
                        spark.currentOffset += spark.velocity * dt
                        spark.rotation += spark.rotationSpeed * dt
                        spark.life -= spark.decayRate
                        spark.alpha = (spark.life * pointer.alpha).coerceIn(0f, 1f)
                        spark.currentRadius = spark.maxRadius * spark.life.coerceIn(0f, 1f)
                        if (spark.life <= 0f) {
                            sparkleIter.remove()
                        }
                    }

                    // Remove pointer if released and all effects have faded
                    if (!pointer.isPressed && (pointer.alpha <= 0.01f || pointer.sparkles.isEmpty() && pointer.alpha <= 0.05f)) {
                        iterator.remove()
                    }
                }
                frameTick++
            }
        }
    }

    Box(
        modifier = modifier
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        for (change in event.changes) {
                            handlePointerChange(change, activePointers)
                        }
                    }
                }
            }
            .drawWithContent {
                drawContent()
                // Render HDR light bloom, energy wave, and sparkles on top of content
                // Read frameTick to trigger redraws during animation
                @Suppress("UNUSED_VARIABLE")
                val tick = frameTick

                for (pointer in activePointers) {
                    drawHdrTouchEffect(pointer)
                }
            },
    ) {
        content()
    }
}

/**
 * Handles pointer events (down, move, up) and generates sparkle bursts.
 */
private fun handlePointerChange(
    change: PointerInputChange,
    activePointers: MutableList<ActiveTouchPointer>,
) {
    val existing = activePointers.find { it.id == change.id }

    if (change.pressed) {
        if (existing == null) {
            // New touch down: create pointer and spawn initial sparkle burst
            val pointer = ActiveTouchPointer(
                id = change.id,
                position = change.position,
                isPressed = true,
                bloomRadius = 40f,
                targetBloomRadius = 220f,
            )
            spawnSparkles(pointer.position, count = 12, pointer.sparkles)
            activePointers.add(pointer)
        } else {
            // Pointer moved: update position and spawn subtle trailing sparkles if moved significantly
            val distanceMoved = (change.position - existing.position).getDistance()
            existing.position = change.position
            existing.isPressed = true
            if (distanceMoved > 14f && existing.sparkles.size < 30) {
                spawnSparkles(existing.position, count = 3, existing.sparkles, speedMultiplier = 0.5f)
            }
        }
    } else {
        // Pointer up / released
        existing?.let {
            it.isPressed = false
            // Spawn a celebratory exit micro-burst
            spawnSparkles(it.position, count = 6, it.sparkles, speedMultiplier = 1.2f)
        }
    }
}

/**
 * Spawns a cluster of randomized sparkle particles around an origin.
 */
private fun spawnSparkles(
    origin: Offset,
    count: Int,
    destList: MutableList<SparkleParticle>,
    speedMultiplier: Float = 1.0f,
) {
    val colors = listOf(HdrCoreWhite, HdrGlowCyan, HdrGlowMagenta, HdrSparkleGold)
    for (i in 0 until count) {
        val angle = Random.nextFloat() * 2f * PI.toFloat()
        val speed = (Random.nextFloat() * 180f + 60f) * speedMultiplier
        val velocity = Offset(cos(angle) * speed, sin(angle) * speed)
        val initialOffset = origin + Offset(cos(angle) * 12f, sin(angle) * 12f)
        val size = Random.nextFloat() * 14f + 6f
        val color = colors[Random.nextInt(colors.size)]

        destList.add(
            SparkleParticle(
                currentOffset = initialOffset,
                velocity = velocity,
                maxRadius = size,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 360f,
                color = color,
            ),
        )
    }
}

/**
 * Renders the HDR bloom, expanding energy wavefront, and sparkling stars for a single pointer.
 */
private fun DrawScope.drawHdrTouchEffect(pointer: ActiveTouchPointer) {
    val alpha = pointer.alpha.coerceIn(0f, 1f)
    if (alpha <= 0.005f) return

    val center = pointer.position
    val radius = pointer.bloomRadius.coerceAtLeast(10f)

    // 1. Expanding energy wavefront (simulates light skimming across surrounding button edges)
    val waveRadius = pointer.energyWaveRadius
    if (waveRadius > 10f && waveRadius < radius * 2.0f) {
        val waveAlpha = ((1.0f - waveRadius / (radius * 2.0f)) * 0.45f * alpha).coerceIn(0f, 1f)
        drawCircle(
            color = Color(1.2f, 2.4f, 2.8f, waveAlpha, ColorSpaces.ExtendedSrgb),
            center = center,
            radius = waveRadius,
            style = Stroke(width = 2.5.dp.toPx()),
        )
    }

    // 2. HDR Radial Bloom light source (CL 4034508 AndroidShader @ColorLong)
    val bloomColors = listOf(
        Color(3.0f, 3.0f, 3.2f, 0.85f * alpha, ColorSpaces.ExtendedSrgb), // Piercing HDR white core
        Color(0.8f, 2.2f, 2.8f, 0.45f * alpha, ColorSpaces.ExtendedSrgb), // Vivid cyan inner bloom
        Color(2.2f, 0.4f, 1.8f, 0.20f * alpha, ColorSpaces.ExtendedSrgb), // Magenta halo falloff
        HdrTransparentCyan,                                                // Transparent edge (matching ColorSpace!)
    )

    val bloomBrush = Brush.radialGradient(
        colors = bloomColors,
        center = center,
        radius = radius,
    )

    drawCircle(
        brush = bloomBrush,
        center = center,
        radius = radius,
    )

    // 3. Inner high-intensity light core
    drawCircle(
        color = Color(3.5f, 3.5f, 3.5f, 0.9f * alpha, ColorSpaces.ExtendedSrgb),
        center = center,
        radius = 8.dp.toPx(),
    )

    // 4. Sparkle particles (4-pointed diamond stars)
    val starPath = Path()
    for (sparkle in pointer.sparkles) {
        val sparkAlpha = sparkle.alpha.coerceIn(0f, 1f)
        if (sparkAlpha <= 0.01f) continue

        starPath.reset()
        starPath.addFourPointStar(
            center = sparkle.currentOffset,
            outerRadius = sparkle.currentRadius,
            innerRadius = sparkle.currentRadius * 0.25f,
            rotationDeg = sparkle.rotation,
        )

        // Draw star sparkle with HDR color
        drawPath(
            path = starPath,
            color = sparkle.color.copy(alpha = sparkAlpha),
        )

        // Tiny piercing center glint
        drawCircle(
            color = HdrCoreWhite.copy(alpha = sparkAlpha),
            center = sparkle.currentOffset,
            radius = (sparkle.currentRadius * 0.18f).coerceAtLeast(1.5f),
        )
    }
}

/**
 * Builds a 4-pointed star path centered at [center].
 */
private fun Path.addFourPointStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    rotationDeg: Float,
) {
    val rad = Math.toRadians(rotationDeg.toDouble()).toFloat()
    for (i in 0 until 4) {
        val outerAngle = rad + i * (PI.toFloat() / 2f)
        val innerAngle = outerAngle + (PI.toFloat() / 4f)

        val ox = center.x + cos(outerAngle) * outerRadius
        val oy = center.y + sin(outerAngle) * outerRadius
        val ix = center.x + cos(innerAngle) * innerRadius
        val iy = center.y + sin(innerAngle) * innerRadius

        if (i == 0) {
            moveTo(ox, oy)
        } else {
            lineTo(ox, oy)
        }
        lineTo(ix, iy)
    }
    close()
}
