Project: /quality/_project.yaml
book_path: /quality/_book.yaml
description: This document provides detailed instructions on how to manually define, collect, and measure Baseline Profile rules for Android apps, serving as an alternative to automated generation methods like the Jetpack Macrobenchmark library.
keywords_public: Baseline Profiles, manual creation, manual collection, profgen, ART profiles, performance optimization, Android app performance, adb, ProfileInstaller, rule syntax

{% include "_shared/_javlin.html" %}
{% include "studio/profile/_profile_javlin.html" %}
{% include "_shared/_versions.html" %}
{% include "/jetpack/androidx/variables/_profileinstaller.md" %}

{% setvar api34_higher %}<div class="ds-selector-tabs"><section><h3 id="api34+">API 34 and higher</h3>{% endsetvar %}
{% setvar api34_lower %}</section><section><h3 id="api33-">API 33 and lower</h3>{% endsetvar %}
{% setvar api34_end %}</section></div>{% endsetvar %}

# Manually create and measure Baseline Profiles

We highly recommend automating generation of profile rules using the [Jetpack
Macrobenchmark
library][1] to reduce
manual effort and increase general scalability. However, it is possible to
manually create and measure profile rules in your app.

## Define profile rules manually {:#define-rules-manually}

You can define profile rules manually in an app or a library module by creating
a file called `baseline-prof.txt` located in the `src/main` directory. This is
the same folder that contains the `AndroidManifest.xml` file.

The file specifies one rule per line. Each rule represents a pattern for
matching methods or classes in the app or library that needs to be optimized.

The syntax for these rules is a superset of the human-readable ART profile
format (HRF) when using `adb shell profman --dump-classes-and-methods`. The
syntax is similar to the [syntax for descriptors and
signatures][2], but lets
wildcards be used to simplify the rule-writing process.

The following example shows a few Baseline Profile rules included in the Jetpack
Compose library:

```bash
HSPLandroidx/compose/runtime/ComposerImpl;->updateValue(Ljava/lang/Object;)V
HSPLandroidx/compose/runtime/ComposerImpl;->updatedNodeCount(I)I
HLandroidx/compose/runtime/ComposerImpl;->validateNodeExpected()V
PLandroidx/compose/runtime/CompositionImpl;->applyChanges()V
HLandroidx/compose/runtime/ComposerKt;->findLocation(Ljava/util/List;I)I
Landroidx/compose/runtime/ComposerImpl;
```

