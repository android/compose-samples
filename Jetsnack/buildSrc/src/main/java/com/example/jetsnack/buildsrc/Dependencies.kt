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

package com.example.jetsnack.buildsrc

object Versions {
    const val ktlint = "0.37.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha04"
    const val junit = "junit:junit:4.13"

    object Accompanist {
        private const val version = "0.1.7.ui-${AndroidX.UI.snapshot}-SNAPSHOT"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
    }

    object Kotlin {
        private const val version = "1.3.72"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.3.7"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha01"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha01"

        object UI {
            const val snapshot = "6602655"
            const val version = "0.1.0-SNAPSHOT"

            const val core = "androidx.ui:ui-core:$version"
            const val layout = "androidx.ui:ui-layout:$version"
            const val material = "androidx.ui:ui-material:$version"
            const val tooling = "androidx.ui:ui-tooling:$version"
            const val iconsExtended = "androidx.ui:ui-material-icons-extended:$version"
        }

        object Compose {
            const val version = UI.version
            const val kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"

            const val runtime = "androidx.compose:compose-runtime:$version"
        }

        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"

            object Ext {
                private const val version = "1.1.2-rc01"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }
    }
}
