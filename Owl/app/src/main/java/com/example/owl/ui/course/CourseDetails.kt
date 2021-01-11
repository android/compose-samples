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

package com.example.owl.ui.course

import androidx.compose.animation.core.animateAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.CourseRepo
import com.example.owl.model.Lesson
import com.example.owl.model.LessonsRepo
import com.example.owl.model.courses
import com.example.owl.ui.common.CourseListItem
import com.example.owl.ui.common.OutlinedAvatar
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.PinkTheme
import com.example.owl.ui.theme.pink500
import com.example.owl.ui.utils.NetworkImage
import com.example.owl.ui.utils.backHandler
import com.example.owl.ui.utils.lerp
import com.example.owl.ui.utils.scrim
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues

private val FabSize = 56.dp
private const val ExpandedSheetAlpha = 0.96f

@Composable
fun CourseDetails(
    courseId: Long,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    // Simplified for the sample
    val course = remember(courseId) { CourseRepo.getCourse(courseId) }
    // TODO: Show error if course not found.
    CourseDetails(course, selectCourse, upPress)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseDetails(
    course: Course,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    PinkTheme {
        WithConstraints {
            val sheetState = rememberSwipeableState(SheetState.Closed)
            val fabSize = with(AmbientDensity.current) { FabSize.toPx() }
            val dragRange = constraints.maxHeight - fabSize

            backHandler(
                enabled = sheetState.value == SheetState.Open,
                onBack = { sheetState.animateTo(SheetState.Closed) }
            )

            Box(
                // The Lessons sheet is initially closed and appears as a FAB. Make it openable by
                // swiping or clicking the FAB.
                Modifier.swipeable(
                    state = sheetState,
                    anchors = mapOf(
                        0f to SheetState.Closed,
                        -dragRange to SheetState.Open
                    ),
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Vertical
                )
            ) {
                val openFraction = if (sheetState.offset.value.isNaN()) {
                    0f
                } else {
                    -sheetState.offset.value / dragRange
                }.coerceIn(0f, 1f)
                CourseDescription(course, selectCourse, upPress)
                LessonsSheet(
                    course,
                    openFraction,
                    constraints.maxWidth.toFloat(),
                    constraints.maxHeight.toFloat()
                ) { state ->
                    sheetState.animateTo(state)
                }
            }
        }
    }
}

@Composable
private fun CourseDescription(
    course: Course,
    selectCourse: (Long) -> Unit,
    upPress: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        ScrollableColumn {
            CourseDescriptionHeader(course, upPress)
            CourseDescriptionBody(course)
            RelatedCourses(course.id, selectCourse)
        }
    }
}

@Composable
private fun CourseDescriptionHeader(
    course: Course,
    upPress: () -> Unit
) {
    Box {
        NetworkImage(
            url = course.thumbUrl,
            modifier = Modifier
                .fillMaxWidth()
                .scrim(colors = listOf(Color(0x80000000), Color(0x33000000)))
                .aspectRatio(4f / 3f)
        )
        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentColor = Color.White, // always white as image has dark scrim
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = upPress) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack
                )
            }
            Image(
                imageVector = vectorResource(id = R.drawable.ic_logo),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .preferredSize(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        OutlinedAvatar(
            url = course.instructor,
            modifier = Modifier
                .preferredSize(40.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp) // overlap bottom of image
        )
    }
}

@Composable
private fun CourseDescriptionBody(course: Course) {
    Text(
        text = course.subject.toUpperCase(),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 36.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    )
    Text(
        text = course.name,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.preferredHeight(16.dp))
    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = R.string.course_desc),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
    Divider(modifier = Modifier.padding(16.dp))
    Text(
        text = stringResource(id = R.string.what_you_ll_need),
        style = MaterialTheme.typography.h6,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = R.string.needs),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                )
        )
    }
}

