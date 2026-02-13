package com.example.jetsnack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.jetsnack.ui.home.HomeSections
import kotlinx.serialization.Serializable

@Serializable
data object FeedKey : NavKey

@Serializable
data object SearchKey : NavKey

@Serializable
data object CartKey : NavKey

@Serializable
data object ProfileKey : NavKey

@Serializable
data class SnackDetailKey(val snackId: Long, val origin: String) : NavKey

fun NavBackStack<NavKey>.addHomeSection(key: NavKey) {
    // Remove everything except the Feed from the back stack.
    removeAll { it !is FeedKey }
    // Now add the key if it's not the Feed.
    if (key !is FeedKey){
        add(key)
    }
}

fun NavBackStack<NavKey>.currentHomeSectionKey() : NavKey = findLast { it in HomeSections.routes }
    ?: error("No HomeSection key found in the back stack")

@Composable
fun NavBackStack<NavKey>.addSnackDetail() : (snackId: Long, origin: String) -> Unit {
    val lifecycleOwner = LocalLifecycleOwner.current
    return { snackId, origin ->
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            add(SnackDetailKey(snackId, origin))
        }
    }
}
