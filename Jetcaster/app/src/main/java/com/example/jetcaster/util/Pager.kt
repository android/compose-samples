/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.util

import androidx.animation.AnimationClockObservable
import androidx.animation.fling
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.structuralEqualityPolicy
import androidx.ui.animation.AnimatedFloatModel
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.Modifier
import androidx.ui.core.ParentDataModifier
import androidx.ui.core.drawWithContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.draggable
import androidx.ui.graphics.drawscope.scale
import androidx.ui.unit.Density
import androidx.ui.util.lerp
import kotlin.math.roundToInt

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */

class PagerState(
    clock: AnimationClockObservable,
    currentPage: Int = 0,
    minPage: Int = 0,
    maxPage: Int = 0
) {
    private var _minPage by mutableStateOf(minPage)
    var minPage: Int
        get() = _minPage
        set(value) {
            _minPage = value.coerceAtMost(_maxPage)
            _currentPage = _currentPage.coerceIn(_minPage, _maxPage)
        }

    private var _maxPage by mutableStateOf(maxPage, structuralEqualityPolicy())
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(_minPage)
            _currentPage = _currentPage.coerceIn(_minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    inline fun <R> selectPage(block: PagerState.() -> R): R = try {
        selectionState = SelectionState.Undecided
        block()
    } finally {
        selectPage()
    }

    fun selectPage() {
        currentPage -= currentPageOffset.roundToInt()
        currentPageOffset = 0f
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset = AnimatedFloatModel(0f, clock = clock).apply {
        setBounds(-1f, 1f)
    }
    var currentPageOffset: Float
        get() = _currentPageOffset.value
        set(value) {
            val max = if (currentPage == minPage) 0f else 1f
            val min = if (currentPage == maxPage) 0f else -1f
            _currentPageOffset.snapTo(value.coerceIn(min, max))
        }

    fun fling(velocity: Float) {
        if (velocity < 0 && currentPage == maxPage) return
        if (velocity > 0 && currentPage == minPage) return

        _currentPageOffset.fling(velocity) { _, _, _ ->
            selectPage()
        }
    }

    override fun toString(): String = "PagerState{minPage=$minPage, maxPage=$maxPage, " +
        "currentPage=$currentPage, currentPageOffset=$currentPageOffset}"
}

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
fun Pager(
    state: PagerState,
    offscreenLimit: Int = 2,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.() -> Unit
) {
    var pageSize by state { 0 }
    Layout(
        children = {
            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Box(gravity = ContentGravity.Center, modifier = pageData) {
                        scope.pageContent()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            dragDirection = DragDirection.Horizontal,
            onDragStarted = {
                state.selectionState = PagerState.SelectionState.Undecided
            },
            onDragStopped = { velocity ->
                // Velocity is in pixels per second, but we deal in percentage offsets, so we
                // need to scale the velocity to match
                state.fling(velocity / pageSize)
            }
        ) { dy ->
            with(state) {
                val pos = pageSize * currentPageOffset
                val max = if (currentPage == minPage) 0 else pageSize * offscreenLimit
                val min = if (currentPage == maxPage) 0 else -pageSize * offscreenLimit
                val newPos = (pos + dy).coerceIn(min.toFloat(), max.toFloat())
                currentPageOffset = newPos / pageSize
            }
            dy
        }
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentPageOffset
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            measurables
                .map {
                    it.measure(childConstraints) to it.page
                }
                .forEach { (placeable, page) ->
                    // TODO: current this centers each page. We should investigate reading
                    //  gravity modifiers on the child, or maybe as a param to Pager.
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2

                    if (currentPage == page) {
                        pageSize = placeable.width
                    }

                    placeable.place(
                        x = xCenterOffset + ((page - (currentPage - offset)) * placeable.width).roundToInt(),
                        y = yCenterOffset
                    )
                }
        }
    }
}

/**
 * Scope for [Pager] content.
 */
class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    /**
     * Returns the current selected page
     */
    val currentPage: Int
        get() = state.currentPage

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
        get() = state.currentPageOffset

    /**
     * Returns the current selection state
     */
    val selectionState: PagerState.SelectionState
        get() = state.selectionState

    /**
     * Modifier which scales pager items according to their offset position. Similar in effect
     * to a carousel.
     */
    fun Modifier.scalePagerItems(
        unselectedScale: Float
    ): Modifier = Modifier.drawWithContent {
        if (selectionState == PagerState.SelectionState.Selected) {
            // If the pager is 'selected', it's stationary so we use a simple if check
            if (page != currentPage) {
                scale(
                    scaleX = unselectedScale,
                    scaleY = unselectedScale,
                    pivotX = center.x,
                    pivotY = center.y
                ) {
                    this@drawWithContent.drawContent()
                }
            } else {
                drawContent()
            }
        } else {
            // Otherwise the pager is being scrolled, so we need to look at the swipe progress
            // and interpolate between the sizes
            val offsetForPage = page - currentPage + currentPageOffset

            val scale = if (offsetForPage < 0) {
                // If the page is to the left of the current page, we scale from min -> 1f
                lerp(
                    start = unselectedScale,
                    stop = 1f,
                    fraction = (1f + offsetForPage).coerceIn(0f, 1f)
                )
            } else {
                // If the page is to the right of the current page, we scale from 1f -> min
                lerp(
                    start = 1f,
                    stop = unselectedScale,
                    fraction = offsetForPage.coerceIn(0f, 1f)
                )
            }
            scale(scale, scale, center.x, center.y) {
                this@drawWithContent.drawContent()
            }
        }
    }
}
