package com.example.reply.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyApp(
    windowSize: WindowSize,
    foldingDevicePosture: DevicePosture
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

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(drawerContent = {}) {
            ReplyAppContent(navigationType, contentType)
        }
    } else {
        ModalNavigationDrawer(drawerContent = {}) {
            ReplyAppContent(navigationType, contentType)
        }
    }
}

@Composable
fun ReplyAppContent(navigationType: ReplyNavigationType, contentType: ReplyContentType) {
    Row(modifier = Modifier
        .fillMaxSize()) {
        if (navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            // TODO ReplyNavigationRail()
        }
        Column(modifier = Modifier.fillMaxSize()) {
            if (contentType == ReplyContentType.LIST_AND_DETAIL) {
                // TODO ReplyListAndDetailContent()
            } else {
                // TODO ReplyListContent()
            }
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                // TODO ReplyBottomNavigation()
            }
        }
    }
}
