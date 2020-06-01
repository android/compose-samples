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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.ScaffoldState
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.ripple
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
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
    scaffoldState: ScaffoldState = remember { ScaffoldState() },
    interestsRepository: InterestsRepository
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Interests,
                closeDrawer = { scaffoldState.drawerState = DrawerState.Closed }
            )
        },
        topAppBar = {
            TopAppBar(
                title = { Text("Interests") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState = DrawerState.Opened }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = {
            val (currentSection, updateSection) = state { Sections.Topics }
            InterestsScreenBody(currentSection, updateSection, interestsRepository)
        }
    )
}

@Composable
private fun InterestsScreenBody(
    currentSection: Sections,
    updateSection: (Sections) -> Unit,
    interestsRepository: InterestsRepository
) {
    val sectionTitles = Sections.values().map { it.title }

    Column {
        TabRow(
            items = sectionTitles, selectedIndex = currentSection.ordinal
        ) { index, title ->
            Tab(
                text = { Text(title) },
                selected = currentSection.ordinal == index,
                onSelected = {
                    updateSection(Sections.values()[index])
                })
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
    VerticalScroller(modifier = Modifier.padding(top = 16.dp)) {
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
    VerticalScroller {
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
                    ), topic
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
    Toggleable(
        value = selected,
        onValueChange = onSelected,
        modifier = Modifier.ripple()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
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
        InterestsScreen(interestsRepository = FakeInterestsRepository())
    }
}

@Preview("Interests screen dark theme")
@Composable
fun PreviewInterestsScreenDark() {
    ThemedPreview(darkTheme = true) {
        InterestsScreen(
            scaffoldState = ScaffoldState(drawerState = DrawerState.Opened),
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen drawer open")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        InterestsScreen(
            scaffoldState = ScaffoldState(drawerState = DrawerState.Opened),
            interestsRepository = FakeInterestsRepository()
        )
    }
}

@Preview("Interests screen drawer open dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        InterestsScreen(
            scaffoldState = ScaffoldState(drawerState = DrawerState.Opened),
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
