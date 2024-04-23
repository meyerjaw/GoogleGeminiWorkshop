package com.meyerjaw.geminiworkshop.chatexample

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.meyerjaw.geminiworkshop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val history: MutableList<Content> = mutableListOf()

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
                history.add(content(role = "user") { text(message)})                 // <- new code
                response?.let { modelResponse ->                                     // <- ? added
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = ChatParticipant.MODEL,
                            isLoading = false
                        )
                    )
                    history.add(content(role = "model") { text(modelResponse)})      // <- new code
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

    fun clear() {
        _uiState.value.clear()
        history.clear()
    }

    private suspend fun sendToAI(message: String): String? {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )

        val chat = generativeModel.startChat(history)

        return chat.sendMessage(message).text
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