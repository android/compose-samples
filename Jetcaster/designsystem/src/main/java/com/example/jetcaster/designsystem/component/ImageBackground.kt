package com.example.jetcaster.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageBackgroundColorScrim(
    url: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        url = url,
        modifier = modifier,
        overlay = {
            drawRect(color, blendMode = BlendMode.Multiply)
        }
    )
}

@Composable
fun ImageBackgroundRadialGradientScrim(
    url: String?,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        url = url,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset(0f, size.height),
                radius = size.width * 1.5f
            )
            drawRect(brush, blendMode = BlendMode.Multiply)
        }
    )
}

/**
 * Displays an image scaled 150% overlaid by [overlay]
 */
@Composable
fun ImageBackground(
    url: String?,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}
