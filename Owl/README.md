# Owl sample

This sample is a [Jetpack Compose][compose] implementation of [Owl][owl], a Material Design study.

To try out these sample apps, you need to use the latest Canary version of Android Studio 4.2.
You can clone this repository or import the
project from Android Studio following the steps
[here](https://developer.android.com/jetpack/compose/setup#sample).

This sample showcases:

* [Material theming][materialtheming] & light/dark themes
* Custom layout
* Animation

## Screenshots

<img src="screenshots/owl.gif"/>

## Features

#### [Onboarding Screen](app/src/main/java/com/example/owl/ui/onboarding)
The onboarding screen allows users to customize their experience by selecting topics. Notable features:
* Custom [staggered grid layout](app/src/main/java/com/example/owl/ui/onboarding/Onboarding.kt#L239).
* [Topic chip](app/src/main/java/com/example/owl/ui/onboarding/Onboarding.kt#L171) with custom [selection animation](app/src/main/java/com/example/owl/ui/onboarding/Onboarding.kt#L157).

#### [Courses Screen](app/src/main/java/com/example/owl/ui/courses)
The courses screen displays featured and saved course and a search screen. Notable fetures:
* Custom [`StaggeredVerticalGrid`](app/src/main/java/com/example/owl/ui/courses/FeaturedCourses.kt#L161) responsive to available size.
* [`FeaturedCourse`](app/src/main/java/com/example/owl/ui/courses/FeaturedCourses.kt#L70) composable demonstrates usage of [`ConstraintLayout`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary.html#ConstraintLayout(androidx.compose.ui.Modifier,%20kotlin.Function1)).

#### [Course Details Screen](app/src/main/java/com/example/owl/ui/course/CourseDetails.kt)
Displays details of a selected course, featuring:

* A [FloatingActionButton](https://material.io/components/buttons-floating-action-button) that can be clicked or dragged to transform into a [`LessonsSheet`](app/src/main/java/com/example/owl/ui/course/CourseDetails.kt#L309).
* A selection of [`RelatedCourses`](app/src/main/java/com/example/owl/ui/course/CourseDetails.kt#L262) using a nested `BlueTheme`.

#### [Theming](app/src/main/java/com/example/owl/ui/theme)
Owl follows Material Design, customizing [colors](app/src/main/java/com/example/owl/ui/theme/Color.kt), [typography](app/src/main/java/com/example/owl/ui/theme/Type.kt) and [shapes](app/src/main/java/com/example/owl/ui/theme/Shape.kt). These come together in Owl's multiple [themes](app/src/main/java/com/example/owl/ui/theme/Theme.kt), one for each color scheme. Additionaly, Owl supports [image](app/src/main/java/com/example/owl/ui/theme/Images.kt) and [elevation](app/src/main/java/com/example/owl/ui/theme/Elevation.kt) theming, providing alternate images/elevations in light/dark themes.

#### [Common UI](app/src/main/java/com/example/owl/ui/common)
Compose makes it simple to create a library of components and use them throughout the app. See:
* [`CourseListItem`](app/src/main/java/com/example/owl/ui/common/CourseListItem.kt) is used on both the [My Courses](app/src/main/java/com/example/owl/ui/courses/MyCourses.kt) screen and in the related section of the [Course Details](app/src/main/java/com/example/owl/ui/course/CourseDetails.kt) screen.
* [`OutlinedAvatar`](app/src/main/java/com/example/owl/ui/common/OutlinedAvatar.kt) is used on both the [Featured Courses](app/src/main/java/com/example/owl/ui/courses/FeaturedCourses.kt) screen and the [Course Details](app/src/main/java/com/example/owl/ui/course/CourseDetails.kt) screen.

#### [Utilities](app/src/main/java/com/example/owl/ui/utils/)
Owl implements some utility functions of interest:
* [Window insets](https://goo.gle/compose-insets) will likely be provided by the Compose library at some point. Until then this demonstrates how it can be implemented.
* [Navigation](app/src/main/java/com/example/owl/ui/utils/Navigation.kt): an implementation of [Android Architecture Components Navigation](https://developer.android.com/guide/navigation) will be provided for Compose at some point. Until then this class provides a simple [`Navigator`](app/src/main/java/com/example/owl/ui/utils/Navigation.kt#L32) with back-stack and a [`backHandler`](app/src/main/java/com/example/owl/ui/utils/Navigation.kt#L79) effect.

## Data
Domain types are modelled in the [model package](app/src/main/java/com/example/owl/model), each containing static sample data exposed using fake `Repo`s objects.

Imagery is sourced from [Unsplash](https://unsplash.com/) and [Pravatar](https://pravatar.cc/) and loaded using [coil-accompanist][coil-accompanist].


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

[compose]: https://developer.android.com/jetpack/compose
[owl]: https://material.io/design/material-studies/owl.html
[materialtheming]: https://material.io/design/material-theming/overview.html#material-theming
[coil-accompanist]: https://github.com/chrisbanes/accompanist
