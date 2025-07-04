/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.components.EmailDetailAppBar
import com.example.reply.ui.components.ReplyDockedSearchBar
import com.example.reply.ui.components.ReplyEmailListItem
import com.example.reply.ui.components.ReplyEmailThreadItem
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane

@Composable
fun ReplyInboxScreen(
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    navigationType: ReplyNavigationType,
    displayFeatures: List<DisplayFeature>,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
    toggleSelectedEmail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * When moving from LIST_AND_DETAIL page to LIST page clear the selection and user should see LIST screen.
     */
    LaunchedEffect(key1 = contentType) {
        if (contentType == ReplyContentType.SINGLE_PANE && !replyHomeUIState.isDetailOnlyOpen) {
            closeDetailScreen()
        }
    }

    val emailLazyListState = rememberLazyListState()

    // TODO: Show top app bar over full width of app when in multi-select mode

    if (contentType == ReplyContentType.DUAL_PANE) {
        TwoPane(
            first = {
                ReplyEmailList(
                    emails = replyHomeUIState.emails,
                    openedEmail = replyHomeUIState.openedEmail,
                    selectedEmailIds = replyHomeUIState.selectedEmails,
                    toggleEmailSelection = toggleSelectedEmail,
                    emailLazyListState = emailLazyListState,
                    navigateToDetail = navigateToDetail,
                )
            },
            second = {
                ReplyEmailDetail(
                    email = replyHomeUIState.openedEmail ?: replyHomeUIState.emails.first(),
                    isFullScreen = false,
                )
            },
            strategy = HorizontalTwoPaneStrategy(splitFraction = 0.5f, gapWidth = 16.dp),
            displayFeatures = displayFeatures,
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            ReplySinglePaneContent(
                replyHomeUIState = replyHomeUIState,
                toggleEmailSelection = toggleSelectedEmail,
                emailLazyListState = emailLazyListState,
                modifier = Modifier.fillMaxSize(),
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
            )
            // When we have bottom navigation we show FAB at the bottom end.
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.compose)) },
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = stringResource(id = R.string.compose))
                    },
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    expanded = emailLazyListState.lastScrolledBackward ||
                        !emailLazyListState.canScrollBackward,
                )
            }
        }
    }
}

@Composable
fun ReplySinglePaneContent(
    replyHomeUIState: ReplyHomeUIState,
    toggleEmailSelection: (Long) -> Unit,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
) {
    if (replyHomeUIState.openedEmail != null && replyHomeUIState.isDetailOnlyOpen) {
        BackHandler {
            closeDetailScreen()
        }
        ReplyEmailDetail(email = replyHomeUIState.openedEmail) {
            closeDetailScreen()
        }
    } else {
        ReplyEmailList(
            emails = replyHomeUIState.emails,
            openedEmail = replyHomeUIState.openedEmail,
            selectedEmailIds = replyHomeUIState.selectedEmails,
            toggleEmailSelection = toggleEmailSelection,
            emailLazyListState = emailLazyListState,
            modifier = modifier,
            navigateToDetail = navigateToDetail,
        )
    }
}

@Composable
fun ReplyEmailList(
    emails: List<Email>,
    openedEmail: Email?,
    selectedEmailIds: Set<Long>,
    toggleEmailSelection: (Long) -> Unit,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long, ReplyContentType) -> Unit,
) {
    Box(modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        ReplyDockedSearchBar(
            emails = emails,
            onSearchItemSelected = { searchedEmail ->
                navigateToDetail(searchedEmail.id, ReplyContentType.SINGLE_PANE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            state = emailLazyListState,
        ) {
            items(items = emails, key = { it.id }) { email ->
                ReplyEmailListItem(
                    email = email,
                    navigateToDetail = { emailId ->
                        navigateToDetail(emailId, ReplyContentType.SINGLE_PANE)
                    },
                    toggleSelection = toggleEmailSelection,
                    isOpened = openedEmail?.id == email.id,
                    isSelected = selectedEmailIds.contains(email.id),
                )
            }
            // Add extra spacing at the bottom if
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@Composable
fun ReplyEmailDetail(email: Email, modifier: Modifier = Modifier, isFullScreen: Boolean = true, onBackPressed: () -> Unit = {}) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface),
    ) {
        item {
            EmailDetailAppBar(email, isFullScreen) {
                onBackPressed()
            }
        }
        items(items = email.threads, key = { it.id }) { email ->
            ReplyEmailThreadItem(email = email)
        }
        item {
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}
