package com.example.compose.jetchat.conversation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun rememberMeshBackgroundGradientPainter(): MeshGradientPainter {
    return remember {
        MeshGradientPainter(
            rows = 3,
            columns = 3,
            hasBicubicColor = true,
        ) {
            // Row 0 (top edge, y = 0.0f)
            setVertex(0, 0, Offset(0.0000f, 0.0000f), Color(0xFFF0FCB0))
            setVertex(0, 1, Offset(0.3300f, 0.0000f), Color(0xFFF0FCB0))
            setVertex(0, 2, Offset(0.6700f, 0.0000f), Color(0xFFDCFA51))
            setVertex(0, 3, Offset(1.0000f, 0.0000f), Color(0xFFF0FCB0))

            // Row 1 (upper-mid, y ~ 0.21f - 0.43f)
            setVertex(1, 0, Offset(0.0000f, 0.3300f), Color(0xFFDCFA51))
            setVertex(1, 1, Offset(0.3154f, 0.2157f), Color(0xFF63CEAD))
            setVertex(1, 2, Offset(0.6773f, 0.4324f), Color(0xFF80B259))
            setVertex(1, 3, Offset(1.0534f, 0.2919f), Color(0xFF80B259))

            // Row 2 (lower-mid, y ~ 0.62f - 0.87f)
            setVertex(2, 0, Offset(0.0000f, 0.6700f), Color(0xFF63CEAD))
            setVertex(2, 1, Offset(0.2936f, 0.6260f), Color(0xFF63CEAD))
            setVertex(2, 2, Offset(0.6603f, 0.8748f), Color(0xFF43B55F))
            setVertex(2, 3, Offset(1.1238f, 0.6736f), Color(0xFF43B55F))

            // Row 3 (bottom edge, y = 1.0f)
            setVertex(3, 0, Offset(0.0000f, 1.0000f), Color(0xFF05D6A1))
            setVertex(3, 1, Offset(0.3300f, 1.0000f), Color(0xFF1AB2A6))
            setVertex(3, 2, Offset(0.6700f, 1.0000f), Color(0xFF43B55F))
            setVertex(3, 3, Offset(1.0000f, 1.0000f), Color(0xFF43B55F))
        }
    }
}

/**
 * Fullscreen container applying the 4x4 mesh gradient background.
 */
@Composable
fun MeshBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val meshPainter = rememberMeshBackgroundGradientPainter()
    Box(
        modifier = modifier
            .fillMaxSize()
            .paint(meshPainter),
    ) {
        content()
    }
}