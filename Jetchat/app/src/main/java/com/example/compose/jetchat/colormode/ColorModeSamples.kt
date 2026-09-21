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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.ColorSpace
import android.graphics.Gainmap
import android.graphics.ImageDecoder
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.jetchat.R

object ColorModeSamples {

    // Wide Gamut pure primaries
    val p3Red = Color(1.0f, 0.0f, 0.0f, colorSpace = ColorSpaces.DisplayP3)
    val srgbRed = Color(1.0f, 0.0f, 0.0f, colorSpace = ColorSpaces.Srgb)

    val p3Green = Color(0.0f, 1.0f, 0.0f, colorSpace = ColorSpaces.DisplayP3)
    val srgbGreen = Color(0.0f, 1.0f, 0.0f, colorSpace = ColorSpaces.Srgb)

    val p3Blue = Color(0.0f, 0.0f, 1.0f, colorSpace = ColorSpaces.DisplayP3)
    val srgbBlue = Color(0.0f, 0.0f, 1.0f, colorSpace = ColorSpaces.Srgb)

    /**
     * Programmatically creates a bitmap containing an SDR base image and an
     * attached Ultra HDR [Gainmap] (on Android 14+ / API 34+).
     *
     * In SDR/Default and Wide Gamut modes, the light source renders at SDR white (1.0).
     * In HDR mode, HWUI uses the gainmap to boost highlight pixels up to peak hardware nits.
     */
    fun createUltraHdrSampleBitmap(width: Int = 600, height: Int = 400): Bitmap {
        val baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(baseBitmap)

        // Draw scenic dark twilight background gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(
                    AndroidColor.rgb(15, 23, 42),
                    AndroidColor.rgb(30, 41, 59),
                    AndroidColor.rgb(71, 85, 105),
                ),
                floatArrayOf(0f, 0.6f, 1.0f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw base SDR glowing sphere
        val cx = width * 0.5f
        val cy = height * 0.45f
        val radius = height * 0.28f

        val spherePaint = Paint().apply {
            shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(
                    AndroidColor.rgb(255, 255, 255),
                    AndroidColor.rgb(254, 215, 170),
                    AndroidColor.rgb(234, 88, 12),
                    AndroidColor.TRANSPARENT,
                ),
                floatArrayOf(0f, 0.35f, 0.75f, 1.0f),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawCircle(cx, cy, radius, spherePaint)

        // On Android 14+ (API 34), attach a real Gainmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val gainmapBitmap = Bitmap.createBitmap(width / 2, height / 2, Bitmap.Config.ALPHA_8)
            val gmCanvas = Canvas(gainmapBitmap)
            val gcx = cx * 0.5f
            val gcy = cy * 0.5f
            val gradius = radius * 0.5f

            // Full white in gainmap where maximum highlight boost should occur
            val gmPaint = Paint().apply {
                shader = RadialGradient(
                    gcx, gcy, gradius,
                    intArrayOf(
                        AndroidColor.WHITE,
                        AndroidColor.argb(200, 255, 255, 255),
                        AndroidColor.argb(50, 255, 255, 255),
                        AndroidColor.TRANSPARENT,
                    ),
                    floatArrayOf(0f, 0.3f, 0.7f, 1.0f),
                    Shader.TileMode.CLAMP,
                )
            }
            gmCanvas.drawCircle(gcx, gcy, gradius, gmPaint)

            val gainmap = Gainmap(gainmapBitmap).apply {
                setDisplayRatioForFullHdr(4.0f)
                setMinDisplayRatioForHdrTransition(1.0f)
                setRatioMin(1f, 1f, 1f)
                setRatioMax(4f, 4f, 4f)
            }
            baseBitmap.gainmap = gainmap
        }

        return baseBitmap
    }

