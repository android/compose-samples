---
name: compose-performance
description: Diagnose, generate, and optimize Jetpack Compose UI code. Use this skill when generating new Compose code or analyzing performance bottlenecks.
---

# Jetpack Compose Performance Optimization Skill

Use this skill when designing, writing, or reviewing Jetpack Compose code to ensure maximum rendering efficiency, minimize recomposition overhead, and prevent UI jank.

## Prerequisites

The top three things you can do for performance are as follows, they apply broadly to your app and can improve performance dramatically.

- Always be on the latest version of Jetpack Compose. The best performance updates are ones you get for free by being on the latest version.
- Make sure a baseline-profile is setup for your app. Use the `baseline-profile` skill.
- Configure R8 using the `r8-analyzer` skill.

The rest of the checks and actions are specific to individual pieces of code.

---

## 1. Phase Deferral & Lambdas (Composition vs. Layout vs. Draw)

### The Check

Are animated, frequently changing, or scroll-driven values passed directly to Modifiers or Composable parameters?

### The Action

Defer reading state values until the latest possible phase (Layout or Draw) by wrapping them in lambdas. This bypasses the expensive **Composition** phase entirely and goes straight to **Layout** or **Draw**.

### Code Examples

#### A. Modifier Offsets

- **Bad (Recomposes on every pixel change)**:
    ```kotlin
    val offset = scrollState.value
    Modifier.offset(x = offset.dp, y = 0.dp)
    ```
- **Optimized (Skips Composition, goes straight to Layout)**:
    ```kotlin
    val offset = scrollState.value
    Modifier.offset { IntOffset(offset, 0) }
    ```

#### B. Alpha & Rotation Animations

- **Bad (Recomposes on every animation frame)**:
    ```kotlin
    val alpha by animateFloatAsState(targetValue)
    Modifier.alpha(alpha)
    ```
- **Optimized (Skips Composition & Layout, goes straight to Draw)**:
    ```kotlin
    val alpha by animateFloatAsState(targetValue)
    Modifier.graphicsLayer { this.alpha = alpha }
    ```

#### C. Custom Composable Parameters

- **Bad**:
    ```kotlin
    @Composable
    fun Child(value: Float) { ... }
    ```
- **Optimized**:
    ```kotlin
    @Composable
    fun Child(valueProvider: () -> Float) { ... }
    ```

---

## 2. Lazy Layout Constraints & Performance

### The Check

