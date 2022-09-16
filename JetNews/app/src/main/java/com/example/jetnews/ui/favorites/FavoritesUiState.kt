package com.example.jetnews.ui.favorites

import android.util.Log
import com.example.jetnews.model.FavoriteFeed
import com.example.jetnews.model.Post
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
        val selectedPost: Post?,
        val favoriteFeed: FavoriteFeed,
        val selectedPostId: String? =null,
        val isArticleOpen: Boolean,
        override val isLoading: Boolean =false,
        override val searchInput: String = "",
        override val errorMessages: List<ErrorMessage> = emptyList()
    ) : FavoritesUiState


}

data class FavoritesViewModelState(
    val favoriteFeed : FavoriteFeed? =null,
    val selectedPostId: String? =null,
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean =false,
    val selectedPost: Post? =null,
    val isArticleOpen: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String ="",
    val uiActions: FavoriteUiActions = FavoriteUiActions.None
    //val unFavoriteId: String? =null
){

    fun toUiState(): FavoritesUiState{
        return if(favoriteFeed == null){
            FavoritesUiState.NoFavorites(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }else{
           // FavoritesUiState.UnFavorite()
            Log.d("FavoritesUiState", "selectedPost => $selectedPost")
            FavoritesUiState.HasFavorites(
                favoriteFeed = favoriteFeed,
                selectedPostId = selectedPostId,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                isArticleOpen = isArticleOpen,
                selectedPost = selectedPost
            )
        }
    }
}