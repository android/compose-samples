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

package com.example.owl.model

import androidx.compose.runtime.Immutable

@Immutable // Tell Compose runtime that this object will not change so it can perform optimizations
data class Course(
    val id: Long,
    val name: String,
    val subject: String,
    val thumbUrl: String,
    val thumbContentDesc: String,
    val description: String = "",
    val steps: Int,
    val step: Int,
    val instructor: String = "https://i.pravatar.cc/112?$id"
)

/**
 * A fake repo
 */
object CourseRepo {
    fun getCourse(courseId: Long): Course = courses.find { it.id == courseId }!!
    fun getRelated(@Suppress("UNUSED_PARAMETER") courseId: Long): List<Course> = courses
}

val courses = listOf(
    Course(
        id = 0,
        name = "Basic Blocks and Woodturning",
        subject = "Arts & Crafts",
        thumbUrl = "https://source.unsplash.com/oJ7SV6vQfBA",
        thumbContentDesc = "",
        steps = 7,
        step = 1
    ),
    Course(
        id = 1,
        name = "An Introduction To Oil Painting On Canvas",
        subject = "Painting",
        thumbUrl = "https://source.unsplash.com/W9_sznrBmoA",
        thumbContentDesc = "",
        steps = 12,
        step = 1
    ),
    Course(
        id = 2,
        name = "Understanding the Composition of Modern Cities",
        subject = "Architecture",
        thumbUrl = "https://source.unsplash.com/s4I1xpX_ny8",
        thumbContentDesc = "",
        steps = 18,
        step = 1
    ),
    Course(
        id = 3,
        name = "Learning The Basics of Brand Identity",
        subject = "Design",
        thumbUrl = "https://source.unsplash.com/G9_Euqxpu4k",
        thumbContentDesc = "",
        steps = 22,
        step = 1
    ),
    Course(
        id = 4,
        name = "Wooden Materials and Sculpting Machinery",
        subject = "Arts & Crafts",
        thumbUrl = "https://source.unsplash.com/o54RjF-C7xo",
        thumbContentDesc = "",
        steps = 19,
        step = 1
    ),
    Course(
        id = 5,
        name = "Advanced Potter's Wheel",
        subject = "Arts & Crafts",
        thumbUrl = "https://source.unsplash.com/-LHvba-FgAo",
        thumbContentDesc = "",
        steps = 14,
        step = 1
    ),
    Course(
        id = 6,
        name = "Advanced Abstract Shapes & 3D Printing",
        subject = "Arts & Crafts",
        thumbUrl = "https://source.unsplash.com/HQkz_lWT_lY",
        thumbContentDesc = "",
        steps = 17,
        step = 1
    ),
    Course(
        id = 7,
        name = "Beginning Portraiture",
        subject = "Photography",
        thumbUrl = "https://source.unsplash.com/LE0Hp8l9gvs",
        thumbContentDesc = "",
        steps = 22,
        step = 1
    ),
    Course(
        id = 8,
        name = "Intermediate Knife Skills",
        subject = "Culinary",
        thumbUrl = "https://source.unsplash.com/f1xj_KeZ5RM",
        thumbContentDesc = "",
        steps = 14,
        step = 1
    ),
    Course(
        id = 9,
        name = "Pattern Making for Beginners",
        subject = "Fashion",
        thumbUrl = "https://source.unsplash.com/hew8-OoUriU",
        thumbContentDesc = "",
        steps = 7,
        step = 1
    ),
    Course(
        id = 10,
        name = "Location Lighting for Beginners",
        subject = "Photography",
        thumbUrl = "https://source.unsplash.com/pPxJTtxfV1A",
        thumbContentDesc = "",
        steps = 6,
        step = 1
    ),
    Course(
        id = 11,
        name = "Cinematography & Lighting",
        subject = "Film",
        thumbUrl = "https://source.unsplash.com/oIf4VCDztZY",
        thumbContentDesc = "",
        steps = 4,
        step = 1
    ),
    Course(
        id = 12,
        name = "Monuments, Buildings & Other Structures",
        subject = "Photography",
        thumbUrl = "https://source.unsplash.com/KxCJXXGsv9I",
        thumbContentDesc = "",
        steps = 4,
        step = 1
    )
)
