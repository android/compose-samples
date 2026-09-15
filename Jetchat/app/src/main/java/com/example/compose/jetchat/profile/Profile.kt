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

package com.example.compose.jetchat.profile

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.AnimatingFabContent
import com.example.compose.jetchat.components.baselineHeight
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.theme.JetchatTheme

enum class ShowcaseColorMode(
    val label: String,
    val colorSpaceName: String,
    val description: String,
) {
    DisplayP3(
        label = "Display P3",
        colorSpaceName = "ColorSpaces.DisplayP3",
        description = "Wide Color Gamut (WCG) preserved via @ColorLong in Compose shaders & Paint without sRGB downsampling (CL 4034508).",
    ),
    ExtendedSrgbHdr(
        label = "Extended sRGB (HDR)",
        colorSpaceName = "ColorSpaces.ExtendedSrgb",
        description = "High Dynamic Range values (> 1.0f) produce vibrant highlight luminance on HDR displays without clipping.",
    ),
    SrgbClamped(
        label = "Standard sRGB",
        colorSpaceName = "ColorSpaces.Srgb",
        description = "Legacy standard gamut: colors are constrained to 8-bit sRGB color space.",
    ),
}

// Wide Color Gamut (Display P3) colors
private val P3Cyan = Color(0.0f, 0.95f, 1.0f, 1.0f, ColorSpaces.DisplayP3)
private val P3Magenta = Color(1.0f, 0.05f, 0.7f, 1.0f, ColorSpaces.DisplayP3)
private val P3Emerald = Color(0.05f, 1.0f, 0.45f, 1.0f, ColorSpaces.DisplayP3)
private val P3Amber = Color(1.0f, 0.75f, 0.0f, 1.0f, ColorSpaces.DisplayP3)

// Extended sRGB (HDR) colors with values > 1.0f
private val HdrCyan = Color(0.1f, 1.8f, 2.0f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrMagenta = Color(2.0f, 0.1f, 1.4f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrEmerald = Color(0.1f, 2.0f, 0.8f, 1.0f, ColorSpaces.ExtendedSrgb)
private val HdrAmber = Color(2.0f, 1.5f, 0.1f, 1.0f, ColorSpaces.ExtendedSrgb)

// Standard sRGB colors
private val SrgbCyan = Color(0.0f, 0.75f, 0.85f, 1.0f, ColorSpaces.Srgb)
private val SrgbMagenta = Color(0.85f, 0.1f, 0.6f, 1.0f, ColorSpaces.Srgb)
private val SrgbEmerald = Color(0.15f, 0.8f, 0.35f, 1.0f, ColorSpaces.Srgb)
private val SrgbAmber = Color(0.9f, 0.65f, 0.0f, 1.0f, ColorSpaces.Srgb)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    userData: ProfileScreenState,
    nestedScrollInteropConnection: NestedScrollConnection = rememberNestedScrollInteropConnection(),
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    val scrollState = rememberScrollState()
    var colorMode by remember { mutableStateOf(ShowcaseColorMode.DisplayP3) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollInteropConnection)
            .systemBarsPadding(),
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                ProfileHeader(
                    scrollState = scrollState,
                    data = userData,
                    containerHeight = this@BoxWithConstraints.maxHeight,
                    colorMode = colorMode,
                )
                UserInfoFields(
                    userData = userData,
                    containerHeight = this@BoxWithConstraints.maxHeight,
                    colorMode = colorMode,
                    onColorModeChanged = { colorMode = it },
                )
            }
        }

        val fabExtended by remember { derivedStateOf { scrollState.value == 0 } }
        ProfileFab(
            extended = fabExtended,
            userIsMe = userData.isMe(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                // Offsets the FAB to compensate for CoordinatorLayout collapsing behaviour
                .offset(y = ((-100).dp)),
            onFabClicked = { functionalityNotAvailablePopupShown = true },
        )
    }
}

@Composable
private fun UserInfoFields(
    userData: ProfileScreenState,
    containerHeight: Dp,
    colorMode: ShowcaseColorMode,
    onColorModeChanged: (ShowcaseColorMode) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        NameAndPosition(userData)

        // Interactive Wide Color Gamut & HDR Showcase Card
        WideColorGamutShowcaseCard(
            selectedMode = colorMode,
            onModeSelected = onColorModeChanged,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        ProfileProperty(stringResource(R.string.display_name), userData.displayName)

        ProfileProperty(stringResource(R.string.status), userData.status)

        ProfileProperty(stringResource(R.string.twitter), userData.twitter, isLink = true)

        userData.timeZone?.let {
            ProfileProperty(stringResource(R.string.timezone), userData.timeZone)
        }

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.height((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun NameAndPosition(userData: ProfileScreenState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData,
            modifier = Modifier.baselineHeight(32.dp),
        )
        Position(
            userData,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .baselineHeight(24.dp),
        )
    }
}

@Composable
private fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun Position(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.position,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    data: ProfileScreenState,
    containerHeight: Dp,
    colorMode: ShowcaseColorMode,
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) { offset.toDp() }

    val sweepColors = remember(colorMode) {
        when (colorMode) {
            ShowcaseColorMode.DisplayP3 -> listOf(P3Cyan, P3Magenta, P3Emerald, P3Amber, P3Cyan)
            ShowcaseColorMode.ExtendedSrgbHdr -> listOf(HdrCyan, HdrMagenta, HdrEmerald, HdrAmber, HdrCyan)
            ShowcaseColorMode.SrgbClamped -> listOf(SrgbCyan, SrgbMagenta, SrgbEmerald, SrgbAmber, SrgbCyan)
        }
    }

    val backdropBrush = remember(colorMode) {
        when (colorMode) {
            ShowcaseColorMode.DisplayP3 -> Brush.radialGradient(
                colors = listOf(
                    Color(0.0f, 0.95f, 1.0f, 0.4f, ColorSpaces.DisplayP3),
                    Color(1.0f, 0.05f, 0.7f, 0.25f, ColorSpaces.DisplayP3),
                    Color(1.0f, 0.05f, 0.7f, 0.0f, ColorSpaces.DisplayP3),
                ),
            )
            ShowcaseColorMode.ExtendedSrgbHdr -> Brush.radialGradient(
                colors = listOf(
                    Color(0.2f, 1.8f, 2.0f, 0.5f, ColorSpaces.ExtendedSrgb),
                    Color(2.0f, 0.1f, 1.4f, 0.35f, ColorSpaces.ExtendedSrgb),
                    Color(2.0f, 0.1f, 1.4f, 0.0f, ColorSpaces.ExtendedSrgb),
                ),
            )
            ShowcaseColorMode.SrgbClamped -> Brush.radialGradient(
                colors = listOf(
                    Color(0.0f, 0.75f, 0.85f, 0.35f, ColorSpaces.Srgb),
                    Color(0.85f, 0.1f, 0.6f, 0.2f, ColorSpaces.Srgb),
                    Color(0.85f, 0.1f, 0.6f, 0.0f, ColorSpaces.Srgb),
                ),
            )
        }
    }

    val paintAccentColor = remember(colorMode) {
        when (colorMode) {
            ShowcaseColorMode.DisplayP3 -> P3Magenta
            ShowcaseColorMode.ExtendedSrgbHdr -> HdrMagenta
            ShowcaseColorMode.SrgbClamped -> SrgbMagenta
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = containerHeight / 2)
            .padding(top = offsetDp),
        contentAlignment = Alignment.Center,
    ) {
        // Atmospheric Wide-Gamut backdrop shader (Brush.radialGradient - CL 4034508 AndroidShader)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backdropBrush),
        )

        data.photo?.let { photoRes ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(24.dp),
            ) {
                // Wide Gamut Halo & Platform Paint Accent Ring
                Canvas(
                    modifier = Modifier.matchParentSize(),
                ) {
                    val strokeWidth = 5.dp.toPx()
                    val haloRadius = (size.minDimension / 2f) - (strokeWidth / 2f)

                    // 1. Sweep gradient shader with wide color gamut (CL 4034508 AndroidShader)
                    drawCircle(
                        brush = Brush.sweepGradient(sweepColors),
                        radius = haloRadius,
                        style = Stroke(width = strokeWidth),
                    )

                    // 2. Platform Paint drawing via drawIntoCanvas (CL 4034508 AndroidPaint @ColorLong)
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = paintAccentColor
                            this.style = PaintingStyle.Stroke
                            this.strokeWidth = 2.dp.toPx()
                        }
                        canvas.drawCircle(
                            center = center,
                            radius = haloRadius + strokeWidth + 2.dp.toPx(),
                            paint = paint,
                        )
                    }
                }

                Image(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape),
                    painter = painterResource(id = photoRes),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WideColorGamutShowcaseCard(
    selectedMode: ShowcaseColorMode,
    onModeSelected: (ShowcaseColorMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isWindowWcg = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.window?.isWideColorGamut == true
        } else {
            false
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Wide Color Gamut & HDR",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (isWindowWcg) "WCG Active" else "sRGB Display",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isWindowWcg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "CL 4034508: Compose shaders & Paint now preserve non-sRGB colors on API 29+ via @ColorLong.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShowcaseColorMode.entries.forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) },
                        label = {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ColorSpace: ${selectedMode.colorSpaceName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = selectedMode.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        HorizontalDivider()
        Text(
            text = label,
            modifier = Modifier.baselineHeight(24.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val style = if (isLink) {
            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        Text(
            text = value,
            modifier = Modifier.baselineHeight(24.dp),
            style = style,
        )
    }
}

@Composable
fun ProfileError() {
    Text(stringResource(R.string.profile_error))
}

@Composable
fun ProfileFab(extended: Boolean, userIsMe: Boolean, modifier: Modifier = Modifier, onFabClicked: () -> Unit = { }) {
    key(userIsMe) {
        // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            onClick = onFabClicked,
            modifier = modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .height(48.dp)
                .widthIn(min = 48.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        painter = painterResource(id = if (userIsMe) R.drawable.ic_create else R.drawable.ic_chat),
                        contentDescription = stringResource(
                            if (userIsMe) R.string.edit_profile else R.string.message,
                        ),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.message,
                        ),
                    )
                },
                extended = extended,
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ConvPreviewLandscapeMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitMeDefault() {
    JetchatTheme {
        ProfileScreen(meProfile)
    }
}

@Preview(widthDp = 360, heightDp = 480)
@Composable
fun ConvPreviewPortraitOtherDefault() {
    JetchatTheme {
        ProfileScreen(colleagueProfile)
    }
}

@Preview
@Composable
fun ProfileFabPreview() {
    JetchatTheme {
        ProfileFab(extended = true, userIsMe = false)
    }
}
