package com.example.jetnews.ui.favorites

import androidx.compose.runtime.Composable

enum class FavoriteScreenType {
    FavoriteDetails,
    Feed,
    FavoriteWithDetails
}

@Composable
fun getFavoriteScreenType(
    isExpandedScreen: Boolean,
    uiState: FavoritesUiState
): FavoriteScreenType = when(isExpandedScreen){
    false -> {
        when(uiState){
            is FavoritesUiState.HasFavorites ->{
                if (uiState.isArticleOpen){
                    FavoriteScreenType.FavoriteDetails
                }else{
                    FavoriteScreenType.Feed
                }
            }
            is FavoritesUiState.NoFavorites -> FavoriteScreenType.Feed
        }
    }
    true -> FavoriteScreenType.FavoriteWithDetails
}