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

package com.example.jetnews.ui

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.swipeUp
import androidx.ui.test.swipeDown
import androidx.ui.test.performGesture
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test


@MediumTest
class SwipeToRefreshLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Test
    fun testSwipeToRefreshGestureWithScrollableColumn() {
        var refreshInvoked = false
        renderScrollableColumnWithSwipeToRefreshLayout(onRefresh = {
            refreshInvoked = true
        })

        swipeToRefresh()

        assertTrue(refreshInvoked)
    }

    @Test
    fun testSwipeToRefreshGestureWithLazyColumnFor() {
        var refreshInvoked = false
        renderLazyColumnForWithSwipeToRefreshLayout(onRefresh = {
            refreshInvoked = true
        })

        swipeToRefresh()

        assertTrue(refreshInvoked)
    }

    @Test
    fun testSwipeToRefreshGestureWithLazyColumnForIndexed() {
        var refreshInvoked = false
        renderLazyColumnForIndexedWithSwipeToRefreshLayout(onRefresh = {
            refreshInvoked = true
        })

        swipeToRefresh()

        assertTrue(refreshInvoked)
    }

    private fun swipeToRefresh() {
        composeTestRule.onNodeWithTag(swipeTestTag).performGesture {
            swipeDown()
            swipeUp()
        }
    }

    private fun renderScrollableColumnWithSwipeToRefreshLayout(onRefresh: () -> Unit) {
        composeTestRule.setContent {
            CommonSwipeToRefreshLayout(onRefresh = onRefresh) {
                ScrollableColumn {
                    (0 until 10).forEach {
                        Text(text = "Item $it")
                    }
                }
            }
        }
    }

    private fun renderLazyColumnForIndexedWithSwipeToRefreshLayout(onRefresh: () -> Unit) {
        composeTestRule.setContent {
            CommonSwipeToRefreshLayout(onRefresh = onRefresh) {
                val items = (0 until 10).toList()
                LazyColumnForIndexed(items) { _, item ->
                    Text(text = "Item $item")
                }
            }
        }
    }

    private fun renderLazyColumnForWithSwipeToRefreshLayout(onRefresh: () -> Unit) {
        composeTestRule.setContent {
            CommonSwipeToRefreshLayout(onRefresh = onRefresh) {
                val items = (0 until 10).toList()
                LazyColumnFor(items) { item ->
                    Text(text = "Item $item")
                }
            }
        }
    }

    @Composable
    private fun CommonSwipeToRefreshLayout(
        onRefresh: () -> Unit,
        content: @Composable() () -> Unit
    ) {
        SwipeToRefreshLayout(
            refreshingState = false,
            onRefresh = onRefresh,
            refreshIndicator = { RefreshIndicator() },
            content = {
                content()
            }
        )
    }

    @Composable
    private fun RefreshIndicator() {
        Surface(elevation = 10.dp, shape = CircleShape) {
            CircularProgressIndicator(
                modifier = Modifier
                    .preferredSize(36.dp)
                    .padding(4.dp)
            )
        }
    }
}