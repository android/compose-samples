/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.theme

import androidx.ui.material.Typography
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.font.font
import androidx.ui.text.font.fontFamily
import androidx.ui.unit.sp
import com.example.jetnews.R

private val AppFontFamily = fontFamily(
    font(R.font.montserrat_regular),
    font(R.font.montserrat_medium, FontWeight.W500),
    font(R.font.montserrat_semibold, FontWeight.W600)
)

private val BodyFontFamily = fontFamily(
    fonts = listOf(
        font(R.font.domine_regular),
        font(R.font.domine_bold, FontWeight.Bold)
    )
)

val ThemeTypography = Typography(
    h4 = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    )
)
