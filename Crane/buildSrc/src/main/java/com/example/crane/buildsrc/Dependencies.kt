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

package com.example.crane.buildsrc

object Versions {
    const val ktLint = "0.37.2"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha05"
    const val ktLint = "com.pinterest:ktlint:${Versions.ktLint}"
    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val googleMaps = "com.google.android.gms:play-services-maps:17.0.0"

    object Kotlin {
        private const val version = "1.4-M3"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha01"
        const val activityKtx = "androidx.activity:activity-ktx:1.1.0"

        object Compose {
            const val snapshot = "6695716"
            const val version = "0.1.0-SNAPSHOT"

            const val kotlinCompilerVersion = "1.4.0-dev-withExperimentalGoogleExtensions-20200720"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val material = "androidx.compose.material:material:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val layout = "androidx.compose.foundation:foundation-layout:$version"
            const val animation = "androidx.compose.animation:animation:$version"
        }

        object UI {
            const val tooling = "androidx.ui:ui-tooling:${Compose.version}"
        }

        object Lifecycle {
            private const val version = "2.2.0"

            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
        }
    }
}

object Urls {
    const val kotlinEap = "https://dl.bintray.com/kotlin/kotlin-eap/"
    const val snapshotUrl = "https://androidx-dev-prod.appspot.com/snapshots/builds/" + // Dev15
            "${Libs.AndroidX.Compose.snapshot}/artifacts/ui/repository/"
}