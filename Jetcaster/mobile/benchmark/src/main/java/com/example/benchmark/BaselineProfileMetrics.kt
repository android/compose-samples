/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric

/**
 * Custom Metrics to measure baseline profile effectiveness.
 */
class BaselineProfileMetrics {
    companion object {
        /**
         * A [TraceSectionMetric] that tracks the time spent in JIT compilation.
         *
         * This number should go down when a baseline profile is applied properly.
         */
        @OptIn(ExperimentalMetricApi::class)
        val jitCompilationMetric = TraceSectionMetric("JIT Compiling %", label = "JIT compilation")

        /**
         * A [TraceSectionMetric] that tracks the time spent in class initialization.
         *
         * This number should go down when a baseline profile is applied properly.
         */
        @OptIn(ExperimentalMetricApi::class)
        val classInitMetric = TraceSectionMetric("L%/%;", label = "ClassInit")

        /**
         * Metrics relevant to startup and baseline profile effectiveness measurement.
         */
        @OptIn(ExperimentalMetricApi::class)
        val allMetrics = listOf(
            StartupTimingMetric(),
            FrameTimingMetric(),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
            jitCompilationMetric,
            classInitMetric,
        )
    }
}
