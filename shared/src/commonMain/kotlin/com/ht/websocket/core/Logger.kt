package com.ht.websocket.core




fun log(tag: Any?, vararg message: Any) {
    //if (platformKmp.isDebug) {
        var combinedMessage = ""
        message.forEach {
            combinedMessage+= it
            combinedMessage+= " "
        }
        logImplementation(tag, combinedMessage)
    //}
}

internal expect fun logImplementation(tag: Any?, message: String)
