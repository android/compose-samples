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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Row
import androidx.ui.layout.Size
import androidx.ui.layout.Spacing
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.people
import com.example.jetnews.data.publications
import com.example.jetnews.data.topics
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.VectorImageButton

private enum class Sections(val title: String) {
    Topics("Topics"),
    People("People"),
    Publications("Publications")
}

@Composable
fun InterestsScreen(openDrawer: () -> Unit) {

    var section by +state { Sections.Topics }
    val sectionTitles = Sections.values().map { it.title }

    Column {
        TopAppBar(
            title = { Text("Interests") },
            navigationIcon = {
                VectorImageButton(R.drawable.ic_jetnews_logo) {
                    openDrawer()
                }
            }
        )
        TabRow(items = sectionTitles, selectedIndex = section.ordinal) { index, text ->
            Tab(text = text, selected = section.ordinal == index) {
                section = Sections.values()[index]
            }
        }
        Container(modifier = Flexible(1f)) {
            when (section) {
                Sections.Topics -> TopicsTab()
                Sections.People -> PeopleTab()
                Sections.Publications -> PublicationsTab()
            }
        }
    }
}

@Composable
private fun TopicsTab() {
    TabWithSections("Topics", topics)
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
        Column {
            HeightSpacer(16.dp)
            topics.forEach { topic ->
                TopicItem(
                    getTopicKey(
                        tabName,
                        "- ",
                        topic
                    ), topic
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
                    modifier = Spacing(16.dp),
                    style = (+MaterialTheme.typography()).subtitle1)
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
    val image = +imageResource(R.drawable.placeholder_1_1)
    Row(
        modifier = Spacing(left = 16.dp, right = 16.dp)
    ) {
        Container(modifier = Gravity.Center wraps Size(56.dp, 56.dp)) {
            Clip(RoundedCornerShape(4.dp)) {
                DrawImage(image)
            }
        }
        Text(
            text = itemTitle,
            modifier = Flexible(1f) wraps Gravity.Center wraps Spacing(16.dp),
            style = (+MaterialTheme.typography()).subtitle1)
        val selected = isTopicSelected(topicKey)
        SelectTopicButton(
            modifier = Gravity.Center,
            onSelected = {
                selectTopic(topicKey, !selected)
            },
            selected = selected
        )
    }
}

@Composable
private fun TopicDivider() {
    Opacity(0.08f) {
        Divider(Spacing(top = 8.dp, bottom = 8.dp, left = 72.dp))
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
fun preview() {
    InterestsScreen { }
}
