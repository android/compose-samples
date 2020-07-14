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
import androidx.room.Ignore
import androidx.room.Transaction

/**
 * [Room] DAO which provides the implementation for our [TransactionRunner].
 */
@Dao
abstract class TransactionRunnerDao : TransactionRunner {
    @Transaction
    protected open suspend fun runInTransaction(tx: suspend () -> Unit) = tx()

    @Ignore
    override suspend fun invoke(tx: suspend () -> Unit) {
        runInTransaction(tx)
    }
}

/**
 * Interface with operator function which will invoke the suspending lambda within a database
 * transaction.
 */
interface TransactionRunner {
    suspend operator fun invoke(tx: suspend () -> Unit)
}
