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

package com.example.compose.jetchat.data

import com.example.compose.jetchat.R
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.EMOJIS.EMOJI_CLOUDS
import com.example.compose.jetchat.data.EMOJIS.EMOJI_FLAMINGO
import com.example.compose.jetchat.data.EMOJIS.EMOJI_MELTING
import com.example.compose.jetchat.data.EMOJIS.EMOJI_PINK_HEART
import com.example.compose.jetchat.data.EMOJIS.EMOJI_POINTS
import com.example.compose.jetchat.profile.ProfileScreenState

val initialMessages = listOf(
    Message(
        "me",
        "Check it out!",
        "8:07 PM",
    ),
    Message(
        "Taylor Brooks",
        "Here's a demo of the new blur regions feature in action! 🎥",
        "8:06 PM",
        videoUri = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
    ),
    Message(
        "me",
        "Thank you!$EMOJI_PINK_HEART",
        "8:06 PM",
        R.drawable.sticker,
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        "8:05 PM",
    ),
    Message(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        "8:05 PM",
    ),
    Message(
        "John Glenn",
        "Compose newbie as well $EMOJI_FLAMINGO, have you looked at the JetNews sample? " +
            "Most blog posts end up out of date pretty fast but this sample is always up to " +
            "date and deals with async data loading (it's faked but the same idea " +
            "applies) $EMOJI_POINTS https://goo.gle/jetnews",
        "8:04 PM",
    ),
    Message(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data " +
            "loading but haven’t found any good ones $EMOJI_MELTING $EMOJI_CLOUDS. " +
            "What’s the recommended way to load async data and emit composable widgets?",
        "8:03 PM",
    ),
    Message(
        "Shangeeth Sivan",
        "Does anyone know about Glance Widgets its the new way to build widgets in Android!",
        "8:08 PM",
    ),
    Message(
        "Taylor Brooks",
        "Wow! I never knew about Glance Widgets when was this added to the android ecosystem",
        "8:10 PM",
    ),
    Message(
        "John Glenn",
        "Yeah its seems to be pretty new!",
        "8:12 PM",
    ),
    Message(
        "me",
        "Speaking of sweets, check out the cupcakes from the Android release party! 🧁",
        "8:15 PM",
        R.drawable.cupcake,
    ),
    Message(
        "Taylor Brooks",
        "Those look amazing! Reminds me of the classic Donut days 🍩",
        "8:16 PM",
        R.drawable.donut,
    ),
    Message(
        "John Glenn",
        "Donut (1.6) was iconic! But Eclair brought live wallpapers and turn-by-turn navigation 🗺️",
        "8:18 PM",
        R.drawable.eclair,
    ),
    Message(
        "Shangeeth Sivan",
        "Froyo was a massive leap forward with JIT compilation ⚡",
        "8:20 PM",
        R.drawable.froyo,
    ),
    Message(
        "me",
        "And who could forget the gingerbread man statue on the Google lawn? 🍪",
        "8:22 PM",
        R.drawable.gingerbread,
    ),
    Message(
        "Taylor Brooks",
        "Honeycomb 3.0 was dedicated entirely to tablets! Check out this statue 🐝",
        "8:25 PM",
        R.drawable.honeycomb,
    ),
    Message(
        "John Glenn",
        "Ice Cream Sandwich (4.0) unified phones and tablets with Holo design! 🍦🥪",
        "8:27 PM",
        R.drawable.ice_cream_sandwich,
    ),
    Message(
        "Shangeeth Sivan",
        "Jelly Bean introduced Project Butter to guarantee smooth 60fps animations 🧈",
        "8:30 PM",
        R.drawable.jelly_bean,
    ),
    Message(
        "me",
        "Have a break, have a KitKat! 🍫 Translucent system bars started right here.",
        "8:32 PM",
        R.drawable.kitkat,
    ),
    Message(
        "Taylor Brooks",
        "Lollipop (5.0) brought Material Design 1.0 into the world! Look at this statue 🍭",
        "8:35 PM",
        R.drawable.lollipop,
    ),
    Message(
        "John Glenn",
        "Marshmallow (6.0) added runtime permissions and Doze battery mode ☁️",
        "8:38 PM",
        R.drawable.marshmallow,
    ),
    Message(
        "Shangeeth Sivan",
        "Nougat (7.0) gave us multi-window split screen and Vulkan graphics support! 📱",
        "8:40 PM",
        R.drawable.nougat,
    ),
    Message(
        "me",
        "Oreo was one of the coolest statues at the Googleplex 🍪",
        "8:42 PM",
        R.drawable.oreo,
    ),
    Message(
        "Taylor Brooks",
        "And Pie (9.0) with gesture navigation and adaptive battery 🥧",
        "8:45 PM",
        R.drawable.pie,
    ),
    Message(
        "John Glenn",
        "Homemade apple pie from the team potluck today! 🍎🥧",
        "8:48 PM",
        R.drawable.apple_pie,
    ),
    Message(
        "Shangeeth Sivan",
        "Micro-kitchen is fully restocked for the hackathon! 🥨",
        "8:50 PM",
        R.drawable.pretzels,
    ),
    Message(
        "me",
        "Smoothie break before the design review 🍓🥝",
        "8:52 PM",
        R.drawable.smoothies,
    ),
    Message(
        "Taylor Brooks",
        "Fresh fruit delivered to building 43! 🍇🍎",
        "8:55 PM",
        R.drawable.fruit,
    ),
    Message(
        "John Glenn",
        "Cheese board ready for the I/O watch party 🧀",
        "8:58 PM",
        R.drawable.cheese,
    ),
    Message(
        "Shangeeth Sivan",
        "Got chips and salsa too! 🥑",
        "9:00 PM",
        R.drawable.chips,
    ),
    Message(
        "me",
        "Popcorn is popping for the amphitheater demo 🍿",
        "9:02 PM",
        R.drawable.popcorn,
    ),
    Message(
        "Taylor Brooks",
        "Apple chips for the healthy snackers 🍏",
        "9:05 PM",
        R.drawable.apple_chips,
    ),
    Message(
        "John Glenn",
        "Fresh almonds too! 🌰",
        "9:08 PM",
        R.drawable.almonds,
    ),
    Message(
        "Shangeeth Sivan",
        "Apple juice is chilled in the fridge 🧃",
        "9:10 PM",
        R.drawable.apple_juice,
    ),
    Message(
        "me",
        "Desserts galore! This team definitely loves Android snacks 🎉",
        "9:15 PM",
        R.drawable.desserts,
    ),
)

