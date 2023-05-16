package com.example.compose.jetchat.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

/**
 * This function formats date to a beauty form.
 * For the date of today and yesterday, It return sequentially "Today", "Yesterday".
 * For the dates of this year, It returns the name of month's name and the day. ex: "May 15".
 * For the dates of last year and before that, It returns the name of month's name and the day and
 * also year. ex: "May 15, 2022"
 */
@Throws(ParseException::class)
fun convertDateToPrettyDate(strDate: String): String {
    val currentDate = Calendar.getInstance()

    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val formattedDate = dateFormat.parse(strDate)

    val date = Calendar.getInstance()
    date.time = formattedDate!!

    val currentDateYear = currentDate.get(Calendar.YEAR);
    val currentDateMonth = currentDate.get(Calendar.MONTH);
    val currentDateDay = currentDate.get(Calendar.DAY_OF_MONTH);

    val dateYear = date.get(Calendar.YEAR);
    val dateMonth = date.get(Calendar.MONTH);
    val dateDay = date.get(Calendar.DAY_OF_MONTH);

    return if (currentDateYear == dateYear && currentDateMonth == dateMonth && currentDateDay == dateDay) {
        "Today"
    } else if (currentDateYear == dateYear && currentDateMonth == dateMonth && currentDateDay - dateDay == 1) {
        "Yesterday"
    } else if (currentDateYear == dateYear) {
        val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(date.time)
        "$monthName $dateDay"
    } else {
        val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(date.time)
        "$monthName $dateDay, $dateYear"
    }
}

/**
 * Get current time
 */
fun now(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentDate)
}