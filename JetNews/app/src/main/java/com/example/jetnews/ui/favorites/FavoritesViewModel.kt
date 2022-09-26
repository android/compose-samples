package com.example.jetnews.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.favorites.FavoriteRepository
import com.example.jetnews.ui.interests.InterestsViewModel
import com.example.jetnews.utils.ErrorMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FavoritesViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private val favoriteViewModelState =
        MutableStateFlow(FavoritesViewModelState(isLoading = false))


    init {

        print("MyNeWData => NEW POST")

       viewModelScope.launch {
           favoriteRepository.observeFavoritePost()
               .collectLatest {
                   refreshFavorites()
           }
       }

        viewModelScope.launch {


            favoriteRepository.getFirstPost().collectLatest { result ->
                print("MyNeWData => $result")
                when(result){
                    is Result.Success ->{
                        favoriteViewModelState.update {
                            it.copy(selectedPost = result.data)
                        }
                    }
                    is Result.Error ->{
                        // An error occurs while getting post
                    }
                }
            }
        }

    }

    val uiState = favoriteViewModelState.map {
        it.toUiState()
    }.stateIn(viewModelScope,
        SharingStarted.Eagerly,
        favoriteViewModelState.value.toUiState())

    private fun refreshFavorites(){
        favoriteViewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            when(val result = favoriteRepository.fetchFavoritePosts()){
                is Result.Success ->{
                    favoriteViewModelState.update {
                        it.copy(favoriteFeed = result.data, isLoading = false)
                    }
                }
                is Result.Error ->{
                    val msg = result.exception.message ?: "Failed to retrieve favorites"
                    favoriteViewModelState.update {
                        it.copy(errorMessages = listOf(ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error)), isLoading = false)
                    }
                }
            }
        }
    }

    fun unFavorite(postId: String){
        viewModelScope.launch {

            favoriteRepository.unFavoritePost(postId)

//            favoriteViewModelState.update {
//                it.copy(uiActions = FavoriteUiActions.Delete)
//            }
        }
    }

    fun openArticleDetails(postId: String? = null){
        viewModelScope.launch {

            when(val result = favoriteRepository.getSinglePost(postId)){
                is Result.Success ->{
                    favoriteViewModelState.update {
                        it.copy(isArticleOpen = true,
                            selectedPostId = postId,
                            selectedPost = result.data)
                    }
                }
                is Result.Error ->{
                    // Show error!!
                }
            }
        }
    }

    /**
     * Notify that the user interacted with the feed
     */
    fun interactedWithFeed() {
        favoriteViewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    /**
     * Factory for InterestsViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            favoriteRepository: FavoriteRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoritesViewModel(favoriteRepository) as T
            }
        }
    }
}