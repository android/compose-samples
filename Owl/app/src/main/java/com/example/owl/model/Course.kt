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
        thumbUrl = "https://images.unsplash.com/photo-1516562309708-05f3b2b2c238",
        thumbContentDesc = "",
        steps = 7,
        step = 1
    ),
    Course(
        id = 1,
        name = "An Introduction To Oil Painting On Canvas",
        subject = "Painting",
        thumbUrl = "https://images.unsplash.com/photo-1508261301902-79a2d8e78f71",
        thumbContentDesc = "",
        steps = 12,
        step = 1
    ),
    Course(
        id = 2,
        name = "Understanding the Composition of Modern Cities",
        subject = "Architecture",
        thumbUrl = "https://images.unsplash.com/photo-1519999482648-25049ddd37b1",
        thumbContentDesc = "",
        steps = 18,
        step = 1
    ),
    Course(
        id = 3,
        name = "Learning The Basics of Brand Identity",
        subject = "Design",
        thumbUrl = "https://images.unsplash.com/photo-1517602302552-471fe67acf66",
        thumbContentDesc = "",
        steps = 22,
        step = 1
    ),
    Course(
        id = 4,
        name = "Wooden Materials and Sculpting Machinery",
        subject = "Arts & Crafts",
        thumbUrl = "https://images.unsplash.com/photo-1547609434-b732edfee020",
        thumbContentDesc = "",
        steps = 19,
        step = 1
    ),
    Course(
        id = 5,
        name = "Advanced Potter's Wheel",
        subject = "Arts & Crafts",
        thumbUrl = "https://images.unsplash.com/photo-1513096082106-f68f05c8c21c",
        thumbContentDesc = "",
        steps = 14,
        step = 1
    ),
    Course(
        id = 6,
        name = "Advanced Abstract Shapes & 3D Printing",
        subject = "Arts & Crafts",
        thumbUrl = "https://images.unsplash.com/photo-1461887046916-c7426e65460d",
        thumbContentDesc = "",
        steps = 17,
        step = 1
    ),
    Course(
        id = 7,
        name = "Beginning Portraiture",
        subject = "Photography",
        thumbUrl = "https://images.unsplash.com/photo-1555940451-2480c214446f",
        thumbContentDesc = "",
        steps = 22,
        step = 1
    ),
    Course(
        id = 8,
        name = "Intermediate Knife Skills",
        subject = "Culinary",
        thumbUrl = "https://images.unsplash.com/photo-1544965838-54ef8406f868",
        thumbContentDesc = "",
        steps = 14,
        step = 1
    ),
    Course(
        id = 9,
        name = "Pattern Making for Beginners",
        subject = "Fashion",
        thumbUrl = "https://images.unsplash.com/photo-1552737894-aae873ee2737",
        thumbContentDesc = "",
        steps = 7,
        step = 1
    ),
    Course(
        id = 10,
        name = "Location Lighting for Beginners",
        subject = "Photography",
        thumbUrl = "https://images.unsplash.com/photo-1554941829-202a0b2403b8",
        thumbContentDesc = "",
        steps = 6,
        step = 1
    ),
    Course(
        id = 11,
        name = "Cinematography & Lighting",
        subject = "Film",
        thumbUrl = "https://images.unsplash.com/photo-1517523267857-911eef21acae",
        thumbContentDesc = "",
        steps = 4,
        step = 1
    ),
    Course(
        id = 12,
        name = "Monuments, Buildings & Other Structures",
        subject = "Photography",
        thumbUrl = "https://images.unsplash.com/photo-1494145904049-0dca59b4bbad",
        thumbContentDesc = "",
        steps = 4,
        step = 1
    )
)
