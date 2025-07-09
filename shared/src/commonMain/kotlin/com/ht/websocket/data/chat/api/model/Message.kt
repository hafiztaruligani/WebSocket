package com.ht.websocket.data.chat.api.model

import com.ht.websocket.util.getCurrentTime
import kotlin.random.Random

data class Message(
    val id: String = Random.nextInt().toString(),
    val text: String,
    val time: String = getCurrentTime(),
    val userType: UserType
) {
    sealed class UserType {
        abstract val xScaleView: Float
        data object Client: UserType() {
            override val xScaleView: Float
                get() = -1f
        }
        data object Server: UserType() {
            override val xScaleView: Float
                get() = 1f
        }
    }
}