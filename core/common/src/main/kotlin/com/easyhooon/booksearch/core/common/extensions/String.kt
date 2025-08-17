package com.easyhooon.booksearch.core.common.extensions

import com.orhanobut.logger.Logger
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String.toFormattedPrice(): String {
    return if (this.isBlank() || this == "-1") {
        "정보 없음"
    } else {
        try {
            val number = this.toLong()
            DecimalFormat("#,###").format(number)
        } catch (e: NumberFormatException) {
            Logger.e("Failed to format price: '$this'")
            this
        }
    }
}

fun String.toFormattedDate(): String {
    if (this.isBlank()) return ""

    return try {
        val normalizedDatetime = this.replace(" ", "+")
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val parsedDate = ZonedDateTime.parse(normalizedDatetime, inputFormatter)
        parsedDate.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        ""
    }
}
