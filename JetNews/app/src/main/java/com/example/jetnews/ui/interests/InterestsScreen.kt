/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.interests

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.previewDataFrom
import com.example.jetnews.ui.state.uiStateFrom

private enum class Sections(val title: String) {
    Topics("Topics"),
    People("People"),
    Publications("Publications")
}

@Composable
fun InterestsScreen(
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    interestsRepository: InterestsRepository
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Interests,
                closeDrawer = { scaffoldState.drawerState.close() },
                navigateTo = navigateTo
            )
        },
        topBar = {
            TopAppBar(
                title = { Text("Interests") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState.open() }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = {
            val (currentSection, updateSection) = remember { mutableStateOf(Sections.Topics) }
            InterestsScreenBody(currentSection, updateSection, interestsRepository)
        }
    )
}

private val Tabs = Sections.values().toList()

@Composable
private fun InterestsScreenBody(
    currentSection: Sections,
    updateSection: (Sections) -> Unit,
    interestsRepository: InterestsRepository
) {
    Column {
        TabRow(
            selectedTabIndex = currentSection.ordinal
        ) {
            Tabs.forEachIndexed { index, section ->
                Tab(
                    text = { Text(section.title) },
                    selected = section.ordinal == index,
                    onClick = {
                        updateSection(Sections.values()[index])
                    }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when (currentSection) {
                Sections.Topics -> {
                    val topicsState = uiStateFrom(interestsRepository::getTopics)
                    if (topicsState is UiState.Success) {
                        TopicsTab(topicsState.data)
                    }
                }
                Sections.People -> {
                    val peopleState = uiStateFrom(interestsRepository::getPeople)
                    if (peopleState is UiState.Success) {
                        PeopleTab(peopleState.data)
                    }
                }
                Sections.Publications -> {
                    val publicationsState = uiStateFrom(interestsRepository::getPublications)
                    if (publicationsState is UiState.Success) {
                        PublicationsTab(publicationsState.data)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicsTab(topics: Map<String, List<String>>) {
    TabWithSections(tabName = Sections.Topics.title, sections = topics)
}

@Composable
private fun PeopleTab(people: List<String>) {
    TabWithTopics(tabName = Sections.People.title, topics = people)
}

@Composable
private fun PublicationsTab(publications: List<String>) {
    TabWithTopics(tabName = Sections.Publications.title, topics = publications)
}

@Composable
private fun TabWithTopics(tabName: String, topics: List<String>) {
    ScrollableColumn(modifier = Modifier.padding(top = 16.dp)) {
        topics.forEach { topic ->
            TopicItem(
                getTopicKey(
                    tabName,
                    "- ",
                    topic
                ),
                topic
            )
            TopicDivider()
        }
    }
}

@Composable
private fun TabWithSections(
    tabName: String,
    sections: Map<String, List<String>>
) {
    ScrollableColumn {
        sections.forEach { (section, topics) ->
            Text(
                text = section,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.subtitle1
            )
            topics.forEach { topic ->
                TopicItem(
                    getTopicKey(
                        tabName,
                        section,
                        topic
                    ),
                    topic
                )
                TopicDivider()
            }
        }
    }
}

@Composable
private fun TopicItem(topicKey: String, itemTitle: String) {
    val image = imageResource(R.drawable.placeholder_1_1)
    val selected = isTopicSelected(topicKey)
    val onSelected = { it: Boolean ->
        selectTopic(topicKey, it)
    }
    Row(
        modifier = Modifier
            .toggleable(
                value = selected,
                onValueChange = onSelected
            )
            .padding(horizontal = 16.dp)
    ) {
        Image(
            image,
            Modifier
                .gravity(Alignment.CenterVertically)
                .preferredSize(56.dp, 56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Text(
            text = itemTitle,
            modifier = Modifier
                .weight(1f)
                .gravity(Alignment.CenterVertically)
                .padding(16.dp),
            style = MaterialTheme.typography.subtitle1
        )
        SelectTopicButton(
            modifier = Modifier.gravity(Alignment.CenterVertically),
            selected = selected
        )
    }
}

@Composable
private fun TopicDivider() {
    Divider(
        modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
        color = MaterialTheme.colors.surface.copy(alpha = 0.08f)
    )
}

private fun getTopicKey(tab: String, group: String, topic: String) = "$tab-$group-$topic"

private fun isTopicSelected(key: String) = JetnewsStatus.selectedTopics.contains(key)

private fun selectTopic(key: String, select: Boolean) {
    if (select) {
        JetnewsStatus.selectedTopics.add(key)
    } else {
        JetnewsStatus.selectedTopics.remove(key)
    }
}

@Preview("Interests screen")
@Composable
fun PreviewInterestsScreen() {
    ThemedPreview {
        InterestsScreen(
            navigateTo = {},
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen dark theme")
@Composable
fun PreviewInterestsScreenDark() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        InterestsScreen(
            navigateTo = {},
            scaffoldState = scaffoldState,
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen drawer open")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        InterestsScreen(
            navigateTo = {},
            scaffoldState = scaffoldState,
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen drawer open dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        InterestsScreen(
            navigateTo = {},
            scaffoldState = scaffoldState,
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen topics tab")
@Composable
fun PreviewTopicsTab() {
    ThemedPreview {
        TopicsTab(loadFakeTopics())
    }
}

@Preview("Interests screen topics tab dark theme")
@Composable
fun PreviewTopicsTabDark() {
    ThemedPreview(darkTheme = true) {
        TopicsTab(loadFakeTopics())
    }
}

@Composable
private fun loadFakeTopics(): Map<String, List<String>> {
    return previewDataFrom(FakeInterestsRepository()::getTopics)
}

@Preview("Interests screen people tab")
@Composable
fun PreviewPeopleTab() {
    ThemedPreview {
        PeopleTab(loadFakePeople())
    }
}

@Preview("Interests screen people tab dark theme")
@Composable
fun PreviewPeopleTabDark() {
    ThemedPreview(darkTheme = true) {
        PeopleTab(loadFakePeople())
    }
}

@Composable
private fun loadFakePeople(): List<String> {
    return previewDataFrom(FakeInterestsRepository()::getPeople)
}

@Preview("Interests screen publications tab")
@Composable
fun PreviewPublicationsTab() {
    ThemedPreview {
        PublicationsTab(loadFakePublications())
    }
}

@Preview("Interests screen publications tab dark theme")
@Composable
fun PreviewPublicationsTabDark() {
    ThemedPreview(darkTheme = true) {
        PublicationsTab(loadFakePublications())
    }
}

@Composable
private fun loadFakePublications(): List<String> {
    return previewDataFrom(FakeInterestsRepository()::getPublications)
}

@Preview("Interests screen tab with topics")
@Composable
fun PreviewTabWithTopics() {
    ThemedPreview {
        TabWithTopics(tabName = "preview", topics = listOf("Hello", "Compose"))
    }
}

@Preview("Interests screen tab with topics dark theme")
@Composable
fun PreviewTabWithTopicsDark() {
    ThemedPreview(darkTheme = true) {
        TabWithTopics(tabName = "preview", topics = listOf("Hello", "Compose"))
    }
}
