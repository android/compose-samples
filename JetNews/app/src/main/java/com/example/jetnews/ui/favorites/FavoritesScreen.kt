package com.example.jetnews.ui.favorites

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.*
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.data.successOr
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.home.*

@Composable
fun FavoritesScreen(
        navigateTo: (Screen) -> Unit,
        scaffoldState: ScaffoldState = remember { ScaffoldState() }
) {
    Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                AppDrawer(
                        currentScreen = Screen.Favorites,
                        closeDrawer = { scaffoldState.drawerState = DrawerState.Closed },
                        navigateTo = navigateTo
                )
            },
            topBar = {
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
                    favorites = loadFakePosts().filter { JetnewsStatus.favorites.contains(it.id) },
                    navigateTo = navigateTo
            )
        }
    )
}

@Composable
private fun FavoritesScreenBody(
        favorites: List<Post>,
        navigateTo: (Screen) -> Unit
){
    if(favorites.isNotEmpty()) {
        VerticalScroller {
            Column {
                favorites.forEach {
                    Row {
                        FavoriteCard(
                                post = it,
                                navigateTo = navigateTo
                        )
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
fun FavoriteCard(
        post: Post,
        navigateTo: (Screen) -> Unit
) {
    Column(
            modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
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
        FavoritesScreen(navigateTo = {})
    }
}

@Composable
private fun loadFakePosts(): List<Post> {
    var posts: List<Post> = emptyList()
    BlockingFakePostsRepository(ContextAmbient.current).getPosts { result ->
        posts = result.successOr(emptyList())
    }
    return posts
}
