Project: /quality/\_project.yaml
book_path: /quality/\_book.yaml
description: Best practices to diagnose problems and verify Baseline Profiles work correctly for optimal performance benefit.
keywords_public: Android performance, Baseline Profiles, debugging, build issues, installation issues, app startup, ProfileVerifier, benchmarking, Gradle, APK

{% include "_shared/_javlin.html" %}
{% include "studio/_common/_gradle_javlin2.html" %}
{# disableFinding(PARAGRAPH_CONSECUTIVE) #}

# Debug Baseline Profiles {:#debug-baseline-profiles}

This document provides best practices and troubleshooting steps to help diagnose
problems and make sure your Baseline Profiles work correctly to provide the most
benefit.

## Build issues {:#build-issues}

If you have copied the Baseline Profiles example in the [Now in Android][1]
sample app, you might encounter test failures during the Baseline Profile task
stating that the tests cannot be run on an emulator:

```bash
./gradlew assembleDemoRelease
Starting a Gradle Daemon (subsequent builds will be faster)
Calculating task graph as no configuration cache is available for tasks: assembleDemoRelease
Type-safe project accessors is an incubating feature.

> Task :benchmarks:pixel6Api33DemoNonMinifiedReleaseAndroidTest
Starting 14 tests on pixel6Api33

com.google.samples.apps.nowinandroid.foryou.ScrollForYouFeedBenchmark > scrollFeedCompilationNone[pixel6Api33] FAILED
        java.lang.AssertionError: ERRORS (not suppressed): EMULATOR
        WARNINGS (suppressed):
        ...
```

The failures occur because Now in Android uses a Gradle-managed device for
Baseline Profile generation. The failures are expected, because you generally
shouldn't run performance benchmarks on an emulator. However, since you're not
collecting performance metrics when you generate Baseline Profiles, you can run
Baseline Profile collection on emulators for convenience. To use Baseline
Profiles with an emulator, perform the build and installation from the
command-line, and set an argument to enable Baseline Profiles rules:

```bash
installDemoRelease -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
```

Alternatively, you can create a custom run configuration in Android Studio to
enable Baseline Profiles on emulators by selecting
**Run > Edit Configurations**:

<figure class="dex layout optimizations">
  <img src = "/topic/performance/images/studio/run-config-nowinandroid-baselineprofiles.png"
    alt="Add a custom run configuration to create Baseline Profiles in Now in Android">
  <figcaption><b>Figure 1.</b> Add a custom run configuration to create Baseline
  Profiles in Now in Android.</figcaption>
</figure>

Note: Creating a custom run configuration isn't necessary when you add Baseline
Profiles using the Baseline Profile module wizard in Android Studio, which is
recommended. The wizard creates a run configuration for you.

## Verify profile installation and application {:#install-issues}

To check that the APK or Android App Bundle (AAB) you're inspecting is from a
build variant that includes Baseline Profiles, do the following:

1. In Android Studio, select **Build > Analyze APK**.
1. Open your AAB or APK.
1. Confirm that the `baseline.prof` file exists:
   - If you're inspecting an AAB, the profile is at
     `/BUNDLE-METADATA/com.android.tools.build.profiles/baseline.prof`.
   - If you're inspecting an APK, the profile is at
     `/assets/dexopt/baseline.prof`.

     The presence of this file is the first sign of a correct build
     configuration. If it's missing, it means the Android Runtime won't receive
     any pre-compilation instructions at install time.

     <figure class="dex layout optimizations">
      <img src = "/topic/performance/images/studio/baseline-profile-in-apk.png"
      alt="Check for a Baseline Profile using APK Analyzer in Android Studio"
      width="500"> <figcaption><b>Figure 2.</b> Check for a Baseline Profile
      using APK Analyzer in Android Studio.</figcaption> </figure>

Baseline Profiles need to be compiled on the device running the app. When you
install non-debuggable builds using Android Studio or the Gradle wrapper
command-line tool, on-device compilation happens automatically. If you install
the app from the Google Play Store, Baseline Profiles are compiled during
background device updates rather than at install time. When the app is installed
using other tools, the Jetpack [`ProfileInstaller`][3] library is responsible
for enqueueing the profiles for compilation during the next background DEX
optimization process.

In those cases, if you want to make sure your Baseline Profiles are being used,
you might need to [force compilation of Baseline Profiles][4].
[`ProfileVerifier`][5] lets you query the status of the profile installation and
compilation, as shown in the following example:

Note: If you're using an AGP version lower than 8.4 and installing the app using
Android Studio or the Gradle Wrapper command line tool (or tools other than the
Play Store), the Baseline Profile compilation doesn't happen automatically. For
more information, see [Minimum recommended stable versions][6].

<div>
{{javlin_1_kotlin}}
<pre class="prettyprint lang-kotlin">
private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
...
override fun onResume() {
super.onResume()
lifecycleScope.launch {
logCompilationStatus()
}
}

private suspend fun logCompilationStatus() {
withContext(Dispatchers.IO) {
val status = ProfileVerifier.getCompilationStatusAsync().await()
when (status.profileInstallResultCode) {
RESULT_CODE_NO_PROFILE ->
Log.d(TAG, "ProfileInstaller: Baseline Profile not found")
RESULT_CODE_COMPILED_WITH_PROFILE ->
Log.d(TAG, "ProfileInstaller: Compiled with profile")
RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION ->
Log.d(TAG, "ProfileInstaller: Enqueued for compilation")
RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING ->
Log.d(TAG, "ProfileInstaller: App was installed through Play store")
RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST ->
Log.d(TAG, "ProfileInstaller: PackageName not found")
RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ ->
Log.d(TAG, "ProfileInstaller: Cache file exists but cannot be read")
RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE ->
Log.d(TAG, "ProfileInstaller: Can't write cache file")
RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION ->
Log.d(TAG, "ProfileInstaller: Enqueued for compilation")
else ->
Log.d(TAG, "ProfileInstaller: Profile not compiled or enqueued")
}
}
}

</pre>
{{javlin_2_java}}
<pre class="prettyprint lang-java">
{% htmlescape %}
public class MainActivity extends ComponentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();

        logCompilationStatus();
    }

    private void logCompilationStatus() {
         ListeningExecutorService service = MoreExecutors.listeningDecorator(
                Executors.newSingleThreadExecutor());
        ListenableFuture<ProfileVerifier.CompilationStatus> future =
                ProfileVerifier.getCompilationStatusAsync();
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(CompilationStatus result) {
                int resultCode = result.getProfileInstallResultCode();
                if (resultCode == RESULT_CODE_NO_PROFILE) {
                    Log.d(TAG, "ProfileInstaller: Baseline Profile not found");
                } else if (resultCode == RESULT_CODE_COMPILED_WITH_PROFILE) {
                    Log.d(TAG, "ProfileInstaller: Compiled with profile");
                } else if (resultCode == RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION) {
                    Log.d(TAG, "ProfileInstaller: Enqueued for compilation");
                } else if (resultCode == RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING) {
                    Log.d(TAG, "ProfileInstaller: App was installed through Play store");
                } else if (resultCode == RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST) {
                    Log.d(TAG, "ProfileInstaller: PackageName not found");
                } else if (resultCode == RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ) {
                    Log.d(TAG, "ProfileInstaller: Cache file exists but cannot be read");
                } else if (resultCode
                        == RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE) {
                    Log.d(TAG, "ProfileInstaller: Can't write cache file");
                } else if (resultCode == RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION) {
                    Log.d(TAG, "ProfileInstaller: Enqueued for compilation");
                } else {
                    Log.d(TAG, "ProfileInstaller: Profile not compiled or enqueued");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG,
                        "ProfileInstaller: Error getting installation status: " + t.getMessage());
            }
        }, service);
    }

}
{% endhtmlescape %}

