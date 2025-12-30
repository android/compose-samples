package com.baidaidai.animora.components.StartScreen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.baidaidai.animora.R
import com.baidaidai.animora.components.StartScreen.model.BarItemInside
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState

final object NecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun homeTopAppBar(
        onClick: () -> Unit
    ){
        TopAppBar(
            title = {
                Text("Animora")
            },
            actions = {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.info_24px),
                        contentDescription = "Info"
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 5.dp)
        )
    }

    @Composable
    fun homeButtomBar(
        controller: NavHostController
    ){
        val coroutineScope = rememberCoroutineScope()

        NavigationBar {

            val NavigationRenderingList = listOf<BarItemInside>(
                BarItemInside(0, Icons.Outlined.Home, "Home"),
                BarItemInside(1, Icons.AutoMirrored.Outlined.List, "List")
            )

            var selected by rememberSaveable { mutableIntStateOf(0) }
            val navBackStackEntry by controller.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationRenderingList.forEach { item ->
                NavigationBarItem(
                    selected = item.contentDescription == currentRoute,
                    onClick = {
                        selected = item.number
                        coroutineScope.launch {
                            if (currentRoute == item.contentDescription) else controller.navigate(item.contentDescription)
                        }
                    },
                    icon = {
                        Icon(item.pattern, item.contentDescription)
                    },
                    label = {
                        Text(item.contentDescription)
                    }
                )
            }
        }
    }
}