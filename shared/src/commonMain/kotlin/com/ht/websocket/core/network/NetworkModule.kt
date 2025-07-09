package com.ht.websocket.core.network

import com.ht.websocket.core.log
import com.ht.websocket.data.socket.SocketManager
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module



val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

inline fun <T> T.toJsonString(serializer: SerializationStrategy<T>): String {
    return json.encodeToString(serializer, this)
}

inline fun <reified R : Any> String.toDataClass() =
    json.decodeFromString<R>(this)

expect fun getHttpClientEngine(): HttpClientEngine



val networkModule = module {


    single<HttpClient> {
        HttpClient(getHttpClientEngine()) {

            installPlugins()

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = getToken()
                        log(this::class.simpleName,"INSTALL LOAD $token")
                        BearerTokens(token, token)
                    }
                    /*refreshTokens {

                    }*/
                }
            }
        }
    }

}


@OptIn(InternalAPI::class)
object HttpClientProvider: KoinComponent {

    fun newInstance() {
        getKoin().unloadModules(listOf(networkModule))
        getKoin().loadModules(listOf(networkModule))
    }

    fun httpClient(): HttpClient = get()
}


private fun getToken(): String {
    return "1|RfRCucHOqKqrtyGaLevZ6RhXKpPGr7JGmKfpk3lA"
}


fun HttpClientConfig<*>.installPlugins(tag: String = "") {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                log(this::class.simpleName, "KTOR REST $tag: $message")
            }
        }
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(
            json = json
        )
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
    }
    install(UserAgent) {
        // agent = platformKmp.userAgent
    }
    install(WebSockets) {
        pingIntervalMillis = 20_000
    }
}







