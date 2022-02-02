package com.example.jetsnack.tests

import com.example.jetsnack.screens.Cart
import com.example.jetsnack.screens.NavigationBar
import com.example.jetsnack.util.BaseUITest
import com.example.jetsnack.util.Constants
import org.junit.Test

/**
 * Created by André Schabrocker on 2022-01-31
 */
class CartTest : BaseUITest() {

    @Test fun checkCurrentCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        Cart.checkCart(composeTestRule)
    }

    @Test fun removeOneItemFromCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        Cart.removeItems(Cart.QuantityOfRemovedItems.SingleItem, composeTestRule)
    }

    @Test fun removeAllItemsFromCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        Cart.removeItems(Cart.QuantityOfRemovedItems.AllItems, composeTestRule)
    }

    @Test fun increaseQuantityOfItemFromCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        Cart.changeItemQuantity(Cart.ChangeItemQuantity.Increase, composeTestRule)
    }

    @Test fun decreaseQuantityOfItemFromCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        Cart.changeItemQuantity(Cart.ChangeItemQuantity.Decrease, composeTestRule)
    }
}
