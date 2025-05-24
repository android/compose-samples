package com.example.compose.jetchat.conversation

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.navigation.DEST_ROUTE_CONVERSATION
import com.example.compose.jetchat.navigation.DEST_ROUTE_PROFILE
import com.example.compose.jetchat.MainViewModel

@Composable
fun ConversationScreen(activityViewModel: MainViewModel, navHostCont: NavHostController) {
    ConversationContent(
        uiState = exampleUiState,
        navigateToProfile = { user ->
            // Click callback
//            val bundle = bundleOf("userId" to user)
//            findNavController().navigate(
//                R.id.nav_profile,
//                bundle
//            )
            navHostCont.navigate("$DEST_ROUTE_PROFILE/$user")
        },
        onNavIconPressed = {
            activityViewModel.openDrawer()
        }
    )
}