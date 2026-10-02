package com.example.compose.jetchat

import androidx.compose.foundation.shape.CornerRounding
import androidx.compose.foundation.shape.MorphPolygonShape
import androidx.compose.foundation.shape.PolygonShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertNotNull
import org.junit.Test

class PolygonShapeReproTest {

    /**
     * Demonstrates the bug: using `transform { rotate(-90f) }` (or `scaleToFit`) on a layout-scale
     * PolygonShape invokes `RoundedPolygon.transformed`, which triggers floating-point coordinate drift
     * in pixel space (> 1000px) and causes `RoundedPolygon.<init>` to throw IllegalArgumentException
     * ("RoundedPolygon must be contiguous...").
     */
    @Test
    fun testContiguousBugWithTransform() {
        val shape = PolygonShape.star(
            numPoints = 9,
            innerRadiusRatio = 0.8f,
            outerRounding = CornerRounding.fraction(0.5f),
        ).apply {
            transform {
                rotate(-90f)
            }
        }

        val exception = org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            shape.createOutline(
                size = Size(1080f, 2400f),
                layoutDirection = LayoutDirection.Ltr,
                density = Density(2.75f),
            )
        }
        org.junit.Assert.assertTrue(
            "Expected 'must be contiguous' error message, but got: ${exception.message}",
            exception.message?.contains("must be contiguous") == true,
        )
    }

    /**
     * Tests PolygonShape.star without any transform.
     */
    @Test
    fun testWithoutContentScaleSucceeds() {
        val shape = PolygonShape.star(
            numPoints = 9,
            innerRadiusRatio = 0.8f,
            outerRounding = CornerRounding.fraction(0.5f),
        )

        val outline = shape.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(outline)
    }

    /**
     * Demonstrates that `MorphPolygonShape` between the cookie shape and the rounded rectangle
     * succeeds in the snapshot version when no transform is applied.
     */
    @Test
    fun testMorphPolygonShapeSucceeds() {
        val star = PolygonShape.star(
            numPoints = 9,
            innerRadiusRatio = 0.8f,
            outerRounding = CornerRounding.fraction(0.5f),
        )

        val rect = PolygonShape.rectangle(
            topStartRounding = CornerRounding.fraction(0.15f),
            topEndRounding = CornerRounding.fraction(0.15f),
            bottomEndRounding = CornerRounding.Unrounded,
            bottomStartRounding = CornerRounding.Unrounded,
        )

        val morph = MorphPolygonShape(start = star, end = rect) { 0.5f }
        val outline = morph.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(outline)
    }

    @Test
    fun testFullScreenRoundedRectangleResolution() {
        val outline = com.example.compose.jetchat.theme.FullScreenRoundedRectangle.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(outline)
    }

    @Test
    fun testCookie9SidedResolution() {
        val outline = com.example.compose.jetchat.theme.Cookie9Sided.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(outline)
    }

    @Test
    fun testRotatedStarUsingVerticesWorkaround() {
        val rotatedShape = PolygonShape {
            val numPoints = 9
            val radius = size.minDimension / 2f
            val innerRadius = radius * 0.8f
            val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
            val vertices = mutableListOf<androidx.compose.ui.geometry.Offset>()
            val angleOffset = -kotlin.math.PI.toFloat() / 2f
            val step = (2f * kotlin.math.PI.toFloat()) / numPoints
            for (i in 0 until numPoints) {
                val aOuter = angleOffset + step * i
                vertices.add(androidx.compose.ui.geometry.Offset(center.x + radius * kotlin.math.cos(aOuter), center.y + radius * kotlin.math.sin(aOuter)))
                val aInner = angleOffset + step * (i + 0.5f)
                vertices.add(androidx.compose.ui.geometry.Offset(center.x + innerRadius * kotlin.math.cos(aInner), center.y + innerRadius * kotlin.math.sin(aInner)))
            }
            polygon(
                vertices = vertices,
                center = center,
                rounding = CornerRounding.fraction(0.5f),
            )
        }

        val outline = rotatedShape.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(outline)

        val morph = MorphPolygonShape(rotatedShape, com.example.compose.jetchat.theme.FullScreenRoundedRectangle) { 0.5f }
        val morphOutline = morph.createOutline(
            size = Size(1080f, 2400f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2.75f),
        )
        assertNotNull(morphOutline)
    }
}