@Composable
private fun RelatedCourses(
    courseId: Long,
    selectCourse: (Long) -> Unit
) {
    val relatedCourses = remember(courseId) { CourseRepo.getRelated(courseId) }
    BlueTheme {
        Surface(
            color = MaterialTheme.colors.primarySurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                Text(
                    text = stringResource(id = R.string.you_ll_also_like),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 24.dp
                        )
                )
                LazyRow(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        bottom = 32.dp,
                        end = FabSize + 8.dp
                    )
                ) {
                    items(relatedCourses) { related ->
                        CourseListItem(
                            course = related,
                            onClick = { selectCourse(related.id) },
                            titleStyle = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .preferredSize(288.dp, 80.dp),
                            iconSize = 14.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonsSheet(
    course: Course,
    openFraction: Float,
    width: Float,
    height: Float,
    updateSheet: (SheetState) -> Unit
) {
    // Use the fraction that the sheet is open to drive the transformation from FAB -> Sheet
    val fabSize = with(AmbientDensity.current) { FabSize.toPx() }
    val fabSheetHeight = fabSize + AmbientWindowInsets.current.systemBars.bottom
    val offsetX = lerp(width - fabSize, 0f, 0f, 0.15f, openFraction)
    val offsetY = lerp(height - fabSheetHeight, 0f, openFraction)
    val tlCorner = lerp(fabSize, 0f, 0f, 0.15f, openFraction)
    val surfaceColor = lerp(
        startColor = pink500,
        endColor = MaterialTheme.colors.primarySurface.copy(alpha = ExpandedSheetAlpha),
        startFraction = 0f,
        endFraction = 0.3f,
        fraction = openFraction
    )
    Surface(
        color = surfaceColor,
        contentColor = contentColorFor(color = MaterialTheme.colors.primarySurface),
        shape = RoundedCornerShape(topLeft = tlCorner),
        modifier = Modifier.graphicsLayer {
            translationX = offsetX
            translationY = offsetY
        }
    ) {
        Lessons(course, openFraction, surfaceColor, updateSheet)
    }
}

@Composable
private fun Lessons(
    course: Course,
    openFraction: Float,
    surfaceColor: Color = MaterialTheme.colors.surface,
    updateSheet: (SheetState) -> Unit
) {
    val lessons: List<Lesson> = remember(course.id) { LessonsRepo.getLessons(course.id) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // When sheet open, show a list of the lessons
        val lessonsAlpha = lerp(0f, 1f, 0.2f, 0.8f, openFraction)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = lessonsAlpha }
                .statusBarsPadding()
        ) {
            val scroll = rememberScrollState()
            val appBarElevation by animateAsState(if (scroll.value > 0f) 4.dp else 0.dp)
            val appBarColor = if (appBarElevation > 0.dp) surfaceColor else Color.Transparent
            TopAppBar(
                backgroundColor = appBarColor,
                elevation = appBarElevation
            ) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                IconButton(
                    onClick = { updateSheet(SheetState.Closed) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Rounded.ExpandMore)
                }
            }
            ScrollableColumn(
                scrollState = scroll,
                contentPadding = AmbientWindowInsets.current.systemBars.toPaddingValues(
                    top = false
                )
            ) {
                lessons.forEach { lesson ->
                    Lesson(lesson)
                    Divider(startIndent = 128.dp)
                }
            }
        }

        // When sheet closed, show the FAB
        val fabAlpha = lerp(1f, 0f, 0f, 0.15f, openFraction)
        Box(
            modifier = Modifier
                .preferredSize(FabSize)
                .padding(start = 16.dp, top = 8.dp) // visually center contents
                .graphicsLayer { alpha = fabAlpha }
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = { updateSheet(SheetState.Open) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlaylistPlay,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
private fun Lesson(lesson: Lesson) {
    Row(
        modifier = Modifier
            .clickable(onClick = { /* todo */ })
            .padding(vertical = 16.dp)
    ) {
        NetworkImage(
            url = lesson.imageUrl,
            modifier = Modifier.preferredSize(112.dp, 64.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircleOutline,
                        modifier = Modifier.preferredSize(16.dp)
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = lesson.length,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
        Text(
            text = lesson.formattedStepNumber,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

private enum class SheetState { Open, Closed }

@Preview(name = "Course Details")
@Composable
private fun CourseDetailsPreview() {
    val courseId = courses.first().id
    CourseDetails(
        courseId = courseId,
        selectCourse = { },
        upPress = { }
    )
}

@Preview(name = "Lessons Sheet — Closed")
@Composable
private fun LessonsSheetClosedPreview() {
    LessonsSheetPreview(0f)
}

@Preview(name = "Lessons Sheet — Open")
@Composable
private fun LessonsSheetOpenPreview() {
    LessonsSheetPreview(1f)
}

@Preview(name = "Lessons Sheet — Open – Dark")
@Composable
private fun LessonsSheetOpenDarkPreview() {
    LessonsSheetPreview(1f, true)
}

@Composable
private fun LessonsSheetPreview(
    openFraction: Float,
    darkTheme: Boolean = false
) {
    PinkTheme(darkTheme) {
        val color = MaterialTheme.colors.primarySurface
        Surface(color = color) {
            Lessons(
                course = courses.first(),
                openFraction = openFraction,
                surfaceColor = color,
                updateSheet = { }
            )
        }
    }
}

@Preview(name = "Related")
@Composable
private fun RelatedCoursesPreview() {
    val related = courses.random()
    RelatedCourses(
        courseId = related.id,
        selectCourse = { }
    )
}
