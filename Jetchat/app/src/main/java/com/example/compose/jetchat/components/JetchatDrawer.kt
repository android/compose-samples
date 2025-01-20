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

package com.example.compose.jetchat.components

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.widget.WidgetReceiver

@Composable
fun JetchatDrawerContent(
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    selectedMenu: String = "composers"
) {
    // Use windowInsetsTopHeight() to add a spacer which pushes the drawer content
    // below the status bar (y-axis)
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeader()
        DividerItem()
        DrawerItemHeader("Chats")
        ChatItem("composers", selectedMenu == "composers") {
            onChatClicked("composers")
        }
        ChatItem("droidcon-nyc", selectedMenu == "droidcon-nyc") {
            onChatClicked("droidcon-nyc")
        }
        DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader("Recent Profiles")
        ProfileItem(
            "Ali Conors (you)", meProfile.photo,
            selectedMenu == meProfile.userId
        ) {
            onProfileClicked(meProfile.userId)
        }
        ProfileItem(
            "Taylor Brooks", colleagueProfile.photo,
            selectedMenu == colleagueProfile.userId
        ) {
            onProfileClicked(colleagueProfile.userId)
        }
        if (widgetAddingIsSupported(LocalContext.current)) {
            DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
            DrawerItemHeader("Settings")
            WidgetDiscoverability()
        }
    }
}

@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = CenterVertically) {
        JetchatIcon(
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.jetchat_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChatItem(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat),
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun ProfileItem(
    text: String,
    @DrawableRes profilePic: Int?,
    selected: Boolean = false,
    onProfileClicked: () -> Unit
) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onProfileClicked),
        verticalAlignment = CenterVertically
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(24.dp)
        if (profilePic != null) {
            Image(
                painter = painterResource(id = profilePic),
                modifier = paddingSizeModifier.then(Modifier.clip(CircleShape)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
@Preview
fun DrawerPreview() {
    JetchatTheme {
        Surface {
            Column {
                JetchatDrawerContent({}, {})
            }
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    JetchatTheme(isDarkTheme = true) {
        Surface {
            Column {
                JetchatDrawerContent({}, {})
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WidgetDiscoverability() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                addWidgetToHomeScreen(context)
            }),
        verticalAlignment = CenterVertically
    ) {
        Text(
            stringResource(id = R.string.add_widget_to_home_page),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun addWidgetToHomeScreen(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val myProvider = ComponentName(context, WidgetReceiver::class.java)
    if (widgetAddingIsSupported(context)) {
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
private fun widgetAddingIsSupported(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported
}
