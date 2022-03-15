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
    devicePosture: StateFlow<DevicePosture>
) {
    val foldingDevicePosture = devicePosture.collectAsState().value

    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val (navigationType, contentType) = when (windowSize) {
        WindowSize.Compact -> {
            Pair(ReplyNavigationType.BOTTOM_NAVIGATION, ReplyContentType.LIST_ONLY)
        }
        WindowSize.Medium -> {
            if (foldingDevicePosture != DevicePosture.NormalPosture) {
                Pair(ReplyNavigationType.NAVIGATION_RAIL, ReplyContentType.LIST_AND_DETAIL)
            } else {
                Pair(ReplyNavigationType.NAVIGATION_RAIL, ReplyContentType.LIST_ONLY)
            }
        }
        WindowSize.Expanded -> {
            Pair(ReplyNavigationType.FIXED_NAVIGATION_DRAWER, ReplyContentType.LIST_AND_DETAIL)
        }
    }

    if (navigationType == ReplyNavigationType.FIXED_NAVIGATION_DRAWER) {
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
            //ReplyNavigationRail()
        }
        Column(modifier = Modifier.fillMaxSize()) {
            if (contentType == ReplyContentType.LIST_AND_DETAIL) {
                // ReplyListAndDetailContent()
            } else {
                // ReplyListContent()
            }
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                //ReplyBottomNavigation()
            }
        }
    }
}
