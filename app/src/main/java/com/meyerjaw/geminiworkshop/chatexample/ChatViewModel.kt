package com.meyerjaw.geminiworkshop.chatexample

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        _uiState.value.addMessage(
            ChatMessage(
                text = message,
                participant = ChatParticipant.USER,
                isLoading = true
            )
        )
        viewModelScope.launch {
            try {
                val response = sendToAI(message)

                _uiState.value.replaceLastPendingMessage()

                response.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = ChatParticipant.MODEL,
                            isLoading = false
                        )
                    )
                }
            } catch (t: Throwable) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = t.message ?: "An Unknown error occurred",
                        participant = ChatParticipant.ERROR,
                        isLoading = false
                    )
                )
            }
        }
    }

    fun clear() = _uiState.value.clear()

    private suspend fun sendToAI(message: String): String {
        // TODO call AI with query
        delay(2000L)
        return "I'm sorry, I don't know how to respond to \"$message\"."
    }
}

class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages

    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }

    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isLoading = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }

    fun clear() {
        _messages.clear()
    }
}