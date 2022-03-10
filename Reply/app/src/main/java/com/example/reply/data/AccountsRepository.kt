package com.example.reply.data

import kotlinx.coroutines.flow.Flow

/**
 * An Interface contract to get all accounts info for User.
 */
interface AccountsRepository {
    fun getDefaultUserAccount(): Flow<Account>
    fun getAllUserAccounts(): Flow<List<Account>>
    fun getContactAccountByUid(uid: Long): Flow<Account>
}