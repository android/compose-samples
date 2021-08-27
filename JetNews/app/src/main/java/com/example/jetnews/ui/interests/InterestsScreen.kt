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

package com.example.jetnews.ui.interests

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.TopicsMap
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.ui.components.InsetAwareTopAppBar
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.supportWideScreen
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.runBlocking

enum class Sections(@StringRes val titleResId: Int) {
    Topics(R.string.interests_section_topics),
    People(R.string.interests_section_people),
    Publications(R.string.interests_section_publications)
}

/**
 * TabContent for a single tab of the screen.
 *
 * This is intended to encapsulate a tab & it's content as a single object. It was added to avoid
 * passing several parameters per-tab from the stateful composable to the composable that displays
 * the current tab.
 *
 * @param section the tab that this content is for
 * @param section content of the tab, a composable that describes the content
 */
class TabContent(val section: Sections, val content: @Composable () -> Unit)

/**
 * Displays the Interests screen.
 *
 * @param interestsViewModel ViewModel that handles the business logic of this screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for screen Scaffold
 */
@Composable
fun InterestsScreen(
    interestsViewModel: InterestsViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    // UiState of the InterestsScreen
    val uiState by interestsViewModel.uiState.collectAsState()

    // Describe the screen sections here since each section needs 2 states and 1 event.
    // Pass them to the stateless InterestsScreen using a tabContent.
    val topicsSection = TabContent(Sections.Topics) {
        val selectedTopics by interestsViewModel.selectedTopics.collectAsState()
        TopicList(
            topics = uiState.topics,
            selectedTopics = selectedTopics,
            onTopicSelect = { interestsViewModel.toggleTopicSelection(it) }
        )
    }

    val peopleSection = TabContent(Sections.People) {
        val selectedPeople by interestsViewModel.selectedPeople.collectAsState()
        PeopleList(
            people = uiState.people,
            selectedPeople = selectedPeople,
            onPersonSelect = { interestsViewModel.togglePersonSelected(it) }
        )
    }

    val publicationSection = TabContent(Sections.Publications) {
        val selectedPublications by interestsViewModel.selectedPublications.collectAsState()
        PublicationList(
            publications = uiState.publications,
            selectedPublications = selectedPublications,
            onPublicationSelect = { interestsViewModel.togglePublicationSelected(it) }
        )
    }

    val tabContent = listOf(topicsSection, peopleSection, publicationSection)
    val (currentSection, updateSection) = rememberSaveable { mutableStateOf(tabContent.first().section) }
    InterestsScreen(
        tabContent = tabContent,
        tab = currentSection,
        onTabChange = updateSection,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}

/**
 * Stateless interest screen displays the tabs specified in [tabContent]
 *
 * @param tabContent (slot) the tabs and their content to display on this screen, must be a non-empty
 * list, tabs are displayed in the order specified by this list
 * @param tab (state) the current tab to display, must be in [tabContent]
 * @param onTabChange (event) request a change in [tab] to another tab from [tabContent]
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) the state for the screen's [Scaffold]
 */
@Composable
fun InterestsScreen(
    tabContent: List<TabContent>,
    tab: Sections,
    onTabChange: (Sections) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {

            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.cd_interests),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            painter = painterResource(R.drawable.ic_jetnews_logo),
                            contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Open search */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.cd_search)
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        }
    ) {
        TabContent(tab, onTabChange, tabContent)
    }
}

/**
 * Displays a tab row with [currentSection] selected and the body of the corresponding [tabContent].
 *
 * @param currentSection (state) the tab that is currently selected
 * @param updateSection (event) request a change in tab selection
 * @param tabContent (slot) tabs and their content to display, must be a non-empty list, tabs are
 * displayed in the order of this list
 */
@Composable
private fun TabContent(
    currentSection: Sections,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>
) {
    val selectedTabIndex = tabContent.indexOfFirst { it.section == currentSection }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary,

        ) {
            tabContent.forEachIndexed { index, tabContent ->
                val colorText = if (selectedTabIndex == index) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        updateSection(tabContent.section)
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = tabContent.section.titleResId),
                        color = colorText,
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.paddingFromBaseline(top = 20.dp)
                    )
                }
            }
        }
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .supportWideScreen()
        ) {
            // display the current tab content which is a @Composable () -> Unit
            tabContent[selectedTabIndex].content()
        }
    }
}

