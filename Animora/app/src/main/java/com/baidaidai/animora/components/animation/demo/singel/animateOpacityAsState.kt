package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 演示 [animateFloatAsState] 在控制透明度（alpha）时的用法。
 *
 * 当 [blueState] 改变时，这个 Composable 会自动地、平滑地
 * 在完全不透明（1f）和完全透明（0f）之间创建过渡动画。
 *
 * @param blueState 一个布尔值状态，用于在两种透明度之间切换。
 */
@Composable
fun animateOpacityAsState(
    blueState: Boolean
){
    val alpha by animateFloatAsState(
        targetValue = if (blueState) 0f else 1f
    )
    Column {
        Box(
            modifier = Modifier
                .size(100.dp)
                .alpha(alpha)
                .background(color = Color.Gray),
            content = {}
        )
    }
}
