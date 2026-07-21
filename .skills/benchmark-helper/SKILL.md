---
name: benchmark-helper
description: Set up, write, and run Android Macrobenchmarks and Baseline Profiles.
---

# Android Macrobenchmark & Baseline Profile Setup

Use this skill when you need to set up a benchmark module, write startup or scrolling benchmarks, generate baseline profiles, or configure build types for performance testing.

---

## 1. Benchmark Module Setup

### A. Detect and Create Benchmark Module (If Missing)
If the project does not already have a benchmark module, you must set one up:

1. **Configure the Project-level `build.gradle.kts`**:
   Ensure the benchmark/baselineprofile plugin is available in the root.
   ```kotlin
   plugins {
       alias(libs.plugins.androidx.baselineprofile) apply false
   }
   ```

2. **Create a `:benchmark` Module**:
   Create a new directory named `benchmark` at the root, and add a `build.gradle.kts` file using the `com.android.test` plugin.
   
   > [!IMPORTANT]
   > **Standalone Test Module Source Sets**: In a standalone test module (`com.android.test`), all test classes (benchmarks and generators) **must be placed in `src/main/java/`** (or `src/main/kotlin/`), NOT in `src/androidTest/java/`. If placed in `src/androidTest`, the test runner will ignore them completely.
   
   > [!IMPORTANT]
   > **Self-Instrumenting Configuration**: Standalone test modules must be configured as self-instrumenting so the test runner runs in a separate process, allowing it to kill and compile the target app.
   
   ```kotlin
   plugins {
       id("com.android.test")
       id("kotlin-android")
   }

   android {
       namespace = "com.example.benchmark"
       compileSdk = 35 // Match target app's compileSdk

       compileOptions {
           sourceCompatibility = JavaVersion.VERSION_17
           targetCompatibility = JavaVersion.VERSION_17
       }

       kotlin {
           compilerOptions {
               jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
           }
       }

       defaultConfig {
           minSdk = 23
           targetSdk = 35
           testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            // Enable detailed composition tracing in Perfetto
            testInstrumentationRunnerArguments["androidx.benchmark.fullTracing.enable"] = "true"
       }

       targetProjectPath = ":app"
       // Enable the benchmark to run separately from the app process
       experimentalProperties["android.experimental.self-instrumenting"] = true

       buildTypes {
           create("benchmark") {
               isMinifyEnabled = false
               signingConfig = signingConfigs.getByName("debug")
               matchingFallbacks.add("release")
           }
       }
   }

   dependencies {
       implementation(libs.androidx.test.ext.junit)
       implementation(libs.androidx.test.uiautomator)
       implementation(libs.androidx.benchmark.macro.junit4)
        // Enable Perfetto tracing in benchmarks
        implementation("androidx.tracing:tracing-perfetto:1.0.0")
        implementation("androidx.tracing:tracing-perfetto-binary:1.0.0")
   }
   ```
   Add `:benchmark` to `settings.gradle.kts`:
   ```kotlin
   include(":benchmark")
   ```

3. **Configure the Target App Module (`:app`)**:
   Configure a non-debuggable, non-obfuscated `benchmark` build type in `:app/build.gradle.kts`.
   
   > [!WARNING]
   > **R8 / Minification Gotchas**: Enabling minification (`isMinifyEnabled = true`) on a `com.android.test` module or its target app can cause R8 to strip critical transitive classes (like `androidx.tracing.Trace`), resulting in `NoClassDefFoundError` at runtime during instrumentation. 
   
   > **Fix**: Inherit the `benchmark` build type from `debug` instead of `release`, but set `isDebuggable = false` to run at near-release speeds without R8/shrinking issues:
   
   ```kotlin
   plugins {
       id("com.android.application")
   }

   dependencies {
        // Enable composition tracing (adds Composable function names to Perfetto traces)
        implementation("androidx.compose.runtime:runtime-tracing")
    }

    android {
       // ...
       buildTypes {
           create("benchmark") {
               initWith(getByName("debug"))
               isDebuggable = false
               signingConfig = signingConfigs.getByName("debug")
           }
       }
   }
   ```
   Ensure the app's `src/benchmark/AndroidManifest.xml` is configured to allow profiling:
   ```xml
   <manifest xmlns:android="http://schemas.android.com/apk/res/android">
       <!-- Required for macrobenchmark to write trace files on some API levels -->
       <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
       <application>
           <profileable android:shell="true" />
       </application>
   </manifest>
   ```

---

## 2. Writing Benchmarks

### A. Add Startup Benchmarks (Cold & Hot Start)
Create a new file `src/androidTest/java/<package_path>/ExampleStartupBenchmark.kt` in the `:benchmark` module to measure cold and hot starts of the main application.

