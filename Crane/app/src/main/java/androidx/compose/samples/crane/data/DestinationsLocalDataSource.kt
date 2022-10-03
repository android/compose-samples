/*
 * Copyright 2022 The Android Open Source Project
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

@file:Suppress("ktlint:max-line-length") // String constants read better
package androidx.compose.samples.crane.data

import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_IMAGE_WIDTH = "250"

/**
 * Annotated with Singleton as the class created a lot of objects.
 */
@Singleton
class DestinationsLocalDataSource @Inject constructor() {
    val craneRestaurants = listOf(
        ExploreModel(
            city = NAPLES,
            description = "1286 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?ixlib=rb-1.2.1&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = DALLAS,
            description = "2241 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1495749388945-9d6e4e5b67b1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = CORDOBA,
            description = "876 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1562625964-ffe9b2f617fc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=250&q=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = MADRID,
            description = "5610 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1515443961218-a51367888e4b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = MALDIVAS,
            description = "1286 Restaurants",
            imageUrl = "https://images.unsplash.com/flagged/photo-1556202256-af2687079e51?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = ASPEN,
            description = "2241 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1542384557-0824d90731ee?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = BALI,
            description = "876 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1567337710282-00832b415979?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        )
    )

    val craneHotels = listOf(
        ExploreModel(
            city = MALDIVAS,
            description = "1286 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = ASPEN,
            description = "2241 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1445019980597-93fa8acb246c?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = BALI,
            description = "876 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1570213489059-0aac6626cade?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = BIGSUR,
            description = "5610 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1561409037-c7be81613c1f?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = NAPLES,
            description = "1286 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1455587734955-081b22074882?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = DALLAS,
            description = "2241 Available Properties",
            imageUrl = "https://images.unsplash.com/46/sh3y2u5PSaKq8c4LxB3B_submission-photo-4.jpg?ixlib=rb-1.2.1&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = CORDOBA,
            description = "876 Available Properties",
            imageUrl = "https://images.unsplash.com/photo-1570214476695-19bd467e6f7a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        )
    )

    val craneDestinations = listOf(
        ExploreModel(
            city = KHUMBUVALLEY,
            description = "Nonstop - 5h 16m+",
            imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = MADRID,
            description = "Nonstop - 2h 12m+",
            imageUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = BALI,
            description = "Nonstop - 6h 20m+",
            imageUrl = "https://images.unsplash.com/photo-1518548419970-58e3b4079ab2?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = ROME,
            description = "Nonstop - 2h 38m+",
            imageUrl = "https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = GRANADA,
            description = "Nonstop - 2h 12m+",
            imageUrl = "https://images.unsplash.com/photo-1534423839368-1796a4dd1845?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = MALDIVAS,
            description = "Nonstop - 9h 24m+",
            imageUrl = "https://images.unsplash.com/photo-1544550581-5f7ceaf7f992?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = WASHINGTONDC,
            description = "Nonstop - 7h 30m+",
            imageUrl = "https://images.unsplash.com/photo-1557160854-e1e89fdd3286?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = BARCELONA,
            description = "Nonstop - 2h 12m+",
            imageUrl = "https://images.unsplash.com/photo-1562883676-8c7feb83f09b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = CRETE,
            description = "Nonstop - 1h 50m+",
            imageUrl = "https://images.unsplash.com/photo-1486575008575-27670acb58db?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
        ExploreModel(
            city = NAPLES,
            description = "1286 Restaurants",
            imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?ixlib=rb-1.2.1&auto=format&fit=crop&w=$DEFAULT_IMAGE_WIDTH"
        ),
    )
}
