package com.example.jetnews.ui.favorites

import android.content.Context
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.article.postContentItems
import com.example.jetnews.ui.article.sharePost
import com.example.jetnews.ui.home.PostTopBar
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MasterDetailFavoriteScreen (
    modifier: Modifier,
    isShowTopbar: Boolean,
    uiState: FavoritesUiState,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    onInteractWithList: () -> Unit,
    onUnFavorite: (String) -> Unit,
    interactWithFavorite: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenFirstPost: () -> Unit,
    context: Context = LocalContext.current
){
    Row(modifier){
        FavoriteScreenList(
            isShowTopbar = isShowTopbar,
            snackbarHostState ,
            openDrawer,
            isExpandedScreen ,
            uiState
        ) { uiState, modifier ->

            Row(modifier = modifier) {
                FavoriteList(
                    favorites = uiState.favoriteFeed.favorites,
                    modifier = modifier.width(334.dp)
                        .notifyInput(onInteractWithList),
                    onUnFavorite = onUnFavorite,
                    interactWithFavorite = interactWithFavorite
                )

                Log.d("FavoriteScreenList", "${uiState.selectedPost}")
                Crossfade(targetState = uiState.selectedPost) { selectedPost ->
                    if(selectedPost == null){
                        onOpenFirstPost()
                    }
                    key(selectedPost?.id) {
                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            modifier = modifier.padding(horizontal = 16.dp)
                                .fillMaxSize()
                                .notifyInput {
                                    selectedPost?.run{
                                        interactWithFavorite(id)
                                    }
                                }
                        ) {
                            stickyHeader {
                                val favorites = uiState.favoriteFeed.favorites
                                PostTopBar(
                                    isFavorite = uiState.favoriteKeys.contains(selectedPost?.id),
                                    onToggleFavorite = {
                                        if (selectedPost != null) {
                                            onToggleFavorite(selectedPost.id)
                                        }
                                    },
                                    onSharePost = {
                                        selectedPost?.run {
                                            sharePost(this, context)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.End)
                                )
                            }
                            selectedPost?.run {
                                postContentItems(this)
                            }
                        }
                    }
                }
            }

        }


    }
}

private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }