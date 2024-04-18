package com.meyerjaw.geminiworkshop.ui

import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.meyerjaw.geminiworkshop.MainTopAppBar
import com.meyerjaw.geminiworkshop.R
import com.meyerjaw.geminiworkshop.textimageexample.TextImageViewModel
import com.meyerjaw.geminiworkshop.ui.theme.GeminiApplicationTheme
import com.meyerjaw.geminiworkshop.util.BooleanPreviewParamProvider
import com.meyerjaw.geminiworkshop.util.UriSaver
import com.meyerjaw.geminiworkshop.util.longPressToCopyToClipboard
import kotlinx.coroutines.launch


@Composable
fun TextImageScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: TextImageViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
    val imageLoader = ImageLoader.Builder(LocalContext.current).build()
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(
                openDrawer = openDrawer,
                titleResId = R.string.text_and_image_title,
                clear = {
                    viewModel.clear()
                    imageUris.clear()
                }
            )
        }
    ) { paddingValues ->

        TextImageQueryView(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            isLoading = uiState.isLoading,
            inputText = uiState.inputMessage,
            response = uiState.response,
            isError = uiState.isError,
            imageUris = imageUris,
            onInputChange = {  viewModel.updateQuery(it) },
            sendQueryOnClick = { inputText, selectedItems ->
                coroutineScope.launch {
                    val bitmaps = selectedItems.mapNotNull { uri ->
                        val imageRequest = imageRequestBuilder
                            .data(uri)
                            // Scale the image down to 768px for faster uploads
                            .size(size = 768)
                            .precision(Precision.EXACT)
                            .build()
                        try {
                            val result = imageLoader.execute(imageRequest)
                            if (result is SuccessResult) {
                                return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                            } else {
                                return@mapNotNull null
                            }
                        } catch (e: Exception) {
                            return@mapNotNull null
                        }
                    }
                    viewModel.sendQuery(inputText, bitmaps)
                }
            }
        )
    }
}

@Composable
private fun TextImageQueryView(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    inputText: String = "",
    response: String = "",
    isError: Boolean = false,
    imageUris: MutableList<Uri> = mutableListOf(),
    onInputChange: (String) -> Unit,
    sendQueryOnClick: (String, List<Uri>) -> Unit
) {
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris.add(it)
        }
    }

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
        LazyRow(
            modifier = Modifier.padding(all = 8.dp)
        ) {
            items(imageUris) { imageUri ->
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .requiredSize(72.dp)
                )
            }
        }
        Row {
            Button(
                onClick = {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_photo_library),
                    contentDescription = stringResource(id = R.string.add_images)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            ProgressButton(
                text = stringResource(id = R.string.send_to_ai_button_text),
                isLoading = isLoading
            ) { sendQueryOnClick(inputText, imageUris.toList()) }
        }

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
private fun PreviewTextImageQueryView(
    @PreviewParameter(BooleanPreviewParamProvider::class) isLoading: Boolean
) {
    GeminiApplicationTheme {
        var text by remember { mutableStateOf("") }
        TextImageQueryView(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxSize(),
            isLoading = isLoading,
            response = "Lorem ipsum dolor sit amet.",
            inputText = text,
            onInputChange = { text = it} ,
            sendQueryOnClick = { _, _ -> },
        )
    }
}