You can try modifying profile rules in this [sample Compiler Explorer
project](https://godbolt.org/z/zYf5Pqb8h). Note that Compiler Explorer only
supports the human-readable ART profile format (HRF), so wildcards aren't
supported.

### Rule syntax

These rules take one of two forms to target either methods or classes:

```none
[FLAGS][CLASS_DESCRIPTOR]->[METHOD_SIGNATURE]
```

A class rule uses the following pattern:

```none
[CLASS_DESCRIPTOR]
```
See the following table for a detailed description:

Syntax             | Description
------------------ | -----------
`FLAGS`            | Represents one or more of the characters `H`, `S`, and `P` to indicate whether this method must be flagged as `Hot`, `Startup`, or `Post Startup` in regards to the startup type. <br><br>A method with the `H` flag indicates that it is a "hot" method, meaning it is called many times during the lifetime of the app. <br><br>A method with the `S` flag indicates that it is a method called during startup. <br><br>A method with the `P` flag indicates that it is a method called after startup. <br><br>A class present in this file indicates that it is used during startup and must be pre-allocated in the heap to avoid the cost of class loading. ART compiler employs various optimization strategies, such as AOT compilation of these methods and performing layout optimizations in the generated AOT file.
`CLASS_DESCRIPTOR` | Descriptor for the targeted method's class. For example, `androidx.compose.runtime.SlotTable` has a descriptor of `Landroidx/compose/runtime/SlotTable;`. L is prepended here per the [Dalvik Executable (DEX) format][2].
`METHOD_SIGNATURE` | Signature of the method, including the name, parameter types, and return types of the method. For example:<br><br> `// LayoutNode.kt` <br><br>`fun isPlaced():Boolean {` <br> `// ...`<br>`}` <br><br>on `LayoutNode` has the signature `isPlaced()Z`.

These patterns can have wildcards to have a single rule encompass multiple
methods or classes. For guided assistance when writing with rule syntax in
Android Studio, see the [Android Baseline Profiles][4] plugin.

An example of a wildcard rule might look something like this:

```bash
HSPLandroidx/compose/ui/layout/**->**(**)**
```

### Supported types in Baseline Profile rules {:#supported-types}

Baseline Profile rules support the following types. For details on these types,
see the [Dalvik Executable (DEX)
format][2].

Character        | Type      | Description
---------------- | --------- | ----------------------------------------------
`B`              | byte      | Signed byte
`C`              | char      | Unicode character code point encoded in UTF-16
`D`              | double    | Double-precision floating point value
`F`              | float     | Single-precision floating point value
`I`              | int       | Integer
`J`              | long      | Long integer
`S`              | short     | Signed short
`V`              | void      | Void
`Z`              | boolean   | True or false
`L` (class name) | reference | An instance of a class name

Additionally, libraries can define rules that are packaged in AAR artifacts.
When you build an APK to include these artifacts, the rules are merged
together—similar to how manifest merging is done—and compiled to a
compact binary ART profile that is specific to the APK.

ART leverages this profile when the APK is used on devices to AOT compile a
specific subset of the app at install-time on Android 9 (API level 28),
or Android 7 (API level 24) when using
[`ProfileInstaller`][6].

## Manually collect Baseline Profiles

You can manually generate a Baseline Profile without setting up the
Macrobenchmark library and create UI automations of your critical user journeys.
Although we recommend using Macrobenchmarks, it might not always be possible.
For example, if you're using a non-Gradle build system, then you can't use the
Baseline Profile Gradle plugin. In such cases, you can manually collect Baseline
Profile rules. This is much easier if you use a device or emulator running API
34 and higher. Although it's still possible with lower API levels, it requires
root access, and you need to use an emulator running an AOSP image. You can
collect rules directly by doing the following:

1. Install a release version of your app on a test device. The app build type
must **not** be R8-optimized and must **not** be debuggable to capture a profile
that can be used by the build system.
1. Disable profile installation and kill the app.<p/>
If your APK has a dependency on the Jetpack [Profile Installer][6] library, the
library bootstraps a profile on the first launch of your APK. This can interfere
with the profile generation process, so disable it with the following
command:<p/>
<pre class="prettyprint">
adb shell am broadcast -a androidx.profileinstaller.action.SKIP_FILE WRITE_SKIP_FILE {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}/androidx.profileinstaller.ProfileInstallReceiver
</pre>
1. Reset app compilation and clear any profiles.<p/>
<div>
{{api34_higher}}
<pre class="prettyprint">
adb shell cmd package compile -f -m verify {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
adb shell pm art clear-app-profiles {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre>
{{api34_lower}}
<pre class="prettyprint">
adb root
adb shell cmd package compile --reset {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre>
{{api34_end}}
</div><p/>

1. Run the app and manually navigate through your critical user journeys you
want to collect a profile for.
1. Wait at least five seconds to let profiles stabilize.

1. Perform the save action, and wait for the save to complete. If
your APK has a dependency on the Jetpack Profile Installer library, use that to
dump the profiles:<p/>
<pre class="prettyprint">
adb shell am broadcast -a androidx.profileinstaller.action.SAVE_PROFILE {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}/androidx.profileinstaller.ProfileInstallReceiver
sleep 1 # wait 1 second
adb shell am force-stop {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre>
If you're not using Profile Installer, dump the profiles manually on an
emulator using the following command:<p/>
<pre class="prettyprint">
adb root
adb shell killall -s SIGUSR1 {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
sleep 1 # wait 1 second
adb shell am force-stop {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre>
1. Convert the binary profiles that are generated to text:<p/>
<div>
{{api34_higher}}
<pre class="prettyprint">
adb shell pm dump-profiles --dump-classes-and-methods {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre>
{{api34_lower}}
<p/>Determine whether a reference profile or a current profile has been created.
A reference profile is located in the following location:<p/>
<pre class="prettyprint">
/data/misc/profiles/ref/${{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}/primary.prof
</pre><p/>
A current profile is located in the following location:<p/>
<pre class="prettyprint">
/data/misc/profiles/cur/0/{{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}/primary.prof
</pre><p/>
Determine the location of the APK:<p/>
<pre class="prettyprint">
adb root
adb shell pm path {{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}
</pre><p/>
Perform the conversion:<p/>
<pre class="prettyprint">
adb root
adb shell profman --dump-classes-and-methods --profile-file={{ '<var>' }}$PROFILE_PATH{{ '</var>' }} --apk={{ '<var>' }}$APK_PATH{{ '</var>' }} > /data/misc/profman/{{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}-primary.prof.txt
</pre><p/>
{{api34_end}}
</div>
1. Use `adb` to retrieve the dumped profile from the device: <p/>
<pre class="prettyprint">
adb pull /data/misc/profman/{{ '<var>' }}$PACKAGE_NAME{{ '</var>' }}-primary.prof.txt {{ '<var>' }}PATH_TO_APP_MODULE{{ '</var>' }}/src/main/
</pre>

This pulls the generated profile rules and installs them into your app module.
The next time you build the app, the Baseline Profile is included. Verify this
by following the steps in [Installation issues][7].

## Manually measure app improvements {:#measuring-baseline}

Note: For greater stability and accuracy, we recommend using Macrobenchmark
to measure performance impact, as it can measure repeatedly in a loop, capture
traces for performance debugging, and increase reliability&mdash;for example, by
clearing the operating system's disk cache.

We highly recommend that you measure app improvements through benchmarking.
However, if you'd like to measure improvements manually, you can get started by
measuring the unoptimized [app
startup][8] for reference.

```posix-terminal
PACKAGE_NAME=com.example.app

# Force Stop App
adb shell am force-stop $PACKAGE_NAME
# Reset compiled state
adb shell cmd package compile --reset $PACKAGE_NAME

# Measure App startup
# This corresponds to `Time to initial display` metric.
adb shell am start-activity -W -n $PACKAGE_NAME/.ExampleActivity \
 | grep "TotalTime"
```

Next, sideload the Baseline Profile.

Note: This workflow is only supported on version Android 9 (API 28) to Android
11 (API 30). For more information, see [Compilation behavior across Android
versions][9].

```posix-terminal
# Unzip the Release APK first.
unzip release.apk

# Create a ZIP archive.
# The name should match the name of the APK.
# Copy `baseline.prof{m}` and rename it `primary.prof{m}`.
cp assets/dexopt/baseline.prof primary.prof
cp assets/dexopt/baseline.profm primary.profm

# Create an archive.
zip -r release.dm primary.prof primary.profm

# Confirm that release.dm only contains the two profile files:
unzip -l release.dm
# Archive:  release.dm
#   Length      Date    Time    Name
# ---------  ---------- -----   ----
#      3885  1980-12-31 17:01   primary.prof
#      1024  1980-12-31 17:01   primary.profm
# ---------                     -------
#                               2 files

# Install APK + Profile together.
adb install-multiple release.apk release.dm
```

To verify that the package was optimized on install, run the following command:

```posix-terminal
# Check dexopt state.
adb shell dumpsys package dexopt | grep -A 1 $PACKAGE_NAME
```

The output must state that the package is compiled:

```none
[com.example.app]
  path: /data/app/~~YvNxUxuP2e5xA6EGtM5i9A==/com.example.app-zQ0tkJN8tDrEZXTlrDUSBg==/base.apk
  arm64: [status=speed-profile] [reason=install-dm]
```

Now, you can measure app startup performance like before but without
resetting the compiled state. Ensure that you don't reset the compiled state for
the package.

```posix-terminal
# Force stop app
adb shell am force-stop $PACKAGE_NAME

# Measure app startup
adb shell am start-activity -W -n $PACKAGE_NAME/.ExampleActivity \
 | grep "TotalTime"
```

Note: For greater stability and accuracy, it's recommended to use Macrobenchmark
to measure performance impact, as it can measure repeatedly in a loop, capture
traces for performance debugging, and increase reliability (for example, by
clearing the operating system's disk cache).

## Baseline Profiles and profgen {:#android-baseline}

This section describes what the _profgen_ tool does when building a compact
binary version of a [Baseline
Profile][10].

[Profgen-cli][11] helps with profile compilation, introspection, and
transpiling ART profiles, so they can be installed on Android-powered devices
regardless of the target SDK version.

Profgen-cli is a CLI that compiles the HRF of a Baseline Profile to its
compiled format. The CLI also ships in
the [`cmdline-tools`][12] repository as part of the Android
SDK.

These features are available in the `studio-main` branch:

```bash
➜ ..{{"<strong>"}}/cmdline-tools/latest/bin{{"</strong>"}}
apkanalyzer
avdmanager
lint
{{"<strong>"}}profgen{{"</strong>"}}
retrace
screenshot2
sdkmanager
```

### Build compact binary profiles with Profgen-cli {:#building-compact}

The commands available with Profgen-cli are `bin`, `validate`, and
`dumpProfile`. To see the available commands, use `profgen --help`:

```bash
➜  {{"<strong>"}}profgen --help{{"</strong>"}}
Usage: profgen options_list
Subcommands:
    bin - Generate Binary Profile
    validate - Validate Profile
    dumpProfile - Dump a binary profile to a HRF

Options:
    --help, -h -> Usage info
```

Use the `bin` command to generate the compact binary profile. The
following is an example invocation:

```bash
profgen bin ./baseline-prof.txt \
  --apk ./release.apk \
  --map ./obfuscation-map.txt \
  --profile-format v0_1_0_p \
  --output ./baseline.prof \
```

To see the available options, use `profgen bin options_list`:

```bash
Usage: profgen bin options_list
Arguments:
    profile -> File path to Human Readable profile { String }
Options:
    --apk, -a -> File path to apk (always required) { String }
    --output, -o -> File path to generated binary profile (always required)
    --map, -m -> File path to name obfuscation map { String }
    --output-meta, -om -> File path to generated metadata output { String }
    --profile-format, -pf [V0_1_0_P] -> The ART profile format version
      { Value should be one of [
         v0_1_5_s, v0_1_0_p, v0_0_9_omr1, v0_0_5_o, v0_0_1_n
        ]
      }
    --help, -h -> Usage info
```

The first argument represents the path to the `baseline-prof.txt` HRF.

Profgen-cli also needs the path to the release build of the APK and an
[obfuscation map][13] that is used to obfuscate the APK when
using R8 or Proguard. This way, `profgen` can translate source symbols in the
HRF to their corresponding obfuscated names when building the compiled profile.

Because ART profiles formats aren't forward or backward compatible, provide a
profile format so that `profgen` packages profile metadata (`profm`) that you
can use to transcode one ART profile format to another when required.

### Profile formats and platform versions {:#profile-formats}

Note: When bundling the profile in the `assets` folder, always target the format
v0_1_0_p.

The following options are available when choosing a profile format:

Profile format | Platform version    | API level |
   :---:       |       :---:         |   :---:
v0_1_5_s       | Android S+          | 31+
v0_1_0_p       | Android P, Q, and R | 28-30
v0_0_9_omr1    | Android O MR1       | 27
v0_0_5_o       | Android O           | 26
v0_0_1_n       | Android N           | 24-25

Copy the `baseline.prof` and `baseline.profm` output files into the
`assets` or `dexopt` folder in the APK.

#### Obfuscation maps {:#obfuscation-maps}

You only need to provide the obfuscation map if the HRF uses source symbols. If
the HRF is generated from a release build that is already obfuscated and
there is no mapping necessary, you can ignore that option and copy the outputs
to the `assets` or `dexopt` folder.

## Traditional installation of Baseline Profiles {:#traditional-installation}

Baseline Profiles are traditionally delivered to a device in one of two ways.

### Use `install-multiple` with DexMetadata {:#install-multiple}

On devices running API 28 and later, the Play client downloads the APK and
DexMetadata (DM) payload for an APK version being installed. The DM contains the
profile information that is passed on to Package Manager on device.

The APK and DM are installed as part of a single install session using
something like:

`adb install-multiple base.apk base.dm`

Note: The right profile DM payload is delivered based on the device SDK version
where the APK download request is being made from. Play generates a
 tuple by transcoding profiles packaged as v0_1_0_p
to every known profile version in use to deliver the correct version.

### Jetpack ProfileInstaller {:#androidx-profileinstaller}

On devices running API level 29 and later, the [Jetpack
ProfileInstaller][6] library provides an alternative
mechanism to _install_ a profile packaged into `assets`or `dexopt` after the APK
is installed on the device. [`ProfileInstaller`][15] is invoked by
[`ProfileInstallReceiver`][16] or by the app directly.

The ProfileInstaller library transcodes the profile based on the target device
SDK version, and copies the profile into the `cur` directory on device (a
package-specific staging directory for ART profiles on the device).

Once the device is idle, the profile is then picked up by a process called
`bg-dexopt` on device.

Note: ProfileInstaller can backport ART profiles all the way to
Android N, even though Play delivery of Baseline Profiles using
`install-multiple` is only supported on Android P devices and later.
Therefore, it's important to declare a dependency on the `ProfileInstaller`
library when using Baseline Profiles.

### Sideload a Baseline Profile {:#sideload-baseline-profile}

This section describes how to install a Baseline Profile given an APK.

#### Broadcast with `androidx.profileinstaller` {:#broadcast-androidx.profileinstaller}

On devices running API 24 and later, you can broadcast a command to install the
profile:

```bash
# Broadcast the install profile command - moves binary profile from assets
#     to a location where ART uses it for the next compile.
#     When successful, the following command prints "1":
adb shell am broadcast \
    -a androidx.profileinstaller.action.INSTALL_PROFILE \
    <pkg>/androidx.profileinstaller.ProfileInstallReceiver

# Kill the process
am force-stop <pkg>

# Compile the package based on profile
adb shell cmd package compile -f -m speed-profile <pkg>
```

ProfileInstaller isn't present in most APKs with Baseline Profiles&mdash;which
is in about 77K of 450K apps in Play&mdash;though it is present in effectively
every APK using Compose. This is because libraries can provide profiles without
declaring a dependency on ProfileInstaller. Adding a dependency in each
library with a profile applies starting with Jetpack.

#### Use `install-multiple` with profgen or DexMetaData {:#install-multiple-profgen}

On devices running API 28 and later, you can sideload a Baseline Profile
without having to have the ProfileInstaller library in the app.

To do so, use Profgen-cli:

```bash
profgen extractProfile \
        --apk app-release.apk \
        --output-dex-metadata app-release.dm \
        --profile-format V0_1_5_S # Select based on device and the preceding table.

# Install APK and the profile together
adb install-multiple appname-release.apk appname-release.dm
```

To support APK splits, run the preceding extract profile steps once per APK. At
install time, pass each APK and associated `.dm` file, ensuring the APK and
`.dm` names match:

```bash
adb install-multiple appname-base.apk appname-base.dm \
appname-split1.apk appname-split1.dm
```

#### Verification

To verify that the profile is correctly installed, you can use the steps from
[Manually measure app improvements][17].

### Dump the contents of a binary profile {:#dumping-contents}

To introspect the contents of a compact binary version of a
Baseline Profile, use the Profgen-cli `dumpProfile` option:

```bash
Usage: profgen dumpProfile options_list
Options:
    --profile, -p -> File path to the binary profile (always required)
    --apk, -a -> File path to apk (always required) { String }
    --map, -m -> File path to name obfuscation map { String }
    --strict, -s [true] -> Strict mode
    --output, -o -> File path for the HRF (always required) { String }
    --help, -h -> Usage info
```

`dumpProfile` needs the APK because the compact binary representation only
stores DEX offsets and, therefore, it needs them to reconstruct class and method
names.

Strict mode is enabled by default, and this performs a compatibility check of
the profile to the DEX files in the APK. If you are trying to debug profiles
that were generated by another tool, you might get compatibility failures that
prevent you from being able to dump for investigation. In such cases, you can
disable strict mode with `--strict false`. However, in most cases you should
keep strict mode enabled.

An [obfuscation map][13] is optional; when provided, it
helps remap obfuscated symbols to their human readable versions for ease of use.

{% verbatim %}<devsite-recommendations>{% endverbatim %}

## Recommended for you

*   Note: link text is displayed when JavaScript is off
*   [Best practices for SQLite performance][19]
*   [Baseline Profiles {:#baseline-profiles}][20]
*   [Stuck partial wake locks][21]

{%
verbatim
%}</devsite-recommendations>{% endverbatim %}

[1]: /topic/performance/baselineprofiles/measure-baselineprofile
[2]: https://source.android.com/devices/tech/dalvik/dex-format
[4]: https://plugins.jetbrains.com/plugin/17384-android-baseline-profiles
[6]: /jetpack/androidx/releases/profileinstaller
[7]: /topic/performance/baselineprofiles/debug-baseline-profiles#installation_issues
[8]: /topic/performance/vitals/launch-time#time-initial
[9]: /topic/performance/baselineprofiles/overview#compilation-behaviors
[10]: /topic/performance/baselineprofiles/overview
[11]: https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-main/profgen/profgen-cli/src/main/kotlin/com/android/tools/profgen/cli/
[12]: /studio/command-line
[13]: #obfuscation-maps
[15]: /reference/androidx/profileinstaller/ProfileInstaller
[16]: /reference/androidx/profileinstaller/ProfileInstallReceiver
[17]: /topic/performance/baselineprofiles/manually-create-measure#measuring-baseline
[19]: /topic/performance/sqlite-performance-best-practices
[20]: /topic/performance/baselineprofiles/overview
[21]: /topic/performance/vitals/wakelock