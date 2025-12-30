package com.baidaidai.animora.components.animation.demo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.keyframesWithSpline
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

object animatable {

    /**
     * 演示 [Animatable.animateTo] 的基本用法。
     *
     * 这个 Composable 会根据 [blueState] 的状态来驱动一个 [Box] 的透明度（alpha）动画。
     * 当 `blueState` 为 true 时，它会将 alpha 动画到 0.5，等待一秒，然后再动画到 0。
     * 当 `blueState` 为 false 时，它会将 alpha 动画回 1。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun AnimateTo(blueState: Boolean){
        var alphaValue = remember { Animatable(1f) }
        LaunchedEffect(blueState) {
            if (blueState){
                alphaValue.animateTo(0.5f)
                delay(1000)
                alphaValue.animateTo(0f)
            }else{
                alphaValue.animateTo(1f)
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .alpha(alphaValue.value)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }

    /**
     * 演示 [Animatable] 如何与 [spring] 动画规格结合使用。
     *
     * 这个 Composable 会使用一个具有中等回弹效果（[Spring.DampingRatioMediumBouncy]）
     * 和中等刚度（[Spring.StiffnessMedium]）的弹簧动画来驱动 [Box] 的宽度变化。
     * 动画由 [blueState] 的变化触发。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun withMediumSpringSpec(blueState: Boolean){
        var widthValue = remember { Animatable(100f) }
        LaunchedEffect(blueState) {
            if (blueState){
                widthValue.animateTo(
                    targetValue = 50f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                delay(1000)
                widthValue.animateTo(
                    targetValue = 300f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }else{
                widthValue.animateTo(
                    targetValue = 100f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp,width = widthValue.value.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }

    /**
     * 演示 [Animatable] 如何与自定义的 [CubicBezierEasing] 缓动曲线结合使用。
     *
     * 这个 Composable 使用一个 [tween] 动画规格，并结合一个自定义的贝塞尔曲线，
     * 来创建一个独特的缓动效果，用于驱动 [Box] 的宽度变化。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun withDIYBezier(blueState: Boolean){
        var widthValue = remember { Animatable(100f) }
        LaunchedEffect(blueState) {
            val animSpec = tween<Float>(
                durationMillis = 1000,
                easing = CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
            )
            if (blueState){
                widthValue.animateTo(
                    targetValue = 50f,
                    animationSpec = animSpec
                )
                delay(1000)
                widthValue.animateTo(
                    targetValue = 300f,
                    animationSpec = animSpec
                )
            }else{
                widthValue.animateTo(
                    targetValue = 100f,
                    animationSpec = animSpec
                )
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp,width = widthValue.value.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }

    /**
     * 演示 [Animatable] 如何与 [keyframesWithSpline] 动画结合使用。
     *
     * 这个 Composable 通过一系列关键帧来驱动 [Box] 的宽度动画。
     * 动画会在给定的时间点之间，使用样条曲线平滑地插值宽度值，
     * 从而对动画的进程提供精细的控制。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun withKeyframesSpline(blueState: Boolean) {
        val widthValue = remember { Animatable(100f) }
        LaunchedEffect(blueState) {
            val animSpec = keyframesWithSpline<Float> {
                durationMillis = 3000 // 总动画时长
                50f at 500 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
                150f at 2000 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
                300f at 3000 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
            }

            if (blueState) {
                widthValue.animateTo(
                    targetValue = 300f, // 最终目标值
                    animationSpec = animSpec
                )
            } else {
                widthValue.animateTo(
                    targetValue = 100f,
                    animationSpec = snap()
                )
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = widthValue.value.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }

    /**
     * 演示 [Animatable] 如何与 [infiniteRepeatable] 动画结合使用。
     *
     * 这个 Composable 使用一个无限重复的关键帧动画来驱动 [Box] 的宽度变化，
     * 并在每个周期结束时反转方向。
     * 注意：虽然 `Animatable` 可以启动一个无限动画，但这会阻塞协程。
     * 对于“即发即忘”的无限动画，[rememberInfiniteTransition] 通常是更好的选择。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun withInfinityRepeatable(blueState: Boolean) {
        val widthValue = remember { Animatable(100f) }

        LaunchedEffect(blueState) {
            if (blueState) {
                widthValue.animateTo(
                    targetValue = 300f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 3000
                            50f at 500 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
                            150f at 2000 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
                            300f at 3000 using CubicBezierEasing(1.00f, 0.00f, 0.00f, 1.00f)
                        },
                        repeatMode = RepeatMode.Reverse
                    )
                )
            } else {
                widthValue.animateTo(
                    targetValue = 100f,
                    animationSpec = snap()
                )
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = widthValue.value.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }

    /**
     * 演示 [Animatable] 如何与 [snap] 动画规格结合使用。
     *
     * 这个 Composable 会立即改变 [Box] 的宽度，而没有任何动画过渡。
     * `snap` 规格对于那些不需要过渡的即时状态变更非常有用。
     *
     * @param blueState 一个布尔值状态，用于触发动画。
     */
    @Composable
    fun withSnap(blueState: Boolean) {
        val widthValue = remember { Animatable(100f) }

        LaunchedEffect(blueState) {
            if (blueState) {
                widthValue.animateTo(
                    targetValue = 300f,
                    animationSpec = snap()
                )
            } else {
                widthValue.animateTo(
                    targetValue = 100f,
                    animationSpec = snap()
                )
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = widthValue.value.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }
    }
}

