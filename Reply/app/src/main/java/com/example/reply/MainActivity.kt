package com.example.reply

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.ui.AppBarConfiguration
import com.example.reply.data.Email
import com.example.reply.home.EmailScrollList
import com.example.reply.home.LoadingContent

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeScreenFragment,
                R.id.detailScreenFragment
            )
        )
    }
}


//val appContainer = (application as ReplyApplication).container
// setContent {
//    ReplyApp(appContainer = appContainer)
//}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    emails: UiState<List<Email>>,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier,
    navigationAction: (Long) -> Unit
) {

    if (emails.hasError) {
        val errorEmail = "Load Error"
        val retryEmail = "Retry"
        // Show snackbar using a coroutine, when the coroutine is cancelled the snackbar will
        // automatically dismiss. This coroutine will cancel whenever posts.hasError changes, and
        // only start when posts.hasError is true (due to the above if-check).
        LaunchedTask(emails.hasError) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorEmail,
                actionLabel = retryEmail
            )
            when (snackbarResult) {
                SnackbarResult.ActionPerformed -> onRefreshPosts()
                SnackbarResult.Dismissed -> onErrorDismiss()
            }
        }
    }
    LoadingContent(
        empty = emails.firstTimeLoad,
        emptyContent = { EmptyContent() },
        loading = emails.loading,
        onRefresh = onRefreshPosts,
        content = {
            HomeScreenErrorContent(
                emails = emails,
                navigationAction = navigationAction,
                onRefresh = {
                    onRefreshPosts()
                },
                modifier = it.then(modifier)
            )
        }
    )

}


@ExperimentalMaterialApi
@Composable
internal fun HomeScreenErrorContent(
    emails: UiState<List<Email>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    navigationAction: (Long) -> Unit
) {
    if (emails.data != null) {
//        EmailScrollList(emails.data, modifier)
        EmailScrollList(
            initialEmails = emails.data,
            modifier = modifier,
            navigationAction = navigationAction
        )
    } else if (!emails.hasError) {
        // if there are no posts, and no error, let the user refresh manually
        TextButton(onClick = onRefresh, modifier.fillMaxSize()) {
            Text("Tap to load content", textAlign = TextAlign.Center)
        }
    } else {
        // there's currently an error showing, don't show any content
        Box(modifier.fillMaxSize()) { /* empty screen */ }
    }
}


@Composable
fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize().preferredSize(100.dp)) {
        val image = vectorResource(id = R.drawable.ic_reply_logo)
        Image(
            asset = image,
            modifier = Modifier.align(Alignment.Center),
            contentScale = ContentScale.Crop
        )
    }
}

