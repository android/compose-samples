package com.baidaidai.animora.components.spring.model

import androidx.compose.animation.core.Spring
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class springSpecParameterData(
    val dampingRatio: Float,
    val stiffness: Float
)

class springSpecStudioViewModel: ViewModel(){
    private val _enableSpring = MutableStateFlow(true)
    private val _springSpecValue = MutableStateFlow(
        value = springSpecParameterData(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        )
    )
    val enableSpring = _enableSpring.asStateFlow()
    val springSpecValue = _springSpecValue.asStateFlow()

    fun changeSpringSpecValue(
        dampingRatio: Float,
        stiffness: Float
    ){
        _springSpecValue.update { oldSpringSpecParameterData ->
            oldSpringSpecParameterData.copy(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    }
    fun changeEnableSpringStatus(state: Boolean){
        _enableSpring.update { state }
    }
}