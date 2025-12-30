package com.baidaidai.animora.components.spring.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.offset
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

object NecessaryComponents {
    /**
     * Spring 动画工作室页面的顶部应用栏（TopAppBar）。
     *
     * @param content 要在标题中显示的文本。
     * @param onClick 返回按钮的点击事件回调。
     */
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalSharedTransitionApi::class
    )
    @Composable
    fun springSpecTopAppBar(
        content: String,
        onClick: () -> Unit

    ){
        TopAppBar(
            title = {
                Text(text = content)
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

    /**
     * Spring 动画工作室页面的悬浮操作按钮（FloatingActionButton）。
     *
     * 这个按钮用于触发预览动画的开始。
     *
     * @param onClick 按钮的点击事件回调。
     */
    @Composable
    fun springSpecFloatActionButton(
        onClick:()-> Unit
    ){
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = { Icon(Icons.Outlined.Animation, "Click To Start Animation") },
            text = { Text(text = "Start Animation") },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.Companion
                .offset(
                    y = (-10).dp,
                    x = (-15).dp
                )
        )
    }
}