package com.example.reply.data

import kotlinx.coroutines.flow.Flow

/**
 * An Interface contract to get all enails info for a User.
 */
interface EmailsRepository {
    fun getAllEmails(): Flow<List<Email>>
    fun getCategoryEmails(category: MailboxType): Flow<List<Email>>
    fun getAllFolders(): List<String>
    fun getEmailFromId(id: Long): Flow<Email?>
}