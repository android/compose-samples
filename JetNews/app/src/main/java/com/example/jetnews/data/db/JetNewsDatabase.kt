package com.example.jetnews.data.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jetnews.model.Favorite

@Database(entities = [Favorite::class], version = 1, exportSchema = false)
abstract class JetNewsDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    companion object{
        @Volatile var INSTANCE :  JetNewsDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): JetNewsDatabase{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    JetNewsDatabase::class.java,
                    "jet_news_db")
                    // .addCallback()
                    .build()
            }
            return INSTANCE!!
        }
    }
}