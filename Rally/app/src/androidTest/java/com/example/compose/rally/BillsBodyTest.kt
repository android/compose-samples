package com.example.compose.rally

import androidx.compose.ui.graphics.Color
import androidx.ui.test.createComposeRule
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.theme.RallyTheme
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class BillsBodyTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersAnEmptyListOfAccounts() {
        renderBillsBody(emptyList())

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersOnlyOneAccount() {
        renderBillsBody(
            listOf(
                Bill("First bill", "Jan 29", 1.0f, Color.Green)
            )
        )

        compareScreenshot(composeTestRule)
    }

    @Test
    fun rendersManyDifferentAccounts() {
        renderBillsBody(
            listOf(
                Bill("First bill", "Jan 1", 1.0f, Color.Green),
                Bill("Second bill", "Feb 1", 2.0f, Color.Blue),
                Bill("Third bill", "Mar 1", 3.0f, Color.Red),
                Bill("Fourth bill", "Nov 20", 4.0f, Color.Magenta)
            )
        )

        compareScreenshot(composeTestRule)
    }

    private fun renderBillsBody(accounts: List<Bill>) {
        composeTestRule.setContent {
            RallyTheme {
                BillsBody(accounts)
            }
        }
    }

}