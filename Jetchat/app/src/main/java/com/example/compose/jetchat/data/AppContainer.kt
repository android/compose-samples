package com.example.compose.jetchat.data

import com.example.compose.jetchat.data.messages.MessagesRepository
import com.example.compose.jetchat.data.messages.MessagesRepositoryImpl

interface AppContainer {
    val messagesRepository: MessagesRepository
}

class AppContainerImpl : AppContainer {
    override val messagesRepository by lazy {
        MessagesRepositoryImpl()
    }
}
