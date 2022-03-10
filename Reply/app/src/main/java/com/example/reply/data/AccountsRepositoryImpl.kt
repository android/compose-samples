package com.example.reply.data

import com.example.reply.data.local.LocalAccountsDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AccountsRepositoryImpl : AccountsRepository {

    override fun getDefaultUserAccount(): Flow<Account> = flow {
        delay(1500)
        emit(LocalAccountsDataProvider.getDefaultUserAccount())
    }

    override fun getAllUserAccounts(): Flow<List<Account>> = flow {
        delay(1500)
        emit(LocalAccountsDataProvider.allUserAccounts)
    }


    override fun getContactAccountByUid(uid: Long): Flow<Account> = flow {
        delay(1500)
        emit(LocalAccountsDataProvider.getContactAccountByUid(uid))
    }
}