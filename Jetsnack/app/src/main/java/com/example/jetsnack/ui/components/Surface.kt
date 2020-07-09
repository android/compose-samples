package com.example.jetsnack.ui.components

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.drawShadow
import androidx.ui.core.zIndex
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentColorAmbient
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.drawBorder
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.Shape
import androidx.ui.graphics.compositeOver
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme
import kotlin.math.ln

/**
 * An alternative to [androidx.ui.material.Surface] utilizing [com.example.jetsnack.ui.theme.JetsnackColorPalette]
 */
@Composable
fun JetsnackSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textPrimary,
    border: Border? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.drawShadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(elevation.value)
            + (if (border != null) Modifier.drawBorder(border, shape) else Modifier)
            .drawBackground(
                color = getBackgroundColorForElevation(color, elevation),
                shape = shape
            )
            .clip(shape)
    ) {
        Providers(ContentColorAmbient provides contentColor, children = content)
    }
}

@Composable
private fun getBackgroundColorForElevation(color: Color, elevation: Dp): Color {
    return if (elevation > 0.dp //&& https://issuetracker.google.com/issues/161429530
    //JetsnackTheme.colors.isDark //&&
    //color == JetsnackTheme.colors.uiBackground
    ) {
        color.withElevation(elevation)
    } else {
        color
    }
}

/**
 * Applies a [Color.White] overlay to this color based on the [elevation]. This increases visibility
 * of elevation for surfaces in a dark theme.
 */
private fun Color.withElevation(elevation: Dp): Color {
    val foreground = calculateForeground(elevation)
    return foreground.compositeOver(this)
}

// TODO: b/145802792 - clarify this algorithm
/**
 * @return the alpha-modified [Color.White] to overlay on top of the surface color to produce
 * the resultant color.
 */
private fun calculateForeground(elevation: Dp): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return Color.White.copy(alpha = alpha)
}
