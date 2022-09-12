package com.example.jetnews.ui.favorites

import android.util.Log
import com.example.jetnews.model.FavoriteFeed
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.utils.ErrorMessage

/**
 * UI state for the Favorites route
 *
 * This is derived from FavoriteViewModelState
 */
sealed interface FavoritesUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * No post has been marked favorite yet
     */
    data class NoFavorites(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ): FavoritesUiState


    /**
     * An internal representation of the favorites route state, in a raw form
     */
    data class HasFavorites(
        val favorites: FavoriteFeed? = null,
        val selectedPostId: String? =null,
        override val isLoading: Boolean =false,
        override val searchInput: String = "",
        override val errorMessages: List<ErrorMessage> = emptyList()
    ) : FavoritesUiState

}

data class FavoritesViewModelState(
    val favoriteFeed : FavoriteFeed? = null,
    val selectedPostId: String? =null,
    val isLoading: Boolean =false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String ="",
    val uiActions: FavoriteUiActions = FavoriteUiActions.None
    //val unFavoriteId: String? =null
){
//    fun toUiActionState(): FavoriteActions{
//        return
//    }


    fun toUiState(): FavoritesUiState{
        return if((favoriteFeed == null)
            or(favoriteFeed?.favorite?.isEmpty() ==true) ){
            FavoritesUiState.NoFavorites(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }else{
           // FavoritesUiState.UnFavorite()
            FavoritesUiState.HasFavorites(
                favorites = favoriteFeed,
                selectedPostId = selectedPostId,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
            )
        }
    }
}