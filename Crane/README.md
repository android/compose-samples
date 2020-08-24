# Crane sample

[Crane](https://material.io/design/material-studies/crane.html) is a travel app part of the Material
Studies built with [Jetpack Compose](https://developer.android.com/jetpack/compose).
The goal of the sample is to showcase Material components, draggable UI elements, Android Views
inside Compose, and UI state handling.

To try out this sample app, you need to use the latest Canary version of Android Studio 4.2.
You can clone this repository or import the
project from Android Studio following the steps
[here](https://developer.android.com/jetpack/compose/setup#sample).

## Screenshots

<img src="screenshots/crane.gif"/>

## Features

This sample contains 4 screens:
- __Landing__ [screen][landing] that fades out after 2 seconds then slides the main content in from
the bottom of the screen.
- __Home__ [screen][home] where you can explore flights, hotels, and restaurants specifying
the number of people.
 - Clicking on the number of people refreshes the destinations.
 - The [backdrop](https://material.io/components/backdrop) is draggable and can pin to the top of
 the screen, just under the search criteria, and to the bottom. Implemented [here][backdrop].
 - Destination's images are retrieved using the [coil-accompanist][coil-accompanist] library.
- __Calendar__ [screen][calendar]. Tapping on __Select Dates__ takes you to a calendar built
completely from scratch. It makes a heavy usage of Compose's state APIs.
- Destination's __Details__ [screen][details]. When tapping on a destination, a new screen
implemented using a different Activity will be displayed. In there, you can see the a `MapView`
embedded in Compose and Compose buttons updating the Android View. Notice how you can also
interact with the `MapView` seamlessly.

## Google Maps SDK

To get the MapView working, you need to get an API key as
the [documentation says](https://developers.google.com/maps/documentation/android-sdk/get-api-key),
and include it in the `local.properties` file as follows:

```
google.maps.key={insert_your_api_key_here}
```

## Data

The data is hardcoded in the _CraneData_ [file][data] and exposed to the UI using the
[MainViewModel][mainViewModel]. Image resources are retrieved from
[Unsplash](https://unsplash.com/).

## Testing

Crane has Compose-only tests (e.g. [HomeTest][homeTest]) but also tests covering Compose and the
view-based system (e.g. [DetailsActivityTest][detailsTest]). The latter uses the `onActivity`
method of the `ActivityScenarioRule` to access information from the `MapView`.

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

[landing]: app/src/main/java/androidx/compose/samples/crane/home/LandingScreen.kt
[home]: app/src/main/java/androidx/compose/samples/crane/home/CraneHome.kt
[backdrop]: app/src/main/java/androidx/compose/samples/crane/ui/BackdropFrontLayer.kt
[calendar]: app/src/main/java/androidx/compose/samples/crane/calendar/Calendar.kt
[details]: app/src/main/java/androidx/compose/samples/crane/details/DetailsActivity.kt
[data]: app/src/main/java/androidx/compose/samples/crane/data/CraneData.kt
[mainViewModel]: app/src/main/java/androidx/compose/samples/crane/home/MainViewModel.kt
[homeTest]: app/src/androidTest/java/androidx/compose/samples/crane/home/HomeTest.kt
[detailsTest]: app/src/androidTest/java/androidx/compose/samples/crane/details/DetailsActivityTest.kt
[coil-accompanist]: https://github.com/chrisbanes/accompanist
