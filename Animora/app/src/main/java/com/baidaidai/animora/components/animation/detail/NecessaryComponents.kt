package com.baidaidai.animora.components.animation.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.LocalAnimatedContentScope
import com.baidaidai.animora.LocalSharedTransitionScope

final object NecessaryComponents {
    /**
     * 动画详情页面的顶部应用栏（TopAppBar）。
     *
     * 这个 Composable 包含了返回按钮和标题。标题部分利用了 `sharedBounds`
     * 来支持共享元素转场动画，使其能在不同屏幕间平滑过渡。
     *
     * @param content 顶部应用栏要显示的标题文本。
     * @param onClick 返回按钮的点击事件回调。
     */
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalSharedTransitionApi::class
    )
    @Composable
    fun animationDetailsTopAppBar(
        content: String,
        onClick: () -> Unit,
    ){
        val sharedTransitionScope = LocalSharedTransitionScope.current
        val animatedContentScope = LocalAnimatedContentScope.current
        with(sharedTransitionScope){
            TopAppBar(
                title = {
                    Text(
                        text = content,
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState("AnimationTitle-${content}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    }

    /**
     * 动画详情页面的悬浮操作按钮（FloatingActionButton）。
     *
     * 这个 Composable 用于触发示例动画的开始。
     *
     * @param onClick 按钮的点击事件回调，通常用于启动或重置动画。
     */
    @Composable
    fun animationDetailsFloatActionButton(
        onClick:()-> Unit
    ){
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = { Icon(Icons.Outlined.Animation, "Click To Start Animation") },
            text = { Text(text = "Start Animation") },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .offset(
                    y = (-10).dp,
                    x = (-15).dp
                )
        )
    }
}