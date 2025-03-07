package com.example.jetcaster

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.jetcaster.ui.JetcasterNavController.navigateToUpNext
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NavigationTest {
    @get:Rule
    val rule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun launchAndNavigate() {
        val activity = rule.activity

        val navController = activity.navController

        rule.waitUntil {
            navController.currentDestination?.route != null
        }

        assertEquals("player?page={page}", navController.currentDestination?.route)

        navController.navigateToUpNext()

        assertEquals("upNext", navController.currentDestination?.route)
    }
}