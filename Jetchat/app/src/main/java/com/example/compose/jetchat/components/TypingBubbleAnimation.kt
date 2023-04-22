import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun TypingBubbleAnimation(modifier: Modifier = Modifier) {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.primary

    // animate index of highlighted bubble (goes up to 4 to add slight pause between repeats)
    val transition = rememberInfiniteTransition()
    val bubbleIndex by transition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing)
        )
    )

    val baseDiameter = 10.dp

    Row(modifier.fillMaxWidth(0.8f)) {
        repeat(3) { index ->
            // controls bubble squished-ness
            val heightPct = if (index == bubbleIndex) 0.6f else 1f
            val widthPct = if (index == bubbleIndex) 1.1f else 1f

            val height by animateDpAsState(baseDiameter * heightPct)
            val width by animateDpAsState(baseDiameter * widthPct)

            val color by animateColorAsState(if (index == bubbleIndex) highlightColor else baseColor)

            TypeBubble(color = { color }, size = { DpSize(width, height) }, baseSize = baseDiameter)
        }
    }
}

@Composable
fun RowScope.TypeBubble(color: () -> Color, size: () -> DpSize, baseSize: Dp) {
    Box(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.Bottom)
            .drawBehind {
                val sizePx = size().toSize()
                val heightPx = sizePx.height

                // update y coordinate so bubble gets squished from the top, not the bottom
                drawOval(color(), Offset(center.x, center.y + (baseSize.toPx() - heightPx)), sizePx)
            }
    )
}