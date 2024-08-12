/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.spotless)
}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")

subprojects {
    apply {
        plugin(rootProject.libs.plugins.spotless.get().pluginId)
    }
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint(libs.versions.ktlint.get())
                .setEditorConfigPath("${project.rootDir}/.editorconfig")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
        // Additional configuration for Kotlin Gradle scripts
        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            ktlint(libs.versions.ktlint.get()) // Apply ktlint to Gradle Kotlin scripts
            // Look for the first line that doesn't have a block comment (assumed to be the license)
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
    }
}
