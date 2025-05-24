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

package com.example.compose.jetchat.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(activityViewModel: MainViewModel, profileViewModel: ProfileViewModel) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            AppBar(activityViewModel, topAppBarScrollBehavior)
        },
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        ProfileData(profileViewModel, paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(activityViewModel: MainViewModel, scrollBehavior: TopAppBarScrollBehavior) {
    JetchatAppBar(
        // Reset the minimum bounds that are passed to the root of a compose tree
        modifier = Modifier.wrapContentSize(),
        onNavIconPressed = { activityViewModel.openDrawer() },
        title = { },
        actions = {

            var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
            if (functionalityNotAvailablePopupShown) {
                FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
            }

            // More icon
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {
                        functionalityNotAvailablePopupShown = true
                    })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.more_options)
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ProfileData(profileViewModel: ProfileViewModel, paddingValues: PaddingValues) {
    val userData by profileViewModel.userData.observeAsState()

    if (userData == null) {
        ProfileError()
    } else {
        ProfileScreen(
            userData = userData!!,
            paddingValues
        )
    }
}