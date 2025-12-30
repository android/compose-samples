package com.baidaidai.animora.components.animation.demo.singel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.R

/**
 * 演示实验性的 [SharedTransitionLayout] 和 [Modifier.sharedElement] API。
 *
 * 这个 Composable 展示了如何在两种不同状态之间共享一个 [Image] 组件。
 * 当 [blueState] 改变时，图片会在两种不同尺寸和裁剪形状之间平滑地过渡，
 * 创造出共享元素转场的效果。
 *
 * @param blueState 一个布尔值状态，用于在两种布局状态之间切换。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun sharedElement(blueState: Boolean){
    SharedTransitionLayout {
        AnimatedContent(
            targetState = blueState,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ){ blueState ->
            if (blueState){
                Image(
                    painter = painterResource(R.drawable.demo_tree),
                    contentDescription = "A image",
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState("image"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .size(200.dp)
                        .clip(shape = CircleShape)
                )
            }else{
                Image(
                    painter = painterResource(R.drawable.demo_tree),
                    contentDescription = "A image",
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState("image"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .size(100.dp)
                        .clip(shape = CircleShape)
                )
            }
        }
    }
}