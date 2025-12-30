### 是什么？

infiniteRepeatable是一种无限重复动画包装器 (AnimationSpec) API。
- `infiniteRepeatable`是Compose动画中一个非常实用的`AnimationSpec`包装器，它的主要作用是将任何一个标准的动画规范（如`tween`、`spring`等）转换为一个可以无限循环播放的动画。
- 这意味着一旦动画开始，它将持续不断地重复其定义的序列，直到Composable被移除或状态不再触发动画。
- 这个API非常适合用于实现那些需要持续进行的背景动画、加载指示器、闪烁效果或者任何需要不断重复的视觉元素，为用户提供持续的视觉反馈。
- 它简化了无限循环动画的实现，开发者无需手动管理循环逻辑，只需定义一次动画，`infiniteRepeatable`就会自动处理其无限重复播放，从而提升了用户界面的动态感和活跃度。

这个 API 接受1个核心输入：animation。
animation表示一个标准的动画规范（如`tween`、`spring`等），它定义了单次动画的行为。
- animation应使用类似于：`animation = tween(1000)` 的参数

当一个动画规范被 infiniteRepeatable 包装后，它会持续不断地重复其定义的序列，直到 Composable 被移除。

### 适用于：

这个API可以适用于一下情况

1. 创建一个持续旋转的加载指示器动画。
2. 实现一个不断闪烁的光标或提示图标。
3. 为背景添加一个无限循环的、平滑过渡的颜色渐变或脉冲效果。
