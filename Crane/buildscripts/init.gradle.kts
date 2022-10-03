/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

val ktlintVersion = "0.46.1"

initscript {
    val spotlessVersion = "6.10.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion")
    }
}

allprojects {
    if (this == rootProject) {
        return@allprojects
    }
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint(ktlintVersion).editorConfigOverride(
                            mapOf(
                                "ktlint_code_style" to "android",
                                "ij_kotlin_allow_trailing_comma" to true,
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
    }
}