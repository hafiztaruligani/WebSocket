package com.ht.websocket.data.chat.implementation.repository

import com.ht.websocket.core.network.EmptyDataResponse
import com.ht.websocket.core.network.LoadDataResource
import com.ht.websocket.core.network.Type
import com.ht.websocket.core.network.toDataClass
import com.ht.websocket.data.chat.api.model.Chat
import com.ht.websocket.data.chat.api.model.Message
import com.ht.websocket.data.chat.api.repository.ChatRepository
import com.ht.websocket.data.chat.implementation.remote.socket.MessageEventResponse
import com.ht.websocket.data.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatRepositoryImpl(
    private val socketManager: SocketManager
) : ChatRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val chat = MutableStateFlow(Chat(listOf())) // mock database


    init {
        onEvent()
    }

    private fun onEvent() {
        coroutineScope.launch {
            socketManager.socketEvent.collect {
                val message = it.data?.toDataClass<MessageEventResponse>()
                message?.toModel(Message.UserType.Server)?.let {
                    addMessageToChat(it)
                }
            }
        }
    }

    override fun getChat(): Flow<Chat> {
        return chat
    }

    override suspend fun sendMessage(message: Message) {
        addMessageToChat(message)
        mockAdminSendingMessage()
    }

    // mock admin sending/replying message (from rest api)
    private fun mockAdminSendingMessage() {
        val loadDataResource = object : LoadDataResource<EmptyDataResponse, EmptyDataResponse>(
            Type.GET("send-test-message")
        ) {
            override suspend fun convertToDataModel(data: EmptyDataResponse?): EmptyDataResponse? = data

            override suspend fun shouldCheckLocal(): Boolean = false

            override suspend fun loadFromDatabase(): EmptyDataResponse? = null

            override suspend fun saveToLocal(data: EmptyDataResponse?) {}
        }.getResult<Any>()
        coroutineScope.launch {
            loadDataResource.collect{}
        }
    }

    private fun addMessageToChat(message: Message) {
        coroutineScope.launch {
            val messages = this@ChatRepositoryImpl.chat.value.messages
            chat.update {
                it.copy(
                    messages = messages + listOf(message)
                )
            }
        }
    }
}