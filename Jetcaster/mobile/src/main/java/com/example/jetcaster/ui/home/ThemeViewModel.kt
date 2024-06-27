package com.example.jetcaster.ui.home

import androidx.lifecycle.ViewModel
import com.example.jetcaster.core.data.repository.AppTheme
import com.example.jetcaster.core.data.repository.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeManager: ThemeManager
): ViewModel() {

    private var currentThemeIndex = 0

    fun setTheme() {
        val theme = AppTheme.entries[currentThemeIndex]
        themeManager.updateTheme(theme)
        currentThemeIndex = (++currentThemeIndex) % AppTheme.entries.size
    }
} 