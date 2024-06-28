package com.example.compose.jetchat.widget

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.compose.jetchat.widget.composables.MessagesWidget

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        provideContent {
            // Your Compose Code here
            GlanceTheme {
                MessagesWidget()
            }
        }
    }

    companion object {
        internal val ICON_SQUARE = DpSize(50.dp, 50.dp)
        internal val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        internal val MEDIUM_SQUARE = DpSize(150.dp, 150.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            ICON_SQUARE,
            SMALL_SQUARE,
            MEDIUM_SQUARE,
        )
    )
}