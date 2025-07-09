package com.ht.websocket.data.auth.api.repository

import com.ht.websocket.core.state.ResourceState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun socketAuth(socketId: String): Flow<ResourceState<String?>>

}