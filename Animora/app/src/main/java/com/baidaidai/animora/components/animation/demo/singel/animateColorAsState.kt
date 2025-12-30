package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 演示 [animateColorAsState] 的用法。
 *
 * 当 [blueState] 改变时，这个 Composable 会自动地、平滑地
 * 在灰色和红色之间创建颜色过渡动画。
 *
 * @param blueState 一个布尔值状态，用于在两种颜色之间切换。
 */
@Composable
fun animateColorAsState(
    blueState: Boolean
){
    val color by animateColorAsState(
        targetValue = if (blueState) Color.Red else Color.Gray
    )
    Column {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = color)
            ,
            content = {}
        )
    }
}
