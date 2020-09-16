/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally.ui.overview

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Card
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.RallyScreen
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.RallyAlertDialog
import com.example.compose.rally.ui.components.RallyDivider
import com.example.compose.rally.ui.components.formatAmount

@Composable
fun OverviewBody(onScreenChange: (RallyScreen) -> Unit = {}) {
    ScrollableColumn(contentPadding = PaddingValues(16.dp)) {
        AlertCard()
        Spacer(Modifier.preferredHeight(RallyDefaultPadding))
        AccountsCard(onScreenChange)
        Spacer(Modifier.preferredHeight(RallyDefaultPadding))
        BillsCard(onScreenChange)
    }
}

/**
 * The Alerts card within the Rally Overview screen.
 */
@Composable
private fun AlertCard() {
    var showDialog by remember { mutableStateOf(false) }
    val alertMessage = "Heads up, you've used up 90% of your Shopping budget for this month."

    if (showDialog) {
        RallyAlertDialog(
            onDismiss = {
                showDialog = false
            },
            bodyText = alertMessage,
            buttonText = "Dismiss".toUpperCase()
        )
    }
    Card {
        Column {
            AlertHeader {
                showDialog = true
            }
            RallyDivider(
                modifier = Modifier.padding(start = RallyDefaultPadding, end = RallyDefaultPadding)
            )
            AlertItem(alertMessage)
        }
    }
}

@Composable
private fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.padding(RallyDefaultPadding).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                text = "Alerts",
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        TextButton(
            onClick = onClickSeeAll,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "SEE ALL",
                style = MaterialTheme.typography.button,
            )
        }
    }
}

@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier.padding(RallyDefaultPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1f),
                text = message
            )
            IconButton(
                onClick = {},
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(Icons.Filled.Sort)
            }
        }
    }
}

/**
 * Base structure for cards in the Overview screen.
 */
@Composable
private fun <T> OverviewScreenCard(
    title: String,
    amount: Float,
    onClickSeeAll: () -> Unit,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "$" + formatAmount(
                    amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .preferredHeight(1.dp)
                    .background(colors(item))
            )
        }
    }
}
/**
 * The Accounts card within the Rally Overview screen.
 */
@Composable
private fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.accounts.map { account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Accounts)
        },
        data = UserData.accounts,
        colors = { it.color },
        values = { it.balance }
    ) { account ->
        AccountRow(
            name = account.name,
            number = account.number,
            amount = account.balance,
            color = account.color
        )
    }
}
/**
 * The Bills card within the Rally Overview screen.
 */
@Composable
private fun BillsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.bills.map { bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Bills)
        },
        data = UserData.bills,
        colors = { it.color },
        values = { it.amount }
    ) { bill ->
        BillRow(
            name = bill.name,
            due = bill.due,
            amount = bill.amount,
            color = bill.color
        )
    }
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.preferredHeight(44.dp).fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

private val RallyDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3
