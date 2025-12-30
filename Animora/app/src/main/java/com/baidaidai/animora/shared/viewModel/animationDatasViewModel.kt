package com.baidaidai.animora.shared.viewModel

import androidx.lifecycle.ViewModel
import com.baidaidai.animora.components.StartScreen.list.model.animationList
import com.baidaidai.animora.shared.dataClass.AnimationDatas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class animationDatasViewModel: ViewModel() {
    private val _selectedAnimation = MutableStateFlow(animationList[0])
    val selectedAnimation = _selectedAnimation.asStateFlow()


    fun changeSelectedAnimation(animationDatas: AnimationDatas) {
        _selectedAnimation.update {
            animationDatas
        }
    }
}
