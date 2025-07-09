package com.ht.websocket.data.socket.request

import kotlinx.serialization.Serializable

@Serializable
data class SubscribeRequest(
    val event: String,
    val data: Data
) {

    @Serializable
    data class Data(
        val channel: String,
        val auth: String
    )
}