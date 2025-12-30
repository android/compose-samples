package com.baidaidai.animora.components.animation.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.baidaidai.animora.LocalAnimationViewModel
import com.baidaidai.animora.shared.viewModel.blueStateViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

/**
 * 作为 AnimationDetails 页面入口的容器型 Composable。
 *
 * 该函数负责组合动画展示与说明内容，
 * 并协调动画触发状态与页面导航行为。
 *
 * ## 页面结构
 * - 使用独立的 [Scaffold] 作为页面基底
 * - 顶部栏与悬浮按钮用于控制页面导航与动画状态
 * - 页面主体由动画展示区域与说明文档组成
 *
 * ## 状态与数据来源
 * - 动画触发状态由 [blueStateViewModel] 提供并控制
 * - 当前展示的动画与其说明内容来自 [LocalAnimationViewModel]
 * - 动画说明文档以 Markdown 形式从 assets 中加载并渲染
 *
 * 该 Composable 不负责创建或管理任何 ViewModel，
 * 仅消费上层已提供的状态与数据。
 *
 * @param blueStateViewModel 控制动画触发状态的 ViewModel
 * @param navController 用于处理页面返回行为的 [NavController]
 *
 * @see blueStateViewModel
 * @see LocalAnimationViewModel
 * @see NavController
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun animationDetailContainer(
    blueStateViewModel: blueStateViewModel,
    navController: NavController,
){
    val animationDatasViewModel = LocalAnimationViewModel.current
    val blueStateViewModel = blueStateViewModel

    val blueState by blueStateViewModel.blueState.collectAsState()
    val animationDatas by animationDatasViewModel.selectedAnimation.collectAsState()

    val assetsManager = LocalContext.current.assets
    var inputStream = assetsManager.open(animationDatas.details)
    var markdownContent = inputStream.bufferedReader().use { it.readText() }

    val scrollableState = rememberScrollableState { it }

    Scaffold(
        topBar = {
            NecessaryComponents.animationDetailsTopAppBar(
                content = animationDatas.name,
            ) {
                navController.popBackStack()
                blueStateViewModel.changeBlueState(false)
            }
        },
        floatingActionButton = {
            NecessaryComponents.animationDetailsFloatActionButton {
                blueStateViewModel.changeBlueState(!blueState)
            }
        }
    ){ contentPaddingValues->
        Column(
            modifier = Modifier
                .padding(contentPaddingValues)
                .scrollable(
                    state = rememberScrollableState { 1f },
                    orientation = Orientation.Vertical
                )
                .padding(horizontal = 30.dp)
        ){
            Card(
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.error,
                    disabledContentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(30.dp)
                ) {
                    animationDatas.animationFiles(blueStateViewModel.blueState.collectAsState().value)
                }
            }
            HorizontalDivider(
                thickness = 3.dp,
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
            )
            MarkdownText(
                markdown = markdownContent,
                modifier = Modifier
                    .verticalScroll(
                        state = rememberScrollState()
                    )
                    .padding(
                        bottom = 100.dp
                    )
            )
        }
    }
}