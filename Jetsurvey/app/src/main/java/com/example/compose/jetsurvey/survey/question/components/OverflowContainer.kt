package com.example.compose.jetsurvey.survey.question.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection

/**
 * Custom layout which allow content to be placed linearly and wraps when overflow
 */
@Composable
fun OverflowContainer(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // TODO: replace the LayoutDirection hardcoding
    val paddingRight = contentPadding.calculateRightPadding(LayoutDirection.Ltr).value.toInt()
    val paddingLeft = contentPadding.calculateLeftPadding(LayoutDirection.Ltr).value.toInt()
    val paddingTop = contentPadding.calculateTopPadding().value.toInt()
    val paddingBottom = contentPadding.calculateBottomPadding().value.toInt()

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val placeablePositions = mutableListOf<IntOffset>()
        var xPosition = paddingLeft
        var yPosition = paddingTop
        var maxPlacableHeightInRow = 0
        placeables.forEach { placeable ->
            if (xPosition + placeable.width + paddingRight + paddingLeft >= constraints.maxWidth) {
                xPosition = paddingLeft
                yPosition += maxPlacableHeightInRow + paddingTop + paddingBottom
                maxPlacableHeightInRow = 0
            }
            if (placeable.height > maxPlacableHeightInRow) maxPlacableHeightInRow = placeable.height
            placeablePositions.add(IntOffset(xPosition, yPosition))
            xPosition += placeable.width + paddingRight + paddingLeft
        }

        val layoutHeight = yPosition + maxPlacableHeightInRow + paddingBottom
        layout(constraints.maxWidth, layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(x = placeablePositions[index].x, y = placeablePositions[index].y)
            }
        }
    }
}
