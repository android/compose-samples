package com.example.jetlagged

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun HomeScreenDrawer() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
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

        fun animateToState(drawerState: DrawerState, velocity: Float = 0f) {
            coroutineScope.launch {
                when (drawerState) {
                    DrawerState.Open -> translationX.animateTo(
                        drawerWidth,
                        initialVelocity = velocity
                    )

                    DrawerState.Closed -> translationX.animateTo(
                        0f,
                        initialVelocity = velocity
                    )
                }
            }
        }

        fun toggleDrawerState() {
            animateToState(
                if (drawerState == DrawerState.Open) {
                    DrawerState.Closed
                } else {
                    DrawerState.Open
                }, velocity = 0f
            )
            drawerState = if (drawerState == DrawerState.Open) {
                DrawerState.Closed
            } else {
                DrawerState.Open
            }
        }

        HomeScreenDrawer(
            selectedScreen = screenState,
            onScreenSelected = { screen ->
                screenState = screen
            }
        )
        ScreenContents(selectedScreen = screenState,
            onDrawerClicked = ::toggleDrawerState,
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = translationX.value
                    this.scaleX = lerp(1f, 0.8f, translationX.value / drawerWidth)
                    this.scaleY = lerp(1f, 0.8f, translationX.value / drawerWidth)
                    this.shape = RoundedCornerShape(RoundedCorners)
                    this.clip = true
                    this.shadowElevation = 32f
                }
                .pointerInput(Unit) {
                    val decay = splineBasedDecay<Float>(this)
                    val velocityTracker = VelocityTracker()

                    detectHorizontalDragGestures(onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val targetOffsetX = decay.calculateTargetValue(
                            translationX.value,
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
                            translationX.snapTo(translationX.value + dragAmount)
                            velocityTracker.addPosition(
                                change.uptimeMillis, Offset(dragAmount, 0f)
                            )
                        }
                    }
                }
        )
    }
}


@Composable
fun ScreenContents(
    selectedScreen: Screen,
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        when (selectedScreen) {
            Screen.Home ->
                JetLaggedScreen(
                    modifier = Modifier,
                    onDrawerClicked = onDrawerClicked
                )

            Screen.SleepDetails ->
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {

                }

            Screen.Leaderboard ->
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {

                }

            Screen.Settings ->
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {

                }
        }
    }
}

enum class DrawerState {
    Open,
    Closed
}

@Composable
private fun HomeScreenDrawer(
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
        Screen.values().forEach {
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

private val RoundedCorners = 16.dp
private val DrawerWidth = 300.dp

enum class Screen(val text: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    SleepDetails("Sleep", Icons.Default.Bedtime),
    Leaderboard("Leaderboard", Icons.Default.Leaderboard),
    Settings("Settings", Icons.Default.Settings),
}

