package com.baidaidai.animora.shared.dataClass

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

final data class AnimationDatas(
    val id: Int,
    val name: String,
    @StringRes
    val shortInfo: Int,
    val details: String,
    val animationFiles:@Composable (Boolean) -> Unit
)