package com.example.jetnews.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.favorites.FavoriteRepository
import com.example.jetnews.ui.interests.InterestsViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private val favoriteViewModelState = MutableStateFlow(FavoritesViewModelState(isLoading = true))

    init {
        refreshFavorites()
    }

    val uiState = favoriteViewModelState.map {
        it.toUiState()
    }.stateIn(viewModelScope,
        SharingStarted.Eagerly,
        favoriteViewModelState.value.toUiState())

    fun refreshFavorites(){
        favoriteViewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
          //  val result = postsRepository.getPostsFeed()
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