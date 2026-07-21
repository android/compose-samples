Project: /quality/\_project.yaml
book_path: /quality/\_book.yaml
description: Generate Baseline Profiles to improve app startup and runtime performance using Android Studio and Jetpack Macrobenchmark.
keywords_public: Baseline Profile, Macrobenchmark, Android Studio, app performance, startup optimization, Android Gradle Plugin

{% include "_shared/_javlin.html" %}
{% include "studio/profile/_profile_javlin.html" %}
{% include "_shared/_versions.html" %}
{% include "studio/_common/_gradle_javlin2.html" %}
{% include "/jetpack/androidx/variables/_benchmark.md" %}
{% include "/jetpack/androidx/variables/_profileinstaller.md" %}
{% include "/studio/releases/android_gradle_plugin_and_android_studio_compatibility.md" %}

# Create Baseline Profiles {:#creating-profile-rules}

Automatically generate profiles for every app release using the [Jetpack
Macrobenchmark library][1] and
[`BaselineProfileRule`][2]. We recommend that you
use `com.android.tools.build:gradle:8.0.0` or higher, which comes with build
improvements when using Baseline Profiles.

Important: To keep installation turnaround during development low, Baseline Profiles are only installed for
release builds.

These are the general steps to create a new Baseline Profile:

1. Set up the Baseline Profile module.
2. Define the JUnit test that helps generate Baseline Profiles.
3. Add the Critical User Journeys (CUJs) that you want to optimize.
4. Generate the Baseline Profile.

After you generate the Baseline Profile, benchmark it using a physical device to
measure the speed improvements.

## Create a new Baseline Profile with AGP 8.2 or higher {:#create-new-profile}

The easiest way to create a new Baseline Profile is to use the Baseline Profile
module template, available starting Android Studio Iguana and Android Gradle
Plugin (AGP) 8.2.

The Android Studio Baseline Profile Generator module template automates the
creation of a new module to generate and
<a href="/topic/performance/baselineprofiles/measure-baselineprofile">benchmark</a>
Baseline Profiles. Running the template generates most of the typical build
configuration, Baseline Profile generation, and verification code. The template
creates code to generate and benchmark Baseline Profiles to measure app
startup.

### Set up the Baseline Profile module {:#set-up-module}

To run the Baseline Profile module template, follow these steps:

<ol>
  <li>Select <b>File &gt; New &gt; New Module</b></li>
  <li>Select the <b>Baseline Profile Generator</b> template in the
  <b>Templates</b> panel and configure it:

  <figure>
    <img src="/topic/performance/images/studio/baseline-profile-generator-module-template.png" alt="">
    <figcaption><b>Figure 1.</b> Baseline Profile Generator module template.</figcaption>
  </figure>

  <p>The fields in the template are the following:</p>
  <ul>
    <li><b>Target application</b>: defines which app the Baseline Profile is generated for. When you have only a single app module in your project, there is only one item in this list.</li>
    <li><b>Module name</b>: the name you want for the Baseline Profile module
    being created.</li>
    <li><b>Package name</b>: the package name you want for the Baseline Profile
    module.</li>
    <li><b>Language</b>: whether you want the generated code to be Kotlin or
    Java.</li>
    <li><b>Build configuration language</b>: whether you want to use Kotlin
    Script (KTS) or Groovy for your build configuration scripts.</li>
    <li><b>Use Gradle-managed device</b>: if you're using
    <a href="/studio/test/gradle-managed-devices">Gradle-managed devices</a> to
    test your app.</li>
  </ul>
  </li>
  <li>Click <b>Finish</b> and the new module is created. If you are using source
  control, you might be prompted to add the newly created module files to source
  control.</li>
</ol>

### Define the Baseline Profile generator {:#define-generator}

The newly created module contains tests to both generate and benchmark the
Baseline Profile and test only basic app startup. We recommend that you augment
these to include CUJs and advanced startup workflows. Make sure that any tests
related to app startup are in a `rule` block with `includeInStartupProfile` set
to `true`; conversely, for optimal performance make sure that any tests not
related to app startup are not included in a Startup Profile. App startup
optimizations are used to define a special part of a Baseline Profile called a
[Startup Profile][3].

It helps maintainability if you abstract these CUJs outside of the generated
Baseline Profile and benchmark code so that they can be used for both. This
means that changes to your CUJs are used consistently.

