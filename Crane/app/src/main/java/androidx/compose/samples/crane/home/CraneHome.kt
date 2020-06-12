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

package androidx.compose.samples.crane.home

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.State
import androidx.compose.samples.crane.base.CraneDrawer
import androidx.compose.samples.crane.base.CraneTabBar
import androidx.compose.samples.crane.base.CraneTabs
import androidx.compose.samples.crane.base.ExploreSection
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.ui.BackdropFrontLayerDraggable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.layout.Column
import androidx.ui.material.DrawerState
import androidx.ui.material.ModalDrawerLayout

typealias OnExploreItemClicked = (ExploreModel) -> Unit

enum class CraneScreen {
    FLY, SLEEP, EAT
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    destinations: List<ExploreModel>,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    viewModel: MainViewModel
) {
    CraneHome(
        modifier = modifier,
        viewModel = viewModel,
        onExploreItemClicked = onExploreItemClicked,
        onDateSelectionClicked = onDateSelectionClicked,
        suggestedDestinations = destinations
    )
}

@Composable
private fun CraneHome(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    suggestedDestinations: List<ExploreModel>
) {
    val (drawerState, onDrawerStateChange) = state { DrawerState.Closed }
    ModalDrawerLayout(
        drawerState = drawerState,
        onStateChange = onDrawerStateChange,
        gesturesEnabled = drawerState == DrawerState.Opened,
        drawerContent = { CraneDrawer() },
        bodyContent = {
            CraneHomeContent(
                modifier = modifier,
                viewModel = viewModel,
                suggestedDestinations = suggestedDestinations,
                onExploreItemClicked = onExploreItemClicked,
                onDateSelectionClicked = onDateSelectionClicked,
                openDrawer = { onDrawerStateChange(DrawerState.Opened) }
            )
        }
    )
}

@Composable
fun CraneHomeContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    suggestedDestinations: List<ExploreModel>,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    openDrawer: () -> Unit
) {
    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    val tabSelected = state { CraneScreen.FLY }

    BackdropFrontLayerDraggable(
        modifier = modifier,
        staticChildren = { staticModifier ->
            Column(modifier = staticModifier) {
                HomeTabBar(openDrawer, tabSelected)
                SearchContent(
                    tabSelected,
                    viewModel,
                    onPeopleChanged,
                    onDateSelectionClicked,
                    onExploreItemClicked
                )
            }
        },
        backdropChildren = { backdropModifier ->
            when (tabSelected.value) {
                CraneScreen.FLY -> {
                    ExploreSection(
                        modifier = backdropModifier,
                        title = "Explore Flights by Destination",
                        exploreList = suggestedDestinations,
                        onItemClicked = onExploreItemClicked
                    )
                }
                CraneScreen.SLEEP -> {
                    ExploreSection(
                        modifier = backdropModifier,
                        title = "Explore Properties by Destination",
                        exploreList = viewModel.hotels,
                        onItemClicked = onExploreItemClicked
                    )
                }
                CraneScreen.EAT -> {
                    ExploreSection(
                        modifier = backdropModifier,
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
    tabSelected: MutableState<CraneScreen>,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        CraneTabs(
            modifier = tabBarModifier,
            titles = CraneScreen.values().map { it.name },
            tabSelected = tabSelected.value,
            onTabSelected = { newTab ->
                tabSelected.value = CraneScreen.values()[newTab.ordinal]
            }
        )
    }
}

@Composable
private fun SearchContent(
    tabSelected: State<CraneScreen>,
    viewModel: MainViewModel,
    onPeopleChanged: (Int) -> Unit,
    onDateSelectionClicked: () -> Unit,
    onExploreItemClicked: OnExploreItemClicked
) {
    when (tabSelected.value) {
        CraneScreen.FLY -> FlySearchContent(
            searchUpdates = FlySearchContentUpdates(
                onPeopleChanged = onPeopleChanged,
                onToDestinationChanged = { viewModel.toDestinationChanged(it) },
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        )
        CraneScreen.SLEEP -> SleepSearchContent(
            sleepUpdates = SleepSearchContentUpdates(
                onPeopleChanged = onPeopleChanged,
                onDateSelectionClicked = onDateSelectionClicked,
                onExploreItemClicked = onExploreItemClicked
            )
        )
        CraneScreen.EAT -> EatSearchContent(
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
