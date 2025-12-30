package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 演示 [Modifier.animateContentSize] 的用法。
 *
 * 当 [Box] 的尺寸根据 [blueState] 改变时，
 * `animateContentSize` 修饰符会自动为尺寸变化创建动画效果。
 *
 * @param blueState 一个布尔值状态，用于改变 [Box] 的宽度。
 */
@Composable
fun animateContentSize(blueState: Boolean){
    AnimatedContent(blueState) { blueState ->
        Column {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = if(blueState) 300.dp else 100.dp)
                    .background(color = Color.Gray)
                    .animateContentSize(),
                content = {}
            )
        }
    }
}