package com.example.jetcaster.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.ui.NavigationViewModel
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun Search() {
    Surface(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            SearchBar()
            Spacer(Modifier.height(16.dp))
            SearchResults()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun SearchBar() {
    val navigationViewModel = viewModel(NavigationViewModel::class.java)
    val searchViewModel = viewModel(SearchViewModel::class.java)
    val searchState = searchViewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = searchState.value.searchText,
            onValueChange = searchViewModel::onSearchTextChanged,
            label = { Text(stringResource(R.string.search_podcast_prompt)) },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        IconButton(onClick = navigationViewModel::onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.close)
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun SearchResults() {
    val searchViewModel = viewModel(SearchViewModel::class.java)
    val searchState by searchViewModel.state.collectAsState()

    LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 128.dp)) {
        items(searchState.results) {
            PodcastGridItem(
                podcastTitle = it.title,
                podcastImageUrl = it.imageUrl,
                modifier = Modifier
                    .clickable { /* TODO */ }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun PodcastGridItem(
    podcastTitle: String,
    podcastImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Image(
            painter = rememberCoilPainter(request = podcastImageUrl, fadeIn = true),
            contentDescription = podcastTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = podcastTitle,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}