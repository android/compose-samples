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

plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.spotless) apply false
}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        ratchetFrom = "origin/main"
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint().editorConfigOverride(
                mapOf(
                    "ktlint_code_style" to "android_studio",
                    "ij_kotlin_allow_trailing_comma" to true,
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    // These rules were introduced in ktlint 0.46.0 and should not be
                    // enabled without further discussion. They are disabled for now.
                    // See: https://github.com/pinterest/ktlint/releases/tag/0.46.0
                    "disabled_rules" to
                            "filename," +
                            "annotation,annotation-spacing," +
                            "argument-list-wrapping," +
                            "double-colon-spacing," +
                            "enum-entry-name-case," +
                            "multiline-if-else," +
                            "no-empty-first-line-in-method-block," +
                            "package-name," +
                            "trailing-comma," +
                            "spacing-around-angle-brackets," +
                            "spacing-between-declarations-with-annotations," +
                            "spacing-between-declarations-with-comments," +
                            "unary-op-spacing"
                )
            )
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            // Look for the first line that doesn't have a block comment (assumed to be the license)
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }
}
