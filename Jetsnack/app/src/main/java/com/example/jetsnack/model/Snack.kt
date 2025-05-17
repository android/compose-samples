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

package com.example.jetsnack.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.example.jetsnack.R
import kotlin.random.Random

@Immutable
data class Snack(
    val id: Long,
    val name: String,
    @DrawableRes
    val imageRes: Int,
    val price: Long,
    val tagline: String = "",
    val tags: Set<String> = emptySet(),
)

/**
 * Static data
 */

val snacks = listOf(
    Snack(
        id = 1L,
        name = "Cupcake",
        tagline = "A tag line",
        imageRes = R.drawable.cupcake,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Donut",
        tagline = "A tag line",
        imageRes = R.drawable.donut,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Eclair",
        tagline = "A tag line",
        imageRes = R.drawable.eclair,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Froyo",
        tagline = "A tag line",
        imageRes = R.drawable.froyo,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Gingerbread",
        tagline = "A tag line",
        imageRes = R.drawable.gingerbread,
        price = 499,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Honeycomb",
        tagline = "A tag line",
        imageRes = R.drawable.honeycomb,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Ice Cream Sandwich",
        tagline = "A tag line",
        imageRes = R.drawable.ice_cream_sandwich,
        price = 1299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Jellybean",
        tagline = "A tag line",
        imageRes = R.drawable.jelly_bean,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "KitKat",
        tagline = "A tag line",
        imageRes = R.drawable.kitkat,
        price = 549,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Lollipop",
        tagline = "A tag line",
        imageRes = R.drawable.lollipop,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Marshmallow",
        tagline = "A tag line",
        imageRes = R.drawable.marshmallow,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Nougat",
        tagline = "A tag line",
        imageRes = R.drawable.nougat,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Oreo",
        tagline = "A tag line",
        imageRes = R.drawable.oreo,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Pie",
        tagline = "A tag line",
        imageRes = R.drawable.pie,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Chips",
        imageRes = R.drawable.chips,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Pretzels",
        imageRes = R.drawable.pretzels,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Smoothies",
        imageRes = R.drawable.smoothies,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Popcorn",
        imageRes = R.drawable.popcorn,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Almonds",
        imageRes = R.drawable.almonds,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Cheese",
        imageRes = R.drawable.cheese,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apples",
        tagline = "A tag line",
        imageRes = R.drawable.apples,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple sauce",
        tagline = "A tag line",
        imageRes = R.drawable.apple_sauce,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple chips",
        tagline = "A tag line",
        imageRes = R.drawable.apple_chips,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple juice",
        tagline = "A tag line",
        imageRes = R.drawable.apple_juice,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple pie",
        tagline = "A tag line",
        imageRes = R.drawable.apple_pie,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Grapes",
        tagline = "A tag line",
        imageRes = R.drawable.grapes,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Kiwi",
        tagline = "A tag line",
        imageRes = R.drawable.kiwi,
        price = 299,
    ),
    Snack(
        id = Random.nextLong(),
        name = "Mango",
        tagline = "A tag line",
        imageRes = R.drawable.mango,
        price = 299,
    ),
)