```kotlin
package com.example.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val targetPackageName = "com.example.app" // Replace with target app package

    @Test
    fun startupCold() = benchmarkRule.measureRepeated(
        packageName = targetPackageName,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.None, // Measure uncompiled or use CompilationMode.Partial()
        startupMode = StartupMode.COLD,
        iterations = 5
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun startupHot() = benchmarkRule.measureRepeated(
        packageName = targetPackageName,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.None,
        startupMode = StartupMode.HOT,
        iterations = 5
    ) {
        pressHome()
        startActivityAndWait()
    }
}
```

### B. Analyze App for Additional Benchmarks
Do not stop at startup. Analyze the rest of the application to identify critical user journeys (CUJs) and performance-critical areas that should be benchmarked:
1. **Search for Scrollable Containers**:
   Scan the codebase for `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`, `HorizontalPager`, `VerticalPager`, or `RecyclerView`.
   *Action*: For any major list or pager (especially those displaying images or complex cards), add a benchmark using `FrameTimingMetric` that scrolls the list.
2. **Identify Navigation Hotspots**:
   Find the main navigation graph (e.g., `NavHost`, `NavGraphBuilder.composable`).
   *Action*: Create benchmarks that measure the transition time between key destinations/screens using `FrameTimingMetric` and `TraceSectionMetric("Compose:recompose")`.
3. **Locate Complex Animations / UI States**:
   Look for custom animations, heavy state transitions, or bottom sheets.
   *Action*: Write macrobenchmarks that trigger these animations/transitions repeatedly.

Example of a scrolling benchmark:
```kotlin
@Test
fun scrollFeedFrameTiming() = benchmarkRule.measureRepeated(
    packageName = targetPackageName,
    metrics = listOf(FrameTimingMetric()),
    compilationMode = CompilationMode.Partial(),
    startupMode = StartupMode.WARM,
    iterations = 5
) {
    pressHome()
    startActivityAndWait()

    // Wait for the feed list to appear
    val list = device.findObject(By.res("feed_list")) // Ensure test tags or resource IDs are set
    list.setGestureMargin(device.displayWidth / 5)
    
    // Scroll down and up
    list.drag(Point(list.visibleCenter.x, list.visibleBounds.bottom - 100), Speed.MEDIUM)
    device.waitForIdle()
}
```

---

## 3. Running Benchmarks & Workarounds

### A. Avoid Build Variant Conflicts
Do **not** run `./gradlew :benchmark:connectedAndroidTest` as it will run all build variants (e.g. `benchmarkBenchmark` and `benchmarkRelease`) sequentially. The first run will leave the app or background services running, causing the second run to fail with:
`Package <package_name> must not be running prior to cold start!`

**Fix**: Always run the specific Gradle task for your target variant:
```bash
./gradlew :benchmark:connectedBenchmarkBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=<package_name>.benchmark.YourBenchmarkClass -P android.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR
```

### B. Emulator & UI Automation Workarounds
When running macrobenchmarks on emulators or preview OS versions (e.g. Android 16/API 36), several runtime failures commonly occur. Use these workarounds to make your benchmarks robust:

#### 1. Bypassing `startActivityAndWait()` Launch Failures
*   **Issue**: `startActivityAndWait()` can fail with `Unable to confirm activity launch completion []` on emulators or new APIs due to logcat timing or rendering callback issues.
*   **Fix**: For UI and frame timing benchmarks (using `FrameTimingMetric`), bypass the library's launch helper. Manually launch the activity using the test context and wait for the package to appear:
    ```kotlin
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 10000)
    ```

#### 2. Compose Test Tag Stripping in Benchmark Builds
*   **Issue**: In non-debuggable builds (`isDebuggable = false`—which we use for benchmarks to get accurate speed measurements), the Compose compiler strips out `testTag` metadata from the layout tree by default. This causes UiAutomator selectors like `By.res("MyTestTag")` to fail.
*   **Fix**: Write robust UiAutomator selectors using behavioral or structural properties instead of test tags:
    *   Find scrollable containers using `By.scrollable(true)`.
    *   Find buttons/items by content description (`By.desc("...")`) or text (`By.text("...")`).

#### 3. Slow Emulator Rendering (Timeouts)
*   **Issue**: Cold starting the app on a slow emulator can take longer than the default 5-second wait timeout, causing tests to fail.
*   **Fix**: Increase the wait timeout for critical UI elements (like scrollable lists or buttons) to **15 seconds** (`15000` ms) to prevent flakiness:
    ```kotlin
    val list = device.wait(Until.findObject(By.scrollable(true)), 15000)
    ```

