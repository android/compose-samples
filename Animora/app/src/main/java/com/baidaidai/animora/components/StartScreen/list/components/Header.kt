package com.baidaidai.animora.components.StartScreen.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import okhttp3.internal.http2.Header

@Composable
fun Header(
    content: String
){
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .height(30.dp)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.secondaryContainer,
            thickness = 30.dp,
            modifier = Modifier
                .fillMaxSize()
        )
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = content,
            modifier = Modifier
                .padding(start = 20.dp)
        )
    }
}