</pre>
{{javlin_3_end}}
</div>

The following result codes provide hints for the cause of some issues:

`RESULT_CODE_COMPILED_WITH_PROFILE`
: The profile is installed, compiled, and is used whenever the app is run. This
is the result you want to see.

`RESULT_CODE_ERROR_NO_PROFILE_EMBEDDED`
: No profile is found in the APK being run. Ensure that you're using a build
variant that includes Baseline Profiles if you see this error, and that the APK
contains a profile.

`RESULT_CODE_NO_PROFILE`
: No profile was installed for this app when installing the app through app
store or package manager. The main reason for this to error code is that profile
installer did not run due to [`ProfileInstallerInitializer`][7] being disabled.
Note that when this error is reported an embedded profile was still found in the
application APK. When an embedded profile is not found, the error code returned
is `RESULT_CODE_ERROR_NO_PROFILE_EMBEDDED`.

`RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION`
: A profile is found in the APK or AAB and is enqueued for compilation. When a
profile is installed by `ProfileInstaller`, it is queued for compilation the
next time background DEX optimization is run by the system. The profile isn't
active until compilation completes. Don't attempt to benchmark your Baseline
Profiles until compilation is complete. You might need to [force compilation of
Baseline Profiles][4]. This error won't occur when app is installed from Play
Store or package manager on devices running Android 9 (API 28) and higher,
because compilation is performed during installation.

`RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING`
: A non-matching profile is installed and the app has been compiled with it.
This is the result of installation through Google Play store or package manager.
Note that this result differs from `RESULT_CODE_COMPILED_WITH_PROFILE` because
the non-matching profile will only compile any methods that are still shared
between the profile and the app. The profile is effectively smaller than
expected, and fewer methods will be compiled than were included in the Baseline
Profile.

`RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE`
: `ProfileVerifier` can't write the verification result cache file. This can
either happen because something is wrong with the app folder permissions or if
there isn't enough free disk space on the device.

`RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION`
: ProfileVerifier` is running on an unsupported API version of Android.
ProfileVerifier` supports only Android 9 (API level 28) and higher.

`RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST`
: A [`PackageManager.NameNotFoundException`][8] is thrown when querying the
[`PackageManager`][9] for the app package. This should rarely happen. Try
uninstalling the app and reinstalling everything.

`RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ`
: A previous verification result cache file exists, but it can't be read. This
should rarely happen. Try uninstalling the app and reinstalling everything.

### Use `ProfileVerifier` in production {:#use-profileverifier}

In production, you can use `ProfileVerifier` in conjunction with
analytics-reporting libraries, such as [Google Analytics for Firebase][10], to
generate analytics events indicating the profile status. For example, this
alerts you quickly if a new app version is released that doesn't contain
Baseline Profiles.

### Force compilation of Baseline Profiles {:#force-compilation}

If the compilation status of your Baseline Profiles is
`RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION`, you can force immediate
compilation using [`adb`][11]:

```bash
adb shell cmd package compile -r bg-dexopt {{ '<var>' }}PACKAGE_NAME{{ '</var>' }}
```

### Check Baseline Profile compilation state without `ProfileVerifier` {:#check-compilation-state}

If you aren't using `ProfileVerifier`, you can check the compilation state using
`adb`, although it doesn't give as deep insights as `ProfileVerifier`:

```bash
adb shell dumpsys package dexopt | grep -A 2 {{ '<var>' }}PACKAGE_NAME{{ '</var>' }}
```

Using `adb` produces something similar to the following:

```bash
  [com.google.samples.apps.nowinandroid.demo]
    path: /data/app/~~dzJiGMKvp22vi2SsvfjkrQ==/com.google.samples.apps.nowinandroid.demo-7FR1sdJ8ZTy7eCLwAnn0Vg==/base.apk
{{'<b>'}}      arm64: [status=speed-profile] [reason=bg-dexopt] [primary-abi]{{'</b>'}}
        [location is /data/app/~~dzJiGMKvp22vi2SsvfjkrQ==/com.google.samples.apps.nowinandroid.demo-7FR1sdJ8ZTy7eCLwAnn0Vg==/oat/arm64/base.odex]
```

The status value indicates the profile compilation status and is one of the
following values:

| Compilation status          | Meaning                                      |
| --------------------------- | -------------------------------------------- |
| `speed{{'&#8209;'}}profile` | A compiled profile exists and is being used. |
| `verify`                    | No compiled profile exists.                  |

A `verify` status doesn't mean that the APK or AAB doesn't contain a profile,
because it can be queued for compilation by the next background DEX optimization
task.

The reason value indicates what triggers the compilation of the profile and is
one of the following values:

| Reason                   | Meaning                                               |
| ------------------------ | ----------------------------------------------------- |
| `install{{'&#8209;'}}dm` | A Baseline Profile was compiled manually or by Google |

: :Play when the app is installed. :
|`bg{{'&#8209;'}}dexopt` |A profile was compiled while your device was idle. |
: :This might be a Baseline Profile, or it might be a :
: :profile collected during app usage. :
|`cmdline` |The compilation was triggered using adb. |
: :This might be a Baseline Profile, or it might be a :
: :profile collected during app usage. :

## Verify Startup Profile application to DEX and `r8.json` {:#verify-startup-profile-dex}

Startup Profile rules are used at build time by R8 to optimize the layout of
classes in your DEX files. This build-time optimization is different from how
Baseline Profiles (`baseline.prof`) are used, as they are packaged within the
APK or AAB for ART to perform on-device compilation. Because Startup Profile
rules are applied during the build process itself, there isn't a separate
`startup.prof` file within your APK or AAB to inspect. The effect of Startup
Profiles is visible in the DEX file layout instead.

### Inspect DEX arrangement with `r8.json` (Recommended for AGP 8.8 or higher) {:#check-r8}

