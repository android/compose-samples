package com.example.jetnews.ui.favorites

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.*
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.*
import com.example.jetnews.ui.home.*

@Composable
fun FavoritesScreen(scaffoldState: ScaffoldState = remember { ScaffoldState() }) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Favorites,
                closeDrawer = { scaffoldState.drawerState = DrawerState.Closed })
        },
        topAppBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState = DrawerState.Opened }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = {
            FavoritesScreenBody(
                favorites = posts.filter { JetnewsStatus.favorites.contains(it.id) }
            )
        }
    )
}

@Composable
private fun FavoritesScreenBody(
    favorites: List<Post>
){
    if(favorites.isNotEmpty()) {
        VerticalScroller {
            Column {
                favorites.forEach {
                    Row {
                        FavoriteCard(it)
                    }
                }
            }
        }
    }
    else {
        Text("There are no favorites",
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun FavoriteCard(post: Post) {
    Clickable(
        modifier = Modifier.ripple(),
        onClick = { navigateTo(Screen.Article(postId = post.id), reset = false) }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            PostImage(post, Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                PostTitle(post)
                AuthorAndReadTime(post)
            }
            FavoriteButton(
                isFavorited = isFavorited(post.id),
                onFavorite = { toggleFavorite(postId = post.id) }
            )
        }
    }
}

@Preview("Favorites Screen")
@Composable
fun PreviewFavoritesScreen() {
    ThemedPreview {
        FavoritesScreen()
    }
}


@Preview("Favorites Screen Dark Theme")
@Composable
fun PreviewFavoritesScreenDark() {
    ThemedPreview(darkThemeColors) {
        FavoritesScreen()
    }
}