#### 4. Emulator COLD Start Workaround (When using WARM/HOT)
If the benchmark has `self-instrumenting` enabled or is running on an emulator, `COLD` start mode often fails because the test runner cannot kill the process it is running inside.
*   **Fix**: Use `StartupMode.WARM` or `StartupMode.HOT`.
*   **Robust UI Navigation**: When using `WARM`/`HOT` start, the app is not killed between iterations. Your UI automation block must be robust:
    1. Wrap screen transitions in `try-catch` blocks in case the screen is already navigated.
    2. **Crucial**: At the end of the iteration block, navigate the app back to the initial screen (e.g., using `device.pressBack()`) so the next iteration starts from the correct state.

```kotlin
        benchmarkRule.measureRepeated(
            packageName = "<package_name>",
            compilationMode = compilationMode,
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric(),
                TraceSectionMetric("Compose:recompose"),
            ),
            startupMode = StartupMode.WARM,
            iterations = 3,
        ) {
            pressHome()
            val context = InstrumentationRegistry.getInstrumentation().context
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)!!
            context.startActivity(intent)
            device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 10000)
            
            // Perform interactions...
            
            // Return to Home screen for the next iteration
            device.pressBack()
            device.waitForIdle()
        }
```

#### 5. Gating Transitions and Waiting for Network Content
*   **Issue**: In scrolling or navigation benchmarks, the transition to the target screen might take time, or the list container might appear empty while data loads from the network. Clicking or scrolling immediately will result in inaccurate or empty measurements.
*   **Fix**: 
    1. **Gate the transition**: Wait for the previous screen's key elements to be completely gone before proceeding:
       ```kotlin
       device.wait(Until.gone(By.text("Sign In")), 15000)
       ```
    2. **Wait for network content**: Wait for a specific child element, text pattern, or list item class that indicates actual content has loaded:
       ```kotlin
       // Wait for a text pattern unique to loaded list items
       device.wait(Until.hasObject(By.textContains(".com")), 15000)
       ```

#### 6. Signature Mismatch (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`)
*   **Issue**: Running the benchmark fails with a signature mismatch because a debug version of the app is already installed with a different signature.
*   **Fix**: Always uninstall both the target app and the test app before running the benchmark:
    ```bash
    adb uninstall <target_package_name>
    adb uninstall <target_package_name>.test
    ```

#### 7. Android 16 Profile Extraction Crash
*   **Issue**: On Android 16 (API 35/36), the `pm dump-profiles` command outputs extra diagnostic lines, causing the `androidx.benchmark` profile extractor to crash.
*   **Fix**: Use an **Android 14 (API 34)** or **Android 15 (API 35)** emulator for generating Baseline Profiles.

---

## 4.  Recomposition Tracking

To diagnose and verify that your optimizations successfully eliminated recompositions, you can track recomposition counts and durations directly in your macrobenchmarks:

1. **Configure Compose Runtime Tracing**:
   Ensure composition tracing is enabled in both `:app` and `:benchmark` modules (see Section 1).

2. **Add `TraceSectionMetric` to your Benchmark**:
   Use `TraceSectionMetric` with the name of the Composable or trace section you want to measure. You can measure both total time (`Mode.Sum`) and recomposition count (`Mode.Count`):
   
   ```kotlin
   override val metrics: List<Metric> = listOf(
       FrameTimingMetric(),
       // Measure total recomposition/composition time for the Composable
       TraceSectionMetric("MyComposableName", TraceSectionMetric.Mode.Sum),
       // Measure the number of times the Composable recomposed
       TraceSectionMetric("MyComposableName", TraceSectionMetric.Mode.Count)
   )
   ```

3. **Finding the Exact Compose Trace Section Name**:
   *   **Important**: The Jetpack Compose compiler formats tracing sections by appending the file name and the starting line number of the Composable, e.g., `"com.example.app.MyComponent (MyComponent.kt:45)"`.
   *   Because the line number might differ slightly from the function declaration due to compiler-inserted code, do not guess the line number.
   *   **Solution**: Run a Python helper script to query the captured `.perfetto-trace` database for the exact slice name:
       ```python
       # query_slices.py
       import sys
       from perfetto.trace_processor import TraceProcessor

       def query_slices(trace_path, search_term):
           tp = TraceProcessor(file_path=trace_path)
           query = f"""
               SELECT name, COUNT(*) as count, SUM(dur) / 1e6 as total_dur_ms 
               FROM slice WHERE name LIKE '%{search_term}%' 
               GROUP BY name ORDER BY total_dur_ms DESC LIMIT 10;
           """
           qr = tp.query(query)
           for row in qr:
               print(f"{row.name:<80} | {row.count:<10} | {row.total_dur_ms:.2f} ms")

       if __name__ == "__main__":
           query_slices(sys.argv[1], sys.argv[2])
       ```
       Run it as:
       ```bash
       python3 query_slices.py path/to/trace.perfetto-trace "MyComponent"
       ```
       Use the exact printed name in your `TraceSectionMetric` constructor.
