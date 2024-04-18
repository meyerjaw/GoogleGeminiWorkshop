package com.meyerjaw.geminiworkshop.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.meyerjaw.geminiworkshop.MainTopAppBar
import com.meyerjaw.geminiworkshop.R
import com.meyerjaw.geminiworkshop.chatexample.ChatMessage
import com.meyerjaw.geminiworkshop.chatexample.ChatParticipant
import com.meyerjaw.geminiworkshop.chatexample.ChatViewModel
import com.meyerjaw.geminiworkshop.ui.theme.GeminiApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    openDrawer: () -> Unit,
    viewModel: ChatViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MainTopAppBar(
                openDrawer = openDrawer,
                clear = viewModel::clear,
                titleResId = R.string.chat_title,
            )
        },
        bottomBar = {
            MessageBox(
                onSendMessage = viewModel::sendMessage,
                resetScroll = { coroutineScope.launch {
                    listState.scrollToItem(0)
                }
                }
            )
        }
    ) { paddingValues ->
        ChatList(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            uiState.messages,
            listState
        )
    }
}

@Composable
private fun ChatList(
    modifier: Modifier = Modifier,
    chatMessages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        state = listState
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubble(chatMessage = message)
        }
    }
}

@Composable
private fun ChatBubble(
    chatMessage: ChatMessage
) {
    val isModelMessage = chatMessage.participant == ChatParticipant.MODEL ||
            chatMessage.participant == ChatParticipant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        ChatParticipant.ERROR -> MaterialTheme.colorScheme.errorContainer
        ChatParticipant.USER -> MaterialTheme.colorScheme.primaryContainer
        ChatParticipant.MODEL -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }

    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = chatMessage.participant.name,
            style = MaterialTheme.typography.bodySmall
        )
        Row {
            if (chatMessage.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        text = chatMessage.text,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

    }
}

@Composable
private fun MessageBox(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit
) {
    var userMessage by rememberSaveable { mutableStateOf("") }

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(start = 8.dp)
        .padding(vertical = 8.dp)) {
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = userMessage,
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                )
            )
            IconButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send),
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMessageBox() {
    GeminiApplicationTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            MessageBox(
                onSendMessage = {},
                resetScroll = {}
            )
        }
    }
}

@Preview(name = "light")
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewChatBubble(
    @PreviewParameter(ChatMessagePreviewParamProvider::class) message: ChatMessage
) {
    GeminiApplicationTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ChatBubble(chatMessage = message)
        }
    }
}

private class ChatMessagePreviewParamProvider : PreviewParameterProvider<ChatMessage> {
    override val values: Sequence<ChatMessage>
        get() = sequenceOf(
            ChatMessage(text = "Hello", participant = ChatParticipant.USER),
            ChatMessage(text = "How are you today?", participant = ChatParticipant.MODEL),
            ChatMessage(text = "An unexpected error has occurred.", participant = ChatParticipant.ERROR),
        )
}