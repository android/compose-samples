# Jetpack Compose Samples
<img src="readme/samples_montage.gif" alt="Jetpack Compose Samples" width="1024" />

This repository contains a set of individual Android Studio projects to help you learn about
Compose in Android. Each sample demonstrates different use cases, complexity levels and APIs.

For more information, please [read the documentation](https://developer.android.com/jetpack/compose)

💻 Requirements
------------
To try out these sample apps, you need to use [Android Studio Arctic Fox](https://developer.android.com/studio).
You can clone this repository or import the
project from Android Studio following the steps
[here](https://developer.android.com/jetpack/compose/setup#sample).

🧬 Samples
------------

| Project | |
|:-----|---------|
|  <br><img src="readme/jetnews.png" alt="JetNews" width="240"></img> <br><br> A sample blog post viewer that demonstrates the use of Compose with a typical Material app and real-world architecture. <br><br> • Medium complexity<br>• Varied UI<br>• Light & dark themes<br>• Resource loading<br>• UI Testing <br><br> **[> Browse](JetNews/)**<br><br> | <img src="readme/screenshots/JetNews.png" width="320" alt="Jetnews sample demo"> |
|  |  |
|  <br><img src="readme/jetchat.png" alt="Jetchat" width="240"></img> <br><br>A sample chat app that focuses on UI state patterns and text input.<br><br>• Low complexity<br>• Material Design 3 theme and Material You dynamic color<br>• Resource loading<br>• Back button handling<br>• Integration with Architecture Components: Navigation, Fragments, LiveData, ViewModel<br>• Animation<br>• UI Testing<br><br>**[> Browse](Jetchat/)** <br><br> | <img src="readme/screenshots/Jetchat.png" width="320" alt="Jetchat sample demo">|
|  |  |
| <br><img src="readme/jetsurvey.png" alt="Jetsurvey" width="240"></img> <br><br>A sample survey app that showcases text input, validation and UI state management in Compose.<br><br>• Low complexity<br>• `TextField` and form validation<br>• Snackbar implementation<br>• Element reusability and styling<br>• Various form elements<br><br><br>**[> Browse](Jetsurvey/)** <br><br> | <img src="readme/screenshots/Jetsurvey.png" width="320" alt="Jetsurvey sample demo"> |
|  |  |
| <br><img src="readme/jetsnack.png" alt="Jetsnack" width="240"></img> <br><br>Jetsnack is a sample snack ordering app built with Compose.<br><br>• Medium complexity<br>• Custom design system<br>• Custom layouts<br>• Animation<br><br>**[> Browse](Jetsnack/)** <br><br>  | <img src="readme/screenshots/Jetsnack.png" width="320" alt="Jetsnack sample demo">|
|  |  |
| <br><img src="readme/jetcaster.png" alt="Jetcaster" width="240"></img> <br><br>A sample podcast app that features a full-featured, Redux-style architecture and showcases dynamic themes.<br><br>• Advanced sample<br>• Dynamic theming using podcast artwork<br>• Image fetching<br>• [`WindowInsets`](https://developer.android.com/reference/kotlin/android/view/WindowInsets) support<br>• Coroutines<br>• Local storage with Room<br><br>**[> Browse](Jetcaster/)** <br><br>  | <img src="readme/screenshots/Jetcaster.png" width="320" alt="Jetcaster sample demo">|
|  |  |
| <br><img src="readme/rally.png" alt="Rally" width="240"></img>  <br><br>A Compose implementation of the Rally Material study, a financial app that focuses on data, charts, reusability and animations.<br><br>• Low complexity<br>• Material theming with a dark-only theme<br>• Custom layouts and reusable elements<br>• Charts and tables<br>• Animations<br>• Screenshot tests<br><br>**[> Browse](Rally/)** <br><br>  | <img src="readme/screenshots/Rally.png" width="320" alt="Rally sample demo">|
|  |  |
| <br><img src="readme/crane.png" alt="Crane" width="240"></img>  <br><br>A Compose implementation of the Crane Material study, a travel app that uses Material Design components and Material Theming to create a personalized, on-brand experience.<br><br>• Medium complexity<br>• Draggable UI elements<br>• Android Views inside Compose<br>• UI state handling<br>• UI Tests<br><br>**[> Browse](Crane/)** <br><br>  | <img src="readme/screenshots/Crane.png" width="320" alt="Crane sample demo">|
|  |  |
| <br><img src="readme/owl.png" alt="Owl" width="240"></img>  <br><br>A Compose implementation of the Owl Material study. The Owl brand uses bold color, shape, and typography to express its brand attributes: energy, daring, and fun.<br><br>• Medium complexity<br>• Material theming & light/dark themes<br>• Custom layout<br>• Animation<br><br>**[> Browse](Owl/)** <br><br>  | <img src="readme/screenshots/Owl.png" width="320" alt="Owl sample demo">|


🧬 Additional samples
------------

| Project | |
|:-----|---------|
| <br><img src="readme/material_catalog.png" alt="Material Catalog" width="240"></img>  <br><br>A catalog of Material Design components and features available in Jetpack Compose. See how to implement them and how they look and behave on real devices.<br><br>• Lives in AOSP—always up to date<br>• Uses the same samples as API reference docs<br>• Theme picker to change Material Theming values at runtime<br>• Links to guidelines, docs, source code, and issue tracker<br><br>**[> Get it on Google Play](https://play.google.com/store/apps/details?id=androidx.compose.material.catalog)**<br>**[> Browse on AOSP](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/integration-tests/material-catalog)** <br><br>  | <img src="readme/screenshots/Material_Catalog.png" width="320" alt="Material Catalog sample demo">|
|  |  |
| <br><img src="readme/sunflower.png" alt="Sunflower" width="240"></img>  <br><br>A gardening app illustrating Android development best practices with Android Jetpack. The `compose` branch is partially built with Jetpack Compose.<br><br>• Compose working in an existing app: including integration with strings, resources, and themes and styles<br>• Integration with an existing architecture based on Jetpack libraries<br>• `CollapsingToolbarLayout` behavior implemented manually with Compose<br>• Showing [Snackbars](https://material.io/components/snackbars) with Compose<br><br>**[> Browse](https://goo.gle/sunflower-compose)** <br><br>  | <img src="readme/screenshots/Sunflower.png" width="320" alt="Sunflower sample demo">|
|  |  |
| <br><img src="readme/tivi.png" alt="Tivi" width="240"></img>  <br><br>A work-in-progress TV show tracking Android app, which connects to [Trakt.tv](https://www.trakt.tv).<br><br>• Entirely written in [Kotlin](https://kotlinlang.org) and uses [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) throughout<br>• Uses many of the [Architecture Components](https://developer.android.com/topic/libraries/architecture): Room, Lifecycle, Navigation<br>• Uses [Hilt](https://dagger.dev/hilt) for dependency injection<br><br>**[> Browse](https://tivi.app)** <br><br>  | <img src="readme/screenshots/Tivi.jpg" width="320" alt="Tivi sample demo">|

## License
```
Copyright 2020 The Android Open Source Project

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
