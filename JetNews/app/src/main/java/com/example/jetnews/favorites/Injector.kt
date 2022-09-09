package com.example.jetnews.favorites

import android.app.Application
import com.example.jetnews.data.db.FavoritesDao
import com.example.jetnews.data.db.JetNewsDatabase

object Injector {

    @Volatile var favoriteRepositoryInstance : FavoriteRepository? =null

    @Synchronized
     fun inject(application: Application){
         if(favoriteRepositoryInstance == null){
             provideDao(application)?.run {
                 favoriteRepositoryInstance = FavoriteRepositoryImpl(this)
             }
         }
     }

    private fun provideDao(application: Application): FavoritesDao?{
        return JetNewsDatabase.getDatabase(application)?.favoritesDao()
    }
}