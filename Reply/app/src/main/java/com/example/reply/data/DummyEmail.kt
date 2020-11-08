package com.example.reply.data

import com.example.reply.data.DummyAccounts
import com.example.reply.data.Email

object DummyEmail {

    private val allEmails = mutableListOf(
        Email(
            id = 0,
            sender = DummyAccounts.allUserAccounts[0],
            subject = "Package shipped!",
            body = """Cucumber Mask Facial has shipped. Keep an eye out for a package to arrive between this Thursday and next Tuesday. If for any reason you don't receive your package before the end of next week, please reach out to us for details on your shipment.
        As always, thank you for shopping with us and we hope you love our specially formulated Cucumber Mask!
    """.trimIndent(),
            timeStamp = "1 hrs ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[1],
                DummyAccounts.allUserAccounts[2],
            )
        ),
        Email(
            id = 1,
            sender = DummyAccounts.allUserAccounts[1],
            subject = "Brunch this weekend?",
            body = """
I'll be in your neighborhood doing errands and was hoping to catch you for a coffee this Saturday. If you don't have anything scheduled, it would be great to see you! It feels like its been forever.

If we do get a chance to get together, remind me to tell you about Kim. She stopped over at the house to say hey to the kids and told me all about her trip to Mexico.
            
                            Talk to you soon,
            
                            Ali
                        """.trimIndent(),
            timeStamp = "4 days ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[0],
                DummyAccounts.allUserAccounts[2],
            )
        ),

        Email(
            id = 2,
            sender = DummyAccounts.allUserAccounts[0],
            subject = "Update to Your Itinerary",
            body = "",
            timeStamp = "8 hrs ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[1]
            )
        ),
        Email(
            id = 3,
            sender = DummyAccounts.allUserAccounts[2],
            "Delivered",
            "Your shoes should be waiting for you at home!",
            timeStamp = "16 hrs ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[0],
            )
        ),
        Email(
            id = 4,
            sender = DummyAccounts.allUserAccounts[1],
            "Delivered",
            "Your books should be waiting for you at home!",
            timeStamp = "20 hrs ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[0],
            )

        ),
        Email(
            id = 5,
            sender = DummyAccounts.allUserAccounts[3],
            subject = "Update to Your Notebook",
            body = "",
            timeStamp = "1 day ago",
            recipients = listOf(
                DummyAccounts.allUserAccounts[0],
            )
        )
    )

    fun getEmail(id: Long): Email = allEmails.first { email -> email.id == id }
    fun getAllEmails() = allEmails
}



