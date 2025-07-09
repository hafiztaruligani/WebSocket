package com.ht.websocket.data.auth.implementation.remote.restapi.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocketAuthRequest(
    @SerialName("channel_name")
    val channelName: String,
    @SerialName("socket_id")
    val socketId: String
)
