package com.example.jetnews.favorites

import com.example.jetnews.model.Favorite
import com.example.jetnews.model.FavoriteFeed
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun observeFavoritePost(): Flow<FavoriteFeed>

    suspend fun unFavoritePost(postId: String)// : Result<FavoriteFeed>

    suspend fun fetchFavoritePosts() : Result<FavoriteFeed>

}