package com.example.jetlagged.ui.util

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "small font",
    group = "font scales",
    fontScale = 0.5f
)
@Preview(
    name = "large font",
    group = "font scales",
    fontScale = 1.5f
)
annotation class FontScalePreviews

@Preview(showBackground = true)
@Preview(device = Devices.TABLET, showBackground = true)
@Preview(device = Devices.FOLDABLE, showBackground = true)
@Preview(device = Devices.PIXEL_2)
annotation class MultiDevicePreview