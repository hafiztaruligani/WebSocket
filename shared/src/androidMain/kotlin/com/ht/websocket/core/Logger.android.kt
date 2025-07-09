package com.ht.websocket.core

import android.util.Log

internal actual fun logImplementation(tag: Any?, message: String) {
    Log.d(tag?.toString(), message)
}