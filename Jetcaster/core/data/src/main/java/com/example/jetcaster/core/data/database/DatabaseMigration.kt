/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        if (!columnExists(db, "episodes", "enclosure")) {
            db.execSQL("ALTER TABLE episodes ADD COLUMN enclosure STRING")
        }
    }
}

private fun columnExists(
    db: SupportSQLiteDatabase,
    tableName: String,
    columnName: String
): Boolean {
    db.query("PRAGMA table_info('$tableName')").use { cursor ->
        val nameColumnIndex = cursor.getColumnIndex("name")

        while (cursor.moveToNext()) {
            if (cursor.getString(nameColumnIndex) == columnName) {
                return true
            }
        }
    }

    return false
}
