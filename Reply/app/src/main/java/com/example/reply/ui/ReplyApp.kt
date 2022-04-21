package com.example.reply.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reply.R
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(drawerContent = {
            NavigationDrawerContent()
        }) {
            ReplyAppContent(navigationType, contentType, replyHomeUIState)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent {
                    scope.launch {
                        drawerState.close()
                    }
                }
            },
            drawerState = drawerState
        ) {
            ReplyAppContent(navigationType, contentType, replyHomeUIState) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}

@Composable
fun ReplyAppContent(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier
        .fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail(
                onDrawerClicked = onDrawerClicked
            )
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
fun ReplyNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon =  { Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.navigation_drawer)) }
        )
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
fun NavigationDrawerContent(modifier: Modifier = Modifier, onDrawerClicked: () -> Unit = {}) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    onDrawerClicked.invoke()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(imageVector = Icons.Default.MenuOpen, contentDescription = stringResource(id = R.string.navigation_drawer))
        }
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