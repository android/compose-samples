package com.baidaidai.animora.components.spring

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.baidaidai.animora.components.spring.animationStudio.animationStudio
import com.baidaidai.animora.components.spring.model.springSpecStudioViewModel
import com.baidaidai.animora.components.spring.components.NecessaryComponents
import com.baidaidai.animora.components.spring.components.springSpecControllerHost
import com.baidaidai.animora.components.spring.components.springSpecStudioController
import com.baidaidai.animora.shared.viewModel.blueStateViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

val LocalSpringSpecStudioViewModel = compositionLocalOf<springSpecStudioViewModel>{ error("No springSpecStudioViewModel provided") }

/**
 * Spring 动画工作室的主屏幕容器。
 *
 * 这个 Composable 函数构建了整个 Spring 动画工作室的 UI，包括：
 * - 顶部应用栏（[NecessaryComponents.springSpecTopAppBar]）。
 * - 悬浮操作按钮（[NecessaryComponents.springSpecFloatActionButton]），用于触发动画。
 * - 动画预览区域（[animationStudio]）。
 * - 参数控制器（[springSpecControllerHost]），通过 [CompositionLocalProvider] 注入 ViewModel。
 * - 显示相关信息的 Markdown 文本。
 *
 * @param navController 用于处理导航事件（如返回上一页）的 [NavController]。
 */
@Composable
fun springSpecSceenContainer(
    navController: NavController
){

    val blueStateViewModel = viewModel<blueStateViewModel>()
    val springSpecStudioViewModel = viewModel<springSpecStudioViewModel>()

    val blueState by blueStateViewModel.blueState.collectAsState()

    val assetsManager = LocalContext.current.assets
    var inputStream = assetsManager.open("markdown/springSpec.md")
    var markdownContent = inputStream.bufferedReader().use { it.readText() }

    Scaffold(
        topBar = {
            NecessaryComponents.springSpecTopAppBar(
                content = "SpringSpce Studio"
            ) {
                navController.popBackStack()
                blueStateViewModel.changeBlueState(false)
            }
        },
        floatingActionButton = {
            NecessaryComponents.springSpecFloatActionButton {
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
                    animationStudio(
                        blueState = blueState,
                        springSpecStudioViewModel = springSpecStudioViewModel
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
            )
            CompositionLocalProvider(
                LocalSpringSpecStudioViewModel provides springSpecStudioViewModel
            ) {
                springSpecControllerHost()
            }
            HorizontalDivider(
                thickness = 0.1.dp,
                modifier = Modifier
                    .padding(
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
