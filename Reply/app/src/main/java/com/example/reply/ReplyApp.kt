package com.example.reply

import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.reply.data.DummyEmail.getEmail
import com.example.reply.details.DetailsScreen
import com.example.reply.ui.ReplyTheme
import com.example.reply.util.produceUiState

@ExperimentalLazyDsl
@ExperimentalMaterialApi
@Composable
fun ReplyApp(
    appContainer: AppContainer,
    destination: Destination,
    navigationController: NavController
) {
    ReplyTheme {
        HomeScreenBaseScaffold(
            repository = appContainer.emailRepository,
            destination = destination,
            navigationController = navigationController
        )
    }
}


@ExperimentalLazyDsl
@ExperimentalMaterialApi
@Composable
fun getBodyContent(
    destination: Destination,
    emailRepository: EmailRepository,
    modifier: Modifier,
    navigationController: NavController,
) {
    val navigationAction = EmailNavigationActions(navController = navigationController)
    when (destination) {
        Destination.HomeScreen -> {
            val (allEmails, refreshPost, clearError) = produceUiState(emailRepository) {
                getPosts()
            }

            HomeScreenContent(
                emails = allEmails.value,
                onRefreshPosts = refreshPost,
                onErrorDismiss = clearError,
                scaffoldState = rememberScaffoldState(),
                modifier = modifier,
                navigationAction = navigationAction.showDetails
            )

        }
        is Destination.EmailDetail -> {
            val emailDetails = getEmail(destination.index)
            DetailsScreen(
                email = emailDetails,
                modifier = modifier,
                navigationAction = navigationAction.backPress
            )
        }
    }
}


interface AppContainer {
    val emailRepository: EmailRepository
}

class AppContainerImpl : AppContainer {
    override val emailRepository: EmailRepository by lazy {
        EmailRepositoryImpl()
    }
}
