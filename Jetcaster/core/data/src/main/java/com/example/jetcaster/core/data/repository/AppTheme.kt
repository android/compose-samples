package com.example.jetcaster.core.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ThemeManager {
    val currentTheme: StateFlow<AppTheme>
    fun updateTheme(theme: AppTheme)
}

internal class ThemeStore: ThemeManager {
    override val currentTheme = MutableStateFlow(AppTheme.SYSTEM)

    override fun updateTheme(theme: AppTheme) {
        currentTheme.value = theme
    }
}

enum class AppTheme {
    DYNAMIC,
    SYSTEM,
}