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

package androidx.compose.samples.crane.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.CraneScaffold
import androidx.compose.samples.crane.base.ServiceLocator
import androidx.compose.samples.crane.calendar.model.CalendarDay
import androidx.compose.samples.crane.calendar.model.CalendarMonth
import androidx.compose.samples.crane.calendar.model.DaySelected
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource

fun launchCalendarActivity(context: Context) {
    val intent = Intent(context, CalendarActivity::class.java)
    context.startActivity(intent)
}

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CraneScaffold {
                Surface {
                    CalendarScreen(onBackPressed = { finish() })
                }
            }
        }
    }
}

// Extracted out to a separate variable. If this lambda is used as a trailing lambda in the
// Calendar function, it recomposes the whole Calendar view when clicked on it.
private val onDayClicked: (CalendarDay, CalendarMonth) -> Unit = { calendarDay, calendarMonth ->
    ServiceLocator.datesSelected.daySelected(
        DaySelected(
            day = calendarDay.value.toInt(),
            month = calendarMonth
        )
    )
}

@Composable
fun CalendarScreen(onBackPressed: () -> Unit) {
    CraneScaffold {
        Column {
            val selectedDatesText = ServiceLocator.datesSelected.toString()
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedDatesText.isEmpty()) "Select Dates"
                        else selectedDatesText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Image(asset = vectorResource(id = R.drawable.ic_back))
                    }
                },
                backgroundColor = MaterialTheme.colors.primaryVariant
            )
            Surface {
                Calendar(onDayClicked = onDayClicked)
            }
        }
    }
}
