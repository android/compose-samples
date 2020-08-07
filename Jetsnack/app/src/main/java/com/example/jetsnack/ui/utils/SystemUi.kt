/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetsnack.ui.utils

import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb

interface SystemUiController {
    fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )

    fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )

    fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )
}

fun SystemUiController(window: Window): SystemUiController {
    return SystemUiControllerImpl(window)
}

/**
 * A helper class for setting the navigation and status bar colors for a [Window], gracefully
 * degrading behavior based upon API level.
 */
private class SystemUiControllerImpl(private val window: Window) : SystemUiController {

    /**
     * Set the status bar color.
     *
     * @param color The **desired** [Color] to set. This may require modification if running on an
     * API level that only supports white status bar icons.
     * @param darkIcons Whether dark status bar icons would be preferable. Only available on
     * API 23+.
     * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
     * dark icons were requested but are not available. Defaults to applying a black scrim.
     */
    override fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        val statusBarColor = when {
            darkIcons && Build.VERSION.SDK_INT < 23 -> transformColorForLightContent(color)
            else -> color
        }
        window.statusBarColor = statusBarColor.toArgb()

        if (Build.VERSION.SDK_INT >= 23) {
            @Suppress("DEPRECATION")
            if (darkIcons) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    /**
     * Set the navigation bar color.
     *
     * @param color The **desired** [Color] to set. This may require modification if running on an
     * API level that only supports white navigation bar icons. Additionally this will be ignored
     * and [Color.Transparent] will be used on API 29+ where gesture navigation is preferred or the
     * system UI automatically applies background protection in other navigation modes.
     * @param darkIcons Whether dark navigation bar icons would be preferable. Only available on
     * API 26+.
     * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
     * dark icons were requested but are not available. Defaults to applying a black scrim.
     */
    override fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        val navBarColor = when {
            Build.VERSION.SDK_INT >= 29 -> Color.Transparent // For gesture nav
            darkIcons && Build.VERSION.SDK_INT < 26 -> transformColorForLightContent(color)
            else -> color
        }
        window.navigationBarColor = navBarColor.toArgb()

        if (Build.VERSION.SDK_INT >= 26) {
            @Suppress("DEPRECATION")
            if (darkIcons) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
    }

    /**
     * Set the status and navigation bars to [color].
     *
     * @see setStatusBarColor
     * @see setNavigationBarColor
     */
    override fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        setStatusBarColor(color, darkIcons, transformColorForLightContent)
        setNavigationBarColor(color, darkIcons, transformColorForLightContent)
    }
}

/**
 * An [androidx.compose.runtime.Ambient] holding the current [SysUiController]. Defaults to a
 * no-op controller; consumers should [provide][androidx.compose.runtime.Providers] a real one.
 */
val SysUiController = staticAmbientOf<SystemUiController> {
    FakeSystemUiController
}

private val BlackScrim = Color(0f, 0f, 0f, 0.2f) // 20% opaque black
private val BlackScrimmed: (Color) -> Color = { original ->
    BlackScrim.compositeOver(original)
}

/**
 * A fake implementation, useful as a default or used in Previews.
 */
private object FakeSystemUiController : SystemUiController {
    override fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit

    override fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit

    override fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit
}
