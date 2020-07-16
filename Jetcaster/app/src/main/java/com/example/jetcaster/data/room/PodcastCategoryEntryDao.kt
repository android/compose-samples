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
import androidx.room.Update
import com.example.jetcaster.data.PodcastCategoryEntry

/**
 * [Room] DAO for [PodcastCategoryEntry] related operations.
 */
@Dao
abstract class PodcastCategoryEntryDao {
    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: PodcastCategoryEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: PodcastCategoryEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<PodcastCategoryEntry>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: PodcastCategoryEntry)

    @Delete
    abstract suspend fun delete(entity: PodcastCategoryEntry): Int
}
