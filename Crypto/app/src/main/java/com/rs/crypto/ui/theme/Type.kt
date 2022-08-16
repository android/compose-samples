package com.rs.crypto.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.rs.crypto.R

private val RobotoBlack = FontFamily(Font(R.font.roboto_black))
val RobotoBold = FontFamily(Font(R.font.roboto_bold))
val RobotoRegular = FontFamily(Font(R.font.roboto_regular))

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = RobotoBlack,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    h2 = TextStyle(
        fontFamily = RobotoBold,
        fontSize = 18.sp,
        lineHeight = 30.sp
    ),
    h3 = TextStyle(
        fontFamily = RobotoBold,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    h4 = TextStyle(
        fontFamily = RobotoBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    body1 = TextStyle(
        fontFamily = RobotoRegular,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    body2 = TextStyle(
        fontFamily = RobotoRegular,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = RobotoRegular,
        fontSize = 12.sp,
        lineHeight = 22.sp
    ), // Subtitle 1 is body 3
    subtitle2 = TextStyle(
        fontFamily = RobotoRegular,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ), // Subtitle 2 is body 4
    h5 = TextStyle(
        fontFamily = RobotoRegular,
        fontSize = 10.sp,
        lineHeight = 22.sp
    ), // h5 is body 5
)