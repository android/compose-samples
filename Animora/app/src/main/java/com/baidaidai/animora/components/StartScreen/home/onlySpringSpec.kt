package com.baidaidai.animora.components.StartScreen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A composable that displays a clickable card to navigate to the "SpringSpec" feature.
 *
 * This card serves as a navigation entry point to a specific feature,
 * indicated as "Only For SpringSpec". It includes an icon and text, and a forward arrow
 * to suggest navigation.
 *
 * @param onClick A lambda function to be invoked when the card is clicked.
 */
@Composable
fun onlySpringSpce(
    onClick: ()-> Unit
){
    Card(
        enabled = true,
        onClick = onClick,
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = MaterialTheme.colorScheme.onError
        ),
        modifier = Modifier
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Icon(Icons.Outlined.QuestionMark, contentDescription = "Question LOGO")
                    Text(
                        text = "Only For SpringSpec? (New)",
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
                Icon(Icons.Outlined.ArrowForwardIos, contentDescription = "Go SpringSpec")
            }
        }
    }
}