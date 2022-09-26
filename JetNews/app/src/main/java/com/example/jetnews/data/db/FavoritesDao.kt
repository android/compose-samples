package com.example.jetnews.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetnews.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(vararg favorites: Favorite)

    @Query("SELECT * FROM favorites_table")
    fun collectFavorites(): Flow<List<Favorite>>


    @Query("SELECT * FROM favorites_table")
    suspend fun getFavorites(): List<Favorite>

    @Query("DELETE FROM favorites_table WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)


    @Query("SELECT id FROM favorites_table")
    suspend fun getFavoritePostIds(): List<String>

    @Query("SELECT * FROM favorites_table ORDER BY id DESC LIMIT 1")
    fun getFirstFavorite(): Flow<Favorite?>

}