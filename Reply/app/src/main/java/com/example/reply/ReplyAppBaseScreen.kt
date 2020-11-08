package com.example.reply

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.navigation.NavController

@OptIn(ExperimentalLazyDsl::class)
@ExperimentalMaterialApi
@Composable
internal fun HomeScreenBaseScaffold(
    destination: Destination,
    repository: EmailRepository,
    navigationController: NavController
) {
    val context = ContextAmbient.current.applicationContext

    Scaffold(
        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape
            ) {

                IconButton(
                    icon = { Icon(asset = Icons.Default.Menu) },
                    onClick = {}
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    icon = { Icon(asset = Icons.Filled.Search) },
                    onClick = {
                        Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
                    })
            }
        },
        bodyContent = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            getBodyContent(
                destination = destination,
                emailRepository = repository, modifier,
                navigationController = navigationController
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "FAB clicked", Toast.LENGTH_SHORT).show()
                },
                shape = CircleShape
            ) {
                Icon(asset = Icons.Filled.Send)
            }
        }
    )
}