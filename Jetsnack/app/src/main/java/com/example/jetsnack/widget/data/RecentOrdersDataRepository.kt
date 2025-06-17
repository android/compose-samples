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

package com.example.jetsnack.widget.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat.getString
import androidx.glance.GlanceId
import com.example.jetsnack.R
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.snacks
import com.example.jetsnack.widget.layout.ImageTextListItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake in-memory implementation of repository that produces list of [ImageTextListItemData]
 */
class RecentOrdersDataRepository {
    private val data = MutableStateFlow(listOf<ImageTextListItemData>())
    private var items = demoItems.take(MAX_ITEMS)

    /**
     * Flow of [ImageTextListItemData]s that can be listened to during a Glance session.
     */
    fun data(): Flow<List<ImageTextListItemData>> = data

    /**
     * Loads the list of [ImageTextListItemData]s.
     */
    fun load(context: Context): List<ImageTextListItemData> {
        data.value = if (items.isNotEmpty()) {
            processImagesAndBuildData(items, context)
        } else {
            listOf()
        }

        return data.value
    }

    private fun processImagesAndBuildData(items: List<DemoDataItem>, context: Context): List<ImageTextListItemData> {
        val mappedItems =
            items.map { item ->
                return@map ImageTextListItemData(
                    key = item.key,
                    title = item.title,
                    supportingText = item.supportingText,
                    supportingImage = item.supportingImage,
                    trailingIconButton = R.drawable.add_shopping_cart,
                    trailingIconButtonContentDescription =
                    getString(context, R.string.add_to_cart_content_description),
                    snackKeys = item.snackKeys,
                )
            }

        return mappedItems
    }

    /**
     * snackKey: This app adds snacks to the cart based on where the [Snack] is positioned in a list
     */
    data class DemoDataItem(
        val key: String,
        val snackKeys: List<Int>,
        val orderLine: List<Snack> = snackKeys.map { snacks[it] },
        val title: String = orderLine[0].name,
        val supportingText: String = orderLine.joinToString { it.name },
        @DrawableRes val supportingImage: Int = orderLine[0].imageRes,
        @DrawableRes val trailingIconButton: Int? = null,
        val trailingIconButtonContentDescription: String? = null,
    )

    companion object {
        private const val MAX_ITEMS = 10

        private val demoItems = listOf(
            DemoDataItem(
                key = "1",
                snackKeys = listOf(0, 20),
            ),
            DemoDataItem(
                key = "2",
                snackKeys = listOf(1, 21),
            ),
            DemoDataItem(
                key = "3",
                snackKeys = listOf(2, 22),
            ),
            DemoDataItem(
                key = "4",
                snackKeys = listOf(3, 23),
            ),
            DemoDataItem(
                key = "5",
                snackKeys = listOf(4, 24),
            ),
        )

        private val repositories = mutableMapOf<GlanceId, RecentOrdersDataRepository>()

        /**
         * Returns the repository instance for the given widget represented by [glanceId].
         */
        fun getImageTextListDataRepo(glanceId: GlanceId): RecentOrdersDataRepository = synchronized(repositories) {
            repositories.getOrPut(glanceId) { RecentOrdersDataRepository() }
        }

        /**
         * Cleans up local data associated with the provided [glanceId].
         */
        fun cleanUp(glanceId: GlanceId) {
            synchronized(repositories) {
                repositories.remove(glanceId)
            }
        }
    }
}
