package com.baidaidai.animora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.baidaidai.animora.ui.theme.TestAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baidaidai.animora.components.animation.detail.animationDetailContainer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baidaidai.animora.shared.viewModel.animationDatasViewModel
import androidx.compose.ui.platform.LocalContext
import com.baidaidai.animora.components.spring.springSpecSceenContainer
import com.baidaidai.animora.components.StartScreen.startScreenContainer
import com.baidaidai.animora.shared.viewModel.blueStateViewModel

/**
 * 提供当前动画数据的 [animationDatasViewModel]。
 *
 * 该 [CompositionLocal] 用于在 Compose 层级中共享动画相关状态，
 * 其生命周期应与提供它的上层 Composable 保持一致。
 *
 * 此 Local 不会自行创建或管理 [animationDatasViewModel]。
 * 如果在未通过 [CompositionLocalProvider] 提供的情况下访问，
 * 将在运行时抛出异常。
 *
 * 使用示例：
 *
 * ```kotlin
 * CompositionLocalProvider(
 *     LocalAnimationViewModel provides animationDatasViewModel
 * ) {
 *     // 使用 LocalAnimationViewModel.current
 * }
 * ```
 *
 * @see animationDatasViewModel
 * @see CompositionLocalProvider
 */
val LocalAnimationViewModel = compositionLocalOf<animationDatasViewModel> {
    error("No animationDatasViewModel provided")
}

/**
 * 提供当前 [SharedTransitionScope] 的 [CompositionLocal]。
 *
 * 如果在未提供的情况下访问该值，将在运行时抛出异常。
 * 请确保在使用前已通过 [CompositionLocalProvider] 正确注入。
 *
 * 使用示例：
 *
 * ```kotlin
 * CompositionLocalProvider(
 *     LocalSharedTransitionScope provides this@SharedTransitionLayout
 * ) {
 *     // 可以安全地使用 LocalSharedTransitionScope.current
 * }
 * ```
 *
 * @see SharedTransitionScope
 * @see CompositionLocalProvider
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope>{
    error("No SharedTransitionScope provided")
}

/**
 * 提供当前 [AnimatedContentScope] 的 [CompositionLocal]。
 *
 * 如果在未提供的情况下访问该值，将在运行时抛出异常。
 * 请确保在使用前已通过 [CompositionLocalProvider] 正确注入。
 *
 * 使用示例：
 *
 * ```kotlin
 * CompositionLocalProvider(
 *     LocalAnimatedContentScope provides animatedContentScope
 * ) {
 *     // 可以安全地使用 LocalAnimatedContentScope.current
 * }
 * ```
 *
 * @see AnimatedContentScope
 * @see CompositionLocalProvider
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope>{
    error("No AnimatedContentScope provided")
}

class MainActivity : ComponentActivity() {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterial3ExpressiveApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                val animationDetailsViewModel = viewModel<animationDatasViewModel>()
                CompositionLocalProvider(
                    LocalAnimationViewModel provides animationDetailsViewModel
                ) {
                    AnimoraApp()
                }
            }
        }
    }
}

/**
 * Animora 应用的根 Composable。
 *
 * ## 该函数负责：
 * - 初始化应用级的导航结构（[NavHost]）
 * - 提供跨页面共享的 [SharedTransitionLayout] 上下文
 * - 在导航目的地作用域内注入动画相关的 [CompositionLocal]
 *
 * [AnimoraApp] 本身不包含具体界面实现，
 * 而是作为应用的结构性入口，协调导航、动画作用域与状态注入。
 *
 * ## 架构说明
 * - 每个导航目的地在进入时都会被包裹在同一个
 *   [SharedTransitionLayout] 中，以支持跨页面的共享过渡动画
 * - [LocalSharedTransitionScope] 与 [LocalAnimatedContentScope]
 *   仅在对应的 `composable` 作用域内提供
 * - 应用级的状态（如 [blueStateViewModel]）在此层级创建，
 *   以确保在导航切换过程中保持稳定
 *
 * ⚠️ 该 Composable 假定其子级在使用动画相关 Local 时，
 * 已正确运行在对应的过渡与内容作用域中。
 *
 * @see NavHost
 * @see SharedTransitionLayout
 * @see LocalSharedTransitionScope
 * @see LocalAnimatedContentScope
 */
@ExperimentalMaterial3ExpressiveApi
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun AnimoraApp(
) {

    val homeViewNavController = rememberNavController()
    val totalNavigationController = rememberNavController()
    val blueStateViewModel = viewModel<blueStateViewModel>()
    val context = LocalContext.current

    SharedTransitionLayout {
        NavHost(
            navController = totalNavigationController,
            startDestination = "Start"
        ) {
            composable(
                route = "Start"
            ){
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalAnimatedContentScope provides this@composable
                ) {
                    startScreenContainer(
                        context = context,
                        homeViewNavController = homeViewNavController,
                        totalNavigationController = totalNavigationController,
                    )
                }
            }
            composable(
                route = "Detail"
            ){
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalAnimatedContentScope provides this@composable
                ) {
                    animationDetailContainer(
                        blueStateViewModel = blueStateViewModel,
                        navController = totalNavigationController,
                    )
                }
            }
            composable(
                route = "springStudio"
            ){
                springSpecSceenContainer(
                    navController = totalNavigationController
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun TextBoxPreview() {
    TestAppTheme {
        AnimoraApp()
    }
}