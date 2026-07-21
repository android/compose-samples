Project: /quality/_project.yaml
book_path: /quality/_book.yaml
description: This document details the step-by-step process for generating Baseline Profiles specifically for an Android library, outlining the required module setup and Gradle configurations.
keywords_public: Android performance, Baseline Profiles, library optimization, Gradle plugin, build process, Android libraries

{% include "studio/_common/_gradle_javlin2.html" %}

# Create Baseline Profiles for a library

To create Baseline Profiles for a library, use the
[Baseline Profile Gradle plugin](/topic/performance/baselineprofiles/configure-baselineprofiles).

There are three modules involved in creating Baseline Profiles for a library:

* Sample app module: contains the sample app that uses your library.
* Library module: the module you want to generate the profile for.
* Baseline Profile module: the test module that generates the Baseline Profiles.

To generate a Baseline Profile for a library, perform the following steps:

<div>
<ol>
    <li>Create a new <code>com.android.test</code> module&mdash;for example,
    <code>:baseline-profile</code>.</li>
    <li>Configure the <code>build.gradle.kts</code> file for the
    <code>:baseline-profile</code> module. <a href="/topic/performance/baselineprofiles/create-baselineprofile#create-new-profile-plugin">The configuration is
    essentially the same as for an app</a>, but make sure to set the
    <code>targetProjectPath</code> to the sample app module.</li>
    <li>Create a Baseline Profile test in the <code>:baseline-profile</code>
    test module. This needs to be specific to the sample app and must use all
    the functionalities of the library.</li>
    <li>Update the configuration in <code>build.gradle.ktss</code> file in the
    library module, say <code>:library</code>.</li>
    <ol>
        <li>Apply the plugin <code>androidx.baselineprofile</code>.</li>
        <li>Add a <code>baselineProfile</code> dependency to the
        <code>:baseline-profile</code> module.</li>
        <li>Apply the consumer plugin configuration you want, as shown in the
        following example.</li>
    </ol>
<div>
{{ gsnippet_kotlin}}
<pre class="prettyprint lang-kotlin">
plugins {
    id("com.android.library")
    id("androidx.baselineprofile")
}

android { ... }

dependencies {
    ...
    // Add a baselineProfile dependency to the `:baseline-profile` module.
    baselineProfile(project(":baseline-profile"))
}

// Baseline Profile Gradle plugin configuration.
baselineProfile {

    // <a href="/topic/performance/baselineprofile/configure-baselineprofiles#filter-profile-rules">Filters</a> the generated profile rules. 
    // This example keeps the classes in the `com.library` package all its subpackages.
    filter {
        include "com.mylibrary.**"
    }
}
</pre>
{{ gsnippet_groovy}}
<pre class="prettyprint lang-groovy">
plugins {
    id 'com.android.library'
    id 'androidx.baselineprofile'
}

android { ... }

dependencies {
    ...
    // Add a baselineProfile dependency to the `:baseline-profile` module.
    baselineProfile ':baseline-profile'
}

// Baseline Profile Gradle plugin configuration.
baselineProfile {

    // <a href="/topic/performance/baselineprofile/configure-baselineprofiles#filter-profile-rules">Filters</a> the generated profile rules. 
    // This example keeps the classes in the `com.library` package all its subpackages.
    filter {
        include 'com.mylibrary.**'
    }
}
</pre>
{{ gsnippet_end }}
</div>
    <li>Add the <code>androidx.baselineprofile</code> plugin to the 
    <code>build.gradle.kts</code> file in the app module
    <code>:sample-app</code>.
<div>
{{ gsnippet_kotlin }}
<pre class="prettyprint lang-kotlin">
plugins {
    ...
    id("androidx.baselineprofile")
}
</pre>
{{ gsnippet_groovy }}
<pre class="prettyprint lang-groovy">
plugins {
    ...
    id 'androidx.baselineprofile'
}
</pre>
{{ gsnippet_end }}
</div>
  </li>
  <li>Generate the profile by running the following code:
  <code>./gradlew :library:generateBaselineProfile</code>.</li>
</ol>
</div>

At the end of the generation task, the Baseline Profile is stored at
`library/src/main/generated/baselineProfiles`.