    /**
     * Loads a wide-color bitmap from raw resources while preserving its embedded
     * Display P3 color space and bit-depth.
     */
    fun loadWideGamutBitmap(context: Context, @RawRes resId: Int): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.resources, resId)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.setTargetColorSpace(ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
                }
            } else {
                BitmapFactory.decodeResource(context.resources, resId)
            }
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Official WebKit Wide Color Gamut (Display P3) test image.
 * The background is pure Display P3 red, and the inner WebKit compass logo is
 * standard sRGB red.
 *
 * In Default (sRGB) mode: the display clips both to sRGB (255, 0, 0), making the
 * logo completely invisible.
 * In Display P3 mode: the background expands to vivid wide-gamut red, revealing the
 * WebKit compass logo clearly.
 */
@Composable
fun P3SecretEmblemSwatch(modifier: Modifier = Modifier, colorMode: AppColorMode = AppColorMode.DEFAULT) {
    val context = LocalContext.current
    val imageRequest = remember(context, colorMode) {
        ImageRequest.Builder(context)
            .data(R.raw.webkit_logo_p3)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (colorMode == AppColorMode.WIDE_COLOR_GAMUT || colorMode == AppColorMode.HDR) {
                        colorSpace(ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
                    } else {
                        colorSpace(ColorSpace.get(ColorSpace.Named.SRGB))
                    }
                }
            }
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = "WebKit Display P3 Wide Gamut Test",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
    }
}

/**
 * Real-world photograph with an embedded Display P3 profile showing saturated yellow/green tones.
 */
@Composable
fun YellowFlowerP3Visualizer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val flowerBitmap = remember(context) {
        ColorModeSamples.loadWideGamutBitmap(context, R.raw.yellow_flower_p3)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (flowerBitmap != null) {
            Image(
                bitmap = flowerBitmap.asImageBitmap(),
                contentDescription = "Display P3 Vibrant Yellow Flower",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Side-by-side color swatches comparing sRGB primaries against Display P3 primaries.
 */
@Composable
fun GamutComparisonBars(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GamutRow(
            label = "Red Primary",
            srgbColor = ColorModeSamples.srgbRed,
            p3Color = ColorModeSamples.p3Red,
        )
        GamutRow(
            label = "Green Primary",
            srgbColor = ColorModeSamples.srgbGreen,
            p3Color = ColorModeSamples.p3Green,
        )
        GamutRow(
            label = "Blue Primary",
            srgbColor = ColorModeSamples.srgbBlue,
            p3Color = ColorModeSamples.p3Blue,
        )
    }
}

@Composable
private fun GamutRow(label: String, srgbColor: Color, p3Color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(srgbColor)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("sRGB", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(p3Color)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text("Display P3", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * Ultra HDR visualizer showing an image with an embedded Gainmap.
 */
@Composable
fun UltraHdrGainmapVisualizer(modifier: Modifier = Modifier) {
    val bitmap = remember { ColorModeSamples.createUltraHdrSampleBitmap() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Ultra HDR Gainmap Sample",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Side-by-side gradient strips comparing sRGB vs Display P3 gradient ramps.
 * Note: Android's Shader requires all colors within a single LinearGradient
 * to belong to the exact same ColorSpace.
 */
@Composable
fun SmoothGradientStrip(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column {
            Text("sRGB Gradient Ramp", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0.06f, 0.09f, 0.16f, colorSpace = ColorSpaces.Srgb),
                                Color(1.0f, 0.0f, 0.0f, colorSpace = ColorSpaces.Srgb),
                                Color(0.0f, 1.0f, 0.0f, colorSpace = ColorSpaces.Srgb),
                                Color(0.22f, 0.74f, 0.97f, colorSpace = ColorSpaces.Srgb),
                            ),
                        ),
                    ),
            )
        }
        Column {
            Text("Display P3 Wide Gamut Gradient Ramp", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0.06f, 0.09f, 0.16f, colorSpace = ColorSpaces.DisplayP3),
                                Color(1.0f, 0.0f, 0.0f, colorSpace = ColorSpaces.DisplayP3),
                                Color(0.0f, 1.0f, 0.0f, colorSpace = ColorSpaces.DisplayP3),
                                Color(0.22f, 0.74f, 0.97f, colorSpace = ColorSpaces.DisplayP3),
                            ),
                        ),
                    ),
            )
        }
    }
}
