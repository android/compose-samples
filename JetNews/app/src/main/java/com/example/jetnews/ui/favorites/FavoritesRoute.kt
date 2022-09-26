package com.example.jetnews.ui.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.article.rememberBooleanState

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

    var firstTime by remember {
        mutableStateOf(false)
    }

     FavoriteRoute(uiState = uiState, openDrawer = openDrawer,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = {
            favoritesViewModel.unFavorite(it)
        }, onUnFavorite = {
            favoritesViewModel.unFavorite(it)
        },
        onInteractWithArticleDetails = {favoritesViewModel.openArticleDetails(it)},
        onInteractWithFavorite = {
                                 favoritesViewModel.interactedWithFeed()
        },
        snackbarHostState = snackbarHostState,
        onInteractWithFeed ={
            favoritesViewModel.interactedWithFeed()
        }, onOpenFirstPost = {
            if(isExpandedScreen){
                favoritesViewModel.openArticleDetails()
            }
         }
    )
}

@Composable
fun FavoriteRoute(uiState: FavoritesUiState,
                  onUnFavorite: (String) -> Unit,
                  onInteractWithFeed: () -> Unit,
                  onInteractWithArticleDetails: (String) -> Unit,
                  openDrawer: () -> Unit,
                  onToggleFavorite: (String) -> Unit,
                  isExpandedScreen: Boolean,
                  onInteractWithFavorite: () -> Unit,
                  onOpenFirstPost: () -> Unit,
                  snackbarHostState: SnackbarHostState){
    val favoriteType = getFavoriteScreenType(
        isExpandedScreen = isExpandedScreen,
        uiState = uiState)

    val articleDetailLazyListStates = when(uiState){
        is FavoritesUiState.HasFavorites -> uiState.favoriteFeed.favorites
        is FavoritesUiState.NoFavorites -> emptyList()
    }.associate{
        key(it.id) {
            it.id to rememberLazyListState()
        }
    }

    when(favoriteType){
        FavoriteScreenType.FavoriteDetails -> {
            check(uiState is FavoritesUiState.HasFavorites)
            val post = uiState.selectedPost ?: return
            ArticleScreen(
                post = post,
                isExpandedScreen = isExpandedScreen,
                isFavorite = true,
                onToggleFavorite = {
                    onToggleFavorite(uiState.selectedPost.id)
                },
                lazyListState = articleDetailLazyListStates.getValue(
                    uiState.selectedPost.id
                ),
                onBack = onInteractWithFavorite,
                snackbarVisibilityState = rememberBooleanState()
            )
            BackHandler {
                onInteractWithFeed()
            }
        }
        FavoriteScreenType.Feed -> {
            FavoritesScreen(
                snackbarHostState = snackbarHostState,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                uiState = uiState,
                onUnFavorite = { postId ->
                    onUnFavorite(postId)
                },
                interactWithFavorite = onInteractWithArticleDetails,
                isShowTopbar = true
            )
        }
        FavoriteScreenType.FavoriteWithDetails ->{

            MasterDetailFavoriteScreen(
                isShowTopbar = !isExpandedScreen,
                snackbarHostState = snackbarHostState,
                openDrawer = openDrawer,
                uiState =  uiState,
                onUnFavorite = onUnFavorite,
                interactWithFavorite = onInteractWithArticleDetails,
                isExpandedScreen = isExpandedScreen,
                modifier = Modifier,
                onToggleFavorite = onToggleFavorite,
                onInteractWithList =onInteractWithFavorite,
                onOpenFirstPost = onOpenFirstPost
            )
        }
    }

}