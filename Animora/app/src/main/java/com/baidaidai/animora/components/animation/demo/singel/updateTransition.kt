package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 演示 [updateTransition] 的用法，用于同步多个值的动画。
 *
 * 当 [blueState] 改变时，[updateTransition] 会创建一个“过渡”（transition），
 * 然后使用 `animateColor` 和 `animateDp` 来同时驱动 [Box] 的背景颜色和宽度的动画。
 * 这确保了所有相关的动画都是同步进行的。
 *
 * @param blueState 一个布尔值状态，作为驱动所有动画的目标状态。
 */
@Composable
fun _updateTransition(blueState: Boolean){
    val genLock = updateTransition(blueState)
    val contentColor by genLock.animateColor { blueState->
        if (blueState){
            Color.Red
        }else{
            Color.Gray
        }
    }
    val contentSize by genLock.animateDp { blueState ->
        if (blueState){
            300.dp
        }else{
            100.dp
        }
    }
    Column {
        Box(
            modifier = Modifier
                .size(height = 100.dp, width = contentSize)
                .background(color = contentColor),
            content = {}
        )
    }
}
