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
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.example.jetnews"

    defaultConfig {
        applicationId = "com.example.jetnews"
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

    packaging.resources {
        // Multiple dependency bring these files in. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        excludes += "/META-INF/AL2.0"
        excludes += "/META-INF/LGPL2.1"
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    implementation(androidx.composeAnimation.animation)
    implementation(androidx.composeFoundation.foundationLayout)
    implementation(androidx.composeMaterial.materialIconsExtended)
    implementation(androidx.composeMaterial3.material3)
    implementation(androidx.composeMaterial3.material3WindowSizeClass)
    implementation(androidx.composeRuntime.runtimeLivedata)
    implementation(androidx.composeUi.uiToolingPreview)
    debugImplementation(androidx.composeUi.uiTestManifest)
    debugImplementation(androidx.composeUi.uiTooling)
    implementation(androidx.appcompat.appcompat)
    implementation(androidx.activity.activityKtx)
    implementation(androidx.core.coreKtx)
    implementation(androidx.activity.activityCompose)
    implementation(androidx.glance.glance)
    implementation(androidx.glance.glanceAppwidget)
    implementation(androidx.glance.glanceMaterial3)
    implementation(androidx.lifecycle.lifecycleViewmodelKtx)
    implementation(androidx.lifecycle.lifecycleViewmodelSavedstate)
    implementation(androidx.lifecycle.lifecycleLivedataKtx)
    implementation(androidx.lifecycle.lifecycleViewmodelCompose)
    implementation(androidx.lifecycle.lifecycleRuntimeCompose)
    implementation(androidx.navigation.navigationCompose)
    implementation(androidx.window.window)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.google.android.material)

    androidTestImplementation(androidx.test.core)
    androidTestImplementation(androidx.test.runner)
    androidTestImplementation(androidx.testEspresso.espressoCore)
    androidTestImplementation(androidx.test.rules)
    androidTestImplementation(androidx.testExt.junit)
    androidTestImplementation(androidx.composeUi.uiTest)
    androidTestImplementation(androidx.composeUi.uiTestJunit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.junit)
    // Robolectric dependencies
    testImplementation(androidx.composeUi.uiTestJunit4)
    testImplementation(libs.robolectric)
}

tasks.withType<Test>().configureEach {
    systemProperties["robolectric.logging"] = "stdout"
} 
