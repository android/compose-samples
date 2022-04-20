package com.example.reply.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reply.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyApp(
    windowSize: WindowSize,
    foldingDevicePosture: DevicePosture,
    replyHomeUIState: ReplyHomeUIState
) {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     *
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val navigationType: ReplyNavigationType
    val contentType: ReplyContentType
    when (windowSize) {
        WindowSize.COMPACT -> {
            navigationType = ReplyNavigationType.BOTTOM_NAVIGATION
            contentType = ReplyContentType.LIST_ONLY
        }
        WindowSize.MEDIUM -> {
            navigationType = ReplyNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                ReplyContentType.LIST_AND_DETAIL
            } else {
                ReplyContentType.LIST_ONLY
            }
        }
        WindowSize.EXPANDED -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                ReplyNavigationType.NAVIGATION_RAIL
            } else {
                ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = ReplyContentType.LIST_AND_DETAIL
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(drawerContent = {
            NavigationDrawerContent()
        }) {
            ReplyAppContent(navigationType, contentType, replyHomeUIState)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent()
        }) {
            ReplyAppContent(navigationType, contentType, replyHomeUIState)
        }
    }
}

@Composable
fun ReplyAppContent(navigationType: ReplyNavigationType, contentType: ReplyContentType, replyHomeUIState: ReplyHomeUIState) {
    Row(modifier = Modifier
        .fillMaxSize()) {

        AnimatedVisibility(visible = navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail()
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            if (contentType == ReplyContentType.LIST_AND_DETAIL) {
                ReplyListAndDetailContent(
                    replyHomeUIState = replyHomeUIState,
                    modifier = Modifier.weight(1f),
                    // TODO selection of email and detail navigation
                    selectedItemIndex = 0
                )
            } else {
                ReplyListOnlyContent(replyHomeUIState = replyHomeUIState, modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(visible = navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ReplyBottomNavigationBar()
            }
        }
    }
}

@Composable
@Preview
fun ReplyNavigationRail() {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = {/*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Article, stringResource(id = R.string.tab_article)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.Chat, stringResource(id = R.string.tab_dm)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.People, stringResource(id = R.string.tab_groups)) }
        )
    }
}

@Composable
@Preview
fun ReplyBottomNavigationBar() {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
       NavigationBarItem(
           selected = true,
           onClick = { /*TODO*/ },
           icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
       )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Chat, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
    }
}

@Composable
fun NavigationDrawerContent(modifier: Modifier = Modifier) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(start = 24.dp, top = 48.dp)
    ) {
        DrawerItem(imageVector = Icons.Default.Inbox, title = stringResource(id = R.string.tab_inbox))
        DrawerItem(imageVector = Icons.Default.Article, title = stringResource(id = R.string.tab_article))
        DrawerItem(imageVector = Icons.Default.Chat, title = stringResource(id = R.string.tab_dm))
        DrawerItem(imageVector = Icons.Default.Videocam, title = stringResource(id = R.string.tab_groups))
    }
}

@Composable
fun DrawerItem(
    imageVector: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = imageVector, contentDescription = title)
        Text(text = title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
    }
}