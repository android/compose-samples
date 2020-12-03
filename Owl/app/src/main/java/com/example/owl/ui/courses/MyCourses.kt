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

package com.example.owl.ui.courses

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.common.CourseListItem
import com.example.owl.ui.theme.BlueTheme
import dev.chrisbanes.accompanist.insets.statusBarsHeight

@Composable
fun MyCourses(
    courses: List<Course>,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            Spacer(Modifier.statusBarsHeight())
        }
        item {
            CoursesAppBar()
        }
        itemsIndexed(courses) { index, course ->
            MyCourse(course, index, selectCourse)
        }
    }
}

@Composable
fun MyCourse(
    course: Course,
    index: Int,
    selectCourse: (Long) -> Unit
) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        val stagger = if (index % 2 == 0) 72.dp else 16.dp
        Spacer(modifier = Modifier.preferredWidth(stagger))
        CourseListItem(
            course = course,
            onClick = { selectCourse(course.id) },
            shape = RoundedCornerShape(topLeft = 24.dp),
            modifier = Modifier.preferredHeight(96.dp)
        )
    }
}

@Preview(name = "My Courses")
@Composable
private fun MyCoursesPreview() {
    BlueTheme {
        MyCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}
