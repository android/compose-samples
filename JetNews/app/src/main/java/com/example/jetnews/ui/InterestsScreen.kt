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

package com.example.jetnews.ui

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.composer
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Padding
import androidx.ui.layout.absolutePadding
import androidx.ui.layout.padding
import androidx.ui.material.AppBarIcon
import androidx.ui.material.Divider
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.material.themeTextStyle
import com.example.jetnews.R

private enum class Sections(val title: String) {
    Topics("Topics"),
    People("People"),
    Publications("Publications")
}

@Composable
fun InterestsScreen(icons: Icons, openDrawer: () -> Unit) {

    val state = +state { Sections.Topics }
    val sectionTitles = Sections.values().map { it.title }

    FlexColumn {
        inflexible {
            TopAppBar(
                title = { Text("Interests") },
                navigationIcon = {
                    AppBarIcon(icons.menu) {
                        openDrawer()
                    }
                }
            )
        }
        inflexible {
            TabRow(items = sectionTitles, selectedIndex = state.value.ordinal) { index, text ->
                Tab(text = text, selected = state.value.ordinal == index) {
                    state.value = Sections.values()[index]
                }
            }
        }
        expanded(1f) {
            when (state.value) {
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
    TabWithTopics(Sections.People.title, people)
}

@Composable
private fun PublicationsTab() {
    TabWithTopics(Sections.Publications.title, publications)
}

@Composable
private fun TabWithTopics(tabname: String, topics: List<String>) {
    VerticalScroller {
        Column {
            HeightSpacer(16.dp)
            topics.forEach { topic ->
                TopicItem(getTopicKey(tabname, "- ", topic), topic)
                TopicDivider()
            }
        }
    }
}

@Composable
private fun TabWithSections(
    tabname: String,
    sections: Map<String, List<String>>
) {
    VerticalScroller {
        Column {
            sections.forEach { (section, topics) ->
                Text(section,
                    padding(16.dp),
                    style = +themeTextStyle { subtitle1 })
                topics.forEach { topic ->
                    TopicItem(getTopicKey(tabname, section, topic), topic)
                    TopicDivider()
                }
            }
        }
    }
}
@Composable
private fun TopicItem(topicKey: String, itemTitle: String) {
    val context = +ambient(ContextAmbient)

    Padding(left = 16.dp, right = 16.dp) {
        FlexRow(
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            inflexible {
                Container(width = 56.dp, height = 56.dp) {
                    Clip(RoundedCornerShape(4.dp)) {
                        DrawImage(
                            imageFromResource(
                                context.resources,
                                R.drawable.placeholder_1_1
                            )
                        )
                    }
                }
            }
            expanded(1f) {
                Text(
                    text = itemTitle,
                    modifier = padding(16.dp),
                    style = +themeTextStyle { subtitle1 })
            }
            inflexible {
                val selected = isTopicSelected(topicKey)
                SelectTopicButton(
                    onSelected = {
                        selectTopic(topicKey, !selected)
                    },
                    selected = selected
                )
            }
        }
    }
}

@Composable
private fun TopicDivider() {
    Opacity(0.08f) {
        Divider(absolutePadding(top = 8.dp, bottom = 8.dp, left = 72.dp))
    }
}

private fun getTopicKey(tab: String, group: String, topic: String): String {
    return "$tab-$group-$topic"
}

private fun isTopicSelected(key: String): Boolean {
    val r = JetnewsStatus.selectedTopics.contains(key)
    return r
}

private fun selectTopic(key: String, select: Boolean) {
    if (select) {
        JetnewsStatus.selectedTopics.add(key)
    } else {
        JetnewsStatus.selectedTopics.remove(key)
    }
}
