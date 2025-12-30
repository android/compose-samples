package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 演示如何使用 [AnimatedContent] 和 `animateEnterExit` 来创建内容的出现和消失动画。
 *
 * 当 [blueState] 为 false 时，[Box] 会带着淡入（[fadeIn]）效果出现。
 * 当 [blueState] 变为 true 时，[Box] 会带着淡出（[fadeOut]）效果消失。
 *
 * @param blueState 一个布尔值状态，用于控制 [Box] 的可见性。
 */
@Composable
fun animateContentVisibility(blueState: Boolean) {
    AnimatedContent(blueState) { blueState ->
        if (!blueState){
            Column {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = Color.Gray)
                        .animateEnterExit(
                            enter = fadeIn(),
                            exit = fadeOut()
                        ),
                    content = {}
                )
            }
        }
    }
}
