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

package androidx.compose.samples.crane.home

import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawerLayout
import androidx.compose.material.rememberBackdropState
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.base.CraneDrawer
import androidx.compose.samples.crane.base.CraneTabBar
import androidx.compose.samples.crane.base.CraneTabs
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.viewModel

typealias OnExploreItemClicked = (ExploreModel) -> Unit

enum class CraneScreen {
    Fly, Sleep, Eat
}

@Composable
fun CraneHome(
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalDrawerLayout(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { CraneDrawer() },
        bodyContent = {
            CraneHomeContent(
                modifier = modifier,
                onExploreItemClicked = onExploreItemClicked,
                onDateSelectionClicked = onDateSelectionClicked,
                openDrawer = { drawerState.open() }
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CraneHomeContent(
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = viewModel()
    val suggestedDestinations by viewModel.suggestedDestinations.observeAsState()

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    var tabSelected by remember { mutableStateOf(CraneScreen.Fly) }

    BackdropScaffold(
        modifier = modifier,
        backdropScaffoldState = rememberBackdropState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Transparent,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {
            SearchContent(
                tabSelected,
                viewModel,
                onPeopleChanged,
                onDateSelectionClicked,
                onExploreItemClicked
            )
        },
        frontLayerContent = {
            when (tabSelected) {
                CraneScreen.Fly -> {
                    suggestedDestinations?.let { destinations ->
                        ExploreSection(
                            title = "Explore Flights by Destination",
                            exploreList = destinations,
                            onItemClicked = onExploreItemClicked
                        )
                    }
                }
                CraneScreen.Sleep -> {
                    ExploreSection(
                        title = "Explore Properties by Destination",
                        exploreList = viewModel.hotels,
                        onItemClicked = onExploreItemClicked
                    )
                }
                CraneScreen.Eat -> {
                    ExploreSection(
                        title = "Explore Restaurants by Destination",
                        exploreList = viewModel.restaurants,
                        onItemClicked = onExploreItemClicked
                    )
                }
            }
        }
    )
}

@Composable
private fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        CraneTabs(
            modifier = tabBarModifier,
            titles = CraneScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(CraneScreen.values()[newTab.ordinal]) }
        )
    }
}

@Composable
private fun SearchContent(
    tabSelected: CraneScreen,
    viewModel: MainViewModel,
    onPeopleChanged: (Int) -> Unit,
    onDateSelectionClicked: () -> Unit,
    onExploreItemClicked: OnExploreItemClicked
) {
    when (tabSelected) {
        CraneScreen.Fly -> FlySearchContent(
            searchUpdates = FlySearchContentUpdates(
                onPeopleChanged = onPeopleChanged,
                onToDestinationChanged = { viewModel.toDestinationChanged(it) },
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        )
        CraneScreen.Sleep -> SleepSearchContent(
            sleepUpdates = SleepSearchContentUpdates(
                onPeopleChanged = onPeopleChanged,
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        )
        CraneScreen.Eat -> EatSearchContent(
            eatUpdates = EatSearchContentUpdates(
                onPeopleChanged = onPeopleChanged,
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        )
    }
}

data class FlySearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onToDestinationChanged: (String) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)

data class SleepSearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)

data class EatSearchContentUpdates(
    val onPeopleChanged: (Int) -> Unit,
    val onDateSelectionClicked: () -> Unit,
    val onExploreItemClicked: OnExploreItemClicked
)
