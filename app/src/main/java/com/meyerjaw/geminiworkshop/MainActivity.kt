package com.meyerjaw.geminiworkshop

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.meyerjaw.geminiworkshop.chatexample.ChatViewModel
import com.meyerjaw.geminiworkshop.textexample.TextOnlyViewModel
import com.meyerjaw.geminiworkshop.textimageexample.TextImageViewModel
import com.meyerjaw.geminiworkshop.ui.theme.GeminiApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textOnlyViewModel: TextOnlyViewModel by viewModels()
        val textImageViewModel: TextImageViewModel by viewModels()
        val chatViewModel: ChatViewModel by viewModels()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setContent {
            GeminiApplicationTheme {
                MainNavGraph(
                    textOnlyViewModel = textOnlyViewModel,
                    textImageViewModel = textImageViewModel,
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}