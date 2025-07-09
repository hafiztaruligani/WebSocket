package com.ht.websocket.data.socket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SocketEvent (
    val event: String? = null,
    val data: String? = null
)
