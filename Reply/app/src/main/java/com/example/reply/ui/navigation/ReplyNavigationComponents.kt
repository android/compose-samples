package com.example.reply.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.ui.navigation.ReplyDestinations


@Composable
fun ReplyNavigationRail(
    selectedDestination: String,
    navigateToInbox: () -> Unit,
    navigateToDM: () -> Unit,
    navigateToArticles: () -> Unit,
    navigateToGroups: () -> Unit,
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon =  { Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.navigation_drawer)) }
        )
        FloatingActionButton(
            onClick =  { /*TODO*/ },
            modifier = Modifier.padding(top = 18.dp, bottom = 40.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
                modifier = Modifier.size(18.dp)
            )
        }
        NavigationRailItem(
            selected = selectedDestination == ReplyDestinations.INBOX,
            onClick = navigateToInbox,
            icon =  { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationRailItem(
            selected = selectedDestination == ReplyDestinations.ARTICLES,
            onClick = navigateToArticles,
            icon =  { Icon(imageVector = Icons.Default.Article, stringResource(id = R.string.tab_article)) }
        )
        NavigationRailItem(
            selected = selectedDestination == ReplyDestinations.DM,
            onClick = navigateToDM,
            icon =  { Icon(imageVector = Icons.Outlined.Chat, stringResource(id = R.string.tab_dm)) }
        )
        NavigationRailItem(
            selected = selectedDestination == ReplyDestinations.GROUPS,
            onClick = navigateToGroups,
            icon =  { Icon(imageVector = Icons.Outlined.People, stringResource(id = R.string.tab_groups)) }
        )
    }
}

@Composable
fun ReplyBottomNavigationBar(
    selectedDestination: String,
    navigateToInbox: () -> Unit,
    navigateToDM: () -> Unit,
    navigateToArticles: () -> Unit,
    navigateToGroups: () -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            selected = selectedDestination == ReplyDestinations.INBOX,
            onClick = navigateToInbox,
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = selectedDestination == ReplyDestinations.ARTICLES,
            onClick = navigateToArticles,
            icon = { Icon(imageVector = Icons.Outlined.Chat, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = selectedDestination == ReplyDestinations.DM,
            onClick = navigateToDM,
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = selectedDestination == ReplyDestinations.GROUPS,
            onClick = navigateToGroups,
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    selectedDestination: String,
    navigateToInbox: () -> Unit,
    navigateToDM: () -> Unit,
    navigateToArticles: () -> Unit,
    navigateToGroups: () -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        Modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.MenuOpen,
                    contentDescription = stringResource(id = R.string.navigation_drawer)
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 48.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(id = R.string.compose),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

        }
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.INBOX,
            label = { Text(text = stringResource(id = R.string.tab_inbox), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription =  stringResource(id = R.string.tab_inbox)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = navigateToInbox
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.ARTICLES,
            label = { Text(text = stringResource(id = R.string.tab_article), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Article, contentDescription =  stringResource(id = R.string.tab_article)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = navigateToArticles
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.DM,
            label = { Text(text = stringResource(id = R.string.tab_dm), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Chat, contentDescription =  stringResource(id = R.string.tab_dm)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = navigateToDM
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.GROUPS,
            label = { Text(text = stringResource(id = R.string.tab_groups), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Article, contentDescription =  stringResource(id = R.string.tab_groups)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = navigateToGroups
        )
    }
}