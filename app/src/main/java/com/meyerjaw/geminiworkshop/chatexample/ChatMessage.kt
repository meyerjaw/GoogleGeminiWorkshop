package com.meyerjaw.geminiworkshop.chatexample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    val participant: ChatParticipant,
    var isLoading: Boolean = false
) : Parcelable

enum class ChatParticipant {
    USER, MODEL, ERROR
}
