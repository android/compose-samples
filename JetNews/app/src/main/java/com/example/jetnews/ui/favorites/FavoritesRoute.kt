package com.example.jetnews.ui.favorites

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

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

    val uiActions = favoritesViewModel.uiActions.collectAsState()

    FavoritesScreen(
        uiActions = uiActions.value,
        snackbarHostState = snackbarHostState,
        isExpandedScreen = isExpandedScreen,
        openDrawer = openDrawer,
        favoriteState = favoritesUiState.value,
        onUnFavorite = { postId ->
            favoritesViewModel.unFavorite(postId)
        }
    )

}