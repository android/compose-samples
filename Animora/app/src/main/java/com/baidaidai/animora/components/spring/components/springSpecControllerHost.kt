package com.baidaidai.animora.components.spring.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate

private val springSpecStudioControllerEnterTransition = slideInVertically(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh,
        visibilityThreshold = null
    ),
    initialOffsetY = { 0 }
)
private val springSpecStudioControllerExitTransition = slideOutVertically(
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh,
        visibilityThreshold = null
    ),
    targetOffsetY = { 0 }
)

private val springSpecStudioControllerTransitionSpec = springSpecStudioControllerEnterTransition togetherWith springSpecStudioControllerExitTransition

/**
 * Spring 动画工作室的控制器宿主（Host）面板。
 *
 * 这个 Composable 函数以一个可展开/折叠的卡片形式，容纳了 [springSpecStudioController]。
 * 它包含一个标题行和一个可点击的图标，用于控制内容的可见性。
 * 内容的展开和折叠使用了自定义的 `slideInVertically` 和 `slideOutVertically` 动画。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun springSpecControllerHost(){
    var springSpecControllerExpandedStatus by rememberSaveable { mutableStateOf(false) }
    val expandedRotation by animateFloatAsState(
        targetValue = if (springSpecControllerExpandedStatus) -180f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = EaseInOut
        ),
    )

    Card(
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "SpringSpec Controller",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.padding(start = 3.dp))
                    Badge(){
                        Text("new")
                    }
                }
                IconButton(
                    onClick = {
                        springSpecControllerExpandedStatus = !springSpecControllerExpandedStatus
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = if (springSpecControllerExpandedStatus) "ExpandLess" else "ExpandMore",
                        modifier = Modifier
                            .rotate(expandedRotation)
                    )
                }
            }
            AnimatedContent(
                targetState = springSpecControllerExpandedStatus,
                transitionSpec = { springSpecStudioControllerTransitionSpec },

            ) { status ->
                if (status) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    ) {
                        HorizontalDivider(thickness = 0.5.dp)
                        Spacer(modifier = Modifier.padding(4.dp))
                        springSpecStudioController()
                    }
                }
            }
        }
    }
}