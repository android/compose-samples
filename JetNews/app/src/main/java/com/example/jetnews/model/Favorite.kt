package com.example.jetnews.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class Favorite (
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String? = null,)