package com.example.reply.data

import com.example.reply.data.local.LocalAccountsDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AccountRepositoryImpl : AccountsRepository {

    override fun getDefaultUserAccount(): Flow<Account> = flow {
        delay(2000)
        emit(LocalAccountsDataProvider.getDefaultUserAccount())
    }

    override fun getAllUserAccounts(): Flow<List<Account>> = flow {
        delay(1500)
        emit(LocalAccountsDataProvider.allUserAccounts)
    }


    override fun getContactAccountById(uid: Long): Flow<Account> = flow {
        delay(1500)
        emit(LocalAccountsDataProvider.getContactAccountById(uid))
    }
}