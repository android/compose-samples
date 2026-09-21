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

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.compose.jetchat.R

/**
 * Interactive dialog allowing developers to inspect and compare:
 * 1. Default (sRGB)
 * 2. Wide Color Gamut (Display P3)
 * 3. High Dynamic Range (Ultra HDR)
 */
@Composable
fun ColorModeInspectorDialog(onDismissRequest: () -> Unit) {
    var activeMode by remember { mutableStateOf(AppColorMode.DEFAULT) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Keep the host activity window in sync with the active color mode
    WindowColorModeEffect(targetMode = activeMode)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        // Dynamically scope the chosen color mode to this dialog window
        WindowColorModeEffect(targetMode = activeMode)

        val dialogWindow = rememberCurrentWindow()
        val context = LocalContext.current

        val isWcgSupported = remember(context, dialogWindow) {
            ColorModeHelper.isWideColorGamutSupported(context, dialogWindow)
        }
        val isHdrSupported = remember(dialogWindow) {
            ColorModeHelper.isHdrSupported(dialogWindow)
        }
        val hdrRatio = remember(dialogWindow, activeMode) {
            ColorModeHelper.getHdrSdrRatio(dialogWindow)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_color_lens),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Color Modes & HDR",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Interactive Display Color Inspector",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Mode Selector Segment
                Text(
                    text = "SELECT ACTIVE WINDOW COLOR MODE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start),
                )
                Spacer(Modifier.height(8.dp))

                ColorModeSelector(
                    activeMode = activeMode,
                    onModeSelected = { activeMode = it },
                )

                Spacer(Modifier.height(14.dp))

                // Hardware Status Card
                HardwareStatusCard(
                    activeMode = activeMode,
                    isWcgSupported = isWcgSupported,
                    isHdrSupported = isHdrSupported,
                    hdrRatio = hdrRatio,
                )

                Spacer(Modifier.height(16.dp))

                // Feature Comparison Tabs
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Display P3") },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Ultra HDR") },
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("10-Bit") },
                    )
                }

                Spacer(Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        Text(
                            text = "Wide Color Gamut (Display P3) Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Using the official WebKit 16-bit Display P3 test suite image. " +
                                "In Default (sRGB) mode, wide gamut reds clip to sRGB, making the WebKit compass emblem invisible. " +
                                "In Display P3 mode, the background expands to wide gamut red, revealing the emblem!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(12.dp))
                        P3SecretEmblemSwatch(colorMode = activeMode)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Real-World Display P3 Photograph",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Notice the deeper, richer saturation in the yellow petals and green leaves " +
                                "when switching to Display P3.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(8.dp))
                        YellowFlowerP3Visualizer()
                        Spacer(Modifier.height(16.dp))
                        GamutComparisonBars()
                    }

                    1 -> {
                        Text(
                            text = "Ultra HDR (Gainmap) Highlight Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "This scene uses an Android 14+ Gainmap attached to the bitmap. " +
                                "In Default and Display P3 modes, the light source renders at standard SDR white. " +
                                "In Ultra HDR mode, the panel hardware boosts the highlight past standard peak nits!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(12.dp))
                        UltraHdrGainmapVisualizer()
                    }

                    2 -> {
                        Text(
                            text = "Color Precision (10-bit Depth)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "10-bit/16-bit color channels (RGBA_1010102 / RGBA_F16) provide 1,024 levels per color channel " +
                                "compared to 256 levels in standard 8-bit, removing stepping artifacts in smooth sky gradients.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        Spacer(Modifier.height(12.dp))
                        SmoothGradientStrip()
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Close Button
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun ColorModeSelector(activeMode: AppColorMode, onModeSelected: (AppColorMode) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AppColorMode.entries.forEach { mode ->
            val isSelected = activeMode == mode
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .clickable { onModeSelected(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (mode) {
                        AppColorMode.DEFAULT -> "Default (sRGB)"
                        AppColorMode.WIDE_COLOR_GAMUT -> "Display P3"
                        AppColorMode.HDR -> "Ultra HDR"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun HardwareStatusCard(
    activeMode: AppColorMode,
    isWcgSupported: Boolean,
    isHdrSupported: Boolean,
    hdrRatio: Float,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Display Hardware Capabilities",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "Mode: ${activeMode.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Wide Gamut:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        if (isWcgSupported) "Supported (DCI-P3)" else "Unsupported",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isWcgSupported) Color(0xFF16A34A) else Color.Gray,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("HDR Hardware:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        if (isHdrSupported) "Supported (1000+ nits)" else "Unsupported",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isHdrSupported) Color(0xFF16A34A) else Color.Gray,
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("HDR/SDR Headroom:", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${String.format("%.2f", hdrRatio)}x",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
