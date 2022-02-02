package com.example.jetsnack.util

/**
 * Created by André Schabrocker on 2022-02-01
 */
object Constants {

    sealed class NavigationBar(val id: String) {
        object HomeTab : NavigationBar("HOME")
        object SearchTab : NavigationBar("SEARCH")
        object CartTab : NavigationBar("MY CART")
    }
    sealed class ProductQuantity(val id: String) {
        object Zero : ProductQuantity("0")
        object One : ProductQuantity("1")
        object Two : ProductQuantity("2")
        object Three : ProductQuantity("3")
    }
}