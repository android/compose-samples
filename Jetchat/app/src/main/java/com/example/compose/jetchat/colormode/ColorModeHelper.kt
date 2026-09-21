/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat.colormode

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

enum class AppColorMode(val title: String, val description: String) {
    DEFAULT("sRGB (Default)", "Standard 8-bit color dynamic range (32bpp)"),
    WIDE_COLOR_GAMUT("Display P3", "Wide color gamut (10-bit / 16-bit float)"),
    HDR("Ultra HDR", "High dynamic range with gainmap highlights"),
}

object ColorModeHelper {

    /**
     * Checks if the active display supports Wide Color Gamut (Display P3) and
     * whether the system color manager is active.
     */
    fun isWideColorGamutSupported(context: Context, window: Window? = null): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val display = window?.let { getDisplay(it) }
        val displayWide = display?.isWideColorGamut == true
        val configWide = context.resources.configuration.isScreenWideColorGamut
        return displayWide || configWide
    }

    /**
     * Checks if the physical display hardware supports High Dynamic Range (HDR).
     */
    fun isHdrSupported(window: Window?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || window == null) return false
        val display = getDisplay(window) ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            display.isHdr
        } else {
            val hdrMode = display.mode.supportedHdrTypes
            hdrMode.isNotEmpty()
        }
    }

    /**
     * Queries the live HDR/SDR headroom ratio on Android 14+ (API 34+).
     * Returns 1.0f on devices or OS versions without gainmap/HDR ratio support.
     */
    fun getHdrSdrRatio(window: Window?): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && window != null) {
            val display = getDisplay(window)
            if (display != null) {
                return display.hdrSdrRatio
            }
        }
        return 1.0f
    }

    fun getDisplay(window: Window): Display? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.context.display
        } else {
            val displayManager = window.context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        }
    }

    fun findWindowFromView(view: View): Window? {
        var parent = view.parent
        while (parent != null) {
            if (parent is DialogWindowProvider) {
                return parent.window
            }
            parent = parent.parent
        }
        return findActivity(view.context)?.window
    }

    tailrec fun findActivity(context: Context): Activity? = when (context) {
        is Activity -> context
        is ContextWrapper -> findActivity(context.baseContext)
        else -> null
    }
}

/**
 * Resolves the enclosing [Window] for the current Composable.
 */
@Composable
fun rememberCurrentWindow(): Window? {
    val view = LocalView.current
    return remember(view) { ColorModeHelper.findWindowFromView(view) }
}

/**
 * Lifecycle-safe effect that sets the [targetMode] on the current [Window] and
 * restores the previous color mode when leaving composition.
 */
@Composable
fun WindowColorModeEffect(targetMode: AppColorMode, enabled: Boolean = true) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !enabled) return

    val window = rememberCurrentWindow() ?: return

    DisposableEffect(window, targetMode, enabled) {
        val targetModeInt = when (targetMode) {
            AppColorMode.DEFAULT -> ActivityInfo.COLOR_MODE_DEFAULT

            AppColorMode.WIDE_COLOR_GAMUT -> {
                if (ColorModeHelper.isWideColorGamutSupported(window.context, window)) {
                    ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT
                } else {
                    ActivityInfo.COLOR_MODE_DEFAULT
                }
            }

            AppColorMode.HDR -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                    ColorModeHelper.isHdrSupported(window)
                ) {
                    ActivityInfo.COLOR_MODE_HDR
                } else if (ColorModeHelper.isWideColorGamutSupported(window.context, window)) {
                    ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT
                } else {
                    ActivityInfo.COLOR_MODE_DEFAULT
                }
            }
        }

        val previousMode = window.colorMode
        if (previousMode != targetModeInt) {
            window.colorMode = targetModeInt
        }

        onDispose {
            if (window.colorMode != previousMode) {
                window.colorMode = previousMode
            }
        }
    }
}
