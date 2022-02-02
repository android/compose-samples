package com.example.jetsnack.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.example.jetsnack.R
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.snacks
import com.example.jetsnack.ui.components.*
import com.example.jetsnack.ui.home.cart.*
import com.example.jetsnack.ui.utils.formatPrice
import com.example.jetsnack.util.BaseUITestHelper
import com.example.jetsnack.util.Constants

/**
* Created by André Schabrocker on 2022-02-01
*/object Cart : BaseUITestHelper() {

    sealed class QuantityOfRemovedItems {
        object SingleItem : QuantityOfRemovedItems()
        object AllItems : QuantityOfRemovedItems()
    }

    sealed class ChangeItemQuantity {
        object Increase : ChangeItemQuantity()
        object Decrease : ChangeItemQuantity()
    }

    fun checkCart(composeTestRule: ComposeTestRule) {
        // check all elements of current Cart
        composeTestRule.onNodeWithTag(CART_HEADLINE)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(
            applicationContext.resources.getString(
                R.string.cart_order_header, applicationContext.resources.getQuantityString(R.plurals.cart_order_count, 3, 3)
            )
        ).assertIsDisplayed()

        composeTestRule.onAllNodesWithTag(CART_ITEM)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithTag(INCREASE_QUANTITY)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithTag(DECREASE_QUANTITY)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithTag(QUANTITY_TEXT, true)
            .assertCountEquals(3)
            .assertAll(hasText(applicationContext.resources.getString(R.string.quantity)))

        composeTestRule.onAllNodesWithTag(ITEM_QUANTITY, true)
            .assertCountEquals(3)
            .onFirst()
            .assert(hasText(Constants.ProductQuantity.Two.id))

        composeTestRule.onAllNodesWithTag(REMOVE_ITEM)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithTag(PRODUCT_IMAGE, true)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithText(snacks[4].tagline)
            .assertCountEquals(3)
            .onFirst()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(snacks[4].name)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(formatPrice(snacks[4].price))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(snacks[6].name)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(formatPrice(snacks[6].price))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(snacks[8].name)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(formatPrice(snacks[8].price))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(CART_SUMMARY_TITLE)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.cart_summary_header)))

        composeTestRule.onNodeWithTag(CART_SUBTOTAL_TITLE)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.cart_subtotal_label)))

        composeTestRule.onNodeWithTag(CART_SUBTOTAL_PRICE)
            .assertIsDisplayed()
            .assert(hasText(formatPrice(2* snacks[4].price+3* snacks[6].price+ snacks[8].price)))

        composeTestRule.onNodeWithTag(CART_SHIPPING_TITLE)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.cart_shipping_label)))

        composeTestRule.onNodeWithTag(CART_SHIPPING_PRICE)
            .assertIsDisplayed()
            .assert(hasText(formatPrice(369)))

        composeTestRule.onNodeWithTag(CART_TOTAL_TITLE)
            .assertIsDisplayed()
            .assert(hasText(applicationContext.resources.getString(R.string.cart_total_label)))

        composeTestRule.onNodeWithTag(CART_TOTAL_PRICE)
            .assertIsDisplayed()
            .assert(hasText(formatPrice(2* snacks[4].price+3* snacks[6].price+ snacks[8].price+369)))
        
        // actual checkout not yet implemented in app
        composeTestRule.onNodeWithTag(CHECKOUT_BUTTON)
            .assertIsDisplayed()
            .assertIsEnabled()
            .assert(hasText(applicationContext.resources.getString(R.string.cart_checkout)))
            .performClick()

        // scroll to hidden elements
        UiScrollable(UiSelector().scrollable(true)).scrollToEnd(1)

        // check tem suggestions
        composeTestRule.onNodeWithTag(SNACK_COLLECTION_NAME_TEXT)
            .assertIsDisplayed()
            .assert(hasText(SnackRepo.getInspiredByCart().name))

        composeTestRule.onNodeWithTag(SNACK_COLLECTION_NAME_BUTTON)
            .performClick()

        composeTestRule.onAllNodesWithTag(SNACK_IMAGE, true)
            .assertCountEquals(3)

        composeTestRule.onAllNodesWithTag(SNACK_TITLE, true)
            .assertCountEquals(3)
    }

    fun removeItems(quantityOfRemovedItems: QuantityOfRemovedItems, composeTestRule: ComposeTestRule) {
        // remove one item
        composeTestRule.onAllNodesWithTag(REMOVE_ITEM)
            .assertCountEquals(3)
            .onFirst()
            .performClick()

        when (quantityOfRemovedItems) {
            is QuantityOfRemovedItems.SingleItem -> {
                //check items quantity is removed by 1 element
                composeTestRule.onAllNodesWithTag(CART_ITEM)
                    .assertCountEquals(2)

                composeTestRule.onAllNodesWithTag(INCREASE_QUANTITY)
                    .assertCountEquals(2)

                composeTestRule.onAllNodesWithTag(DECREASE_QUANTITY)
                    .assertCountEquals(2)

                composeTestRule.onAllNodesWithTag(QUANTITY_TEXT, true)
                    .assertCountEquals(2)

                composeTestRule.onAllNodesWithTag(ITEM_QUANTITY, true)
                    .assertCountEquals(2)
                    .onFirst()
                    .assert(hasText(Constants.ProductQuantity.Three.id))

                composeTestRule.onAllNodesWithTag(REMOVE_ITEM)
                    .assertCountEquals(2)

                composeTestRule.onAllNodesWithTag(PRODUCT_IMAGE, true)
                    .assertCountEquals(2)

                // check new prices
                composeTestRule.onNodeWithTag(CART_SUBTOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(3*snacks[6].price+snacks[8].price)))

                composeTestRule.onNodeWithTag(CART_TOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(3*snacks[6].price+snacks[8].price+369)))
            }
            is QuantityOfRemovedItems.AllItems -> {
                //check all items are removed
                composeTestRule.onAllNodesWithTag(REMOVE_ITEM)
                    .assertCountEquals(2)
                    .onFirst()
                    .performClick()

                composeTestRule.onNodeWithTag(REMOVE_ITEM)
                    .assertExists()
                    .performClick()

                composeTestRule.onAllNodesWithTag(CART_ITEM)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(INCREASE_QUANTITY)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(DECREASE_QUANTITY)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(QUANTITY_TEXT, true)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(ITEM_QUANTITY, true)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(REMOVE_ITEM)
                    .assertCountEquals(0)

                composeTestRule.onAllNodesWithTag(PRODUCT_IMAGE, true)
                    .assertCountEquals(0)

                // check new prices
                composeTestRule.onNodeWithTag(CART_SUBTOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(0)))

                composeTestRule.onNodeWithTag(CART_TOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(369)))
            }
        }
    }

    fun changeItemQuantity(changeItemQuantity: ChangeItemQuantity, composeTestRule: ComposeTestRule) {
        when (changeItemQuantity) {
            // increase item quantity and check new quantity
            is ChangeItemQuantity.Increase -> {
                composeTestRule.onAllNodesWithTag(INCREASE_QUANTITY)
                    .onFirst()
                    .performClick()

                composeTestRule.onAllNodesWithTag(ITEM_QUANTITY, true)
                    .assertCountEquals(3)
                    .onFirst()
                    .assert(hasText(Constants.ProductQuantity.Three.id))

                // check new prices
                composeTestRule.onNodeWithTag(CART_SUBTOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(3*snacks[4].price+3*snacks[6].price+snacks[8].price)))

                composeTestRule.onNodeWithTag(CART_TOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(3*snacks[4].price+3*snacks[6].price+snacks[8].price+369)))
            }
            is ChangeItemQuantity.Decrease -> {
                // decrease item quantity and check new quantity
                composeTestRule.onAllNodesWithTag(DECREASE_QUANTITY)
                    .onFirst()
                    .performClick()

                composeTestRule.onAllNodesWithTag(ITEM_QUANTITY, true)
                    .assertCountEquals(3)
                    .onFirst()
                    .assert(hasText(Constants.ProductQuantity.One.id))

                // check new prices
                composeTestRule.onNodeWithTag(CART_SUBTOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(1*snacks[4].price+3*snacks[6].price+snacks[8]
                    .price)))

                composeTestRule.onNodeWithTag(CART_TOTAL_PRICE)
                    .assertIsDisplayed()
                    .assert(hasText(formatPrice(1*snacks[4].price+3*snacks[6].price+snacks[8]
                    .price+369)))
            }
        }
    }
}
