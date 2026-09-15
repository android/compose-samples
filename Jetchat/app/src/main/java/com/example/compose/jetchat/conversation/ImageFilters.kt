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

package com.example.compose.jetchat.conversation

import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.RuntimeColorFilter
import android.os.Build
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asComposeColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect

/**
 * Available image filters for photos and messages.
 */
enum class ImageFilterType(val displayName: String) {
    None("Original"),
    Cyberpunk("Cyberpunk"),
    Duotone("Duotone"),
    Sepia("Sepia"),
    Grayscale("B&W"),
    Warm("Warm"),
    Cool("Cool"),
}

object ImageFilters {

    const val CYBERPUNK_SHADER = """
       vec4 main(half4 in_color) {
           float lum = dot(in_color.rgb, vec3(0.299, 0.587, 0.114));
           vec3 shadow = vec3(0.06, 0.04, 0.25);
           vec3 mid = vec3(0.92, 0.12, 0.58);
           vec3 high = vec3(0.18, 0.94, 0.98);
           vec3 col = lum < 0.5 ? mix(shadow, mid, lum * 2.0) : mix(mid, high, (lum - 0.5) * 2.0);
           return vec4(mix(in_color.rgb, col, 0.7), in_color.a);
       }
   """

    /**
     * Creates a [androidx.compose.ui.graphics.ColorFilter] using [RuntimeColorFilter] on Android 16+ (API 36+)
     * running the cyberpunk AGSL shader, with fallback for earlier API levels.
     */
    fun createCyberpunkColorFilter(): ColorFilter? {
        return getColorFilter(ImageFilterType.Cyberpunk)
    }

    /**
     * Creates a [androidx.compose.ui.graphics.RenderEffect] using [RuntimeColorFilter]
     * (via [RenderEffect.createColorFilterEffect]) instead of a RenderEffect shader.
     */
    fun createCyberpunkRenderEffect(): androidx.compose.ui.graphics.RenderEffect? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
        return try {
            val colorFilter: android.graphics.ColorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                RuntimeColorFilter(CYBERPUNK_SHADER)
            } else {
                ColorMatrixColorFilter(
                    android.graphics.ColorMatrix(
                        floatArrayOf(
                            1.2f, -0.1f, 0.2f, 0f, 25f,
                            -0.2f, 1.1f, 0.3f, 0f, 0f,
                            0.3f, -0.2f, 1.4f, 0f, 38f,
                            0f, 0f, 0f, 1f, 0f,
                        ),
                    ),
                )
            }
            RenderEffect.createColorFilterEffect(colorFilter).asComposeRenderEffect()
        } catch (t: Throwable) {
            null
        }
    }

    /**
     * Returns a [ColorFilter] for the specified [ImageFilterType].
     * For [ImageFilterType.Cyberpunk], uses [RuntimeColorFilter] on Android 16+ (API 36+).
     */
    fun getColorFilter(filter: ImageFilterType): ColorFilter? {
        return when (filter) {
            ImageFilterType.None -> null
            ImageFilterType.Cyberpunk -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    try {
                        RuntimeColorFilter(CYBERPUNK_SHADER).asComposeColorFilter()
                    } catch (t: Throwable) {
                        fallbackCyberpunkColorFilter()
                    }
                } else {
                    fallbackCyberpunkColorFilter()
                }
            }
            ImageFilterType.Duotone -> ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        0.8f, 0.2f, 0.1f, 0f, 25f,
                        0.1f, 0.7f, 0.4f, 0f, 12f,
                        0.2f, 0.1f, 0.9f, 0f, 50f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
            ImageFilterType.Sepia -> ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
            ImageFilterType.Grayscale -> ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                        0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                        0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
            ImageFilterType.Warm -> ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, 10f,
                        0f, 1.0f, 0f, 0f, 0f,
                        0f, 0f, 0.8f, 0f, -10f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
            ImageFilterType.Cool -> ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        0.8f, 0f, 0f, 0f, -10f,
                        0f, 1.0f, 0f, 0f, 0f,
                        0f, 0f, 1.2f, 0f, 15f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
        }
    }

    private fun fallbackCyberpunkColorFilter(): ColorFilter {
        return ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    1.2f, -0.1f, 0.2f, 0f, 25f,
                    -0.2f, 1.1f, 0.3f, 0f, 0f,
                    0.3f, -0.2f, 1.4f, 0f, 38f,
                    0f, 0f, 0f, 1f, 0f,
                ),
            ),
        )
    }
}