### Generate and install the Baseline Profile {:#generate-profile}

The Baseline Profile module template adds a new run configuration to generate
the Baseline Profile. If you use product flavors, Android Studio creates
multiple run configurations so that you can generate separate Baseline Profiles
for each flavor.

Note: To generate and install the Baseline Profile from the command-line
interface, run the `:app:generateBaselineProfile` or
<code>:app:generate<var>Variant</var>BaselineProfile</code> Gradle tasks.

<figure>
  <img src="/topic/performance/images/studio/generate-baseline-profile.png"
  alt="The Generate Baseline Profile run configuration." width="50%">
  <figcaption><b>Figure 2.</b> Running this configuration generates the Baseline
  Profile.</figcaption>
</figure>

When the **Generate Baseline Profile** run configuration completes, it copies
the generated Baseline Profile to the
<code>src/<var>variant</var>/generated/baselineProfiles/baseline-prof.txt</code>
file in the module that is being profiled. The variant options are either the
release build type or a build variant involving the release build type.

The generated Baseline Profile is originally created in `build/outputs`. The
full path is dictated by the variant or flavor of the app being profiled and
whether you use a Gradle-managed device or a connected device for profiling. If
you use the names used by the code and build configurations generated by the
template, the Baseline Profile is created in the
`build/outputs/managed_device_android_test_additional_output/nonminifiedrelease/pixel6Api31/BaselineProfileGenerator_generate-baseline-prof.txt` file. You probably won't
have to interact with this version of the generated Baseline Profile directly
unless you're manually copying it to the target modules (not recommended).

## Create a new Baseline Profile with AGP 8.1 {:#create-new-profile-8-1}

If you aren't able to use the
[Baseline Profile module template][4], use the
Macrobenchmark module template and the Baseline Profile Gradle plugin to create
a new Baseline Profile. We recommend you use these tools starting with Android
Studio Giraffe and AGP 8.1.

Note: Automatic Baseline Profile generation with the Baseline Profile Gradle
plugin is available starting with AGP 8.0, but we recommend using AGP 8.1 for
a better experience.

Here are the steps to create a new Baseline Profile using the Macrobenchmark
module template and Baseline Profile Gradle plugin:

<ol>
    <li><a href="/topic/performance/benchmarking/macrobenchmark-overview#project-setup"> Set
    up a Macrobenchmark module</a> in your Gradle project.</li>
    <li>Define a new class called <code>BaselineProfileGenerator</code>:
<pre class="prettyprint lang-kotlin">
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = "com.example.app",
        profileBlock = {
            startActivityAndWait()
        }
    )

}

</pre>
    <p>The generator can contain interactions with your app beyond app startup.
    This lets you optimize the runtime performance of your app, such as
    scrolling lists, running animations, and navigating within an
    <code>Activity</code>.
    <a href="https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:benchmark/integration-tests/macrobenchmark/src/main/java/androidx/benchmark/integration/macrobenchmark/GithubBrowserBaselineProfile.kt">See
    other examples</a> of tests that use <code>@BaselineProfileRule</code> to
    improve critical user journeys.</p></li>
<li><p>Add the Baseline Profile Gradle plugin
(<code>libs.plugins.androidx.baselineprofile</code>). The plugin makes it easier
to generate Baseline Profiles and maintain them in the future.</p></li>
<li><p>To generate the Baseline Profile, run the
<code>:app:generateBaselineProfile</code> or
<code>:app:generate<var>Variant</var>BaselineProfile</code> Gradle tasks in the
terminal.</p>

<p>Run the generator as an instrumented test
on a rooted physical device, emulator, or
<a href="/studio/test/gradle-managed-devices">Gradle Managed Device</a>.
If you use a Gradle Managed Device, set <code>aosp</code> as the <code>systemImageSource</code>, because you need root
access for the Baseline Profile generator.</p>
<aside class="note">
    <b>Note:</b> When using Jetpack Macrobenchmark 1.2.0-alpha06 and higher, you can generate the Baseline Profile on devices running Android 13 (API 33) and higher without root access.
</aside>
<p>At the end of the generation task, the Baseline Profile is copied to
<code>app/src/<var>variant</var>/generated/baselineProfiles</code>.</p>
</li>
</ol>

### Create a new Baseline Profile without templates {:#create-new-profile-plugin}

