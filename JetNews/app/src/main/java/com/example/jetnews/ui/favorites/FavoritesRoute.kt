package com.example.jetnews.ui.favorites

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.example.jetnews.ui.article.ArticleScreen

@Composable
fun FavoritesRoute(
    favoritesViewModel: FavoritesViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
){

    val favoritesUiState = favoritesViewModel.uiState.collectAsState()

    val uiState = favoritesUiState.value

    val uiActions = favoritesViewModel.uiActions.collectAsState().value

    Log.d("FavoritesRoute", "uiActions: $uiActions")
    FavoriteRoute(uiState = uiState,
        uiActions = uiActions, openDrawer = openDrawer,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = {
            favoritesViewModel.unFavorite(it)
        }, onUnFavorite = {
            favoritesViewModel.unFavorite(it)
        },
        onInteractWithArticleDetails = {favoritesViewModel.openArticleDetails(it)},
        onInteractWithFavorite = {},
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun FavoriteRoute(uiActions: FavoriteUiActions,
                  uiState: FavoritesUiState,
                  onUnFavorite: (String) -> Unit,
                  onInteractWithArticleDetails: (String) -> Unit,
                  openDrawer: () -> Unit,
                  onToggleFavorite: (String) -> Unit,
                  isExpandedScreen: Boolean,
                  onInteractWithFavorite: () -> Unit,
                  snackbarHostState: SnackbarHostState){
    val favoriteType = getFavoriteScreenType(
        isExpandedScreen = isExpandedScreen,
        uiState = uiState)

    val articleDetailLazyListStates = when(uiState){
        is FavoritesUiState.HasFavorites -> uiState.favoriteFeed.favorite
        is FavoritesUiState.NoFavorites -> emptyList()
    }.associate{
        key(it.id) {
            it.id to rememberLazyListState()
        }
    }

    when(favoriteType){
        FavoriteScreenType.FavoriteDetails -> {
            check(uiState is FavoritesUiState.HasFavorites)
            Log.d("FavoriteScreenType", "FavoriteScreenType ----> ${uiState.selectedPost}")

            val post = uiState.selectedPost ?: return
            ArticleScreen(
                post = post,
                isExpandedScreen = isExpandedScreen,
                //onBack = onInteractWithFeed,
                onClickFavorite ={
                    // onClickFavorite(favorite)
                },
                isFavorite = true,
                onToggleFavorite = {
                    onToggleFavorite(uiState.selectedPost.id)
                },
                lazyListState = articleDetailLazyListStates.getValue(
                    uiState.selectedPost.id
                ),
                onBack = onInteractWithFavorite
            )
        }
        FavoriteScreenType.Feed -> {
            FavoritesScreen(
                uiActions = uiActions, //.value,
                snackbarHostState = snackbarHostState,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                favoriteState = uiState,
                onUnFavorite = { postId ->
                    onUnFavorite(postId)
                },
                interactWithFavorite = onInteractWithArticleDetails
            )
        }
        FavoriteScreenType.FavoriteWithDetails ->{
            Log.d("FavoriteScreenType", "FavoriteScreenType ---->")
        }
    }

}