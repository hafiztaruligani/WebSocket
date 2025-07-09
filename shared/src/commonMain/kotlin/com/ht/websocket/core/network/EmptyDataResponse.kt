package com.ht.websocket.core.network

import kotlinx.serialization.Serializable

@Serializable
data class EmptyDataResponse(
    val id: String? = null
)