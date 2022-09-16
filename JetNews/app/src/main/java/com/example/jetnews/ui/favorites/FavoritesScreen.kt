package com.example.jetnews.ui.favorites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.model.Favorite
import com.example.jetnews.model.Post
import com.example.jetnews.ui.utils.UnFavoriteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiActions: FavoriteUiActions,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    favoriteState: FavoritesUiState,
    interactWithFavorite: (String) -> Unit,
    onUnFavorite: (String) -> Unit
){

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorites_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (!isExpandedScreen) {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                painter = painterResource(R.drawable.ic_jetnews_logo),
                                contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Open search */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.cd_search)
                        )
                    }
                }
            )
        },
    ){
        val favScreenModifier = Modifier.padding(it)

        LaunchedEffect(key1 = uiActions){
            when(uiActions){
                is FavoriteUiActions.Delete ->{
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = "No disappointed!",
                    )
                }
                else -> {}
            }
        }

        if (favoriteState.isLoading){
            LoadingContent(favScreenModifier)
        }

        ScreenContent(favoriteState, favScreenModifier,
            onUnFavorite, interactWithFavorite)
    }
}

@Composable
fun ScreenContent(favoriteState: FavoritesUiState, modifier: Modifier,
                  onUnFavorite: (String) -> Unit,
                  interactWithFavorite: (String) -> Unit){
    when(favoriteState){
        is FavoritesUiState.HasFavorites ->{
            favoriteState.favoriteFeed.run {
                FavoriteList(favorites = favorite,
                    modifier = modifier,
                    onUnFavorite, interactWithFavorite)
            }

        }
        is FavoritesUiState.NoFavorites ->{
            NoContent(modifier = modifier)
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier){
   Column(verticalArrangement = Arrangement.Center,
       horizontalAlignment = Alignment.CenterHorizontally,
       modifier = Modifier
           .fillMaxWidth()
           .fillMaxHeight()) {
       CircularProgressIndicator(modifier = modifier)
   }
}

@Composable
fun NoContent(modifier: Modifier){
   Column(modifier = modifier.fillMaxSize(),
       horizontalAlignment = Alignment.CenterHorizontally,
       verticalArrangement = Arrangement.Center) {
       Text("No content",
           style = MaterialTheme.typography.titleMedium)
   }
}

@Composable
fun FavoriteList(favorites: List<Favorite>,
                 modifier: Modifier, onUnFavorite: (String) -> Unit,
                 interactWithFavorite: (String) -> Unit){
    LazyColumn(modifier = modifier){
        items(favorites){ favorite ->
            FavoriteRow(favorite = favorite,
                onUnFavorite, interactWithFavorite)
        }
    }
}

@Composable
fun FavoriteRow(favorite: Favorite, onUnFavorite: (String) -> Unit,
                interactWithFavorite: (String) -> Unit){
    Column(Modifier.clickable{
        interactWithFavorite.invoke(favorite.id)
    }) {
        Row(verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween){
            Image(painter = painterResource(id = favorite.imageThumbnailId),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp, start = 20.dp)
                    .align(alignment = Alignment.Top) )
            FavoriteItemColumn(title = favorite.title,
                author = favorite.subtitle ?: "", timeCreated = "")
            UnFavoriteButton {
                //(TODO): Unfavored when clicked
                onUnFavorite(favorite.id)
            }
        }
        Divider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp))
    }
}

@Composable
fun FavoriteItemColumn(title: String,
                       author: String, timeCreated: String){
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text(title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis)
        Row {
            Text(author)
            Text(timeCreated)
        }
    }
}