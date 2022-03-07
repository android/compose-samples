package com.example.reply.data

import kotlinx.coroutines.flow.Flow

interface EmailsRepository {
    fun getAllEmails(): Flow<List<Email>>
    fun getCategoryEmails(category: MailboxType): Flow<List<Email>>
    fun getAllFolders(): List<String>
    fun getEmailFromId(id: Long): Flow<Email?>
}