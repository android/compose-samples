package com.rs.crypto.models

data class TrendingCurrency(
    val id: Int,
    val currencyName: String,
    val currencyCode: String,
    val imageRes: Int,
    val currentPrice: Float,
    val changes: Float,
    val changeType: String,
    val description: String,
    val chartData: List<Pair<Float, Float>>,
    val wallet: Wallet,
    val transactionHistory: List<Transaction>,
)
