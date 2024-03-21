/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.theme

import androidx.wear.compose.material.Colors
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onPrimaryDark
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.primaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.secondaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryDark

internal val wearColorPalette: Colors = Colors(
    primary = primaryDark,
    primaryVariant = primaryContainerDarkMediumContrast,
    secondary = secondaryDark,
    secondaryVariant = secondaryContainerDarkMediumContrast,
    error = errorDark,
    onPrimary = onPrimaryDark,
    onSecondary = onSecondaryDark,
    onError = onErrorDark
)
