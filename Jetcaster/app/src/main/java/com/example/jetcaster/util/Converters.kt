package com.example.jetcaster.util

fun secondsToPlayerDuration(time: Long): String {
    return "${time / 60}m ${time % 60}s"
}