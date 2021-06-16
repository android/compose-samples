package com.example.jetcaster.ui

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
}