package com.example.jetnews

import com.example.jetnews.data.db.FavoritesDao
import com.example.jetnews.model.Favorite
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesDaoFake( private val fakedb : HashMap<String, Favorite>) : FavoritesDao {

    override suspend fun insert(favorite: Favorite) {
        // Simulate insertion to db
        delay(1_000)
        fakedb[favorite.id] =favorite
    }

    override suspend fun delete(vararg favorites: Favorite) {
        fakedb.remove(favorites.first().id)
    }

    override fun collectFavorites(): Flow<List<Favorite>> {
        return flow{
            emit(fakedb.values.toList())
        }
    }

    override suspend fun getFavorites(): List<Favorite> {
        return fakedb.values.toList()
    }

    override suspend fun deleteFavoriteById(id: String) {
        fakedb.remove(id)
    }

    override suspend fun getFavoritePostIds(): List<String> {
       return fakedb.values.map{it.id}.toList()
    }
}