We recommend creating a Baseline Profile using the Android Studio
[Baseline Profile module template][4]
(preferred) or [Macrobenchmark template][6], but you can
also use the Baseline Profile Gradle plugin by itself. To read more about the
Baseline Profile Gradle plugin, see
[Configure your Baseline Profile generation][7].

Note: The Baseline Profile Gradle plugin is already applied if you use the
[Baseline Profile module template][8].

Here's how to create a Baseline Profile using the Baseline Profile Gradle plugin
directly:

1.  Create a new `com.android.test` module&mdash;for example,
    `:baseline-profile`.
1.  Configure the `build.gradle.kts` file for
    `:baseline-profile`: 1. Apply the `androidx.baselineprofile` plugin. 1. Ensure the `targetProjectPath` points to the
    `:app` module. 1. Optionally, add a
    [Gradle-managed device (GMD)][9].
    In the following example, it's `pixel6Api31`. If not specified,
    the plugin uses a connected device, either emulated or physical. 1. Apply the configuration you want, as shown in the following
    example.

        <div>
        {{ gsnippet_kotlin}}
        <pre class="prettyprint lang-kotlin">
        plugins {
            id("com.android.test")
            id("androidx.baselineprofile")
        }

        android {
            defaultConfig {
                ...
            }

            // Point to the app module, the module that you're generating the Baseline Profile for.
            targetProjectPath = ":app"
            // Configure a GMD (optional).
            testOptions.managedDevices.devices {
                pixel6Api31(com.android.build.api.dsl.ManagedVirtualDevice) {
                    device = "Pixel 6"
                    apiLevel = 31
                    systemImageSource = "aosp"
                }
            }
        }

        dependencies { ... }

        // Baseline Profile Gradle plugin configuration. Everything is optional. This
        // example uses the GMD added earlier and disables connected devices.
        baselineProfile {
            // Specifies the GMDs to run the tests on. The default is none.
            managedDevices += "pixel6Api31"
            // Enables using connected devices to generate profiles. The default is
            // `true`. When using connected devices, they must be rooted or API 33 and
            // higher.
            useConnectedDevices = false
        }
        </pre>
        {{ gsnippet_groovy }}
        <pre class="prettyprint lang-groovy">
        plugins {
            id 'com.android.test'
            id 'androidx.baselineprofile'
        }

        android {
            defaultConfig {
                ...
            }

            // Point to the app module, the module that you're generating the Baseline Profile for.
            targetProjectPath ':app'
            // Configure a GMD (optional).
            testOptions.managedDevices.devices {
                pixel6Api31(com.android.build.api.dsl.ManagedVirtualDevice) {
                    device 'Pixel 6'
                    apiLevel 31
                    systemImageSource 'aosp'
                }
            }
        }

        dependencies { ... }

        // Baseline Profile Gradle plugin configuration. Everything is optional. This
        // example uses the GMD added earlier and disables connected devices.
        baselineProfile {
            // Specifies the GMDs to run the tests on. The default is none.
            managedDevices ['pixel6Api31']
            // Enables using connected devices to generate profiles. The default is
            // `true`. When using connected devices, they must be rooted or API 33 and
            // higher.
            useConnectedDevices false
        }
        </pre>
        {{ gsnippet_end }}
        </div>

1.  Create a Baseline Profile test in the `:baseline-profile` test module. The
    following example is a test that generates a Baseline Profile for app
    startup.

        <div>
        {{javlin_1_kotlin}}
        <pre class="prettyprint lang-kotlin">
        class BaselineProfileGenerator {
        @get:Rule
        val baselineProfileRule = BaselineProfileRule()

        @Test
        fun startup() = baselineProfileRule.collect(
            packageName = "com.example.app",
            profileBlock = {
                uiAutomator { startApp({{"<var>"}}PACKAGE_NAME{{"</var>"}}) }
            }
        )

    }
    </pre>
    {{javlin_2_java}}
    <pre class="prettyprint lang-java">
    public class BaselineProfileGenerator {

            @Rule
            Public BaselineProfileRule baselineRule = new BaselineProfileRule();

            @Test
            Public void startupBaselineProfile() {
                baselineRule.collect(
                    "com.myapp",
                    (scope -> {
                        scope.startActivityAndWait();
                        Return Unit.INSTANCE;
                    })
                )
            }
        }
        </pre>
        {{javlin_3_end}}
        </div>

