package com.meyerjaw.geminiworkshop.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import com.meyerjaw.geminiworkshop.R

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.longPressToCopyToClipboard(context: Context, text: String) =
    this.combinedClickable(
        onClick = {},
        onLongClick = { copyTextToClipboard(context, text) },
        onLongClickLabel = context.getString(R.string.copy_text)
    )

private fun copyTextToClipboard(context: Context, text: String, label: String = "") {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clip)
    Toast.makeText(context, context.getString(R.string.text_copied_text), Toast.LENGTH_SHORT).show()
}