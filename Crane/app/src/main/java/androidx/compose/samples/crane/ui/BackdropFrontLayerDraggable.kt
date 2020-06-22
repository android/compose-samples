/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.ui

import androidx.animation.PhysicsBuilder
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Constraints
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.onPositioned
import androidx.ui.foundation.Box
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.offset
import androidx.ui.layout.preferredSizeIn
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.IntSize
import androidx.ui.unit.dp

enum class FullScreenState {
    MINIMISED,
    COLLAPSED,
    EXPANDED,
}

class DraggableBackdropState(value: FullScreenState = FullScreenState.MINIMISED) {
    var value by mutableStateOf(value)
}

@Composable
fun BackdropFrontLayerDraggable(
    modifier: Modifier = Modifier,
    backdropState: DraggableBackdropState = remember { DraggableBackdropState() },
    staticChildren: @Composable (Modifier) -> Unit,
    backdropChildren: @Composable (Modifier) -> Unit
) {
    var backgroundChildrenSize by state { IntSize(0, 0) }

    Box(modifier) {
        WithConstraints {
            val fullHeight = constraints.maxHeight.toFloat()
            val anchors = remember(backgroundChildrenSize.height) {
                getAnchors(backgroundChildrenSize, fullHeight)
            }

            CraneStateDraggable(
                state = backdropState.value,
                onStateChange = { newExploreState -> backdropState.value = newExploreState },
                anchorsToState = anchors,
                animationBuilder = AnimationBuilder,
                dragDirection = DragDirection.Vertical,
                minValue = VerticalExplorePadding,
                maxValue = fullHeight,
                enabled = true
            ) { model ->
                Stack {
                    staticChildren(Modifier.onPositioned { coordinates ->
                        if (backgroundChildrenSize.height == 0) {
                            backdropState.value = FullScreenState.COLLAPSED
                        }
                        if (backgroundChildrenSize != coordinates.size) {
                            backgroundChildrenSize = coordinates.size
                        }
                    })

                    val shadowColor = MaterialTheme.colors.surface.copy(alpha = 0.8f)
                    val revealValue = backgroundChildrenSize.height / 2
                    if (model.value < revealValue) {
                        Canvas(Modifier.fillMaxSize()) {
                            drawRect(size = size, color = shadowColor)
                        }
                    }

                    val yOffset = with(DensityAmbient.current) {
                        model.value.toDp()
                    }

                    backdropChildren(
                        Modifier.offset(0.dp, yOffset)
                            .preferredSizeIn(currentConstraints(constraints))
                    )
                }
            }
        }
    }
}

private const val ANCHOR_BOTTOM_OFFSET = 130f

private fun getAnchors(
    searchChildrenSize: IntSize,
    fullHeight: Float
): List<Pair<Float, FullScreenState>> {
    val mediumValue = searchChildrenSize.height + 50.dp.value
    val maxValue = fullHeight - ANCHOR_BOTTOM_OFFSET
    return listOf(
        0f to FullScreenState.EXPANDED,
        mediumValue to FullScreenState.COLLAPSED,
        maxValue to FullScreenState.MINIMISED
    )
}

@Composable
private fun currentConstraints(pxConstraints: Constraints): DpConstraints {
    return with(DensityAmbient.current) {
        DpConstraints(pxConstraints)
    }
}

private val AnimationBuilder = PhysicsBuilder<Float>().apply { stiffness = ExploreStiffness }
private const val ExploreStiffness = 1000f
private const val VerticalExplorePadding = 0f
