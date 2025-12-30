### 是什么？

SpringSpec是一种状态驱动的Spring（弹簧）动画 API。

- 基于物理弹簧模型描述动画行为
- 动画由状态变化触发，而非显式时间控制
- 通过阻尼与刚度参数决定动画趋近稳定的方式

这个API接受2个核心输入：dampingRatio、stiffness。

ampingRatio 表示弹簧系统的阻尼比例，用于控制动画是否振荡以及振荡幅度。
- dampingRatio应使用类似于：Spring.DampingRatioMediumBouncy 的实现方式，而不是直接使用魔法数

stiffness 表示弹簧的刚度系数，用于控制动画变化的速度快慢。
- stiffness 应使用类似于：Spring.StiffnessMedium 的实现方式，而不是直接使用魔法数

当dampingRatio或stiffness改变时, SpringSpec会重新计算弹簧模型，并基于新的物理参数驱动动画向目标状态收敛。

---

### 适用于：

这个 API 可以适用于以下情况

1. 需要自然回弹效果的 UI 状态切换
2. 动画可能被频繁打断并重新触发的场景
3. 不关心固定时长，而关心动画稳定状态的过渡效果  