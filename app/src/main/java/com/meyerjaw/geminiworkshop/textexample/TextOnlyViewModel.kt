package com.meyerjaw.geminiworkshop.textexample

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.meyerjaw.geminiworkshop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class TextOnlyViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _uiState = MutableStateFlow(
        savedStateHandle.getStateFlow(TEXT_ONLY_SAVED_STATE_KEY, TextOnlyUiState()).value
    )
    val uiState: StateFlow<TextOnlyUiState> = _uiState.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[TEXT_ONLY_SAVED_STATE_KEY] = _uiState.asStateFlow()
    }

    private suspend fun queryAI(query: String): String {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )

        return generativeModel.generateContent(query).text
            ?: "I'm sorry, I don't know anything about \"$query\"."
    }

    fun updateQuery(input: String) {
        _uiState.update { it.copy(inputMessage = input) }
    }

    fun sendQuery() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val response = queryAI(uiState.value.inputMessage)
            _uiState.update { it.copy(response = response, isLoading = false, isError = false) }
        } catch (t: Throwable) {
            _uiState.update {
                it.copy(
                    isError = true,
                    isLoading = false,
                    response = t.message ?: "An unknown error occurred"
                )
            }
        }
    }

    fun clear() = _uiState.update { TextOnlyUiState() }
}

const val TEXT_ONLY_SAVED_STATE_KEY = "text_only_state_key"

@Parcelize
data class TextOnlyUiState(
    var inputMessage: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : Parcelable