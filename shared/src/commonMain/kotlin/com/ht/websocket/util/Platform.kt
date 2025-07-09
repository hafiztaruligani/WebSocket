package com.ht.websocket.util

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform