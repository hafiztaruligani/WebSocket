package com.ht.websocket.data.socket

import com.ht.websocket.core.log
import com.ht.websocket.core.network.toDataClass
import com.ht.websocket.core.network.toJsonString
import com.ht.websocket.core.state.ResourceState
import com.ht.websocket.data.auth.api.repository.AuthRepository
import com.ht.websocket.data.socket.model.SocketEvent
import com.ht.websocket.data.socket.request.SubscribeRequest
import com.ht.websocket.data.socket.response.ConnectResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


sealed interface ConnectionStatus {
    data object Disconnected: ConnectionStatus
    data object Connecting: ConnectionStatus
    data object Subscribing: ConnectionStatus
    data object Subscribed: ConnectionStatus
}

class SocketManager(
    private val httpClient: HttpClient,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val TAG = "SocketManager"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>()
    val connectionStatus: Flow<ConnectionStatus> = _connectionStatus

    private val _event = MutableSharedFlow<SocketEvent>()
    val socketEvent: Flow<SocketEvent> = _event

    init {
        coroutineScope.launch {
            _connectionStatus.emit(ConnectionStatus.Disconnected)
            launch {
                socketEvent.collect {
                    log(TAG, "socket event $it")
                }
            }
            connectionStatus.collect {
                log(TAG, "connection status $it")
            }
        }
    }

    private var socketSession: DefaultClientWebSocketSession? = null
    private var connectJob: Job? = null
    fun connect() {
        connectJob?.cancel()
        connectJob = coroutineScope.launch {
            _connectionStatus.emit(ConnectionStatus.Connecting)
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = "soketi.asepnasihin.com",
                    path = "/app/my-app-key",
                    request = {
                        url {
                            protocol = URLProtocol.WSS
                            parameters.append("protocol", "7")
                            parameters.append("client", "js")
                            parameters.append("version", "7.2.0")
                        }
                    }
                ) {
                    socketSession = this
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                log(TAG, "incoming TEXT frame: $text")
                                val event = text.toDataClass<SocketEvent>()
                                when(event.event) {
                                    "pusher:connection_established" -> {
                                        _connectionStatus.emit(ConnectionStatus.Subscribing)
                                        val auth = getSocketAuth(event)

                                        if (auth==null) {
                                            connect()
                                            return@webSocket
                                        }

                                        val subscribeRequest = SubscribeRequest(
                                            event = "pusher:subscribe",
                                            data = SubscribeRequest.Data(
                                                channel = "private-chat.0030c23a-3564-4160-898a-4f30b3533e34",
                                                auth = auth
                                            )
                                        ).toJsonString(SubscribeRequest.serializer())

                                        log(TAG, "subscribing: $subscribeRequest")

                                        send(Frame.Text(subscribeRequest))
                                    }
                                    "pusher_internal:subscription_succeeded" -> {
                                        _connectionStatus.emit(ConnectionStatus.Subscribed)
                                    }
                                    else -> {
                                        _event.emit(event) // another event
                                    }
                                }
                            }
                            is Frame.Binary -> {
                                log(TAG, "incoming BINARY frame")
                            }
                            is Frame.Ping -> {
                                log(TAG, "incoming PING")
                            }
                            is Frame.Pong -> {
                                log(TAG, "incoming PONG")
                            }
                            is Frame.Close -> {
                                log(TAG, "incoming CLOSE frame")
                            }
                            else -> {
                                log(TAG, "incoming UNKNOWN frame: $frame")
                            }
                        }
                    }


                }

            } catch (e: Exception) {
                e.printStackTrace()
                delay(500)
                connect()
            }
        }
    }

    // return auth string
    private suspend fun getSocketAuth(
        event: SocketEvent
    ): String? {
        val socketId = event.data?.toDataClass<ConnectResponse>()?.socketId
        log(TAG, "SOCKET_ID ${socketId}")
        socketId ?: run {
            return null
        }


        return suspendCoroutine { result ->
            coroutineScope.launch {
                authRepository.socketAuth(socketId).collect {
                    when(it) {
                        ResourceState.Loading -> {}
                        is ResourceState.Success -> {
                            /*
                            it.data!!.let { auth ->
                                val subscribeRequest = SubscribeRequest(
                                    event = "pusher:subscribe",
                                    data = SubscribeRequest.Data(
                                        channel = "private-chat.0030c23a-3564-4160-898a-4f30b3533e34",
                                        auth = auth
                                    )
                                ).toJsonString(SubscribeRequest.serializer())
                                log(TAG, "subscribing: $subscribeRequest")
                                webSocketSession.send(Frame.Text(subscribeRequest))

                            }*/
                            result.resume(it.data)
                        }
                        /*is ResourceState.Error.Forbidden -> TODO()
                        is ResourceState.Error.Maintenance -> TODO()
                        ResourceState.Error.NoConnection -> TODO()
                        is ResourceState.Error.Other -> TODO()
                        ResourceState.Error.Unauthorized -> TODO()
                        is ResourceState.FromLocal -> TODO()*/
                        else -> result.resume(null)
                    }
                }
            }
        }

    }

    fun disconnect() {
        coroutineScope.launch {
            socketSession?.close()
            connectJob?.cancel()
            _connectionStatus.emit(ConnectionStatus.Disconnected)
        }
    }

}