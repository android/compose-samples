package com.example.compose.jetsurvey.survey.question.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center

class DragTargetInfo<T> {
    var isDragging: Boolean by mutableStateOf(false)
    var hasDragEnd: Boolean by mutableStateOf(false)
    var hasDragEndInAnyDropZone: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable (Boolean) -> Unit)?>(null)
    var dataToTransfer by mutableStateOf<T?>(null)
}

val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo<Any?>() }

@Composable
fun Draggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo<Any?>() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(modifier = modifier.fillMaxSize())
        {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                var currentPosition by remember {
                    mutableStateOf(Offset.Zero)
                }
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        currentPosition = it.windowToLocal(state.dragPosition)
                        targetSize = it.size
                    }
                    .graphicsLayer {
                        val offset = (currentPosition + state.dragOffset)
                        alpha = if (targetSize == IntSize.Zero) 0f else 1f
                        translationX = offset.x.minus(targetSize.center.x)
                        translationY = offset.y.minus(targetSize.center.y)
                    }
                ) {
                    state.draggableComposable?.invoke(false)
                }
            }
        }
    }
}

@Composable
fun <T> DragTarget(
    onItemDropped: (T) -> Unit,
    item: T,
    modifier: Modifier = Modifier,
    content: @Composable ((isDragging: Boolean) -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val state = LocalDragTargetInfo.current

    // Local dragging states
    var isItemDragging by remember { mutableStateOf(false) }
    var hasItemDraggingEnd by remember { mutableStateOf(false) }

    // Check if any item dropped and this is the item
    if (state.hasDragEndInAnyDropZone && hasItemDraggingEnd) {
        hasItemDraggingEnd = false
        state.hasDragEndInAnyDropZone = false
        onItemDropped(item)
    }

    if (isItemDragging) {
        state.dataToTransfer = item
    }

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.positionInWindow()
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    isItemDragging = true
                    state.isDragging = true
                    state.dragPosition = currentPosition + it
                    state.draggableComposable = content
                    state.hasDragEnd = false

                }, onDrag = { change, dragAmount ->
                    change.consume()
                    state.dragOffset += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    state.hasDragEnd = true
                    state.isDragging = false
                    state.dragOffset = Offset.Zero
                    isItemDragging = false
                    hasItemDraggingEnd = true
                }, onDragCancel = {
                    state.dragOffset = Offset.Zero
                    state.isDragging = false
                    isItemDragging = false
                }
            )
        }
    ) {
        content(isItemDragging)
    }
}

@Composable
fun DropZone(
    modifier: Modifier = Modifier,
    isDropZone: Boolean = true,
    content: @Composable BoxScope.(isDraggingInDropZone: Boolean, hasDragEndInDropZone: Boolean) -> Unit
) {
    val state = LocalDragTargetInfo.current
    val dragPosition = state.dragPosition
    val dragOffset = state.dragOffset
    var isItemInTheDropZone by remember { mutableStateOf(false) }

    val hasDragEndInDropZone by remember {
        derivedStateOf { isItemInTheDropZone && state.hasDragEnd }
    }
    if (hasDragEndInDropZone) {
        state.hasDragEndInAnyDropZone = true
    }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInWindow().let { rect ->
                    if (isDropZone) {
                        //TODO check for bug when drops at the edge of zone
                        isItemInTheDropZone = rect.contains(dragPosition + dragOffset)
                                && !rect.contains(dragPosition)
                    }
                }
            }
    ) {
        content(
            isDraggingInDropZone = isItemInTheDropZone,
            hasDragEndInDropZone = hasDragEndInDropZone,
        )
    }
}
