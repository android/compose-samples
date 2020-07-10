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
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap/")
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4-M3")
    }
}

allprojects {

    // Compose dev15
    val snapshotUrl =
        "https://androidx-dev-prod.appspot.com/snapshots/builds/6695716/artifacts/ui/repository/"
    repositories {
        maven {
            url = uri(snapshotUrl)
        }
        google()
        jcenter()
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap/")
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true
        }
    }
}
