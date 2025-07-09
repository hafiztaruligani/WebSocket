package com.ht.websocket.data.chat.api.repository

import com.ht.websocket.data.chat.api.model.Chat
import com.ht.websocket.data.chat.api.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChat(): Flow<Chat>

    suspend fun sendMessage(message: Message)

}