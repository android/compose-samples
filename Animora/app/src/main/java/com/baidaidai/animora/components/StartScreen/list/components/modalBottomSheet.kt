package com.baidaidai.animora.components.StartScreen.list.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun modalBottomSheet(
    onDismissRequest:suspend ()-> Unit,
    modalBottomSheetState: SheetState, @StringRes
    bottomSheetContent: Int
){

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                onDismissRequest()
            }
        },
        sheetState = modalBottomSheetState,
//        modifier = Modifier
//            .blur(1.dp)
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minHeight = 200.dp)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(bottomSheetContent)
            )
        }

    }
}