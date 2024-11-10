package com.example.compose.jetchat.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.compose.jetchat.JetchatApplication
import com.example.compose.jetchat.widget.composables.MessagesWidget

class JetChatWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val application = context.applicationContext as JetchatApplication
        val messagesRepository = application.container.messagesRepository

        provideContent {
            val bookmarks = messagesRepository.observeUnReadMessages()
            GlanceTheme {
                MessagesWidget(bookmarks.toList())
            }
        }
    }
}