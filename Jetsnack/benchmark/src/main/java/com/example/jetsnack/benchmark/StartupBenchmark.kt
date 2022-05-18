/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Base class for benchmarks with different startup modes.
 * Enables app startups from various states of baseline profile or [CompilationMode]s.
 */
@LargeTest
@RunWith(Parameterized::class)
class StartupBenchmark(private val startupMode: StartupMode) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Tests the worst case startup performance. No baseline profile, no pre-compilation.
     */
    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    /**
     * Tests the average startup time a user will see if we didn't include a baseline profile.
     * This is the test you can compare your baseline profile time to to make sure you are actually seeing
     * a benefit.
     */
    @Test
    fun startupPartialNoBaseline() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIterations = 1
        )
    )

    /**
     * Tests the average startup time a user will see with our baseline profile. This is the test
     * you are trying to optimise when working out what to include in your profile. If everything
     * is working correctly this test should have the best performance except for the full
     * compilation.
     */
    @Test
    fun startupBaselineProfile() =
        startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    /**
     * Tests the theoretical best case scenario if everything was pre-compiled.
     */
    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.jetsnack",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = startupMode,
        setupBlock = { pressHome() }
    ) {
        startActivityAndWait()
    }

    companion object {
        @Parameterized.Parameters(name = "mode={0}")
        @JvmStatic
        fun parameters(): List<Array<Any>> = listOf(
            StartupMode.COLD,
            StartupMode.WARM,
            StartupMode.HOT
        ).map { arrayOf(it) }
    }
}
