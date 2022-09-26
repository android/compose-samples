package com.example.jetnews.favorites

import com.example.jetnews.data.Result
import com.example.jetnews.data.db.FavoritesDao
import com.example.jetnews.data.posts.impl.posts
import com.example.jetnews.model.FavoriteFeed
import com.example.jetnews.model.Post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(DelicateCoroutinesApi::class)
class FavoriteRepositoryImpl(private val db: FavoritesDao,
                             val context: CoroutineDispatcher = Dispatchers.IO) : FavoriteRepository {

    private val favoritePost = MutableStateFlow(FavoriteFeed())

    override fun observeFavoritePost(): Flow<FavoriteFeed> = favoritePost

    init {
        GlobalScope.launch{
            db.collectFavorites().collectLatest {
                favoritePost.value = FavoriteFeed(favorites = it)
            }
        }
    }


    override suspend fun getSinglePost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getFirstPost(): Flow<Result<Post?>> {
        return db.getFirstFavorite().flowOn(Dispatchers.IO).map { favorite ->
            if(favorite == null){
                Result.Error(IllegalArgumentException("Favorite post is empty"))
            } else {
                val data = posts.allPosts.find { it.id == favorite.id }
                Result.Success(data)
            }
        }
    }

    override suspend fun unFavoritePost(postId: String) {
        withContext(context){
            db.deleteFavoriteById(postId)
        }
    }

    override suspend fun fetchFavoritePosts(): Result<FavoriteFeed> {
        return withContext(context){
           val favorites=  db.getFavorites()
           Result.Success(FavoriteFeed(favorites))
       }
    }

}