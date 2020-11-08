package com.example.reply.data

import androidx.annotation.DrawableRes

data class Account(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    @DrawableRes val avatar: Int,
    var isAccountChecked: Boolean = false
) {
    val fullName: String = "$firstName $lastName"
}