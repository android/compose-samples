package com.example.jetnews.ui.favorites

sealed class FavoriteUiActions {
    object Delete: FavoriteUiActions()
    object None :FavoriteUiActions()
}