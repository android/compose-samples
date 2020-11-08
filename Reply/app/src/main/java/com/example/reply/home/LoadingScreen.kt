package com.example.reply.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.reply.R
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.reply.SwipeToRefreshLayout
import com.example.reply.ui.ReplyTheme
import com.example.reply.ui.SwipeViewContainerHeight
import com.example.reply.ui.SwipeViewContainerPadding

@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = onRefresh,
            refreshIndicator = {
                RefreshView()

            },
            content = content,
        )
    }
}

@Composable
fun RefreshView() {
    val image = vectorResource(id = R.drawable.ic_reply_logo)

    Surface(elevation = 10.dp, color = MaterialTheme.colors.primaryVariant) {
        Image(
            alignment = Alignment.Center,
            asset = image,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SwipeViewContainerPadding)
                .preferredSize(SwipeViewContainerHeight)
        )
    }
}
