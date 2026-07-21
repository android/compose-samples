Project: /quality/_project.yaml
book_path: /quality/_book.yaml
description: This document explains how to use Jetpack Macrobenchmark to measure the performance improvements of Baseline Profiles, focusing on app startup times like time to initial and full display, using various compilation modes.
keywords_public: Android,performance,Baseline Profiles,Macrobenchmark,app startup,measurement,CompilationMode,startup timing,initial display,full display

{% include "_shared/_javlin.html" %}
{% include "studio/profile/_profile_javlin.html" %}
{% include "_shared/_versions.html" %}

# Benchmark Baseline Profiles with Macrobenchmark library {:#measuring-optimization}

We recommend using [Jetpack Macrobenchmark][1] to test how an app performs when
Baseline Profiles are enabled, and then compare those results to a benchmark
with Baseline Profiles disabled. With this approach, you can measure app startup
time&mdash;both time to initial and full display&mdash;or runtime rendering
performance to see if the frames produced can cause jank.

Macrobenchmarks let you control pre-measurement compilation using the
[`CompilationMode`][2] API. Use different `CompilationMode` values to compare
performance with different compilation states. The following code snippet shows
how to use the `CompilationMode` parameter to measure the benefit of Baseline
Profiles:

<pre class="prettyprint lang-kotlin">
@RunWith(AndroidJUnit4ClassRunner::class)
class ColdStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // No ahead-of-time (AOT) compilation at all. Represents performance of a
    // fresh install on a user's device if you don't enable Baseline Profiles—
    // generally the worst case performance.
    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    // Partial pre-compilation with Baseline Profiles. Represents performance of
    // a fresh install on a user's device.
    @Test
    fun startupPartialWithBaselineProfiles() =
        startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    // Partial pre-compilation with some just-in-time (JIT) compilation.
    // Represents performance after some app usage.
    @Test
    fun startupPartialCompilation() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIteration = 3
        )
    )

    // Full pre-compilation. Generally not representative of real user
    // experience, but can yield more stable performance metrics by removing
    // noise from JIT compilation within benchmark runs.
    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.macrobenchmark.target",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
        }
    ) {
        uiAutomator {
            startApp(packageName)
            onElement(5_000) { viewIdResourceName == "my-content"}
        }
    }
}
</pre>

Caution: Run the benchmarks on a physical device to measure real world
performance. Measuring performance on an Android emulator likely provides
incorrect results, because resources are shared with its hosting machine.

In the following screenshot, you can see the results directly in Android Studio
for the [Now in Android sample][3]{:.external} app ran on Google Pixel 7. The
results show that app startup is fastest when using Baseline Profiles
(**229.0ms**) in contrast with no compilation (**324.8ms**).

<figure id = "results of ColdStartupBenchmark">
    <img src="/topic/performance/images/benchmark_images/baselineprofile_measure_effectiveness.jpg"
    alt="results of ColdstartupBenchmark">
    <figcaption><b>Figure 1.</b> Results of <code>ColdStartupBenchmark</code>
    showing time to initial display for no compilation (324ms), full compilation
    (315ms), partial compilation (312ms), and Baseline Profiles
    (229ms).</figcaption>
</figure>

Tip: You can also retrieve the results as a JSON file to parse them as part of
your CI pipeline. For more information, see [Benchmarking in CI][4].

While the previous example shows app startup results captured with
[`StartupTimingMetric`][5], there are other important metrics worth considering,
such as [`FrameTimingMetric`][6]. For more information about all the types of
metrics, see [Capture Macrobenchmark metrics][7].

## Time to full display {:#time-to-full-display}

The previous example measures the [time to initial display][8] (TTID), which is
the time taken by the app to produce its first frame. However, this doesn't
necessarily reflect the time until the user can start interacting with your app.
The [time to full display][9] (TTFD) metric is more useful in measuring and
optimizing the code paths necessary to have a fully useable app state.

We recommend optimizing for both TTID and TTFD, as both are important. A low
TTID helps the user see that the app is actually launching. Keeping the TTFD
short is important to help ensure that the user can interact with the app
quickly.

For strategies on reporting when the app UI is fully drawn, see [Improve
startup timing accuracy][10].

{% verbatim %}<devsite-recommendations>{% endverbatim %}
## Recommended for you

*   Note: link text is displayed when JavaScript is off
*   [Write a Macrobenchmark][11]
*   [Capture Macrobenchmark metrics][12]
*   [Write automated tests with UI Automator][13]
*   [App startup analysis and optimization {:#app-startup-analysis-optimization}][14]

{%
verbatim
%}</devsite-recommendations>{% endverbatim %}

[1]: /topic/performance/benchmarking/macrobenchmark-overview
[2]: /reference/androidx/benchmark/macro/CompilationMode
[3]: https://goo.gle/nia
[4]: /topic/performance/benchmarking/benchmarking-in-ci
[5]: /reference/androidx/benchmark/macro/StartupTimingMetric
[6]: /reference/androidx/benchmark/macro/FrameTimingMetric
[7]: /topic/performance/benchmarking/macrobenchmark-metrics
[8]: /topic/performance/vitals/launch-time#time-initial
[9]: /topic/performance/vitals/launch-time#time-full
[10]: /topic/performance/benchmarking/macrobenchmark-metrics#startup-accuracy
[13]: /training/testing/other-components/ui-automator