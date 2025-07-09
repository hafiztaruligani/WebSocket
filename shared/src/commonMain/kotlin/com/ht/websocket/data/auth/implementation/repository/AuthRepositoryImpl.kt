package com.ht.websocket.data.auth.implementation.repository

import com.ht.websocket.core.network.LoadDataResource
import com.ht.websocket.core.network.Type
import com.ht.websocket.core.state.ResourceState
import com.ht.websocket.data.auth.api.repository.AuthRepository
import com.ht.websocket.data.auth.implementation.remote.restapi.request.SocketAuthRequest
import com.ht.websocket.data.auth.implementation.remote.restapi.response.SocketAuthResponse
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl : AuthRepository {
    override fun socketAuth(socketId: String): Flow<ResourceState<String?>> =
        object : LoadDataResource<SocketAuthResponse, String?>(
            type = Type.POST("broadcasting/auth")
        ){

            override suspend fun body(): Any {
                return SocketAuthRequest(
                    channelName =  "private-chat.0030c23a-3564-4160-898a-4f30b3533e34",
                    socketId = socketId
                )
            }

            override suspend fun convertToDataModel(data: SocketAuthResponse?): String? {
                return data?.auth
            }

            override suspend fun shouldCheckLocal(): Boolean = false

            override suspend fun loadFromDatabase(): String? = null

            override suspend fun saveToLocal(data: String?) {}

        }.getResult<SocketAuthResponse>()
}