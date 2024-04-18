package com.meyerjaw.geminiworkshop.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.meyerjaw.geminiworkshop.MainTopAppBar
import com.meyerjaw.geminiworkshop.R
import com.meyerjaw.geminiworkshop.textexample.TextOnlyViewModel
import com.meyerjaw.geminiworkshop.ui.theme.GeminiApplicationTheme
import com.meyerjaw.geminiworkshop.util.BooleanPreviewParamProvider
import com.meyerjaw.geminiworkshop.util.longPressToCopyToClipboard

@Composable
fun TextOnlyScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: TextOnlyViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(
                openDrawer = openDrawer,
                titleResId = R.string.textonly_title,
                clear = viewModel::clear
            )
        }
    ) { paddingValues ->

        TextOnlyQueryView(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            isLoading = uiState.isLoading,
            inputText = uiState.inputMessage,
            response = uiState.response,
            isError = uiState.isError,
            onInputChange = viewModel::updateQuery,
            sendQueryOnClick = viewModel::sendQuery
        )
    }
}

@Composable
private fun TextOnlyQueryView(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    inputText: String = "",
    response: String = "",
    isError: Boolean = false,
    onInputChange: (String) -> Unit,
    sendQueryOnClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            value = inputText,
            onValueChange = onInputChange,
            minLines = 2,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        ProgressButton(
            text = stringResource(id = R.string.send_to_ai_button_text),
            isLoading = isLoading,
            onClick = sendQueryOnClick
        )

        val context = LocalContext.current
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .longPressToCopyToClipboard(context, response),
            color = if (isError) Color.Red else MaterialTheme.colorScheme.onBackground,
            text = response
        )
    }
}

@Preview(name = "light", showBackground = true)
@Preview(name = "dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTextOnlyQueryView(
    @PreviewParameter(BooleanPreviewParamProvider::class) isLoading: Boolean
) {
    GeminiApplicationTheme {
        var text by remember { mutableStateOf("") }
        TextOnlyQueryView(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxSize(),
            isLoading = isLoading,
            response = "Lorem ipsum dolor sit amet.",
            inputText = text,
            onInputChange = { text = it} ,
            sendQueryOnClick = {}
        )
    }
}
