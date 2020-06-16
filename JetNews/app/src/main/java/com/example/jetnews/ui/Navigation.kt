package com.example.jetnews.ui

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.jetnews.ui.Screen.Article
import com.example.jetnews.ui.Screen.Home
import com.example.jetnews.ui.Screen.Interests
import com.example.jetnews.ui.ScreenNames.ARTICLE
import com.example.jetnews.ui.ScreenNames.HOME
import com.example.jetnews.ui.ScreenNames.INTERESTS
import com.example.jetnews.utils.getMutableStateOf

/**
 * Screen names (used for serialization)
 */
enum class ScreenNames { HOME, INTERESTS, ARTICLE }

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen(val id: ScreenNames) {
    object Home : Screen(HOME)
    object Interests : Screen(INTERESTS)
    data class Article(val postId: String) : Screen(ARTICLE)
}

/**
 * Helpers for saving and loading a [Screen] object to a [Bundle].
 *
 * This allows us to persist navigation across process death, for example caused by a long video
 * call.
 */
private val SIS_SCREEN = "sis_screen"
private val SIS_NAME = "screen_name"
private val SIS_POST = "post"

/**
 * Convert a screen to a bundle that can be stored in [SavedStateHandle]
 */
private fun Screen.toBundle(): Bundle {
    val retval = Bundle()

    val current = this
    retval.putString(SIS_NAME, current.id.name)
    if (current is Article) {
        retval.putString(SIS_POST, current.postId)
    }
    return retval
}

/**
 * Read a bundle stored by [Screen.toBundle] and return desired screen.
 *
 * If the bundle is empty, or a screen fails to parse, this should produce [Screen.Home].
 *
 * @throws IllegalStateException if [SIS_NAME] is non-null and not handled
 */
private fun Bundle?.toScreen(): Screen? {
    this ?: return null
    return when (getString(SIS_NAME)) {
        HOME.name, null -> Home
        INTERESTS.name -> Interests
        ARTICLE.name -> {
            val postId = getString(SIS_POST) ?: return null
            Article(postId)
        }
        else -> throw IllegalArgumentException("Unknown screen ${getString(SIS_NAME)}")
    }
}

/**
 * This is expected to be replaced by the navigation component, but for now handle navigation
 * manually.
 *
 * Instantiate this ViewModel at the scope that is fully-responsible for navigation, which in this
 * application is [MainActivity].
 *
 * This app has simplified navigation; the back stack is always [Home] or [Home, dest] and more
 * levels are not allowed. To use a similar pattern with a longer back stack, add a stack to
 * hold the back stack state.
 */
class NavigationViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    /**
     * Hold the current screen in a stable observable, restored from savedStateHandle after process
     * death.
     *
     * mutableStateOf is an observable similar to LiveData that's designed to be read by compose. It
     * supports observability via property delegate syntax.
     */
    var currentScreen by savedStateHandle.getMutableStateOf(
        key = SIS_SCREEN,
        save = { it.toBundle() },
        restore = { it.toScreen() ?: Home }
    )
        private set // limit the writes to only inside this class.

    /**
     * Go back (always to [Home]).
     *
     * Returns true if this call caused user-visible navigation. Will always return false
     * when [currentScreen] is [Home].
     */
    @MainThread
    fun onBack(): Boolean {
        val wasHandled = currentScreen != Home
        currentScreen = Home
        return wasHandled
    }

    /**
     * Navigate to requested [Screen].
     *
     * If the requested screen is not [Home], it will always create a back stack with one element:
     * ([Home] -> [screen]). More back entries are not supported in this app.
     */
    @MainThread
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}
