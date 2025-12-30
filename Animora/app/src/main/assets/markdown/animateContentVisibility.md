### 是什么？

animateContentVisibility是一种状态驱动的内容可见性动画 API。
- animateContentVisibility是Compose中一个非常方便的容器Composable，它通过一个布尔型的`visible`状态参数，来控制其子内容是否显示以及在显示/隐藏时的过渡动画。
- 当`visible`状态从`false`变为`true`时，它可以应用预定义的进入动画（如淡入、滑动），使内容优雅地出现在屏幕上；反之，当`visible`从`true`变为`false`时，它会执行退出动画，使内容平滑地消失。
- 这个API极大地简化了UI元素显隐逻辑的实现，开发者无需手动管理动画状态或布局变化，只需关注内容的可见性，animateContentVisibility就能自动处理复杂的动画效果，从而显著提升界面交互的流畅度和用户体验。

这个 API 接受1个核心输入：visible。
visible表示一个布尔值，用于控制其子内容是否显示。
visible应使用类似于：`val visible by remember { mutableStateOf(true) }` 的参数

- 当visible改变时，animateContentVisibility 会应用预定义的进入或退出动画（如淡入、滑动），使内容优雅地出现或消失在屏幕上。

### 适用于：

这个API可以适用于一下情况

1. 当某个条件满足时，平滑地显示或隐藏一个提示信息或错误消息。
2. 在加载数据时，使用淡入淡出效果展示一个加载指示器。
3. 根据用户的滚动行为，平滑地显示或隐藏一个浮动操作按钮（FAB）。