Inspect all `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, and custom lazy layout items.

### The Action

Implement strict item keying, content-type mapping, and index-lookup optimizations.

### Directives

1.  **Require Keys & ContentTypes**:
    Always provide a stable `key` and a `contentType` for all items.
    ```kotlin
    LazyColumn {
        items(
            items = itemList,
            key = { item -> item.id },
            contentType = { item -> item.type }
        ) { item -> ... }
    }
    ```
2.  **Ensure Keys are Bundle-Saveable**:
    > [!IMPORTANT]
    > **Bundle-Saveable Key Requirement**: Keys must be saveable in an Android `Bundle` because `LazyColumn` uses `rememberSaveable` internally to persist scroll states.
    >
    > - **Do NOT** use custom data classes as keys unless they implement `Parcelable` or `Serializable`.
    > - **Do** use unique primitive types (like `String`, `Int`, or `Long`). If needed, combine fields into a unique string: `key = { "${it.id}_${it.timestamp}" }`.
3.  **Avoid `indexOf()` inside Item Bodies**:
    Do not search the list for an item's index inside the layout block (e.g., `list.indexOf(item)`). This turns a linear layout pass into an O(N²) operation and can cause `IndexOutOfBoundsException`. Use `itemsIndexed` instead.
4.  **Avoid `derivedStateOf` for Item Counts**:
    Do not use `derivedStateOf` to calculate list sizes or counts. Read the collection's size directly (e.g., `itemList.size`), as it is already a stable read.
5.  **Avoid Duplicate Keys**:
    Ensure keys are globally unique and deterministic. Never use `hashCode()` or list indices as keys, as they change when items are reordered or inserted.
6.  **Avoid Nested Scrollable Containers**:
    > [!WARNING]
    > **Nested Scrollable Columns**: Never nest a `LazyColumn` inside a parent `Column(Modifier.verticalScroll())` (or `LazyRow` inside `Row(Modifier.horizontalScroll())`).
    > Doing so destroys the lazy recycling mechanism, forcing Compose to measure and render all items at once (making it act like a standard `Column`), which can cause severe scroll lag and `OutOfMemory` crashes.
7.  **Avoid Lazy Layouts for Small, Fixed-Size Lists**:
    > [!TIP]
    > **Small Collections Optimization**: For small, fixed-size collections (e.g. 3-5 tags, buttons, or category chips), prefer using a standard `Row` or `Column` with a `forEach` loop instead of `LazyRow` or `LazyColumn`. Lazy layouts use `SubcomposeLayout` which has a measurement-time subcomposition overhead (~2.5ms per item). For small, static collections, composing all items eagerly is much more performant.

---

## 3. Parameter Stability & Strong Skipping

### The Check

Look for unstable collections (`List<T>`, `Set<T>`, `Map<K, V>`) or unannotated third-party classes passed as parameters to Composables.

### The Action

Standard collections are treated as **unstable** by the Compose compiler because their implementations can be mutated. Unstable parameters force the Composable to recompose even if the data hasn't changed.

### Directives

- **Use Immutable Collections**:
  Wrap standard collections using `kotlinx.collections.immutable` classes:
    ```kotlin
    @Composable
    fun MessageList(messages: ImmutableList<Message>) { ... }
    ```
- **Annotate Stable Classes**:
  Mark data classes that hold unstable types but are never mutated with `@Stable` or `@Immutable`.
- **Use Stability Configuration File for 3rd-Party / Java Classes**:
  Classes from standard Java libraries (like `java.time.LocalDate`, `java.time.LocalDateTime`) or external libraries are treated as unstable by the Compose compiler. You can declare these classes as stable by creating a stability configuration file (e.g. `stability_config.conf`) containing:
    ```
    java.time.LocalDate
    java.time.LocalDateTime
    ```
    And enabling it in your module's `build.gradle.kts` under `composeCompiler { stabilityConfigurationFile = ... }`.

---

## 4. Main Thread Blocking & Caching

### The Check

Scan Composable bodies and effect blocks (`LaunchedEffect`, `DisposableEffect`) for heavy operations, object allocations, disk I/O, or main-thread blocking calls.

### The Action

Move non-UI work off the main thread, avoid allocations in Composable bodies, and cache expensive calculations across recompositions.

### Directives

- **Cache Expensive Computations**:
  If a Composable body performs sorting, filtering, or string manipulation, wrap it in `remember`:
    ```kotlin
    val sortedList = remember(rawList) { rawList.sortedBy { it.timestamp } }
    ```
- **Avoid Allocations in Composable Body**:
  Never allocate or compute heavy objects (like `RoundedPolygon` or complex paths) directly in the Composable body. Wrap them in `remember { ... }`.
- **Watch out for Composable Conversions**:
  Conversion functions like `.toShape()` or `painterResource()` are `@Composable` themselves and internally handle caching. Wrapping them in `remember { ... }` will cause a compilation error.
- **Move I/O and Heavy Work off the Main Thread**:
  Never perform blocking operations (like database queries, disk I/O, or heavy binder transactions such as `context.registerReceiver`) directly on the main thread inside `DisposableEffect` or `LaunchedEffect`. Offload them to `Dispatchers.IO` using a coroutine scope:
    ```kotlin
    LaunchedEffect(fileUri) {
        val data = withContext(Dispatchers.IO) { parseJson(fileUri) }
        state = data
    }
    ```

---

## 5. Effects & Lifecycle Rules

### Directives

- **Lifecycle-Aware Collection**:
  Always use `collectAsStateWithLifecycle()` instead of `collectAsState()` when collecting Kotlin `Flow`s in UI to automatically pause collection when the app goes into the background.
- **Synchronous Effects**:
  Prefer `DisposableEffect` over `LaunchedEffect` if the effect does not require running asynchronous `suspend` functions (e.g., registering/unregistering listeners).
- **Hoist BroadcastReceivers to Prevent Per-Item Registration**:
    > [!IMPORTANT]
    > **BroadcastReceiver Overhead**: Never register a `BroadcastReceiver` (e.g., to listen for `ACTION_TIMEZONE_CHANGED` or connectivity changes) inside list item Composables. This registers a receiver for every single visible item on the main thread, causing severe scrolling lag.
    > Instead, register a single receiver in a parent/screen-level Composable using a `DisposableEffect`, and propagate the state down using a `CompositionLocalProvider`.
- **Prevent Stale Captures**:
  If a long-running or infinite loop in a `LaunchedEffect` references a Composable parameter or callback, use `rememberUpdatedState` to ensure it always uses the latest value without restarting the effect:
    ```kotlin
    val currentCallback by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        while(true) {
            delay(1000)
            currentCallback()
        }
    }
    ```
- **Modern Timers**:
  Avoid using legacy Android handlers like `Handler.postDelayed`. Use `LaunchedEffect` combined with `delay()` for clean, lifecycle-aware timing.
- **Side Effects API Selection Guide**:
  Choose the correct API based on the use case:
    - _Need a coroutine tied to a Composable's lifecycle?_ → **`LaunchedEffect(keys)`**
    - _Need to run synchronous cleanup code when a Composable leaves the composition?_ → **`DisposableEffect(keys) { onDispose { ... } }`**
    - _Need to launch a coroutine from a non-composable callback (e.g., button click, drawer gesture)?_ → **`rememberCoroutineScope()`**
    - _Need to convert/map Compose State changes into a Kotlin Flow?_ → **`snapshotFlow { stateValue }`**
    - _Need to run code exactly once per successful composition/recomposition (e.g., to sync with an external state)?_ → **`SideEffect`**

---

## 6. Back-Writing Prevention

### The Check

Look for layout callbacks like `onGloballyPositioned` or `onSizeChanged` that modify a `MutableState` which in turn triggers a layout pass.

### The Action

> [!CAUTION]
> Writing to state during the layout or measurement phase triggers a new measurement pass, leading to **infinite rendering loops** and immediate UI freezes.

### Alternatives

- If you need to measure an element to position another, use a custom **`Layout`** or **`SubcomposeLayout`** instead of measuring via state.
- Use `Modifier.onLayoutRectChanged` to debounce position updates, especially inside a lazy layout item.
- If you must use state, ensure the state is only updated if the value actually changed (add a guard check before writing).

---

## 7. Custom Drawing (Canvas vs. `drawWithCache`)

### The Check

- Are you using a nested `Canvas` composable solely to draw decorations (backgrounds, borders, indicators) on an existing Composable?
- Are you allocating drawing objects (like `Path`, `Brush`, `Shader`, or `Paint`) inside a `Canvas` draw block or a `Modifier.drawBehind` block?

### The Action

- **Avoid Nested Canvas Nodes**: Avoid using the `Canvas` composable to add decorative drawings to an existing layout. Instead, use `Modifier.drawBehind` or `Modifier.drawWithCache` on the existing Composable to avoid creating an extra node in the layout tree.
- **Cache Allocations**: If your drawing logic allocates objects that depend on the size of the drawing area, use **`Modifier.drawWithCache`**. This caches the allocated objects and only recreates them when the size or read state changes, preventing garbage collection (GC) pressure during the draw phase.

### Code Examples

#### A. Caching Path Allocations

- **Bad (Allocates a new Path on every single draw frame / animation tick)**:
    ```kotlin
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                }
                drawPath(path, Color.Red)
            }
    )
    ```
- **Optimized (Caches the Path, only recreates it if the Box size changes)**:
    ```kotlin
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                }
                onDrawBehind {
                    drawPath(path, Color.Red)
                }
            }
    )
    ```

---

## 8. Performance Verification & Profiling

### The Check

Are you measuring or verifying Compose performance on a **Debug** build or without proper compiler optimization?

### The Action

- > [!IMPORTANT]
  > **Release Builds with R8**: Never measure UI performance or scroll jank on a Debug build. Debug builds disable critical compiler optimizations (like lambda inlining and skipping checks) and include extra debugging overhead.
  > Always verify and profile performance on a **Release build with R8 enabled**. R8 optimizes Compose lambdas extremely aggressively, and the difference in performance can be up to 3-5x.
  > If benchmarking locally, use a non-debuggable build (`isDebuggable = false`) with proguard/R8 enabled.
