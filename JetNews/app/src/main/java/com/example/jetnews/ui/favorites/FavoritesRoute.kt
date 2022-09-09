package com.example.jetnews.ui.favorites

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.jetnews.model.Favorite

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
//    val (favoritesSavedUiState) = rememberSaveable{
//        mutableStateOf(favoritesUiState.value)
//    }

    FavoritesScreen(
        snackbarHostState = snackbarHostState,
        isExpandedScreen = isExpandedScreen,
        openDrawer = openDrawer,
        favoriteState = favoritesUiState.value
    )

}