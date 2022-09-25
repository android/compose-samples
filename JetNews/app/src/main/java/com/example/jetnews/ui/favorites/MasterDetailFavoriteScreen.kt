package com.example.jetnews.ui.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MasterDetailFavoriteScreen (
    modifier: Modifier,
    isShowTopbar: Boolean,
    uiState: FavoritesUiState,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    onUnFavorite: (String) -> Unit,
    interactWithFavorite: (String) -> Unit
){
    Row(modifier){
        FavoriteScreenList(
            isShowTopbar = isShowTopbar,
            snackbarHostState ,
            openDrawer,
            isExpandedScreen ,
            uiState,
            hasPostsContent = { uiState, modifier ->

//                Crossfade(targetState = uiState.selectedPost){ selectedPost ->
//
//                }

            }
        )


    }
}

@Composable
fun FavoriteList(){

}