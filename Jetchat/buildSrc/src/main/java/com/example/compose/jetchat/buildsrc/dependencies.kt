/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.buildsrc

object Versions {
    const val ktlint = "0.39.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha02"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.0.9"

    const val junit = "junit:junit:4.13"

    const val material = "com.google.android.material:material:1.1.0"

    object Kotlin {
        private const val prefix = "org.jetbrains.kotlin:kotlin"
        private const val version = "1.4.20"
        const val stdlib = "$prefix-stdlib-jdk8:$version"
        const val gradlePlugin = "$prefix-gradle-plugin:$version"
        const val extensions = "$prefix-android-extensions:$version"
    }

    object Coroutines {
        private const val prefix = "org.jetbrains.kotlinx:kotlinx-coroutines"
        private const val version = "1.4.2"
        const val core = "$prefix-core:$version"
        const val android = "$prefix-android:$version"
        const val test = "$prefix-test:$version"
    }

    object Accompanist {
        private const val version = "0.4.0"
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:$version"
    }

    object AndroidX {
        private const val prefix = "androidx"
        const val appcompat = "$prefix.appcompat:appcompat:1.2.0-rc01"
        const val coreKtx = "$prefix.core:core-ktx:1.5.0-alpha04"

        object Compose {
            private const val prefix = AndroidX.prefix + ".compose"
            const val snapshot = ""
            const val version = "1.0.0-alpha08"

            const val foundation = "$prefix.foundation:foundation:$version"
            const val layout = "$prefix.foundation:foundation-layout:$version"
            const val material = "$prefix.material:material:$version"
            const val materialIconsExtended = "$prefix.material:material-icons-extended:$version"
            const val runtime = "$prefix.runtime:runtime:$version"
            const val runtimeLivedata = "$prefix.runtime:runtime-livedata:$version"
            const val tooling = "$prefix.ui:ui-tooling:$version"
            const val test = "$prefix.ui:ui-test:$version"
            const val uiTest = "$prefix.ui:ui-test-junit4:$version"
            const val uiUtil = "$prefix.ui:ui-util:${version}"
            const val viewBinding = "$prefix.ui:ui-viewbinding:$version"
        }

        object Navigation {
            private const val prefix = AndroidX.prefix + ".navigation:navigation"
            private const val version = "2.3.0"
            const val fragment = "$prefix-fragment-ktx:$version"
            const val uiKtx = "$prefix-ui-ktx:$version"
        }

        object Test {
            private const val prefix = AndroidX.prefix + ".test"
            private const val version = "1.2.0"
            const val core = "$prefix:core:$version"
            const val rules = "$prefix:rules:$version"

            object Ext {
                private const val version = "1.1.2-rc01"
                const val junit = "$prefix.ext:junit-ktx:$version"
            }

            const val espressoCore = "$prefix.espresso:espresso-core:3.2.0"
        }

        object Lifecycle {
            private const val prefix = AndroidX.prefix + ".lifecycle:lifecycle"
            private const val version = "2.2.0"
            const val extensions = "$prefix-extensions:$version"
            const val livedata = "$prefix-livedata-ktx:$version"
            const val viewmodel = "$prefix-viewmodel-ktx:$version"
        }
    }
}

object Urls {
    const val composeSnapshotRepo = "https://androidx.dev/snapshots/builds/" +
        "${Libs.AndroidX.Compose.snapshot}/artifacts/repository/"
}
