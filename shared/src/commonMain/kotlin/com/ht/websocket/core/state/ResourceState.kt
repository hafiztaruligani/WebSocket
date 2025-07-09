package com.ht.websocket.core.state

sealed class ResourceState<out T> {
    data class Success<T>(val data: T): ResourceState<T>()
    data class FromLocal<T>(val data: T): ResourceState<T>()
    data object Loading: ResourceState<Nothing>()
    sealed class Error(open val message: String = "", open val code: Int = -1): ResourceState<Nothing>() {
        data object NoConnection: Error()
        data object Unauthorized: Error()
        data class Maintenance(
            val duration: Int,
            override val message: String,
            val startAt: String
        ): Error()
        data class Other(override val message: String = "", override val code: Int = -1): Error()
        data class Forbidden(override val message: String): Error()

        fun toErrorUiState(
            message: String? = null,
            onRetry: () -> Unit,
            onCancel: () -> Unit,
            txtLeftButton: String = "Cancel"
        ): ErrorUiState {
            return when(this) {
                Unauthorized -> ErrorUiState.Unauthorized(onRetry, onCancel)
                is Forbidden -> ErrorUiState.Forbidden(
                    message = message ?: "",
                    onCancel = onCancel
                )
                NoConnection -> ErrorUiState.NoConnection(
                    onRetry = onRetry,
                    onCancel = onCancel,
                    txtLeftButton = txtLeftButton
                )
                is Other -> ErrorUiState.Other(
                    message = message ?: this.message,
                    onRetry = onRetry,
                    onCancel = onCancel
                )

                is Maintenance -> ErrorUiState.Maintenance(
                    duration = duration,
                    message = this.message,
                    startAt = startAt,
                    onRetry = onRetry,
                    onCancel = onCancel
                )
            }
        }

    }
}
