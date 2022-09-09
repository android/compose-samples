package com.example.jetnews.favorites

import com.example.jetnews.data.db.FavoritesDao
import com.example.jetnews.model.Favorite
import com.example.jetnews.model.FavoriteFeed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(private val db: FavoritesDao,
                             val context: CoroutineDispatcher = Dispatchers.IO) : FavoriteRepository {

    private val favoritePost = MutableStateFlow(FavoriteFeed())

    override fun observeFavoritePost(): Flow<FavoriteFeed> = favoritePost

    override suspend fun unFavoritePost(postId: String) {
        db.deleteFavoriteById(postId)
        val favorite = db.getFavorites()
        favoritePost.value = FavoriteFeed(favorite = favorite)
    }

    override suspend fun fetchFavoritePosts(): Result<FavoriteFeed> {
       return withContext(context){
           val favorites=  db.getFavorites()
           Result.success(FavoriteFeed(favorites))
       }
    }

}