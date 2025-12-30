package com.baidaidai.animora.components.spring.animationStudio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.components.spring.model.springSpecParameterData
import com.baidaidai.animora.components.spring.model.springSpecStudioViewModel

/**
 * 根据输入参数提供一个动画规格（[AnimationSpec]）。
 *
 * 如果启用了 Spring 动画，它会返回一个根据 [springSpecValue] 配置的 [spring] 规格。
 * 否则，它返回一个默认的 [tween] 规格。
 *
 * @param enableSpring 一个布尔值，决定是否使用 [spring] 动画。
 * @param springSpecValue 包含 `dampingRatio` 和 `stiffness` 的数据类。
 * @return 返回一个 [AnimationSpec<Float>]。
 */
fun animationProvider(
    enableSpring: Boolean,
    springSpecValue: springSpecParameterData
):AnimationSpec<Float> {
    when(enableSpring){
        true -> {
            return spring(
                dampingRatio = springSpecValue.dampingRatio,
                stiffness = springSpecValue.stiffness,
                visibilityThreshold = null
            )
        }
        else -> {
            return tween()
        }
    }
}

/**
 * Spring 动画工作室的动画预览区域。
 *
 * 这个 Composable 函数根据 [springSpecStudioViewModel] 中提供的参数，
 * 实时展示一个 [Box] 的宽度动画。动画的触发由 [blueState] 控制。
 * 动画规格（spring 或 tween）由 `animationProvider` 函数提供。
 *
 * @param blueState 一个布尔值状态，用于触发动画的开始和结束。
 * @param springSpecStudioViewModel 包含动画规格参数（如阻尼比、刚度）和是否启用 Spring 动画的状态的 ViewModel。
 */
@Composable
fun animationStudio(
    blueState: Boolean,
    springSpecStudioViewModel: springSpecStudioViewModel
){
    var widthValue = remember { Animatable(100f) }
    val springSpecValue by springSpecStudioViewModel.springSpecValue.collectAsState()
    val enableSpring by springSpecStudioViewModel.enableSpring.collectAsState()

    LaunchedEffect(blueState) {
        if (blueState){
            widthValue.animateTo(
                targetValue = 300f,
                animationSpec = animationProvider(
                    enableSpring = enableSpring,
                    springSpecValue = springSpecValue
                )
            )
        }else{
            widthValue.animateTo(
                targetValue = 100f,
                animationSpec = animationProvider(
                    enableSpring = enableSpring,
                    springSpecValue = springSpecValue
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