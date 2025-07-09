package com.ht.websocket.presentation.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ht.websocket.data.chat.api.model.Chat
import com.ht.websocket.data.chat.api.model.Message
import com.ht.websocket.data.chat.api.repository.ChatRepository
import com.ht.websocket.data.socket.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val socketManager: SocketManager,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()


    init {
        socketManager.connect()
        collectConnectionStatus()
        collectMessage()
    }

    private fun collectMessage() {
        viewModelScope.launch {
            chatRepository.getChat().collect { chat ->
                _uiState.update {
                    it.copy(
                        chat = chat
                    )
                }
            }
        }
    }

    private fun collectConnectionStatus() {
        viewModelScope.launch {
            socketManager.connectionStatus.collect { connectionStatus ->
                _uiState.update {
                    it.copy(connectionStatus = connectionStatus.toString())
                }
            }

        }
    }

    fun action(action: ChatAction) {
        when(action) {
            ChatAction.CloseChat -> socketManager.disconnect()
            is ChatAction.SendMessage -> {
                sendMessage()
            }

            is ChatAction.InputTextChanged -> _uiState.update {
                it.copy(inputText = action.input)
            }
        }
    }

    private fun sendMessage() {
        if (uiState.value.inputText.isNotBlank()) viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    text = uiState.value.inputText.trimIndent().trim(),
                    userType = Message.UserType.Client
                )
            )
            _uiState.update {
                it.copy(inputText = "")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }

}

data class ChatUiState(
    val connectionStatus: String = "",
    val chat: Chat = Chat(listOf()),
    val inputText: String = ""
)

sealed interface ChatAction {
    data object SendMessage: ChatAction
    data class InputTextChanged(val input: String) : ChatAction
    data object CloseChat: ChatAction
}