package com.baidaidai.animora.components.StartScreen.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class homeScreenBlurViewModel: ViewModel() {
    private var _blurStatus = MutableStateFlow(false)
    val blurStatus = _blurStatus.asStateFlow()

    fun changeBlurStatus(value: Boolean){
        _blurStatus.update {
            value
        }
    }
}