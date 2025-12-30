package com.baidaidai.animora.shared.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class blueStateViewModel: ViewModel() {
    private val _blueState = MutableStateFlow(false)
    val blueState = _blueState.asStateFlow()

    fun changeBlueState(state: Boolean){
        _blueState.value = state
    }
}