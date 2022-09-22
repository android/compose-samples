/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetnews

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jetnews.data.db.JetNewsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JetnewsTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var jetDb: JetNewsDatabase

    @Before
    fun setUp() {

        composeTestRule.launchJetNewsApp(ApplicationProvider.getApplicationContext())

    }


    @Test
    fun app_launches() {
        composeTestRule.onNodeWithText("Top stories for you").assertExists()
    }

    @Test
    fun app_opensArticle() {

        println(composeTestRule.onRoot().printToString())
        composeTestRule.onNodeWithText(text = "Manuel Vivo", substring = true).performClick()

        println(composeTestRule.onRoot().printToString())
        try {
            composeTestRule.onNodeWithText("3 min read", substring = true).assertExists()
        } catch (e: AssertionError) {
            println(composeTestRule.onRoot().printToString())
            throw e
        }
    }

    @Test
    fun app_opensInterests() {
        composeTestRule.onNodeWithContentDescription(
            label = "Open navigation drawer",
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText("Interests").performClick()
        composeTestRule.onNodeWithText("Topics").assertExists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun app_openFavorites() = runTest{
        composeTestRule.onNodeWithText(text = "Manuel Vivo", substring = true).performClick()
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertExists()
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()
        composeTestRule.onNodeWithContentDescription(
            label = "Open navigation drawer",
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText("Favorites").performClick()

        composeTestRule.onNodeWithText("Dagger in Kotlin: Gotchas and Optimizations",
            substring =true)
            .assertExists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun app_openFavorite_details() {

        composeTestRule.onNodeWithText(text = "Manuel Vivo", substring = true).performClick()
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()
        composeTestRule.onNodeWithText("Favorites").performClick()

        composeTestRule.onNodeWithText("Dagger in Kotlin: Gotchas and Optimizations",
            substring =true)
            .performClick()

        composeTestRule.onNodeWithText("Dagger in Kotlin: Gotchas and Optimizations",
            substring =true)
            .assertExists()
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertExists()
    }
}
