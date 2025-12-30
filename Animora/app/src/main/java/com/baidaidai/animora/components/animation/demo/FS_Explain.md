# `demo` 文件夹分析

本文档对 `components/animation/demo` 目录下的文件进行分析和总结。

---

### 目录结构

```
demo/
├── FS_Explain.md
├── animatable.kt
├── infiniteTransition.kt
└── singel/
```

- `FS_Explain.md`: (本文档) 对 `demo` 文件夹的说明。
- `animatable.kt`: 包含使用 `Animatable` API 实现的各种动画效果的示例代码。
- `infiniteTransition.kt`: 包含使用 `rememberInfiniteTransition` API 实现无限循环动画的示例代码。
- `singel` 文件夹包含了更高阶、更易用的 Jetpack Compose 动画 API 示例。
---

### 文件内容总结

#### 1. `animatable.kt`

该文件通过一个名为 `animatable` 的单例对象，提供了多个独立的 Jetpack Compose `@Composable` 函数，用于演示 `Animatable` 的强大功能。`Animatable` 允许对单个值进行动画处理，并能在协程中通过 `animateTo` 方法精确控制动画的启动、停止和规格。

**主要演示内容包括：**

-   `AnimateTo`: 基础用法，演示如何根据状态变化，将一个浮点型 `alphaValue` (透明度) 从一个值动画到另一个值。
-   `withMediumSpringSpec`: 演示如何配合 `spring` 动画规格（`AnimationSpec`），创建具有物理回弹效果的动画。
-   `withDIYBezier`: 演示如何使用 `tween` 和自定义的 `CubicBezierEasing` (贝塞尔曲线)，来创建非线性的、自定义缓动曲线的动画。
-   `withKeyframesSpline`: 演示如何使用 `keyframesWithSpline` 定义一个基于关键帧的复杂动画，允许在不同时间点达到不同的值，并使用缓动曲线平滑过渡。
-   `withInfinityRepeatable`: 演示如何结合 `infiniteRepeatable` 创建一个无限重复的复杂关键帧动画。
-   `withSnap`: 演示如何使用 `snap` 动画规格，使值瞬间变化，无任何过渡效果。

#### 2. `infiniteTransition.kt`

该文件通过一个名为 `infiniteTransition` 的单例对象，提供了两个 `@Composable` 函数，专门用于演示 `rememberInfiniteTransition` 的用法。此 API 用于创建在 Composable 进入组合时自动开始且永不停止的动画。

**主要演示内容包括：**

-   `animateColor`: 演示如何使用 `infiniteTransition.animateColor` 创建一个在两种颜色（灰色和红色）之间无限循环、平滑过渡的颜色动画。
-   `animateFloat`: 演示如何使用 `infiniteTransition.animateFloat` 创建一个使组件尺寸（宽度）在两个浮点值（100f 和 300f）之间无限循环、平滑变化的动画。

---

### `singel` 文件夹分析

`singel` 文件夹包含了更高阶、更易用的 Jetpack Compose 动画 API 示例。这些 API 通常用于实现单一值的状态驱动动画或特定的通用动画效果。

#### 目录结构

```
singel/
├── animateColorAsState.kt
├── animateContentSize.kt
├── animateContentVisibility.kt
├── animateOpacityAsState.kt
├── shareElement.kt
├── shareTransition.kt
└── updateTransition.kt
```

#### 文件内容总结

-   **`animateColorAsState.kt`**: 演示了 `animateColorAsState` 的用法。当目标颜色状态（`targetValue`）改变时，它会自动创建一个颜色过渡动画。这是实现平滑颜色变化最简单的方法。

-   **`animateOpacityAsState.kt`**: 演示了 `animateFloatAsState` 的一个具体应用场景——控制透明度。当目标透明度值（0f 或 1f）改变时，它会平滑地改变组件的 `alpha` 值。

-   **`animateContentSize.kt`**: 演示了 `Modifier.animateContentSize()`。将此修饰符应用于一个组件，当该组件的尺寸因其内容或状态改变而发生变化时，尺寸变化过程会自动产生动画效果。

-   **`animateContentVisibility.kt`**: 演示了 `AnimatedContent` 和 `animateEnterExit` 修饰符，用于在内容出现或消失时应用淡入（`fadeIn`）和淡出（`fadeOut`）动画。

-   **`updateTransition.kt`**: 演示了 `updateTransition` 的用法。当需要根据一个状态（如 `blueState`）同时更新多个动画值（如颜色和尺寸）时，`updateTransition` 是一个理想的选择。它能确保所有相关的动画同步进行。

-   **`shareTransition.kt` & `shareElement.kt`**: 这两个文件演示了实验性的 `SharedTransitionLayout` API，用于实现**共享元素转场动画**。
    -   `shareTransition.kt` 展示了如何使用 `sharedBounds` 和 `sharedElement` 在不同布局状态（`Row` 和 `Column`）之间共享组件的边界和内容，创建平滑的布局转换效果。
    -   `shareElement.kt` 专注于在不同状态下共享单个 `Image` 组件，并改变其尺寸和形状，实现类似 Activity 转场中的共享元素效果。