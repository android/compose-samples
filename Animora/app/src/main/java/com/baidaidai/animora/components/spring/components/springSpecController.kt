package com.baidaidai.animora.components.spring.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.baidaidai.animora.components.spring.model.list.dampingRatioTabRenderingList
import com.baidaidai.animora.components.spring.model.list.stiffnessTabRenderingList
import com.baidaidai.animora.components.spring.model.springSpecStudioViewModel
import androidx.compose.runtime.mutableFloatStateOf
import com.baidaidai.animora.components.spring.LocalSpringSpecStudioViewModel

/**
 * Spring 动画工作室的参数控制器。
 *
 * 这个 Composable 包含两组按钮组（[toggleButton]），分别用于选择预设的
 * `DampingRatio`（阻尼比）和 `Stiffness`（刚度）值。
 * 当用户选择不同的预设值时，它会通过 [LocalSpringSpecStudioViewModel]
 * 更新全局的 Spring 动画参数。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun springSpecStudioController(){

    var dampingRatioTabSelected by rememberSaveable { mutableStateOf(0) }
    var stiffNessTabSelected by rememberSaveable { mutableStateOf(0) }

    var dampingRatioValue by rememberSaveable { mutableFloatStateOf(1f) }
    var stiffNessValue by rememberSaveable { mutableFloatStateOf(50f) }

    val springSpecStudioViewModel = LocalSpringSpecStudioViewModel.current
        Column() {
            Text(
                text = "DampingRatio",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ){
                dampingRatioTabRenderingList.forEachIndexed { index, content ->
                    toggleButton(
                        list = dampingRatioTabRenderingList,
                        index = index,
                        content = content.featureName,
                        checked = dampingRatioTabSelected == index,
                        rowScope = this@Row
                    ){
                        dampingRatioTabSelected = index
                        dampingRatioValue = content.dampingRatioValue
                        springSpecStudioViewModel.changeSpringSpecValue(
                            dampingRatio = dampingRatioValue,
                            stiffness = stiffNessValue
                        )
                    }
                }
            }
            Text(
                text = "Stiffness",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ){
                stiffnessTabRenderingList.forEachIndexed { index, content ->
                    toggleButton(
                        list = stiffnessTabRenderingList,
                        index = index,
                        content = content.featureName,
                        checked = stiffNessTabSelected == index,
                        rowScope = this@Row
                    ){
                        stiffNessTabSelected = index
                        stiffNessValue = content.stiffnessValue
                        springSpecStudioViewModel.changeSpringSpecValue(
                            dampingRatio = dampingRatioValue,
                            stiffness = stiffNessValue
                        )
                    }
                }
            }
        }
}
