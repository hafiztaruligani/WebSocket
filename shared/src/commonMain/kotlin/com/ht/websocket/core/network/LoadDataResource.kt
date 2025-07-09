package com.ht.websocket.core.network


import com.ht.websocket.core.state.ResourceState
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class Type(val path: String, val query: List<Pair<String, String>> = listOf()) {

    data class GET(
        private val _path: String,
        private val _query: List<Pair<String, String>> = listOf(),
    ) : Type(_path, _query)

    data class POST(
        private val _path: String,
        private val _query: List<Pair<String, String>> = listOf(),
    ) : Type(_path, _query)

    data class DELETE(
        private val _path: String,
        private val _query: List<Pair<String, String>> = listOf(),
    ) : Type(_path, _query)

    data class PATCH(
        private val _path: String,
        private val _query: List<Pair<String, String>> = listOf(),
    ) : Type(_path, _query)

}


abstract class LoadDataResource<ResponseType : Any, ResultType>(
    private val type: Type,
    val loadFromLocalFirst: Boolean = false,
) {
    open suspend fun body(): Any? = null

    open suspend fun multipartBody(): MultiPartFormDataContent? = null


    private suspend fun HttpRequestBuilder.setUrl() {
        url {
            protocol = URLProtocol.HTTPS
            host = "test.asepnasihin.com"
            path("/api/" + type.path)

            type.query.forEach {
                parameters.append(it.first, it.second)
            }

            contentType(ContentType.Application.Json)
        }
    }


    suspend fun fetch(): HttpResponse {
        HttpClientProvider.httpClient()
            .apply {
                return when (type) {
                    is Type.GET -> {
                        get {
                            setUrl()
                        }
                    }

                    is Type.POST -> {
                        post {
                            setUrl()
                            this@LoadDataResource.body()?.let {
                                setBody(it)
                            } ?: multipartBody()?.let {
                                setBody(it)
                            }
                        }
                    }

                    is Type.DELETE -> {
                        delete {
                            setUrl()
                            this@LoadDataResource.body()?.let {
                                setBody(it)
                            }
                        }
                    }

                    is Type.PATCH -> {
                        patch {
                            setUrl()
                            this@LoadDataResource.body()?.let {
                                setBody(it)
                            }
                        }
                    }
                }

            }
    }

    abstract suspend fun convertToDataModel(data: ResponseType?): ResultType?
    abstract suspend fun shouldCheckLocal(): Boolean
    abstract suspend fun loadFromDatabase(): ResultType?
    abstract suspend fun saveToLocal(data: ResultType?)


    /**
     * Handles errors from the given JSON error body.
     *
     * @param errorBodyJson The JSON string containing error details. Formatted in [StandardResponse]. Use [StandardResponse.data] for error details.
     * @return A formatted error message for [ResourceState.Error.Other.message] result,
     * or empty string if no error is handled.
     *
     */
    open fun handleError(
        errorBodyJson: String
    ): String? {
        return null
    }

    inline fun <reified T> getResult(): Flow<ResourceState<ResultType?>> = flow {
        try {
            if (loadFromLocalFirst) emit(ResourceState.FromLocal(loadFromDatabase()))
            else emit(ResourceState.Loading)

            val response = fetch()

            val apiResponse: ApiResponse<T> = getApiResponse(response)
            if (apiResponse !is ApiResponse.Success && shouldCheckLocal()) {
                emit(ResourceState.FromLocal(loadFromDatabase()))
            } else {
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        val responseData = apiResponse.data

                        @Suppress("UNCHECKED_CAST")
                        val data = convertToDataModel(responseData as ResponseType?)
                        saveToLocal(data)
                        emit(ResourceState.Success(data))
                    }

                    is ApiResponse.ErrorServer -> {
                        val message = handleError(apiResponse.errorBody ?: "") ?: ""
                        emit(
                            ResourceState.Error.Other(
                                message,
                                apiResponse.code,
                            )
                        )
                    }

                    is ApiResponse.Forbidden -> {
                        val message = handleError(apiResponse.message) ?: ""
                        emit(ResourceState.Error.Forbidden(message))
                    }

                    is ApiResponse.NoConnection -> emit(ResourceState.Error.NoConnection)
                    is ApiResponse.Unauthorized -> emit(ResourceState.Error.Unauthorized)
                    is ApiResponse.Maintenance -> {
                        emit(
                            ResourceState.Error.Maintenance(
                                duration = 0,
                                message = "Server is Maintenance",
                                startAt = "",
                            )
                        )
                    }

                    else -> {
                        emit(ResourceState.Error.Other(code = response.status.value))
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (shouldCheckLocal()) {
                emit(ResourceState.FromLocal(loadFromDatabase()))
            } else {
                emit(ResourceState.Error.NoConnection)
            }
            println(e.stackTraceToString())
        }
    }



}

