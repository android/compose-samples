/*
 * Copyright 2022 The Android Open Source Project
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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.view.WindowCompat
import com.example.jetlagged.ui.theme.JetLaggedTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            JetLaggedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    val draggedX = remember {
                        Animatable(0f)
                    }
                    val velocityTracker = VelocityTracker()
                    val drawerWidth = with (LocalDensity.current) {
                        300.dp.toPx()
                    }
                    draggedX.updateBounds(0f, drawerWidth)
                    val progress = draggedX.value / drawerWidth
                    var drawerState by remember {
                        mutableStateOf(DrawerState.Closed)
                    }
                    val coroutineScope = rememberCoroutineScope()

                    fun animateToState(drawerState: DrawerState, velocity: Float) {
                        coroutineScope.launch {
                            when (drawerState) {
                                DrawerState.Open -> draggedX.animateTo(drawerWidth, initialVelocity = velocity)
                                DrawerState.Closed -> draggedX.animateTo(0f, initialVelocity = velocity)
                            }
                        }
                    }

                    val roundedCorners = 16.dp

                    HomeScreenCustomDrawer()
                    JetLaggedScreen(modifier = Modifier
                        .pointerInput(Unit){
                            val decay = splineBasedDecay<Float>(this)
                            detectHorizontalDragGestures(onDragEnd = {
                                val velocity = velocityTracker.calculateVelocity().x
                                val targetOffsetX = decay.calculateTargetValue(
                                    draggedX.value,
                                    velocity
                                )
                                coroutineScope.launch {
                                    if (targetOffsetX.absoluteValue <= size.width * 0.5f) {
                                        animateToState(DrawerState.Closed, velocity)
                                    } else {
                                        animateToState(DrawerState.Open, velocity)
                                    }
                                }
                            }) { change, dragAmount ->
                                coroutineScope.launch {
                                    draggedX.snapTo(draggedX.value + dragAmount)
                                    velocityTracker.addPosition(
                                        change.uptimeMillis, Offset(dragAmount, 0f)
                                    )
                                }
                            }

                        }
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(width = placeable.width, height = placeable.height) {
                                placeable.placeRelative(
                                    x = draggedX.value.toInt(),
                                    y = 0
                                )
                            }
                        }
                        .graphicsLayer {
                            this.scaleX = lerp(1f, 0.8f, progress)
                            this.scaleY = lerp(1f, 0.8f, progress)
                            this.shape = RoundedCornerShape(roundedCorners)
                            this.clip = true
                            this.shadowElevation = 32f
                        },
                        onDrawerClicked = {
                            animateToState(if (drawerState == DrawerState.Open) {
                                DrawerState.Closed
                            } else {
                                DrawerState.Open
                            }, velocity = 0f)
                            drawerState = if (drawerState == DrawerState.Open) {
                                DrawerState.Closed
                            } else {
                                DrawerState.Open
                            }
                        }
                    )
                }
            }
        }
    }

    enum class DrawerState {
        Open,
        Closed
    }

    @Composable
    private fun HomeScreenCustomDrawer(modifier: Modifier = Modifier) {
        var selectedItem by remember {
            mutableStateOf(Screens.Home)
        }
        Column(
            modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Screens.values().forEach {
                NavigationDrawerItem(
                    label = {
                        Text(it.text)
                    },
                    icon = {
                        Icon(imageVector = it.icon, contentDescription = it.text)
                    },
                    selected = selectedItem == it,
                    onClick = {
                        selectedItem = it
                    },
                )
            }
        }

    }
}

enum class Screens(val text: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    SleepDetails("Sleep", Icons.Default.Bedtime),
    Leaderboard("Leaderboard", Icons.Default.Leaderboard),
    Settings("Settings", Icons.Default.Settings),
}
