# Animora 项目目录结构说明

本文档旨在帮助开发者深入理解 `Animora` 项目中每个文件和目录的用途。

---

## 根目录

此目录是应用主要源码的根目录。

-   `InfoActivity.kt`: “关于”页面的 `Activity`，是该独立页面的入口点。
-   `MainActivity.kt`: 应用的主 `Activity`，承载所有主要的 Jetpack Compose UI 界面。
-   `TODO.md`: 项目的待办事项列表，用于追踪开发任务和未来计划。
-   `FS_Explain.md`: (本文档) 项目文件系统说明。

---

## `components/`

此目录存放应用中所有的 Jetpack Compose UI 组件，并按功能和屏幕进行组织。

### `components/animation/`

存放与动画效果展示相关的组件。

-   **`demo/`**: 包含各种独立、可复现的动画效果示例。
    -   `animatable.kt`: 展示如何使用 `Animatable` API 创建更灵活的动画。
    -   `infiniteTransition.kt`: 展示如何使用 `rememberInfiniteTransition` 创建无限循环的动画。
    -   **`singel/`**: 存放单值动画或简单动画的示例。
        -   `animateColorAsState.kt`: `animateColorAsState` 的用法示例，用于平滑的颜色过渡。
        -   `animateContentSize.kt`: `animateContentSize` 的用法示例，用于使组件尺寸变化时产生动画效果。
        -   `animateContentVisibility.kt`: 内容可见性动画示例，可能指 `AnimatedVisibility`。
        -   `animateOpacityAsState.kt`: 透明度动画示例，可能使用 `animateFloatAsState` 实现。
        -   `shareElement.kt`: 共享元素 (Shared Element) 转场动画的增强版示例。
        -   `shareTransition.kt`: 共享元素转场动画的示例。
        -   `updateTransition.kt`: `updateTransition` 的用法示例，用于管理多个值的状态转换动画。

-   **`detail/`**: 动画详情页面的组件。
    -   `index.kt`: 动画详情页面的主 Composable 函数。
    -   `NecessaryComponents.kt`: 详情页面所需的辅助或通用组件。

### `components/info/`

“关于”页面的 UI 组件。

-   `authorArea.kt`: 显示作者信息的 Composable 组件。
-   `index.kt`: “关于”页面的主 Composable 函数。
-   `infoArea.kt`: 显示应用信息的 Composable 组件。
-   **`infoScreen/`**:
    -   `NecessaryComponents.kt`: “关于”屏幕所需的辅助或通用组件。

### `components/spring/`

新增的 Spring 动画工作室，提供与 `spring` 动画交互的功能。

-   `index.kt`: Spring 动画工作室页面的主 Composable 函数。
-   **`animationStudio/`**:
    -   `index.kt`: 互动式动画工作室的主界面 UI。
-   **`components/`**:
    -   `NecessaryComponents.kt`: Spring 工作室所需的辅助或通用组件。
    -   `buttonGroups.kt`: 用于控制动画参数的按钮组。
    -   `springSpecStudioController.kt`: Spring 动画规格（如刚度、阻尼比）的交互式控制器。
-   **`model/`**:
    -   `index.kt`: Spring 工作室的数据模型。
    -   **`list/`**:
        -   `index.kt`: 定义 Spring 动画示例列表的数据。

### `components/StartScreen/`

应用启动后的主屏幕界面，包含底部导航和各个标签页。

-   `index.kt`: 主屏幕的入口 Composable，可能包含底部导航栏的实现逻辑。

-   **`home/`**: 主页标签页的组件。
    -   `index.kt`: 主页界面的主 Composable 函数。
    -   `introduceCard.kt`: 主页上用于功能介绍或欢迎语的卡片组件。
    -   `onlySpringSpec.kt`: 一个专门用于演示 `spring` 动画规格的组件或示例。

-   **`list/`**: 动画列表标签页的组件。
    -   `index.kt`: 动画列表界面的主 Composable 函数。
    -   **`components/`**: 列表页的子组件。
        -   `animationListItem.kt`: 列表中单个动画项的 Composable 组件。
        -   `Header.kt`: 列表页面的通用页头组件。
        -   `modalBottomSheet.kt`: 列表页面中使用的模态底部工作表 (Modal Bottom Sheet) 组件。
        -   `principledHeader.kt`: 一个遵循特定设计原则的特殊页头组件。
    -   **`model/`**:
        -   `animationList.kt`: 定义动画列表所需的数据模型或提供静态数据。

-   **`model/`**:
    -   `barItemInside.kt`: 定义底部导航栏项目的数据模型或 Composable。
    -   `homeScreenBlurViewModel.kt`: 用于管理主屏幕背景模糊效果状态的 `ViewModel`。

---

## `shared/`

存放跨多个组件或模块共享的代码，以提高代码复用性。

-   **`components/`**:
    -   `NecessaryComponents.kt`: 存放整个应用范围内可复用的通用 UI 组件。
-   **`dataClass/`**:
    -   `AnimationDatas.kt`: 定义应用核心的数据类，如此处是动画相关的数据结构。
-   **`viewModel/`**:
    -   `animationDatasViewModel.kt`: 管理和提供动画数据的 `ViewModel`，处理业务逻辑并与 UI 分离。
    -   `blueStateViewModel.kt`: 一个特定的 `ViewModel`，用于管理某个蓝色主题或状态相关的逻辑。

---

## `ui/`

标准的 Android UI 目录，用于定义应用视觉风格。

-   **`theme/`**:
    -   `Color.kt`: 定义应用的所有颜色值，形成调色板。
    -   `Theme.kt`: 定义应用的整体 Compose 主题，包括颜色、排版和形状。
    -   `Type.kt`: 定义应用的排版样式，如 `H1`, `Body1` 等字体风格。