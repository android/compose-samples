package com.example.reply.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.example.reply.EmptyContent
import com.example.reply.ui.ReplyTheme

@Preview
@Composable
fun LoadingScreenPreview() {
    ReplyTheme {
        RefreshView()
    }
}

@Preview
@Composable
fun EmptyContentPreview() {
    ReplyTheme {
        EmptyContent()
    }
}

@Preview
@Composable
fun BaseScaffold() {
    Scaffold(
        topBar = {},
        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape
            ) {

                IconButton(
                    icon = { Icon(asset = Icons.Filled.Menu) },
                    onClick = {})

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    icon = { Icon(asset = Icons.Filled.Search) },
                    onClick = {})
            }
        },
        bodyContent = {
            Text(text = "SAM")
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape
            ) {
                Icon(asset = Icons.Filled.Send)
            }
        }
    )
}

//@Preview
//@Composable
//fun HomeScreenScaffoldPreview() {
//    ReplyTheme {
//        val postsRepository = EmailRepositoryImpl()
//        val (postUiState, refreshPost, clearError) = produceUiState(postsRepository) {
//            getPosts()
//        }
//
//        HomeScreenContent(
//            emails = postUiState.value,
//            onRefreshPosts = refreshPost,
//            onErrorDismiss = clearError,
//            scaffoldState = rememberScaffoldState()
//        )
//    }
//
//}