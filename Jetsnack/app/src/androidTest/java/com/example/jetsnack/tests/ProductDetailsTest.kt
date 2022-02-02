package com.example.jetsnack.tests

import android.view.KeyEvent
import androidx.compose.ui.test.*
import com.example.jetsnack.R
import com.example.jetsnack.model.*
import com.example.jetsnack.screens.NavigationBar
import com.example.jetsnack.screens.ProductDetails
import com.example.jetsnack.util.BaseUITest
import com.example.jetsnack.util.Constants
import org.junit.Test

/**
 * Created by André Schabrocker on 2022-01-31
 */
class ProductDetailsTest : BaseUITest() {

    @Test fun checkProductDetailsPage() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        ProductDetails.checkProductDetails(composeTestRule)
    }
    @Test fun increaseQuantityAndAddToCart() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        ProductDetails.changeQuantityOfItem(ProductDetails.Quantity.Increase, composeTestRule)
    }

    @Test fun decreaseQuantity() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        ProductDetails.changeQuantityOfItem(ProductDetails.Quantity.Decrease, composeTestRule)
    }

    @Test fun openProductDetailsViaCartTab() {
        NavigationBar.navigateToTab(Constants.NavigationBar.CartTab.id, composeTestRule)
        ProductDetails.openProductDetails(ProductDetails.EntryPoint.Cart, composeTestRule, device)
    }

    @Test fun openProductDetailsViaHomeTab() {
        NavigationBar.navigateToTab(Constants.NavigationBar.HomeTab.id, composeTestRule)
        ProductDetails.openProductDetails(ProductDetails.EntryPoint.Home, composeTestRule, device)
    }

    @Test fun openProductDetailsViaSearchTab() {
        NavigationBar.navigateToTab(Constants.NavigationBar.SearchTab.id, composeTestRule)
        ProductDetails.openProductDetails(ProductDetails.EntryPoint.Search, composeTestRule, device)
    }
}