package com.rs.crypto.composables.trade_components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rs.crypto.composables.prices_components.CurrencyInfoSection
import com.rs.crypto.ui.theme.Gray
import com.rs.crypto.ui.theme.RobotoRegular
import com.rs.crypto.utils.Constants
import com.rs.crypto.utils.DummyData


@Composable
fun TransactSection() {
    TradeTextField(
        label = "From",
        modifier = Modifier
            .fillMaxWidth()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CurrencySelectorDropDown(
            selectedIndex = 0
        )

        CurrencySelectorDropDown(
            selectedIndex = 1
        )
    }

    TradeTextField(
        label = "To",
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
private fun CurrencySelectorDropDown(
    selectedIndex: Int
) {
    var expandedState by remember {
        mutableStateOf(false)
    }

    var dropDownSelectedIndex by remember {
        mutableStateOf(selectedIndex)
    }

    Box {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Gray,
                    shape = RoundedCornerShape((Constants.ELEVATION_VALUE / 2).dp)
                )
                .width(150.dp)
                .clickable {
                    expandedState = true
                }
        ) {
            Row(
                modifier = Modifier
                    .padding((Constants.ELEVATION_VALUE / 2).dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CurrencyInfoSection(
                    currency = DummyData.trendingCurrencies[dropDownSelectedIndex]
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Gray
                )
            }
        }

        DropdownMenu(
            expanded = expandedState,
            onDismissRequest = {
                expandedState = false
            },
            modifier = Modifier
                .width(150.dp)
        ) {
            DummyData.trendingCurrencies.forEachIndexed { index, currency ->
                DropdownMenuItem(onClick = {
                    dropDownSelectedIndex = index
                    expandedState = false
                }) {
                    CurrencyInfoSection(currency = currency)
                }
            }
        }
    }
}

@Composable
private fun TradeTextField(
    label: String,
    modifier: Modifier = Modifier
) {
    var textFieldState by remember {
        mutableStateOf("0")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedText by remember {
        mutableStateOf("")
    }

    var textfieldSize by remember {
        mutableStateOf(Size.Zero)
    }


    OutlinedTextField(
        value = textFieldState,
        onValueChange = {
            textFieldState = it
        },
        modifier = modifier
            .padding(Constants.PADDING_SIDE_VALUE.dp),
        label = {
            Text(text = label)
        },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        maxLines = 1,
        textStyle = TextStyle(
            fontFamily = RobotoRegular,
            fontSize = 14.sp,
            lineHeight = 22.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}