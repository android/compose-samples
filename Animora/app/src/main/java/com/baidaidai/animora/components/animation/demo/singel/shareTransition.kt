package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 演示实验性的 [SharedTransitionLayout] 和 [Modifier.sharedBounds] API。
 *
 * 这个 Composable 展示了如何在不同布局（[Row] 和 [Column]）之间
 * 共享一组 UI 元素的边界和内容。当 [blueState] 改变时，
 * 容器和内部的 [Text] 元素会平滑地过渡到新的位置和布局。
 *
 * @param blueState 一个布尔值状态，用于在两种布局状态之间切换。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun shareTransition(blueState: Boolean){
    SharedTransitionLayout {
        AnimatedContent(blueState){ blueState ->
            if (blueState){
                Row(
                    modifier = Modifier
                        .border(1.dp, color = Color.Black)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState("Border"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .fillMaxWidth()
                    ,
                ){
                    Text(
                        text = "Title",
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("Title"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )
                    Text(
                        text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l",
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("Content"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )
                }
            }else{
                Column(
                    modifier = Modifier
                        .border(1.dp, color = Color.Black)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState("Border"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .size(100.dp)
                    ,
                ){
                    Text(
                        text = "Title",
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("Title"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )
                    Text(
                        text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("Content"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )
                }
            }
        }
    }
}
