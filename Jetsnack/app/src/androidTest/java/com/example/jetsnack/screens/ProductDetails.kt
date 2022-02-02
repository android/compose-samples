package com.example.jetsnack.screens

import android.view.KeyEvent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.uiautomator.*
import com.example.jetsnack.R
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.*
import com.example.jetsnack.ui.home.cart.CART_ITEM
import com.example.jetsnack.ui.home.search.SEARCH_BAR
import com.example.jetsnack.ui.snackdetail.*
import com.example.jetsnack.util.BaseUITestHelper
import com.example.jetsnack.util.Constants

/**
 * Created by André Schabrocker on 2022-02-01
 */
object ProductDetails : BaseUITestHelper() {

    sealed class EntryPoint {
        object Home : EntryPoint()
        object Search : EntryPoint()
        object Cart : EntryPoint()
    }

    sealed class Quantity {
        object Increase : Quantity()
        object Decrease : Quantity()
    }
    // open Product and check that product details are displayed
    fun openProductDetails(entryPoint: EntryPoint, composeTestRule: ComposeTestRule, device: UiDevice) {
        when (entryPoint) {
            // open Product via Home-Tab and check that product details are displayed
            is EntryPoint.Home -> {
                composeTestRule.onAllNodesWithText(snacks[14].name)
                    .onFirst()
                    .performClick()

                composeTestRule.onNodeWithTag(DETAILS_HEADER)
                    .assertIsDisplayed()
                    .assert(hasText(applicationContext.resources.getString(R.string.detail_header)))
            }
            // open Product via Cart-Tab and check that product details are displayed
            is EntryPoint.Cart -> {
                composeTestRule.onAllNodesWithTag(CART_ITEM)
                    .onFirst()
                    .performClick()

                composeTestRule.onNodeWithTag(DETAILS_HEADER)
                    .assertIsDisplayed()
                    .assert(hasText(applicationContext.resources.getString(R.string.detail_header)))
            }
            // search for "Cheese", open Product and check that product details are displayed
            is EntryPoint.Search -> {
                composeTestRule.onNodeWithTag(SEARCH_BAR)
                    .performClick()

                device.apply {
                    pressKeyCode(KeyEvent.KEYCODE_C)
                    pressKeyCode(KeyEvent.KEYCODE_H)
                    pressKeyCode(KeyEvent.KEYCODE_E)
                    pressKeyCode(KeyEvent.KEYCODE_E)
                    pressKeyCode(KeyEvent.KEYCODE_S)
                    pressKeyCode(KeyEvent.KEYCODE_E)
                }

                composeTestRule.onNodeWithText(snacks[19].name)
                    .assertIsDisplayed()
                    .performClick()

                composeTestRule.onNodeWithTag(DETAILS_HEADER)
                    .assertIsDisplayed()
                    .assert(hasText(applicationContext.resources.getString(R.string.detail_header)))
            }
        }
    }

    fun checkProductDetails(composeTestRule: ComposeTestRule) {
        // open first Product
        composeTestRule.onAllNodesWithTag(CART_ITEM)
            .onFirst()
            .performClick()

        //check all elements
        composeTestRule.onNodeWithTag(INCREASE_QUANTITY)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(DECREASE_QUANTITY)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(QUANTITY_TEXT)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.quantity)))

        composeTestRule.onNodeWithTag(ITEM_QUANTITY)
            .assertIsDisplayed()
            .assert(hasText(Constants.ProductQuantity.One.id))

        composeTestRule.onNodeWithTag(ADD_TO_CART_BUTTON)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.add_to_cart)))

        composeTestRule.onNodeWithTag(DETAILS_HEADER)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.detail_header)))

        composeTestRule.onNodeWithTag(DETAILS_TEXT)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.detail_placeholder)))

        // scroll to hidden elements
        UiScrollable(UiSelector().scrollable(true)).scrollToEnd(1)

        // check elements
        composeTestRule.onAllNodesWithTag(EXPAND_DETAILS_BUTTON)
            .onFirst()
            .performClick()

        composeTestRule.onNodeWithText(applicationContext.resources.getString(R.string.ingredients))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(applicationContext.resources.getString(R.string.ingredients_list))
            .assertIsDisplayed()

        composeTestRule.onAllNodesWithTag(SNACK_COLLECTION_NAME_TEXT)
            .onFirst()
            .assert(hasText(SnackRepo.getRelated(6L)[0].name))
        composeTestRule.onAllNodesWithTag(SNACK_IMAGE, true)
            .assertCountEquals(6)

        composeTestRule.onAllNodesWithTag(SNACK_TITLE, true)
            .assertCountEquals(6)
    }

    fun changeQuantityOfItem(quantity: Quantity, composeTestRule: ComposeTestRule) {
        //open first Product
        composeTestRule.onAllNodesWithTag(CART_ITEM)
            .onFirst()
            .performClick()

        when (quantity) {
            // increase quantity and check amount
            is Quantity.Increase -> {
                composeTestRule.onNodeWithTag(INCREASE_QUANTITY)
                    .performClick()

                composeTestRule.onNodeWithTag(ITEM_QUANTITY)
                    .assertIsDisplayed()
                    .assert(hasText(Constants.ProductQuantity.Two.id))
            }
            // decrease quantity and check amount
            is Quantity.Decrease -> {
                composeTestRule.onNodeWithTag(DECREASE_QUANTITY)
                    .performClick()

                composeTestRule.onNodeWithTag(ITEM_QUANTITY)
                    .assertIsDisplayed()
                    .assert(hasText(Constants.ProductQuantity.Zero.id))
            }
        }

        //actual addingToCart is not yet implemented in app
        composeTestRule.onNodeWithTag(ADD_TO_CART_BUTTON)
            .assertIsDisplayed()
            .performClick()
    }
}
