/*
 * Copyright 2022 Google LLC
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

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.example.reply"

    defaultConfig {
        applicationId = "com.example.reply"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Important: change the keystore for a production deployment
        val userKeystore = File(System.getProperty("user.home"), ".android/debug.keystore")
        val localKeystore = rootProject.file("debug_2.keystore")
        val hasKeyInfo = userKeystore.exists()
        create("release") {
            // get from env variables
            storeFile = if (hasKeyInfo) userKeystore else localKeystore
            storePassword = if (hasKeyInfo) "android" else System.getenv("compose_store_password")
            keyAlias = if (hasKeyInfo) "androiddebugkey" else System.getenv("compose_key_alias")
            keyPassword = if (hasKeyInfo) "android" else System.getenv("compose_key_password")
        }
    }

    buildTypes {
        getByName("debug") {

        }

        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro")
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    // Tests can be Robolectric or instrumented tests
    sourceSets {
        val sharedTestDir = "src/sharedTest/java"
        getByName("test") {
            java.srcDir(sharedTestDir)
        }
        getByName("androidTest") {
            java.srcDir(sharedTestDir)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

composeCompiler {
    // Configure compose compiler options if required
    enableStrongSkippingMode = true
}

dependencies {
    implementation(androidx.core.coreKtx)
    implementation(androidx.composeUi.uiToolingPreview)
    debugImplementation(androidx.composeUi.uiTooling)
    implementation(androidx.composeMaterial3.material3)
    implementation(androidx.composeMaterial3.material3WindowSizeClass)
    implementation(androidx.composeMaterial.materialIconsExtended)
    implementation(androidx.lifecycle.lifecycleRuntime)
    implementation(androidx.lifecycle.lifecycleViewmodelCompose)
    implementation(androidx.lifecycle.lifecycleRuntimeCompose)
    implementation(androidx.navigation.navigationCompose)
    implementation(androidx.activity.activityCompose)
    implementation(androidx.window.window)

    implementation("com.google.accompanist:accompanist-adaptive:0.26.2-beta")
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.junit)
    androidTestImplementation(androidx.test.core)
    androidTestImplementation(androidx.test.runner)
    androidTestImplementation(androidx.testEspresso.espressoCore)
    androidTestImplementation(androidx.test.rules)
    androidTestImplementation(androidx.testExt.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(androidx.composeUi.uiTest)
    androidTestImplementation(androidx.composeUi.uiTestJunit4)

    debugImplementation(androidx.composeUi.uiTestManifest)
}
