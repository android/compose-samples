package com.example.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.example.benchmark.helper.HOME_CONTENT_GRID_TAG
import com.example.benchmark.helper.TARGET_PACKAGE_NAME
import com.example.benchmark.helper.forContentGrid
import com.example.benchmark.helper.waitAndFindObject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This benchmark measures app startup performance under various compilation modes,
 * including the impact of Baseline Profiles.
 *
 * Before running this benchmark:
 * 1) Ensure you have a generated Baseline Profile (baseline-prof.txt).
 * 2) Switch your app's active build variant to a release-like variant
 *    that includes `<profileable android:shell="true" />` in its manifest.
 *
 * Run this benchmark from Android Studio or via Gradle to see startup measurements
 * and captured system traces.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Require,
            // Warmup iterations are generally recommended for Baseline Profile benchmarks
            // to allow the JIT compiler to use the profile before measurements.
            // However, for pure startup timing, you might start with 0 or 1.
            // If measuring post-startup jank (like scrolling), more warmup iterations can be beneficial.
            warmupIterations = 1
        )
    )

    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE_NAME,
        metrics = BaselineProfileMetrics.allMetrics,
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = compilationMode, // Apply the specified compilation mode
        setupBlock = {
            killProcess() // Ensure a clean state for each iteration
        }
    ) {
        performStartupAndScroll()
    }

    private fun MacrobenchmarkScope.performStartupAndScroll() {
        // Waits for the first frame of the Activity to be drawn
        startActivityAndWait()

        // 1. Wait for the scrollable home content grid to appear.
        forContentGrid() // This is a helper from your project
        val homeGrid = device.waitAndFindObject(
            By.res(HOME_CONTENT_GRID_TAG),
            5_000, // Adjust timeout as needed
        )

        // 2. Perform a scroll action on the grid.
        // Set gesture margin to avoid triggering system gestures if the grid is near screen edges.
        homeGrid.setGestureMargin(device.displayWidth / 5)

        // Fling downwards. For a LazyVerticalGrid, DOWN is the primary scroll direction.
        homeGrid.fling(Direction.DOWN)

        // 3. Wait for the UI to settle after the scroll.
        device.waitForIdle()

        // Optional: Add further assertions or interactions here.
    }
}
