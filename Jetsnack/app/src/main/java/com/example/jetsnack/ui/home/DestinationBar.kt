package com.example.jetsnack.ui.home

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.statusBarsPadding

@Composable
fun DestinationBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.statusBarsPadding()) {
        TopAppBar(
            backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
            contentColor = JetsnackTheme.colors.textSecondary,
            elevation = 0.dp
        ) {
            Text(
                text = "Delivery to 1600 Amphitheater Way",
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    asset = Icons.Outlined.ExpandMore,
                    tint = JetsnackTheme.colors.brand
                )
            }
        }
        JetsnackDivider()
    }
}