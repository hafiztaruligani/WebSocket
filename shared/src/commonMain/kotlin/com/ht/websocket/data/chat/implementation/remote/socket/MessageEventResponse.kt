package com.ht.websocket.data.chat.implementation.remote.socket

import com.ht.websocket.core.network.toDataClass
import com.ht.websocket.data.chat.api.model.Message
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class MessageEventResponse(
	@SerialName("data")
	val data: Data? = null,

	@SerialName("customer_id")
	val customerId: String? = null,

	@SerialName("user_id")
	val userId: Int? = null
) {
	@Serializable
	data class Data(
		@SerialName("message") val message: String
	)

	fun toModel(userType: Message.UserType) =
		Message(
			text = data?.message.orEmpty(),
			userType = userType
		)
}
