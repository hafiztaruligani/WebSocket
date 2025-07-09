package com.ht.websocket.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun getCurrentTime(): String {
    val currentDateTime: LocalDateTime = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${currentDateTime.hour}.${currentDateTime.minute}"
}