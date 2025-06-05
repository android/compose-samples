/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.jetsnack.widget.layout

import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import com.example.jetsnack.R
import com.example.jetsnack.widget.utils.ActionUtils

/**
 * Content to be displayed when there are no items in the list. To be displayed below the
 * app-specific title bar in the [androidx.glance.appwidget.components.Scaffold] .
 */
@Composable
internal fun EmptyListContent() {
    val context = LocalContext.current

    NoDataContent(
        noDataText = context.getString(R.string.sample_no_data_text),
        noDataIconRes = R.drawable.cupcake,
        actionButtonText = context.getString(R.string.sample_add_button_text),
        actionButtonIcon = R.drawable.cupcake,
        actionButtonOnClick = ActionUtils.actionStartDemoActivity("on-click of add item button"),
    )
}
