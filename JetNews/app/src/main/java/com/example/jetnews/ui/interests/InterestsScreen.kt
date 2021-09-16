/*
 * Copyright 2021 The Android Open Source Project
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material.ScrollableTabRow
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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.ui.JetnewsDestinations
import com.example.jetnews.ui.components.AppNavRail
import com.example.jetnews.ui.components.InsetAwareTopAppBar
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.WindowSize
import com.example.jetnews.utils.getWindowSize
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
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
 * Stateless interest screen displays the tabs specified in [tabContent] adapting the UI to
 * different screen sizes.
 *
 * @param tabContent (slot) the tabs and their content to display on this screen, must be a
 * non-empty list, tabs are displayed in the order specified by this list
 * @param currentSection (state) the current tab to display, must be in [tabContent]
 * @param showNavRail (state) whether the Drawer or NavigationRail needs to be shown
 * @param onTabChange (event) request a change in [currentSection] to another tab from [tabContent]
 * @param navigateToHome (event) request navigation to Home screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) the state for the screen's [Scaffold]
 */
@Composable
fun InterestsScreen(
    tabContent: List<TabContent>,
    currentSection: Sections,
    showNavRail: Boolean,
    onTabChange: (Sections) -> Unit,
    navigateToHome: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Row(Modifier.fillMaxSize()) {
        if (showNavRail) {
            AppNavRail(
                currentRoute = JetnewsDestinations.INTERESTS_ROUTE,
                navigateToHome = navigateToHome,
                navigateToInterests = { /* Do nothing */ },
                modifier = Modifier.statusBarsPadding()
            )
        }
        InterestsScreenContent(
            tabContent = tabContent,
            tab = currentSection,
            onTabChange = onTabChange,
            scaffoldState = scaffoldState,
            // Allow opening the Drawer if the NavRail is not on the screen
            navigationIconContent = if (!showNavRail) {
                {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            painter = painterResource(R.drawable.ic_jetnews_logo),
                            contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            } else {
                null
            }
        )
    }
}

/**
 * Stateless interest screen displays the tabs specified in [tabContent]
 *
 * @param tabContent (slot) the tabs and their content to display on this screen, must be a non-empty
 * list, tabs are displayed in the order specified by this list
 * @param tab (state) the current tab to display, must be in [tabContent]
 * @param onTabChange (event) request a change in [tab] to another tab from [tabContent]
 * @param scaffoldState (state) the state for the screen's [Scaffold]
 * @param navigationIconContent (UI) content to show for the navigation icon
 */
@Composable
private fun InterestsScreenContent(
    tabContent: List<TabContent>,
    tab: Sections,
    onTabChange: (Sections) -> Unit,
    scaffoldState: ScaffoldState,
    navigationIconContent: @Composable (() -> Unit)? = null
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
                navigationIcon = navigationIconContent,
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
        InterestsTabRow(selectedTabIndex, updateSection, tabContent)
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
        Box(modifier = Modifier.weight(1f)) {
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
    topics: List<InterestSection>,
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
fun PeopleList(
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
fun PublicationList(
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
    BoxWithConstraints {
        val itemMaxWidth = rememberItemMaxWidth(windowMaxWidth = maxWidth, columns = 1)
        val topicModifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = itemMaxWidth)

        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .navigationBarsPadding()
        ) {
            items(topics) { topic ->
                TopicItem(
                    itemTitle = topic,
                    selected = selectedTopics.contains(topic),
                    onToggle = { onTopicSelect(topic) },
                    modifier = topicModifier
                )
                TopicDivider(topicModifier)
            }
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
    sections: List<InterestSection>,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {
    BoxWithConstraints {
        val windowSize = remember(maxWidth) { getWindowSize(maxWidth) }
        val columns = remember(windowSize) { if (windowSize == WindowSize.Compact) 1 else 2 }
        val itemMaxWidth = rememberItemMaxWidth(maxWidth, columns)
        val groupedSections = rememberSectionsGroupedInColumns(sections, columns)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            groupedSections.forEach { sectionsInGroup ->
                LazyColumn(
                    Modifier
                        .widthIn(max = itemMaxWidth)
                        .padding(horizontal = if (columns > 1) 8.dp else 0.dp)
                ) {
                    sectionsInGroup.forEach { (section, topics) ->
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
                                selected = selectedTopics.contains(TopicSelection(section, topic)),
                                onToggle = { onTopicSelect(TopicSelection(section, topic)) },
                                modifier = Modifier
                            )
                            TopicDivider(Modifier)
                        }
                    }
                }
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
private fun TopicItem(
    itemTitle: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val image = painterResource(R.drawable.placeholder_1_1)
    Row(
        modifier = modifier
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
                .padding(16.dp)
                .weight(1f), // Break line if the title is too long
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(Modifier.weight(0.01f))
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
private fun TopicDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier.padding(start = 90.dp, top = 8.dp, bottom = 8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    )
}

/**
 * TabRow for the InterestsScreen
 */
@Composable
private fun InterestsTabRow(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>
) {
    BoxWithConstraints {
        val windowSize = remember(maxWidth) { getWindowSize(maxWidth) }
        when (windowSize) {
            WindowSize.Compact -> {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    InterestsTabRowContent(selectedTabIndex, updateSection, tabContent)
                }
            }
            WindowSize.Medium, WindowSize.Expanded -> {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary,
                    edgePadding = 0.dp
                ) {
                    InterestsTabRowContent(
                        selectedTabIndex = selectedTabIndex,
                        updateSection = updateSection,
                        tabContent = tabContent,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InterestsTabRowContent(
    selectedTabIndex: Int,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>,
    modifier: Modifier = Modifier
) {
    tabContent.forEachIndexed { index, content ->
        val colorText = if (selectedTabIndex == index) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
        }
        Tab(
            selected = selectedTabIndex == index,
            onClick = { updateSection(content.section) },
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(id = content.section.titleResId),
                color = colorText,
                style = MaterialTheme.typography.subtitle2,
                modifier = modifier.paddingFromBaseline(top = 20.dp)
            )
        }
    }
}

/**
 * Return the interest sections grouped given the number of columns that fill the screen.
 * Sections are distributed to balance the number of items on each column.
 */
@Composable
private fun rememberSectionsGroupedInColumns(
    sections: List<InterestSection>,
    columns: Int
): List<List<InterestSection>> = remember(sections, columns) {
    val sectionsInColumns = List<MutableList<InterestSection>>(columns) { mutableListOf() }
    val elementsInColumns = IntArray(columns)
    // For each section, find the column with less elements and add it there
    sections.forEach { section ->
        val shortestColumnIndex = elementsInColumns.withIndex().minByOrNull { it.value }!!.index
        sectionsInColumns[shortestColumnIndex] += section
        elementsInColumns[shortestColumnIndex] += section.interests.size
    }
    sectionsInColumns.toList()
}

/**
 * Returns the max width for a Topic Item given maxWidth and number of columns constraints.
 */
@Composable
private fun rememberItemMaxWidth(windowMaxWidth: Dp, columns: Int) =
    remember(windowMaxWidth, columns) {
        if (columns == 1) {
            // If the space available is small, fill the screen
            if (windowMaxWidth < 600.dp) Dp.Infinity else 600.dp
        } else {
            // Calculate the max width each item can take given the number of columns
            val itemMaxWidth = windowMaxWidth / columns
            // Limit the width of the item for them to not be too big given the available space
            if (windowMaxWidth < 800.dp) min(itemMaxWidth, 350.dp) else min(itemMaxWidth, 450.dp)
        }
    }

@Composable
fun rememberTabContent(
    interestsViewModel: InterestsViewModel
): List<TabContent> {
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

    return listOf(topicsSection, peopleSection, publicationSection)
}

@Preview("Interests screen", "Interests")
@Preview("Interests screen (dark)", "Interests", uiMode = UI_MODE_NIGHT_YES)
@Preview("Interests screen (big font)", "Interests", fontScale = 1.5f)
@Composable
fun PreviewInterestsScreenDrawer() {
    JetnewsTheme {
        val tabContent = getFakeTabsContent()
        val (currentSection, updateSection) = rememberSaveable {
            mutableStateOf(tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            onTabChange = updateSection,
            openDrawer = { },
            navigateToHome = { },
            showNavRail = false,
            scaffoldState = rememberScaffoldState()
        )
    }
}

@Preview("Interests screen navrail", "Interests", device = Devices.PIXEL_C)
@Preview(
    "Interests screen navrail (dark)", "Interests",
    uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_C
)
@Preview(
    "Interests screen navrail (big font)", "Interests",
    fontScale = 1.5f, device = Devices.PIXEL_C
)
@Composable
fun PreviewInterestsScreenNavRail() {
    JetnewsTheme {
        val tabContent = getFakeTabsContent()
        val (currentSection, updateSection) = rememberSaveable {
            mutableStateOf(tabContent.first().section)
        }

        InterestsScreen(
            tabContent = tabContent,
            currentSection = currentSection,
            onTabChange = updateSection,
            openDrawer = { },
            navigateToHome = { },
            showNavRail = true,
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
            TopicList(topics, setOf()) { }
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
            PeopleList(people, setOf()) { }
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
            PublicationList(publications, setOf()) { }
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