val unreadMessages = initialMessages.filter { it.author != "me" }

val exampleUiState = ConversationUiState(
    initialMessages = initialMessages,
    channelName = "#composers",
    channelMembers = 42,
)

/**
 * Example colleague profile
 */
val colleagueProfile = ProfileScreenState(
    userId = "12345",
    photo = R.drawable.someone_else,
    name = "Taylor Brooks",
    status = "Away",
    displayName = "taylor",
    position = "Senior Android Dev at Openlane",
    twitter = "twitter.com/taylorbrookscodes",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "2",
)

/**
 * Example "me" profile.
 */
val meProfile = ProfileScreenState(
    userId = "me",
    photo = R.drawable.ali,
    name = "Ali Conors",
    status = "Online",
    displayName = "aliconors",
    position = "Senior Android Dev at Yearin\nGoogle Developer Expert",
    twitter = "twitter.com/aliconors",
    timeZone = "In your timezone",
    commonChannels = null,
)

object EMOJIS {
    // EMOJI 15
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"

    // EMOJI 14 🫠
    const val EMOJI_MELTING = "\uD83E\uDEE0"

    // ANDROID 13.1 😶‍🌫️
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    // ANDROID 12.0 🦩
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"

    // ANDROID 12.0  👉
    const val EMOJI_POINTS = " \uD83D\uDC49"
}
