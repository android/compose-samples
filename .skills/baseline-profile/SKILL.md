---
name: baseline-profile
description: Generate and integrate Baseline Profiles to optimize Android application startup and scrolling performance.
---

# Android Baseline Profiles

Use this skill to set up, generate, and integrate Baseline Profiles to improve application startup time and reduce first-run scroll jank.

---

## 1. Setup

### A. Add the Baseline Profile Plugin

Ensure the `androidx.baselineprofile` plugin is configured in your project.

1. **Root `build.gradle.kts`**:

   ```kotlin
   plugins {
       alias(libs.plugins.androidx.baselineprofile) apply false
   }
   ```

2. **App `:app` `build.gradle.kts`**:

   ```kotlin
   plugins {
       id("com.android.application")
       id("androidx.baselineprofile")
   }

   dependencies {
       // Link the benchmark module as the producer of baseline profiles
       baselineProfile(project(":benchmark"))
   }
   ```

3. **Benchmark `:benchmark` `build.gradle.kts`**:
   ```kotlin
   plugins {
       id("com.android.test")
       id("kotlin-android")
       id("androidx.baselineprofile")
   }
   ```

---

## 2. Profile Generation

### A. Standard Generation (Using the Plugin)

1. **Write the Generator Class** in `src/main/java/` of your `:benchmark` module:

   ```kotlin
   package com.example.benchmark

   import androidx.baselineprofile.SnapshotFilters
   import androidx.benchmark.macro.junit4.BaselineProfileRule
   import androidx.test.ext.junit.runners.AndroidJUnit4
   import org.junit.Rule
   import org.junit.Test
   import org.junit.runner.RunWith

   @RunWith(AndroidJUnit4::class)
   class GenerateBaselineProfile {
       @get:Rule
       val baselineProfileRule = BaselineProfileRule()

       @Test
       fun generate() = baselineProfileRule.collect(
           packageName = "com.example.app",
           filterPredicate = SnapshotFilters.PackageAndPath
       ) {
           pressHome()
           startActivityAndWait()

           // Exercise critical user journeys (CUJs)
           val list = device.findObject(By.scrollable(true))
           list?.setGestureMargin(device.displayWidth / 5)
           list?.drag(Point(list.visibleCenter.x, list.visibleBounds.bottom - 100), Speed.MEDIUM)
           device.waitForIdle()
       }
   }
   ```

2. **Run the Generator**:
   ```bash
   ./gradlew :app:generateBaselineProfile -P android.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR
   ```
   The plugin saves the profile to `app/src/main/generated/baselineProfiles/baseline-prof.txt`.

### B. Manual Generation (Fallback)

If the Gradle plugin fails or you encounter Android 16 compatibility issues, refer to the manual generation steps in [Manual Generation Fallback](/reference/debug-baseline-profiles.md#force-compilation) or run a manual test class:

1. Reset compilation: `adb shell cmd package compile --reset <PACKAGE_NAME>`
2. Run journeys multiple times.
3. Flush profiles: `adb shell killall -s SIGUSR1 <PACKAGE_NAME>`
4. Dump profiles: `adb shell pm dump-profiles --dump-classes-and-methods <PACKAGE_NAME>`
5. Extract from `/data/misc/profman/<PACKAGE_NAME>-primary.prof.txt` to `app/src/main/baseline-prof.txt`.

---

## 3. Verification & Diagnostics

To verify that your profiles are correctly packaged, compiled on-device, and applied, refer to the following reference guides:

- **Verify Packaging (APK Analyzer)**: Confirm the presence of `baseline.prof` in the APK or AAB. See [Verify Profile Packaging](/reference/debug-baseline-profiles.md#install-issues).
- **Verify DEX Layout (R8 Verification)**: Confirm Startup Profile application via `r8.json` or by inspecting the primary DEX file. See [Verify Startup Profile DEX](/reference/debug-baseline-profiles.md#verify-startup-profile-dex).
- **Verify On-Device Compilation**:
  - Use `ProfileVerifier` in code to query installation status. See [Use ProfileVerifier](/reference/debug-baseline-profiles.md#install-issues).
  - Use `adb shell dumpsys package dexopt` to check for `status=speed-profile`. See [Check Compilation State via ADB](/reference/debug-baseline-profiles.md#check-compilation-state).
  - Force immediate compilation: `adb shell cmd package compile -r bg-dexopt <PACKAGE_NAME>`. See [Force Compilation](/reference/debug-baseline-profiles.md#force-compilation).

---

## 4. Measurement & Benchmarking Best Practices

For best practices on measuring the impact of Baseline Profiles and isolating them from library-contributed profiles:

- **Accurate Startup Metrics**: Measure TTID and TTFD, and report TTFD in Compose using `ReportDrawn`. See [Correctly Benchmark Startup](/reference/debug-baseline-profiles.md#correct-benchmark-startup).
- **Isolating Custom Profiles**: Create a `releaseWithoutCustomProfile` build variant to compare the impact of custom profiles vs. library-only profiles. See [Isolating Custom Profiles](/reference/debug-baseline-profiles.md#library-custom-profiles).
- **Minimize Benchmark Noise**: Avoid I/O and network requests during startup. See [Avoid I/O-bound Startup](/reference/debug-baseline-profiles.md#avoid-io).

---

## 5. Troubleshooting & Common Issues

- **Emulator Block on Generation**: If generation fails on emulators, run with the enabledRules argument. See [Build Issues on Emulator](/reference/debug-baseline-profiles.md#build-issues).
- **Signature Mismatches**: Refer to [benchmark-helper Troubleshooting](/benchmark-helper/SKILL.md#6-signature-mismatch-install_failed_update_incompatible) to resolve installation conflicts.
