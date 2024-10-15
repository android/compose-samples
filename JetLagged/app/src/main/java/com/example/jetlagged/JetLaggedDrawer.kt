/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetlagged

import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.launch

@Composable
fun HomeScreenDrawer(windowSizeClass: WindowSizeClass) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        var drawerState by remember {
            mutableStateOf(DrawerState.Closed)
        }
        var screenState by remember {
            mutableStateOf(Screen.Home)
        }

        val translationX = remember {
            Animatable(0f)
        }

        val drawerWidth = with(LocalDensity.current) {
            DrawerWidth.toPx()
        }
        translationX.updateBounds(0f, drawerWidth)

        val coroutineScope = rememberCoroutineScope()

        suspend fun closeDrawer(velocity: Float = 0f) {
            translationX.animateTo(targetValue = 0f, initialVelocity = velocity)
            drawerState = DrawerState.Closed
        }
        suspend fun openDrawer(velocity: Float = 0f) {
            translationX.animateTo(targetValue = drawerWidth, initialVelocity = velocity)
            drawerState = DrawerState.Open
        }
        fun toggleDrawerState() {
            coroutineScope.launch {
                if (drawerState == DrawerState.Open) {
                    closeDrawer()
                } else {
                    openDrawer()
                }
            }
        }
        val velocityTracker = remember {
            VelocityTracker()
        }
        PredictiveBackHandler(drawerState == DrawerState.Open) { progress ->
            try {
                progress.collect { backEvent ->
                    val targetSize = (drawerWidth - (drawerWidth * backEvent.progress))
                    translationX.snapTo(targetSize)
                    velocityTracker.addPosition(
                        SystemClock.uptimeMillis(),
                        Offset(backEvent.touchX, backEvent.touchY)
                    )
                }
                closeDrawer(velocityTracker.calculateVelocity().x)
            } catch (e: CancellationException) {
                openDrawer(velocityTracker.calculateVelocity().x)
            }
            velocityTracker.resetTracking()
        }

        HomeScreenDrawerContents(
            selectedScreen = screenState,
            onScreenSelected = { screen ->
                screenState = screen
            }
        )

        val draggableState = rememberDraggableState(onDelta = { dragAmount ->
            coroutineScope.launch {
                translationX.snapTo(translationX.value + dragAmount)
            }
        })
        val decay = rememberSplineBasedDecay<Float>()
        ScreenContents(
            windowWidthSizeClass = windowSizeClass.widthSizeClass,
            selectedScreen = screenState,
            onDrawerClicked = ::toggleDrawerState,
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale = lerp(1f, 0.8f, translationX.value / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale
                    val roundedCorners = lerp(0f, 32.dp.toPx(), translationX.value / drawerWidth)
                    this.shape = RoundedCornerShape(roundedCorners)
                    this.clip = true
                    this.shadowElevation = 32f
                }
                // This example is showing how to use draggable with custom logic on stop to snap to the edges
                // You can also use `anchoredDraggable()` to set up anchors and not need to worry about more calculations.
                .draggable(
                    draggableState, Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val targetOffsetX = decay.calculateTargetValue(
                            translationX.value,
                            velocity
                        )
                        coroutineScope.launch {
                            val actualTargetX = if (targetOffsetX > drawerWidth * 0.5) {
                                drawerWidth
                            } else {
                                0f
                            }
                            // checking if the difference between the target and actual is + or -
                            val targetDifference = (actualTargetX - targetOffsetX)
                            val canReachTargetWithDecay =
                                (
                                    targetOffsetX > actualTargetX && velocity > 0f &&
                                        targetDifference > 0f
                                    ) ||
                                    (
                                        targetOffsetX < actualTargetX && velocity < 0 &&
                                            targetDifference < 0f
                                        )
                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity,
                                    animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(actualTargetX, initialVelocity = velocity)
                            }
                            drawerState = if (actualTargetX == drawerWidth) {
                                DrawerState.Open
                            } else {
                                DrawerState.Closed
                            }
                        }
                    }
                )
        )
    }
}

@Composable
private fun ScreenContents(
    windowWidthSizeClass: WindowWidthSizeClass,
    selectedScreen: Screen,
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        when (selectedScreen) {
            Screen.Home ->
                JetLaggedScreen(
                    windowSizeClass = windowWidthSizeClass,
                    modifier = Modifier,
                    onDrawerClicked = onDrawerClicked
                )

            Screen.SleepDetails ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }

            Screen.Leaderboard ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }

            Screen.Settings ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                }
        }
    }
}

private enum class DrawerState {
    Open,
    Closed
}

@Composable
private fun HomeScreenDrawerContents(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Screen.entries.forEach {
            NavigationDrawerItem(
                label = {
                    Text(it.text)
                },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.text)
                },
                selected = selectedScreen == it,
                onClick = {
                    onScreenSelected(it)
                },
            )
        }
    }
}

private val DrawerWidth = 300.dp

private enum class Screen(val text: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    SleepDetails("Sleep", Icons.Default.Bedtime),
    Leaderboard("Leaderboard", Icons.Default.Leaderboard),
    Settings("Settings", Icons.Default.Settings),
}