/**
 * Display the list for the topic tab
 *
 * @param topics (state) topics to display, mapped by section
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 */
@Composable
private fun TopicList(
    topics: TopicsMap,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {
    TabWithSections(topics, selectedTopics, onTopicSelect)
}

/**
 * Display the list for people tab
 *
 * @param people (state) people to display
 * @param selectedPeople (state) currently selected people
 * @param onPersonSelect (event) request a person selection be changed
 */
@Composable
private fun PeopleList(
    people: List<String>,
    selectedPeople: Set<String>,
    onPersonSelect: (String) -> Unit
) {
    TabWithTopics(people, selectedPeople, onPersonSelect)
}

/**
 * Display a list for publications tab
 *
 * @param publications (state) publications to display
 * @param selectedPublications (state) currently selected publications
 * @param onPublicationSelect (event) request a publication selection be changed
 */
@Composable
private fun PublicationList(
    publications: List<String>,
    selectedPublications: Set<String>,
    onPublicationSelect: (String) -> Unit
) {
    TabWithTopics(publications, selectedPublications, onPublicationSelect)
}

/**
 * Display a simple list of topics
 *
 * @param topics (state) topics to display
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 */
@Composable
private fun TabWithTopics(
    topics: List<String>,
    selectedTopics: Set<String>,
    onTopicSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
            .navigationBarsPadding()
    ) {
        items(topics) { topic ->
            TopicItem(
                topic,
                selected = selectedTopics.contains(topic)
            ) { onTopicSelect(topic) }
            TopicDivider()
        }
    }
}

/**
 * Display a sectioned list of topics
 *
 * @param sections (state) topics to display, grouped by sections
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic+section selection be changed
 */
@Composable
private fun TabWithSections(
    sections: TopicsMap,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {
    LazyColumn(Modifier.navigationBarsPadding()) {
        sections.forEach { (section, topics) ->
            item {
                Text(
                    text = section,
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics { heading() },
                    style = MaterialTheme.typography.subtitle1
                )
            }
            items(topics) { topic ->
                TopicItem(
                    itemTitle = topic,
                    selected = selectedTopics.contains(TopicSelection(section, topic))
                ) { onTopicSelect(TopicSelection(section, topic)) }
                TopicDivider()
            }
        }
    }
}

/**
 * Display a full-width topic item
 *
 * @param itemTitle (state) topic title
 * @param selected (state) is topic currently selected
 * @param onToggle (event) toggle selection for topic
 */
@Composable
private fun TopicItem(itemTitle: String, selected: Boolean, onToggle: () -> Unit) {
    val image = painterResource(R.drawable.placeholder_1_1)
    Row(
        modifier = Modifier
            .toggleable(
                value = selected,
                onValueChange = { onToggle() }
            )
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null, // decorative
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(56.dp, 56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Text(
            text = itemTitle,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(Modifier.weight(1f))
        SelectTopicButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            selected = selected
        )
    }
}

/**
 * Full-width divider for topics
 */
@Composable
private fun TopicDivider() {
    Divider(
        modifier = Modifier.padding(start = 90.dp, top = 8.dp, bottom = 8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    )
}

@Preview("Interests screen", "Interests")
@Preview("Interests screen (dark)", "Interests", uiMode = UI_MODE_NIGHT_YES)
@Preview("Interests screen (big font)", "Interests", fontScale = 1.5f)
@Preview("Interests screen (large screen)", "Interests", device = Devices.PIXEL_C)
@Composable
fun PreviewInterestsScreen() {
    JetnewsTheme {
        val tabContent = getFakeTabsContent()
        val (currentSection, updateSection) = rememberSaveable {
            mutableStateOf(tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            tab = currentSection,
            onTabChange = updateSection,
            openDrawer = { },
            scaffoldState = rememberScaffoldState()
        )
    }
}

@Preview("Interests screen topics tab", "Topics")
@Preview("Interests screen topics tab (dark)", "Topics", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopicsTab() {
    val topics = runBlocking {
        (FakeInterestsRepository().getTopics() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TopicList(topics, setOf(), {})
        }
    }
}

@Preview("Interests screen people tab", "People")
@Preview("Interests screen people tab (dark)", "People", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPeopleTab() {
    val people = runBlocking {
        (FakeInterestsRepository().getPeople() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            PeopleList(people, setOf(), {})
        }
    }
}

@Preview("Interests screen publications tab", "Publications")
@Preview("Interests screen publications tab (dark)", "Publications", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPublicationsTab() {
    val publications = runBlocking {
        (FakeInterestsRepository().getPublications() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            PublicationList(publications, setOf(), {})
        }
    }
}

private fun getFakeTabsContent(): List<TabContent> {
    val interestsRepository = FakeInterestsRepository()
    val topicsSection = TabContent(Sections.Topics) {
        TopicList(
            runBlocking { (interestsRepository.getTopics() as Result.Success).data },
            emptySet()
        ) { }
    }
    val peopleSection = TabContent(Sections.People) {
        PeopleList(
            runBlocking { (interestsRepository.getPeople() as Result.Success).data },
            emptySet()
        ) { }
    }
    val publicationSection = TabContent(Sections.Publications) {
        PublicationList(
            runBlocking { (interestsRepository.getPublications() as Result.Success).data },
            emptySet()
        ) { }
    }

    return listOf(topicsSection, peopleSection, publicationSection)
}