1.  Update the `build.gradle.kts` file in the app module, for example `:app`.
    1. Apply the plugin `androidx.baselineprofile`.
    1. Add a `baselineProfile` dependency to the `:baseline-profile` module.

    <div>
    {{ gsnippet_kotlin }}
    <pre class="prettyprint lang-kotlin">
    plugins {
        id("com.android.application")
        id("androidx.baselineprofile")
    }

    android {
    // There are no changes to the `android` block.
    ...
    }

    dependencies {
    ...
    // Add a `baselineProfile` dependency on the `:baseline-profile` module.
    baselineProfile(project(":baseline-profile"))
    }
    </pre>
    {{ gsnippet_groovy }}
    <pre class="prettyprint lang-groovy">
    plugins {
        id 'com.android.application'
        id 'androidx.baselineprofile'
    }

    android {
    // No changes to the `android` block.
    ...
    }

    dependencies {
    ...
    // Add a `baselineProfile` dependency on the `:baseline-profile` module.
    baselineProfile ':baseline-profile'
    }
    </pre>
    {{ gsnippet_end }}
    </div>

1.  Generate the profile by running the `:app:generateBaselineProfile`
    or <code>:app:generate<var>Variant</var>BaselineProfile</code> Gradle tasks.
1.  At the end of the generation task, the Baseline Profile is copied to
    <code>app/src/<var>variant</var>/generated/baselineProfiles</code>.

## Create a new Baseline Profile with AGP 7.3-7.4 {:#create-new-profile-7-4}

It's possible to generate Baseline Profiles with AGP 7.3-7.4, but we strongly
recommend upgrading to at least AGP 8.1 so you can use the Baseline Profile
Gradle plugin and its latest features.

If you need to create Baseline Profiles with AGP 7.3-7.4, the steps are the same
as the [steps for AGP 8.1][6], with the following
exceptions:

- Don't add the Baseline Profile Gradle plugin.
- To generate the Baseline Profiles, execute the Gradle task `./gradlew [emulator name][flavor][build type]AndroidTest`. For example, `./gradlew :benchmark:pixel6Api31BenchmarkAndroidTest`.
- You must [manually apply the generated Baseline Profile rules to your code][11].

### Manually apply generated rules {:#apply-rules}

The Baseline Profile generator creates a Human Readable Format (HRF) text file
on the device and copies it to your host machine. To apply the generated profile
to your code, follow these steps:

1.  Locate the HRF file in the build folder of the module you generate the
    profile in:
    `[module]/build/outputs/managed_device_android_test_additional_output/[device]`.

    Profiles follow the `[class name]-[test method name]-baseline-prof.txt`
    naming pattern, which looks like this:
    `BaselineProfileGenerator-startup-baseline-prof.txt`.

1.  Copy the generated profile to `src/main/` and rename the file to
    `baseline-prof.txt`.

    Note: If you're using a version of the Android Gradle plugin earlier than
    8.0, the `baseline-prof.txt` file isn't shown in the **Android** view in
    Android Studio.

1.  Add a dependency to the [ProfileInstaller library][12]
    in your app's `build.gradle.kts` file to enable local Baseline Profile
    compilation where [Cloud Profiles][13] aren't available. This is
    the only way to sideload a Baseline Profile locally.

    ```groovy
    dependencies {
         implementation("androidx.profileinstaller:profileinstaller:{{ androidx_profileinstaller_stable }}")
    }
    ```

1.  Build the production version of your app while the applied HRF rules are
    compiled into binary form and included in the APK or AAB. Then distribute
    your app as usual.

## Benchmark the Baseline Profile {:#benchmark-baseline-profile}

To benchmark your Baseline Profile, create a new Android Instrumented Test Run
configuration from the gutter action that executes the benchmarks defined in
the `StartupBenchmarks.kt` or `StartupBencharks.java` file. To learn more about benchmark
testing, see [Create a Macrobenchmark
class][14]
and [Automate measurement with the Macrobenchmark
library][15].

<figure>
  <img src="/topic/performance/images/studio/gutter-action.png" alt=""
  width="50%">
  <figcaption><b>Figure 3.</b> Run Android Tests from the gutter
  action.</figcaption>
</figure>

When you run this within Android Studio, the build output contains details of
the speed improvements that the Baseline Profile provides:

