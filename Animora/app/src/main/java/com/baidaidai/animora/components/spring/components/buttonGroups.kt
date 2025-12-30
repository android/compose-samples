package com.baidaidai.animora.components.spring.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *  一个在 [ButtonGroup] 内部使用的、可切换的按钮。                                                                                                                                                                                                                    │
 *                                                                                                                                                                                                                                                                     │
 *  这个 Composable 函数根据其在按钮组中的位置（第一个、中间或最后一个）                                                                                                                                                                                               │
 * 自动应用不同的形状，以创建连接在一起的视觉效果。                                                                                                                                                                                                                   │
 *                                                                                                                                                                                                                                                                    │
 * @param list 包含所有按钮的列表，用于判断当前按钮的位置。                                                                                                                                                                                                           │
 * @param index 当前按钮在 [list] 中的索引。                                                                                                                                                                                                                          │
 * @param content 按钮上显示的文本。                                                                                                                                                                                                                                  │
 * @param checked 按钮当前是否处于选中状态。                                                                                                                                                                                                                          │
 * @param rowScope [RowScope] 的实例，用于在行布局中正确放置按钮。                                                                                                                                                                                                    │
 * @param onCheckedChange 当按钮的选中状态改变时触发的回调。
  */

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun toggleButton(
    list:List<*>,
    index: Int,
    content: String,
    checked: Boolean,
    rowScope: RowScope,
    onCheckedChange:()-> Unit
){
    with(rowScope){
        ToggleButton(
            checked = checked,
            onCheckedChange = {
                onCheckedChange()
            },
            shapes = when(index){
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                list.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            },
            modifier = Modifier
                .height(32.dp)
                .weight(
                    weight = if (checked) 1.5f else 1.2f
                )
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(
            modifier = Modifier
                .width(2.dp)
        )
    }
}