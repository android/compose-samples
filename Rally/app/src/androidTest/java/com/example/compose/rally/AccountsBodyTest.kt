package com.example.compose.rally

import androidx.compose.ui.graphics.Color
import androidx.ui.test.createComposeRule
import com.example.compose.rally.data.Account
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.theme.RallyTheme
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class AccountsBodyTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersAnEmptyListOfAccounts() {
        renderAccountsBody(emptyList())

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersOnlyOneAccount() {
        renderAccountsBody(
            listOf(
                Account("First account", 1111, 1.0f, Color.Green)
            )
        )

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersManyDifferentAccounts() {
        renderAccountsBody(
            listOf(
                Account("First account", 1111, 1.0f, Color.Green),
                Account("Second account", 2222, 2.0f, Color.Blue),
                Account("Third account", 3333, 3.0f, Color.Red),
                Account("Fourth account", 4444, 4.0f, Color.Magenta)
            )
        )

        compareScreenshot(composeTestRule)
    }

    private fun renderAccountsBody(accounts: List<Account>) {
        composeTestRule.setContent {
            RallyTheme {
                AccountsBody(accounts)
            }
        }
    }

}