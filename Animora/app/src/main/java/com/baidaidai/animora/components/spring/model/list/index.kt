package com.baidaidai.animora.components.spring.model.list

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.R

data class dampingRatioTabInfo(
    val featureName:String,
    val dampingRatioValue: Float
)
data class stiffnessTabInfo(
    val featureName:String,
    val stiffnessValue: Float
)

val dampingRatioTabRenderingList = listOf<dampingRatioTabInfo>(
    dampingRatioTabInfo(
        featureName = "No",
        dampingRatioValue = 1f
    ),
    dampingRatioTabInfo(
        featureName = "Low",
        dampingRatioValue = 0.75f
    ),
    dampingRatioTabInfo(
        featureName = "Mid",
        dampingRatioValue = 0.5f
    ),
    dampingRatioTabInfo(
        featureName = "High",
        dampingRatioValue = 0.2f
    )
)

val stiffnessTabRenderingList = listOf<stiffnessTabInfo>(
    stiffnessTabInfo(
        featureName = "Small",
        stiffnessValue = 50f
    ),
    stiffnessTabInfo(
        featureName = "Low",
        stiffnessValue = 200f
    ),
    stiffnessTabInfo(
        featureName = "Mid",
        stiffnessValue = 400f
    ),
    stiffnessTabInfo(
        featureName = "High",
        stiffnessValue = 1500f
    ),
    stiffnessTabInfo(
        featureName = "Big",
        stiffnessValue = 10000f
    )
)