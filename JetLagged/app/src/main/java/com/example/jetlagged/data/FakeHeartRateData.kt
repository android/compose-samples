package com.example.jetlagged.data

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

data class HeartRateData(val date: LocalDateTime, val rate: Int)

fun generateFakeHeartRateData() : List<HeartRateData>{
    val entries = 50
    val listEntries = mutableListOf<HeartRateData>()
    var dateTime = LocalDateTime.now()
    (0..entries).forEach {
        val newDateTime =dateTime.plusMinutes(30)
        listEntries.add(it, HeartRateData(newDateTime, Random.nextInt()))
        dateTime = newDateTime
    }
    return listEntries
}