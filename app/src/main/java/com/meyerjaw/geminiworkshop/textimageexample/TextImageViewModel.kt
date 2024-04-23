package com.meyerjaw.geminiworkshop.textimageexample

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.meyerjaw.geminiworkshop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class TextImageViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _uiState = MutableStateFlow(
        savedStateHandle.getStateFlow(TEXT_IMAGE_SAVED_STATE_KEY, TextImageUiState()).value
    )
    val uiState: StateFlow<TextImageUiState> = _uiState.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[TEXT_IMAGE_SAVED_STATE_KEY] = _uiState.asStateFlow()
    }

    private suspend fun queryAI(query: String, bitmaps: List<Bitmap>): String {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey
        )

        val inputContent = content {
            for (bitmap in bitmaps) {
                image(bitmap)
            }
            text(query)
        }

        return generativeModel.generateContent(inputContent).text
            ?: "I'm sorry, I don't know anything about \"$query\"."
    }

    fun updateQuery(input: String) {
        _uiState.update { it.copy(inputMessage = input) }
    }

    fun sendQuery(inputText: String, bitmaps: List<Bitmap>) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val response = queryAI(query = inputText, bitmaps = bitmaps)
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

    fun clear() = _uiState.update { TextImageUiState() }
}

const val TEXT_IMAGE_SAVED_STATE_KEY = "text_image_state_key"

@Parcelize
data class TextImageUiState(
    var inputMessage: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : Parcelable