<pre class="prettyprint none">
StartupBenchmarks_startupCompilationBaselineProfiles
timeToInitialDisplayMs   min 161.8,   median 178.9,   max 194.6
StartupBenchmarks_startupCompilationNone
timeToInitialDisplayMs   min 184.7,   median 196.9,   max 202.9
</pre>

## Capture all required code paths {:#capture-code-paths}

The two key metrics for measuring app startup times are as follows:

[Time to initial display (TTID)][16]
: The time it takes to display the first frame of the application UI.

[Time to full display (TTFD)][17]
: TTID plus the time to display content that is loaded asynchronously after the initial frame is displayed.

TTFD is reported once the
[`reportFullyDrawn()`][18]
method of the
[`ComponentActivity`][19]
is called. If `reportFullyDrawn()` is never called, the TTID is reported
instead. You might need to delay when `reportFullyDrawn()` is called until after
the asynchronous loading is complete. For example, if the UI contains a dynamic
list such as a [`RecyclerView`][20] or [lazy
list][21], the list might be populated by a background
task that completes after the list is first drawn and, therefore, after the UI
is marked as fully drawn. In such cases, code that runs after the UI reaches
fully drawn state isn't included in the Baseline Profile.

To include the list population as part of your Baseline Profile, get the
`FullyDrawnReporter` by using
[`getFullyDrawnReporter()`][22]
and add a reporter to it in your app code. Release the reporter once the
background task finishes populating the list. The `FullyDrawnReporter` doesn't
call the `reportFullyDrawn()` method until all reporters are released. By doing
this, Baseline Profile includes the code paths required to populate the list.
This doesn't change the app's behavior for the user, but it lets the Baseline
Profile include all the necessary code paths.

If your app uses [Jetpack Compose][23], use the following APIs to
indicate fully drawn state:

- [`ReportDrawn`][24]
  indicates that your composable is immediately ready for interaction.
- [`ReportDrawnWhen`][25]
  takes a predicate, such as `list.count > 0`, to indicate when your
  composable is ready for interaction.
- [`ReportDrawnAfter`][26]
  takes a suspending method that, when it completes, indicates that your
  composable is ready for interaction.

{% verbatim %}<devsite-recommendations>{% endverbatim %}

## Recommended for you

- Note: link text is displayed when JavaScript is off
- [Capture Macrobenchmark metrics][27]
- [Write a Macrobenchmark][28]
- [JankStats library][29]

{% verbatim %}</devsite-recommendations>{% endverbatim %}

[1]: /macrobenchmark
[2]: /reference/kotlin/androidx/benchmark/macro/junit4/BaselineProfileRule
[3]: /topic/performance/baselineprofiles/dex-layout-optimizations
[4]: #create-new-profile
[6]: #create-new-profile-8-1
[7]: /topic/performance/baselineprofiles/use-baselineprofile-gradle-plugin
[8]: /topic/performance/create-baselineprofile#create-new-profile
[9]: /studio/test/gradle-managed-devices
[11]: #apply-rules
[12]: /jetpack/androidx/releases/profileinstaller
[13]: /topic/performance/baselineprofiles/overview#cloud-profiles
[14]: /topic/performance/benchmarking/macrobenchmark-overview#create-macrobenchmark
[15]: /topic/performance/baselineprofiles/measure-baselineprofile
[16]: /topic/performance/vitals/launch-time#time-initial
[17]: /topic/performance/vitals/launch-time#time-full
[18]: /reference/androidx/activity/ComponentActivity#reportFullyDrawn()
[19]: /reference/androidx/activity/ComponentActivity
[20]: /develop/ui/views/layout/recyclerview
[21]: /jetpack/compose/lists#lazy
[22]: /reference/androidx/activity/ComponentActivity#getFullyDrawnReporter()
[23]: /jetpack/compose
[24]: /reference/kotlin/androidx/activity/compose/ReportDrawn.composable#ReportDrawn()
[25]: /reference/kotlin/androidx/activity/compose/ReportDrawnWhen.composable#ReportDrawnWhen(kotlin.Function0)
[26]: /reference/kotlin/androidx/activity/compose/ReportDrawnAfter.composable#ReportDrawnAfter(kotlin.coroutines.SuspendFunction0)
[27]: /topic/performance/benchmarking/macrobenchmark-metrics.md
[28]: /topic/performance/benchmarking/macrobenchmark-overview.md
[29]: /topic/performance/jankstats.md
