package com.baidaidai.animora.components.animation.demo

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object infiniteTransition {

    /**
     * 演示如何使用 [rememberInfiniteTransition] 创建一个无限循环的颜色动画。
     *
     * 这个 Composable 使用 `infiniteTransition.animateColor` 来驱动一个 [Box] 的背景颜色，
     * 使其在灰色和红色之间平滑地、无限地来回过渡。
     * 动画规格使用了 `tween` 和 `RepeatMode.Reverse`。
     */
    @Composable
    fun animateColor(){
        val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition Demo")
        val bgColor:Color by infiniteTransition.animateColor(
            initialValue = Color.Gray,
            targetValue = Color.Red,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500,
                    easing = EaseInOut
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        Column {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color = bgColor),
                content = {}
            )
        }
    }

    /**
     * 演示如何使用 [rememberInfiniteTransition] 创建一个无限循环的浮点值动画。
     *
     * 这个 Composable 使用 `infiniteTransition.animateFloat` 来驱动一个 [Box] 的宽度，
     * 使其在 100.dp 和 300.dp 之间平滑地、无限地来回变化。
     * 动画规格使用了 `tween` 和 `RepeatMode.Reverse`。
     */
    @Composable
    fun animateFloat(){
        val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition Demo")
        val size by infiniteTransition.animateFloat(
            initialValue = 100f,
            targetValue = 300f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = EaseInOut
                ),
                repeatMode = RepeatMode.Reverse
            )

        )
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = size.dp)
                    .background(color = Color.Gray),
                content = {}
            )
        }

    }

}
