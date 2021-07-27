# Jetnews sample
Jetnews is a sample news reading app, built with
[Jetpack Compose](https://developer.android.com/jetpack/compose). The goal of the sample is to
showcase the current UI capabilities of Compose.

To try out this sample app, you need to use 
[Android Studio Arctic Fox](https://developer.android.com/studio).
You can clone this repository or import the
project from Android Studio following the steps
[here](https://developer.android.com/jetpack/compose/setup#sample).

Screenshots
-----------
<img src="screenshots/jetnews_demo.gif" alt="Screenshot">

## Features

This sample contains three screens: a list of articles, a detail page for articles, and a page to
subscribe to topics of interest. The navigation from the the list of articles to the interests
screen uses a navigation drawer.

### App scaffolding

Package [`com.example.jetnews.ui`][1]

[`JetnewsApp.kt`][2] arranges the different screens in the `NavDrawerLayout`. 

[`JetnewsNavGraph.kt`][3] configures the navigation routes and actions in the app. 

[1]: app/src/main/java/com/example/jetnews/ui
[2]: app/src/main/java/com/example/jetnews/ui/JetnewsApp.kt
[3]: app/src/main/java/com/example/jetnews/ui/JetnewsNavGraph.kt

### Main article list

Package [`com.example.jetnews.ui.home`][3]

This screen shows how to create different custom Composable functions and combine them in a list
that scrolls vertically and horizontally.

See how to:

* Use `Row`s and `Column`s to arrange the contents of the UI
* Add an `AppBar`
* Use `MaterialTypography` and opacity to style the text
* Use `Shape` to round the corners of the images
* Use elevation to make the `Card`s stand out from the background

[3]: app/src/main/java/com/example/jetnews/ui/home

### Article detail

Package [`com.example.jetnews.ui.article`][4]

This screen dives into the Text API, showing how to use different fonts than the ones defined in
[`Typograhy`][5]. It also adds a bottom appbar, with custom actions.

[4]: app/src/main/java/com/example/jetnews/ui/article
[5]: app/src/main/java/com/example/jetnews/ui/theme/Type.kt

### Interests screen

Package [`com.example.jetnews.ui.interests`][6]

This screens shows how to use Tabs and switch content depending on the selected tab. It
also includes a custom checkbox button, [SelectTopicButton][7]
that uses a `Toggleable` composable function to provide
the on/off behaviour and semantics, while drawing a custom UI. The UI of the button is partly
drawn with low-level primitives and partly overlaying images. See also how to visualize
on and off, light and dark version in the Android Studio Preview.

[6]: app/src/main/java/com/example/jetnews/ui/interests
[7]: app/src/main/java/com/example/jetnews/ui/interests/SelectTopicButton.kt

### Data

The data in the sample is static, held in the `com.example.jetnews.data` package.

### Instrumented and Robolectric tests

UI tests can be run on device/emulators or on JVM with Robolectric.

* To run Instrumented tests use the "Instrumented tests" run configuration or run the `./gradlew connectedCheck` command.
* To run tests with Robolectric use the "Robolectric tests" run configuration or run the `./gradlew testDebug` command.

## License

```
Copyright 2021 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
