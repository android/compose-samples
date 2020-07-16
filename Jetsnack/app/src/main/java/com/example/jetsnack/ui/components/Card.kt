package com.example.jetsnack.ui.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textPrimary,
    border: Border? = null,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    JetsnackSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}