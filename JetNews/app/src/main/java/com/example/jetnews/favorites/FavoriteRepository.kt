package com.example.jetnews.favorites

import com.example.jetnews.data.Result
import com.example.jetnews.model.Favorite
import com.example.jetnews.model.FavoriteFeed
import com.example.jetnews.model.Post
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun observeFavoritePost(): Flow<FavoriteFeed>

    suspend fun unFavoritePost(postId: String)// : Result<FavoriteFeed>

    suspend fun fetchFavoritePosts() : Result<FavoriteFeed>

    suspend fun getSinglePost(postId: String?): Result<Post>

    suspend fun getFirstPost(): Flow<Result<Post?>>
}