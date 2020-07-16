/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jetcaster.data.Category
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [Category] related operations.
 */
@Dao
abstract class CategoriesDao {
    @Query(
        """
        SELECT categories.* FROM categories
        INNER JOIN (
            SELECT category_id, COUNT(podcast_uri) AS podcast_count FROM podcast_category_entries
            GROUP BY category_id
        ) ON category_id = categories.id
        ORDER BY podcast_count DESC
        LIMIT :limit
        """
    )
    abstract fun categoriesSortedByPodcastCount(
        limit: Int
    ): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name: String): Category?

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Category>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Category)

    @Delete
    abstract suspend fun delete(entity: Category): Int
}
