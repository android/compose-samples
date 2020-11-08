package com.example.reply.data

data class Email(
    val id: Long,
    val sender: Account,
    val subject: String = "",
    val body: String = "",
    val timeStamp: String,
    val recipients: List<Account>
) {
    val emailHeader: String = "${sender.fullName} - $timeStamp"
    val hasBody: Boolean = body.isNotBlank()
}
