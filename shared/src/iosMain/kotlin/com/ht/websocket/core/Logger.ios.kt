package com.ht.websocket.core

import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime

internal actual fun logImplementation(tag: Any?, message: String) {
    val clock = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    println("${clock.hour}:${clock.minute}:${clock.second}\t$tag\t\t$message")
}