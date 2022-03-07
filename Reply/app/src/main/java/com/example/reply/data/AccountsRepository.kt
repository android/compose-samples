package com.example.reply.data

import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getDefaultUserAccount(): Flow<Account>
    fun getAllUserAccounts(): Flow<List<Account>>
    fun getContactAccountById(uid: Long): Flow<Account>
}