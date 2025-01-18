package com.example.apod_fetch.utils

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {

    @SuppressLint("NewApi")
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Converts a LocalDate to a String formatted as "yyyy-MM-dd".
     *
     * @param date The LocalDate to format.
     * @return The formatted date string.
     */
    @SuppressLint("NewApi")
    fun formatDate(date: LocalDate): String {
        return date.format(formatter)
    }
}