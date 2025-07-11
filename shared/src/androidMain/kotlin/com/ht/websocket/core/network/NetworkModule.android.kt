package com.ht.websocket.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun getHttpClientEngine(): HttpClientEngine {
    return OkHttp.create()
}