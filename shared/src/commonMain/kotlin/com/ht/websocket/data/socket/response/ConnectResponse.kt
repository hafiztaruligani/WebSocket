package com.ht.websocket.data.socket.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ConnectResponse(

	@SerialName("socket_id")
	val socketId: String? = null,

	@SerialName("activity_timeout")
	val activityTimeout: Int? = null
)
