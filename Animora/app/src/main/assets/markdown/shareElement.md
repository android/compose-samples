### 是什么？

sharedElement 是一种**状态驱动**的**共享元素过渡动画** API。
- 用于在不同 Composable 状态之间建立“同一元素”的视觉连续性
- 通过共享 key，将新旧 Composable 绑定为同一个视觉元素
- 在状态切换时自动计算位置、尺寸与裁剪差异并生成过渡动画

这个 API 接受 2 个核心输入：sharedContentState、animatedVisibilityScope。

- sharedContentState 应使用类似于：rememberSharedContentState(key: Any) 的参数，其中 key 需要在状态切换前后保持稳定
- animatedVisibilityScope 应使用类似于：this@AnimatedContent 或 this@AnimatedVisibility 的作用域引用

当 sharedContentState 对应的元素在 animatedVisibilityScope 控制的状态发生变化时，  
sharedElement 会在 SharedTransitionLayout 提供的环境中，  
自动匹配新旧 Composable，  
并对该元素执行平滑的跨布局共享过渡动画。

### 适用于：

这个 API 可以适用于以下情况：

1. 列表页与详情页之间的图片、头像等视觉焦点的共享过渡
2. 同一元素在不同布局容器之间切换（如 Row 与 Column）
3. 基于状态变化，对同一 UI 元素进行尺寸、位置或层级强调的动画表现