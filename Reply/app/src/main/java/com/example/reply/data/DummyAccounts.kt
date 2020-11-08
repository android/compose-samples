package com.example.reply.data

import com.example.reply.R
import com.example.reply.data.Account

object DummyAccounts {

    val allUserAccounts = mutableListOf(
        Account(
            1,
            "Samsruti",
            "Dash",
            "sam@gmail.com",
            R.drawable.ic_android_black_24dp,
            false
        ),
        Account(
            2,
            "John",
            "Doe",
            "john@gmail.com",
            R.drawable.ic_android_black_24dp,
            false
        ),
        Account(
            3,
            "Samsruti",
            "Dash",
            "abcde@gmail.com",
            R.drawable.ic_android_black_24dp,
            true
        ),
        Account(
            4,
            "Samsruti",
            "Dash",
            "doe@gmail.com",
            R.drawable.ic_android_black_24dp,
            false
        )

    )
}