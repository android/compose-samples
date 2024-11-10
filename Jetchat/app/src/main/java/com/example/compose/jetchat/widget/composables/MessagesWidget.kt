package com.example.compose.jetchat.widget.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.Text
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.R
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.widget.theme.JetChatGlanceTextStyles
import com.example.compose.jetchat.widget.theme.JetchatGlanceColorScheme

@Composable
fun MessagesWidget(messages: List<Message>) {
    Scaffold(titleBar = {
        TitleBar(
            startIcon = ImageProvider(R.drawable.ic_jetchat),
            iconColor = null,
            title = LocalContext.current.getString(R.string.messages_widget_title),
        )
    }, backgroundColor = JetchatGlanceColorScheme.colors.background) {
        LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
            messages.forEach {
                item {
                    Column(modifier = GlanceModifier.fillMaxWidth()) {
                        MessageItem(it)
                        Spacer(modifier = GlanceModifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(modifier = GlanceModifier.clickable(actionStartActivity<NavActivity>()).fillMaxWidth()) {
        Text(
            text = message.author,
            style = JetChatGlanceTextStyles.titleMedium
        )
        Text(
            text = message.content,
            style = JetChatGlanceTextStyles.bodyMedium,
        )
    }
}

@Preview
@Composable
fun MessageItemPreview() {
    MessageItem(Message("John", "This is a preview of the message Item", "8:02PM"))
}

@Preview
@Composable
fun WidgetPreview() {
    MessagesWidget(listOf(Message("John", "This is a preview of the message Item", "8:02PM")))
}
