### 是什么？

animateFloat是一种无限循环的浮点值动画 API。
- `animateFloat`是Compose `rememberInfiniteTransition`的又一个强大扩展，它专注于创建无限循环的浮点数值动画。
- 通过定义一个起始浮点值和一个目标浮点值，并结合一个动画规范，`animateFloat`能够在这些值之间持续不断地进行平滑循环过渡。
- 由于浮点数可以代表多种视觉属性（如尺寸比例、透明度、旋转角度、平移距离等），`animateFloat`成为了驱动各种持续动态效果的灵活工具。
- 开发者可以利用它轻松实现元素的持续缩放、淡入淡出、旋转、来回移动等无限循环动画，而无需手动管理复杂的动画状态和循环逻辑。
- 它极大地丰富了Compose界面的表现力，为用户提供持续的视觉反馈和沉浸式体验。

这个 API 接受2个核心输入：initialValue, targetValue。
initialValue, targetValue表示动画的起始和目标浮点值。
- initialValue应使用类似于：`initialValue = 0f` 的参数
- targetValue应使用类似于：`targetValue = 360f` 的参数

当定义了这两个值后，animateFloat 会在这些值之间持续不断地进行平滑循环过渡，用于驱动各种视觉属性。

### 适用于：

这个API可以适用于一下情况

1. 通过驱动 `rotate` 修饰符，创建一个无限旋转的加载动画。
2. 通过驱动 `scale` 修饰符，实现一个持续放大和缩小的“脉冲”效果。
3. 通过驱动 `alpha` 修饰符，让一个UI元素实现无限循环的淡入淡出效果。
