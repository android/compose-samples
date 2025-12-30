package com.baidaidai.animora

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.baidaidai.animora.components.StartScreen.components.NecessaryComponents
import com.baidaidai.animora.shared.viewModel.animationDatasViewModel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Test
    fun navigate_to_list_view(){
        composeTestRule.setContent {
            val animationDetailsViewModel = viewModel<animationDatasViewModel>()
            CompositionLocalProvider(
                LocalAnimationViewModel provides animationDetailsViewModel
            ) {
                AnimoraApp()
            }
        }
        composeTestRule.onNodeWithContentDescription(
            label = "List",
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText("shareBorder").assertExists()
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Test
    fun navigate_to_info_activity_and_back(){
        composeTestRule.setContent {
            val animationDetailsViewModel = viewModel<animationDatasViewModel>()
            CompositionLocalProvider(
                LocalAnimationViewModel provides animationDetailsViewModel
            ) {
                AnimoraApp()
            }
        }
        composeTestRule.onNodeWithContentDescription(
            label = "Info",
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText("About").assertExists()
        composeTestRule.onNodeWithContentDescription(
            label = "Back",
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText("Home").assertExists()
    }

}