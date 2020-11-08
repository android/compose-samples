package com.example.reply.home

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.reply.data.DummyEmail
import com.example.reply.data.Email
import com.example.reply.ui.EmailContainerPadding
import com.example.reply.ui.ReplyTheme


@ExperimentalMaterialApi
@ExperimentalLayout
@Preview
@Composable
fun previewEmailHeader() {
    ReplyTheme(darkTheme = true) {
        EmailFull(DummyEmail.getEmail(1)) {}
    }
}


@ExperimentalMaterialApi
@Composable
fun EmailScrollList(
    initialEmails: List<Email>,
    modifier: Modifier,
    navigationAction: (Long) -> Unit
) {
    ScrollableColumn(modifier = modifier) {

        initialEmails.forEach { email ->
            EmailFull(email = email) { navigationAction(email.id) }
        }

    }
}

@ExperimentalMaterialApi
@Composable
fun EmailFull(email: Email, onClickItem: () -> Unit) {
    val context = ContextAmbient.current.applicationContext

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickItem)
            .padding(
                top = 2.dp,
                start = 2.dp,
                end = 2.dp
            )
    ) {
        Column(modifier = Modifier.padding(EmailContainerPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EmailContentHeader(email, Modifier.weight(1f))
                Spacer(modifier = Modifier.padding(4.dp))
                val avatarImage = vectorResource(id = email.sender.avatar)
                val imageModifier = Modifier
                    .preferredSize(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = {
                        Toast.makeText(
                            context,
                            "${email.sender.fullName} profile image",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                Image(
                    asset = avatarImage,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )

            }
            if (email.hasBody) {
                Text(
                    text = email.body,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@Composable
private fun EmailContentHeader(email: Email, modifier: Modifier) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                text = email.emailHeader,
                style = MaterialTheme.typography.body2,
                maxLines = 2
            )
        }
        Text(
            text = email.subject,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

}

enum class EmailContentType {
    MESSAGE_WITH_IMAGES_ONLY,
    MESSAGE_SIMPLE,
    MESSAGE_WITH_ATTACHMENTS
}