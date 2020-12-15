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

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.AmbientElevationOverlay
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.common.OutlinedAvatar
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.OwlTheme
import com.example.owl.ui.utils.NetworkImage
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import kotlin.math.ceil

@Composable
fun FeaturedCourses(
    courses: List<Course>,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableColumn(modifier = modifier.statusBarsPadding()) {
        CoursesAppBar()
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            courses.forEach { course ->
                FeaturedCourse(course, selectCourse)
            }
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun FeaturedCourse(
    course: Course,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(4.dp),
        color = MaterialTheme.colors.surface,
        elevation = OwlTheme.elevations.card,
        shape = MaterialTheme.shapes.medium
    ) {
        val featuredString = stringResource(id = R.string.featured)
        ConstraintLayout(
            modifier = Modifier
                .clickable(
                    onClick = { selectCourse(course.id) }
                )
                .semantics {
                    contentDescription = featuredString
                }
        ) {
            val (image, avatar, subject, name, steps, icon) = createRefs()
            NetworkImage(
                url = course.thumbUrl,
                modifier = Modifier
                    .aspectRatio(4f / 3f)
                    .constrainAs(image) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                    }
            )
            val outlineColor = AmbientElevationOverlay.current?.apply(
                color = MaterialTheme.colors.surface,
                elevation = OwlTheme.elevations.card
            ) ?: MaterialTheme.colors.surface
            OutlinedAvatar(
                url = course.instructor,
                outlineColor = outlineColor,
                modifier = Modifier
                    .preferredSize(38.dp)
                    .constrainAs(avatar) {
                        centerHorizontallyTo(parent)
                        centerAround(image.bottom)
                    }
            )
            Text(
                text = course.subject.toUpperCase(),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.overline,
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(subject) {
                        centerHorizontallyTo(parent)
                        top.linkTo(avatar.bottom)
                    }
            )
            Text(
                text = course.name,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .constrainAs(name) {
                        centerHorizontallyTo(parent)
                        top.linkTo(subject.bottom)
                    }
            )
            val center = createGuidelineFromStart(0.5f)
            Icon(
                imageVector = Icons.Rounded.OndemandVideo,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .preferredSize(16.dp)
                    .constrainAs(icon) {
                        end.linkTo(center)
                        centerVerticallyTo(steps)
                    }
            )
            Text(
                text = course.steps.toString(),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier
                    .padding(
                        start = 4.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .constrainAs(steps) {
                        start.linkTo(center)
                        top.linkTo(name.bottom)
                    }
            )
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}

@Preview(name = "Featured Course")
@Composable
private fun FeaturedCoursePreview() {
    BlueTheme {
        FeaturedCourse(
            course = courses.first(),
            selectCourse = { }
        )
    }
}

@Preview(name = "Featured Courses Portrait")
@Composable
private fun FeaturedCoursesPreview() {
    BlueTheme {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}

@Preview(name = "Featured Courses Dark")
@Composable
private fun FeaturedCoursesPreviewDark() {
    BlueTheme(darkTheme = true) {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}

@Preview(
    name = "Featured Courses Landscape",
    widthDp = 640,
    heightDp = 360
)
@Composable
private fun FeaturedCoursesPreviewLandscape() {
    BlueTheme {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}
