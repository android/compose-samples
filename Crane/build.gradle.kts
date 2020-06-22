/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}

allprojects {

    // Compose dev14
    val snapshotUrl =
        "https://androidx-dev-prod.appspot.com/snapshots/builds/6602655/artifacts/ui/repository/"
    repositories {
        maven {
            url = uri(snapshotUrl)
        }
        google()
        jcenter()
    }
    // Don't require parens on fun type annotations e.g. `@Composable~()~ () -> Unit`. Remove with KT1.4
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true

            freeCompilerArgs = freeCompilerArgs +
                    listOf("-XXLanguage:+NonParenthesizedAnnotationsOnFunctionalTypes")

            // Compose is now based on the Kotlin 1.4 compiler, but we need to use the 1.3.x Kotlin
            // library due to library compatibility, etc. Therefore we explicit set our apiVersion
            // to 1.3 to fix any warnings. Binary dependencies (such as Compose) can continue to
            // use 1.4 if built with that library.
            // TODO: remove this once we move to Kotlin 1.4
            apiVersion = "1.3"
        }
    }
}
