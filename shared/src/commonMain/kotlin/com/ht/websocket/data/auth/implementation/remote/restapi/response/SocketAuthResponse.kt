package com.ht.websocket.data.auth.implementation.remote.restapi.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocketAuthResponse(
    @SerialName("auth")
    val auth: String? = null
)
