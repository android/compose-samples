package com.example.reply.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email

@Composable
fun ReplyListOnlyContent(
    replyHomeUIState: ReplyHomeUIState,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(replyHomeUIState.emails) { email ->
            ReplyEmailListItem(email = email)
        }
    }
}

@Composable
fun ReplyListAndDetailContent(
    replyHomeUIState: ReplyHomeUIState,
    modifier: Modifier = Modifier,
    selectedItemIndex: Int = 0
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyColumn(modifier = modifier.weight(1f)) {
            items(replyHomeUIState.emails) { email ->
                ReplyEmailListItem(email = email)
            }
        }
        LazyColumn(modifier = modifier.weight(1f)) {
            items(replyHomeUIState.emails[selectedItemIndex].threads) { email ->
                ReplyEmailThreadItem(email = email)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyEmailListItem(
    email: Email,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
    Card(modifier = modifier,) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "20 mins ago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyEmailThreadItem(
    email: Email,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
    Card(modifier = modifier, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "20 mins ago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.reply_all),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
fun ReplyProfileImage(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier.size(40.dp),
) {
    Image(
        modifier = modifier.clip(CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = description,
    )
}