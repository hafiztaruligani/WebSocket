package com.ht.websocket.core.network

import com.ht.websocket.core.network.ApiResponse.ErrorServer
import com.ht.websocket.core.network.ApiResponse.Forbidden
import com.ht.websocket.core.network.ApiResponse.Maintenance
import com.ht.websocket.core.network.ApiResponse.NoConnection
import com.ht.websocket.core.network.ApiResponse.NotFound
import com.ht.websocket.core.network.ApiResponse.Success
import com.ht.websocket.core.network.ApiResponse.Unauthorized
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText


suspend inline fun <reified T>getApiResponse(response: HttpResponse): ApiResponse<T> {

    val result: ApiResponse<T> =
        when(response.status.value) {
            in (200..299) -> {
                val body = response.body<T>()
                Success(body)
            }
            403 -> Forbidden(response.bodyAsText())
            401 -> Unauthorized
            404 -> NotFound
            408 -> NoConnection
            503 -> Maintenance(response.bodyAsText())

            else -> ErrorServer(
                response.status.description,
                response.status.value,
                response.bodyAsText()
            )
        }
    return result
}

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T?, val message: String? = null) : ApiResponse<T>()
    data class ErrorServer(val message: String = "", val code: Int = -1, val errorBody: String? = null) : ApiResponse<Nothing>()
    data class Maintenance(val message: String) : ApiResponse<Nothing>()
    data object NotFound : ApiResponse<Nothing>()
    data object Unauthorized : ApiResponse<Nothing>()
    data object NoConnection : ApiResponse<Nothing>()
    data class Forbidden(val message: String): ApiResponse<Nothing>()
}

