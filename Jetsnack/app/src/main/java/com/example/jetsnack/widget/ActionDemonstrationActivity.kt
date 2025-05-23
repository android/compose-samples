/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.platform.ui.appwidgets.glance.layout

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.glance.action.ActionParameters

internal val ActionSourceMessageKey = ActionParameters.Key<String>("actionSourceMessageKey")

/**
 * Activity that is launched on clicks from different parts of sample widgets. Displays string
 * describing source of the click.
 */
class ActionDemonstrationActivity : ComponentActivity() {

  override fun onResume() {
    super.onResume()
    setContent {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        val source = intent.getStringExtra(ActionSourceMessageKey.name) ?: "Unknown"
        Text("Launched from $source")
      }
    }
  }
}