package com.baidaidai.animora.components.StartScreen.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.components.StartScreen.list.model.animationList
import com.baidaidai.animora.shared.dataClass.AnimationDatas

@Composable
fun principledHeader(
    animationListDatas: AnimationDatas
){
    when(animationListDatas.id){
        0 -> {
            Header(content = "animate*AsState Family")
        }
        4 -> {
            Header(content = "SharedTransition Family")
        }
        6 -> {
            Header(content = "updateTransition Family")
        }
        7 -> {
            Header(content = "Animatable Family")
        }
        13 -> {
            Header(content = "InfiniteTransition Family")
        }
    }
}

@PreviewLightDark
@Composable
private fun _principleHeaderPreview(){
    principledHeader(animationList[1])
}