For projects using Android Gradle Plugin (AGP) 8.8 or higher, you can verify
whether the Startup Profile was applied by inspecting the generated `r8.json`
file. This file is packaged within your AAB.

1. Open your AAB archive and locate the `r8.json` file.
1. Search the file for the `dexFiles` array, which lists the generated DEX
   files.
1. Look for a `dexFiles` object that contains the key-value pair `"startup":
true`. This explicitly indicates that the Startup Profile rules were applied
   to optimize the layout of that specific DEX file.

   ```json
   "dexFiles": [
    {
      "checksum": "...",
      "startup": true // This flag confirms profile application to this DEX file
    },
    // ... other DEX files
   ]
   ```

### Inspect DEX arrangement for all AGP versions {:#inspect-dex-arrangement}

If you're using an AGP version lower than 8.8, inspecting the DEX files is the
primary way to verify that your Startup Profile has been correctly applied. You
can also use this method if you are using AGP 8.8 or higher and want to manually
check the DEX layout. For example, if you aren't seeing the expected performance
improvements. To inspect the DEX arrangement, do the following:

1. Open your AAB or APK using **Build > Analyze APK** in Android Studio.
1. Navigate to the first DEX file. For example, `classes.dex`.
1. Inspect the contents of this DEX file. You should be able to verify that the
   critical classes and methods defined in your Startup Profile file
   (`startup-prof.txt`) are present in this primary DEX file. A
   successful application means that these startup-critical components are
   prioritized for faster loading.

## Performance issues {:#performance-issues}

This section shows some best practices for correctly defining and benchmarking
your Baseline Profiles to get the most benefits from them.

### Correctly benchmark startup metrics {:#correct-benchmark-startup}

Your Baseline Profiles will be more effective if your startup metrics are
well-defined. The two key metrics are [time to initial display (TTID)][12] and
[time to full display (TTFD)][13].

TTID is when the app draws its first frame. It's important to keep this as short
as possible because displaying something shows the user that the app is running.
You can even display an indeterminate progress indicator to show that the app is
responsive.

TTFD is when the app can actually be interacted with. It's important to keep
this as short as possible to avoid user frustration. If you correctly signal
TTFD, you're telling the system that the code that's run on the way to TTFD is
part of app startup. The system is more likely to place this code in the profile
as a result.

Keep both TTID and TTFD as low as possible to make your app feel responsive.

The system is able to detect TTID, display it in Logcat, and report it as part
of startup benchmarks. However, the system is unable to determine TTFD, and it's
the app's responsibility to report when it reaches a fully drawn interactive
state. You can do this by calling [`reportFullyDrawn()`][14], or
[`ReportDrawn`][15] if you're using Jetpack Compose. If you have multiple
background tasks that all need to complete before the app is considered fully
drawn, then you can use [`FullyDrawnReporter`][16], as described in [Improve
startup timing accuracy][17].

#### Library profiles and custom profiles {:#library-custom-profiles}

When benchmarking the impact of profiles, it can be difficult to separate the
benefits of your app's profiles from profiles contributed by libraries, such as
Jetpack libraries. When you build your APK the Android Gradle plugin adds any
profiles in library dependencies as well as your custom profile. This is good
for optimizing overall performance, and is recommended for your release builds.
However, it makes it hard to measure how much additional performance gain comes
from your custom profile.

A quick way to manually see the additional optimization provided by your custom
profile is to remove it, and run your benchmarks. Then replace it and run your
benchmarks again. Comparing the two will show you the optimizations provided by
the library profiles alone, and the library profiles plus your custom profile.

An automatable way of comparing profiles is by creating a new build variant that
contains only the library profiles and not your custom profile. Compare
benchmarks from this variant to the release variant that contains both the
library profiles and your custom profiles. The following example shows how
to set up the variant that includes only library profiles. Add a new variant
named `releaseWithoutCustomProfile` to your profile consumer module, which is
typically your app module:

<div>
{{gsnippet_kotlin}}
<pre class="prettyprint lang-kotlin">
android {
  ...
  buildTypes {
    ...
    // Release build with only library profiles.
    create("releaseWithoutCustomProfile") {
      initWith(release)
    }
    ...
  }
  ...
}
...
dependencies {
  ...
  // Remove the baselineProfile dependency.
  // baselineProfile(project(":baselineprofile"))
}

baselineProfile {
variants {
create("release") {
from(project(":baselineprofile"))
}
}
}

