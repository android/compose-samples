package com.example.reply

import com.example.reply.data.DummyEmail
import com.example.reply.data.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import kotlin.random.Random

interface EmailRepository {
    suspend fun getPosts(): Result<List<Email>>
}

class EmailRepositoryImpl : EmailRepository {

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0

    override suspend fun getPosts(): Result<List<Email>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(DummyEmail.getAllEmails())
            }

        }
    }
}
