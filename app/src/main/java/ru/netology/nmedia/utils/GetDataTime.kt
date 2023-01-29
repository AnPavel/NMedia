package ru.netology.nmedia.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getDataTime() {
    val timestamp = ZonedDateTime.now(ZoneId.of("Europa/Moscow"))
        .format(DateTimeFormatter.ofPattern("MM.dd.yyy hh.mm.ss a"))

}
