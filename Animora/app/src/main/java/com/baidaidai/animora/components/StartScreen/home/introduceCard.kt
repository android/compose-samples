package com.baidaidai.animora.components.StartScreen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.animora.R

/**
 * A composable that displays an introduction card for the author.
 *
 * This card contains the author's avatar, name, GitHub handle, and a short note.
 * It is styled using Material Design components.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun introduceCard(){
    Card(
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onError
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row {
                Image(
                    painter = painterResource(R.drawable.createrbai),
                    contentDescription = "Avater",
                    modifier = Modifier
                        .size(100.dp)
                )
                Column(
                    modifier = Modifier
                        .height(100.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Author: Creater. Bai",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text= "Github: @Baidaidai-GFWD-origin",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            )
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            ){
                Text(
                    text = stringResource(R.string.creater_note),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}