</pre>
{{gsnippet_groovy}}
<pre class="prettyprint lang-groovy">
android {
  ...
  buildTypes {
    ...
    // Release build with only library profiles.
    releaseWithoutCustomProfile {
      initWith(release)
    }
    ...
  }
  ...
}
...
dependencies {
  ...
  // Remove the baselineProfile dependency.
  // baselineProfile ':baselineprofile"'
}

baselineProfile {
variants {
release {
from(project(":baselineprofile"))
}
}
}

</pre>
{{gsnippet_end}}
</div>

The preceding code example removes the `baselineProfile` dependency from all
variants and selectively applies it to only the `release` variant. It might seem
counterintuitive that the library profiles are still being added when the
dependency on the profile producer module is removed. However, this module is
only responsible for generating your custom profile. The Android Gradle
plugin is still running for all variants, and is responsible for including
library profiles.

You also need to add the new variant to the profile generator module. In this
example the producer module is named `:baselineprofile`.

<div>
{{gsnippet_kotlin}}
<pre class="prettyprint lang-kotlin">
android {
  ...
    buildTypes {
      ...
      // Release build with only library profiles.
      create("releaseWithoutCustomProfile") {}
      ...
    }
  ...
}
</pre>
{{gsnippet_groovy}}
<pre class="prettyprint lang-groovy">
android {
  ...
    buildTypes {
      ...
      // Release build with only library profiles.
      releaseWithoutCustomProfile {}
      ...
    }
  ...
}
</pre>
{{gsnippet_end}}
</div>

When you run the benchmark from Android Studio, select a
`releaseWithoutCustomProfile` variant to measure performance with only library
profiles, or select a `release` variant to measure performance with library
and custom profiles.

### Avoid I/O-bound app startup {:#avoid-io}

If your app is performing a lot of I/O calls or networks calls during startup,
it can negatively affect both app startup time and the accuracy of your startup
benchmarking. These heavyweight calls can take indeterminate amounts of time
that can vary over time and even between iterations of the same benchmark. I/O
calls are generally better than network calls, because the latter can be
affected by factors external to the device and on the device itself. Avoid
network calls during startup. Where using one or other is unavoidable, use I/O.

We recommend making your app architecture support app startup without network or
I/O calls, even if only to use it when benchmarking startup. This helps ensure
the lowest possible variability between different iterations of your benchmarks.

If your app uses Hilt, you can provide fake I/O-bound implementations when
benchmarking in [Microbenchmark and Hilt][18].

### Cover all important user journeys {:#cover-user-journeys}

It's important to accurately cover all of the important user journeys in your
Baseline Profile generation. Any user journeys that aren't covered won't be
improved by Baseline Profiles. The most effective baseline profiles include all
common startup user journeys as well as performance-sensitive in-app user
journeys such as scrolling lists.

### A/B testing compile-time profile changes {:#ab-testing-profile-changes}

Since Startup and Baseline Profiles are a compile-time optimization, directly
A/B testing different APKs using Google Play Store is generally not supported
for production releases. To assess the impact in a production-like environment,
consider the following approaches:

- **Off-cycle release**: Upload an off-cycle release to a small percentage of
  your user base that only includes the profile change. This lets you gather
  real-world metrics on the performance difference.

- **Local benchmarking**: Locally benchmark your app with and without the
  profile applied. However, be aware that local benchmarking shows you the
  best-case scenario for profiles, as it doesn't include the effects of [Cloud
  Profiles][19] from ART that are present in production devices.

[1]: https://github.com/android/nowinandroid
[2]: /topic/performance/baselineprofiles/dex-layout-optimizations
[3]: /jetpack/androidx/releases/profileinstaller
[4]: #force-compilation
[5]: /reference/androidx/profileinstaller/ProfileVerifier
[6]: /topic/performance/baselineprofiles/overview#recommended-versions
[7]: /reference/androidx/profileinstaller/ProfileInstallerInitializer
[8]: /reference/android/content/pm/PackageManager.NameNotFoundException
[9]: /reference/android/content/pm/PackageManager
[10]: https://firebase.google.com/products/analytics
[11]: /tools/adb
[12]: /topic/performance/vitals/launch-time#time-initial
[13]: /topic/performance/vitals/launch-time#time-full
[14]: /reference/androidx/activity/ComponentActivity#reportFullyDrawn()
[15]: /reference/kotlin/androidx/activity/compose/ReportDrawn.composable#ReportDrawn()
[16]: /reference/androidx/activity/FullyDrawnReporter
[17]: /topic/performance/benchmarking/macrobenchmark-metrics#startup-accuracy
[18]: https://github.com/android/performance-samples/tree/main/MacrobenchmarkSample
[19]: /topic/performance/baselineprofiles/overview#cloud-profiles
