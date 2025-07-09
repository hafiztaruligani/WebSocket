package com.ht.websocket.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.min


fun getCurrentTime(): String {
    val currentDateTime: LocalDateTime = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    var hour = currentDateTime.hour.toString()
    if (hour.length<=1) hour = "0$hour"
    var minutes = currentDateTime.minute.toString()
    if (minutes.length<=1) minutes = "0${minutes}"
    return "${hour}.${minutes}"
}