/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import androidx.ui.core.Clip
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.foundation.Box
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.ScaffoldState
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.people
import com.example.jetnews.data.publications
import com.example.jetnews.data.topics
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.VectorImageButton

private enum class Sections(val title: String) {
    Topics("Topics"),
    People("People"),
    Publications("Publications")
}

@Composable
fun InterestsScreen(scaffoldState: ScaffoldState = remember { ScaffoldState() }) {
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
                    VectorImageButton(R.drawable.ic_jetnews_logo) {
                        scaffoldState.drawerState = DrawerState.Opened
                    }
                }
            )
        },
        bodyContent = {
            val (currentSection, updateSection) = state { Sections.Topics }
            InterestsScreenBody(currentSection, updateSection)
        }
    )
}

@Composable
private fun InterestsScreenBody(
    currentSection: Sections,
    updateSection: (Sections) -> Unit
) {
    val sectionTitles = Sections.values().map { it.title }

    Column {
        TabRow(items = sectionTitles, selectedIndex = currentSection.ordinal) { index, title ->
            Tab(
                text = { Text(title) },
                selected = currentSection.ordinal == index,
                onSelected = {
                    updateSection(Sections.values()[index])
                })
        }
        Container(modifier = LayoutFlexible(1f)) {
            when (currentSection) {
                Sections.Topics -> TopicsTab()
                Sections.People -> PeopleTab()
                Sections.Publications -> PublicationsTab()
            }
        }
    }
}

@Composable
private fun TopicsTab() {
    TabWithSections(
        "Topics",
        topics
    )
}

@Composable
private fun PeopleTab() {
    TabWithTopics(
        Sections.People.title,
        people
    )
}

@Composable
private fun PublicationsTab() {
    TabWithTopics(
        Sections.Publications.title,
        publications
    )
}

@Composable
private fun TabWithTopics(tabName: String, topics: List<String>) {
    VerticalScroller {
        Column(modifier = LayoutPadding(top = 16.dp)) {
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
}

@Composable
private fun TabWithSections(
    tabName: String,
    sections: Map<String, List<String>>
) {
    VerticalScroller {
        Column {
            sections.forEach { (section, topics) ->
                Text(
                    text = section,
                    modifier = LayoutPadding(16.dp),
                    style = (MaterialTheme.typography()).subtitle1)
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
}

@Composable
private fun TopicItem(topicKey: String, itemTitle: String) {
    val image = imageResource(R.drawable.placeholder_1_1)

    Ripple(bounded = true) {
        val selected = isTopicSelected(topicKey)
        val onSelected = { it: Boolean ->
            selectTopic(topicKey, it)
        }
        Toggleable(selected, onSelected) {
            // TODO(b/150060763): Remove box after "Bug in ripple + modifiers."
            Box {
                Row(
                    modifier = LayoutPadding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
                ) {
                    Container(modifier = LayoutGravity.Center + LayoutSize(56.dp, 56.dp)) {
                        Clip(RoundedCornerShape(4.dp)) {
                            SimpleImage(image = image)
                        }
                    }
                    Text(
                            text = itemTitle,
                            modifier = LayoutFlexible(1f) + LayoutGravity.Center + LayoutPadding(16.dp),
                            style = (MaterialTheme.typography()).subtitle1
                    )
                    SelectTopicButton(
                            modifier = LayoutGravity.Center,
                            selected = selected
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicDivider() {
    Opacity(0.08f) {
        Divider(LayoutPadding(start = 72.dp, top = 8.dp, end = 0.dp, bottom = 8.dp))
    }
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

@Preview
@Composable
fun PreviewInterestsScreen() {
    InterestsScreen()
}

@Preview
@Composable
private fun PreviewDrawerOpen() {
    InterestsScreen(scaffoldState = ScaffoldState(drawerState = DrawerState.Opened))
}

@Preview
@Composable
fun PreviewTopicsTab() {
    TopicsTab()
}

@Preview
@Composable
fun PreviewPeopleTab() {
    PeopleTab()
}

@Preview
@Composable
fun PreviewTabWithTopics() {
    TabWithTopics(tabName = "preview", topics = listOf("Hello", "Compose"))
}
