package com.example.compose.jetchat.data.messages

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.initialMessages

interface MessagesRepository {

    fun observeUnReadMessages(): SnapshotStateList<Message>
}

class MessagesRepositoryImpl: MessagesRepository{

    private val unReadMessages = mutableStateListOf<Message>()

    init {
        unReadMessages.addAll(initialMessages.filter { it.author != "me" })
    }

    override fun observeUnReadMessages(): SnapshotStateList<Message>{
        return unReadMessages
    }

}