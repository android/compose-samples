/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import com.example.jetsnack.R
import com.example.jetsnack.ui.MainActivity
import com.example.jetsnack.widget.data.RecentOrdersDataRepository
import com.example.jetsnack.widget.data.RecentOrdersDataRepository.Companion.getImageTextListDataRepo
import com.example.jetsnack.widget.layout.ImageTextListItemData
import com.example.jetsnack.widget.layout.ImageTextListLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentOrdersWidget : GlanceAppWidget() {
    // Unlike the "Single" size mode, using "Exact" allows us to have better control over rendering in
    // different sizes. And, unlike the "Responsive" mode, it doesn't cause several views for each
    // supported size to be held in the widget host's memory.
    override val sizeMode: SizeMode = SizeMode.Exact

    override val previewSizeMode = SizeMode.Responsive(
        setOf(
            DpSize(256.dp, 115.dp), // 4x2 cell min size
            DpSize(260.dp, 180.dp), // Medium width layout, height with header
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = getImageTextListDataRepo(id)

        val initialItems = withContext(Dispatchers.Default) {
            repo.load(context)
        }

        provideContent {
            GlanceTheme {
                val items by repo.data().collectAsState(initial = initialItems)

                key(LocalSize.current) {
                    WidgetContent(
                        items = items,
                        shoppingCartActionIntent = Intent(
                            context.applicationContext,
                            MainActivity::class.java,
                        )
                            .setAction(Intent.ACTION_VIEW)
                            .setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK,
                            )
                            .setData("https://jetsnack.example.com/home/cart".toUri()),
                    )
                }
            }
        }
    }

    @Composable
    fun WidgetContent(items: List<ImageTextListItemData>, shoppingCartActionIntent: Intent) {
        val context = LocalContext.current

        ImageTextListLayout(
            items = items,
            title = context.getString(R.string.widget_title),
            titleIconRes = R.drawable.widget_logo,
            titleBarActionIconRes = R.drawable.shopping_cart,
            titleBarActionIconContentDescription = context.getString(
                R.string.shopping_cart_button_label,
            ),
            titleBarAction = actionStartActivity(shoppingCartActionIntent),
            shoppingCartActionIntent = shoppingCartActionIntent,
        )
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val repo = RecentOrdersDataRepository()
        val items = repo.load(context)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    items = items,
                    shoppingCartActionIntent = Intent(),
                )
            }
        }
    }
}

class RecentOrdersWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = RecentOrdersWidget()

    @SuppressLint("RestrictedApi")
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            RecentOrdersDataRepository.cleanUp(AppWidgetId(it))
        }
        super.onDeleted(context, appWidgetIds)
    }
}
