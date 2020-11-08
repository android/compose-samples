package com.example.reply.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.reply.data.Email
import java.lang.StringBuilder


@Composable
fun DetailsScreen(
    email: Email,
    modifier: Modifier,
    navigationAction: () -> Unit
) {
    ScrollableColumn(modifier = modifier.padding(16.dp)) {
        EmailDetailsHeader(
            details = email,
            navigationAction = navigationAction
        )
        Text(text = email.body, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun EmailDetailsHeader(details: Email, navigationAction: () -> Unit) {
    Column {
        Text(
            text = details.subject,
            style = MaterialTheme.typography.h3
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = details.emailHeader,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2
                )
                val recipientTitle = StringBuilder().append("To <")
                details.recipients.forEach {
                    recipientTitle.append(it.fullName)
                    recipientTitle.append(", ")
                }
                recipientTitle.append(">")
                Text(
                    text = recipientTitle.toString(),
                    style = MaterialTheme.typography.body2,
                    maxLines = 2
                )
                TextButton(onClick = { navigationAction() }) {
                    Text(text = "Back")
                }
            }
            generateUserAvatarImageView(details, CircleShape)
        }
    }

}

@Composable
private fun generateUserAvatarImageView(
    details: Email,
    CircleShape: RoundedCornerShape
) {
    val avatarImage = vectorResource(id = details.sender.avatar)
    val imageModifier = Modifier
        .preferredSize(40.dp)
        .clip(CircleShape)
        .clickable(onClick = {})
    Image(
        asset = avatarImage,
        modifier = imageModifier,
        contentScale = ContentScale.Crop,
    )
}


