package com.ht.websocket.core.state


sealed interface ErrorUiState {
    val onRetry: () -> Unit
    val onCancel: () -> Unit

    data class NoConnection(
        override val onRetry: () -> Unit,
        override val onCancel: () -> Unit,
        val txtLeftButton: String = "Cancel"
    ) : ErrorUiState

    data class Unauthorized(override val onRetry: () -> Unit, override val onCancel: () -> Unit) :
        ErrorUiState

    data class Other(
        override val onRetry: () -> Unit,
        override val onCancel: () -> Unit,
        val message: String,
    ): ErrorUiState

    data class Forbidden(
        val message: String,
        override val onRetry: () -> Unit = {},
        override val onCancel: () -> Unit
    ) : ErrorUiState

    data class Maintenance(
        val duration: Int,
        val message: String,
        val startAt: String,
        override val onRetry: () -> Unit = {},
        override val onCancel: () -> Unit
    ) : ErrorUiState
}