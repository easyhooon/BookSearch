package com.easyhooon.booksearch.core.common.toast

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ToastMessageEffect(
    userMessageStateHolder: UserMessageStateHolder,
) {
    val context = LocalContext.current
    val userMessage = userMessageStateHolder.userMessage

    LaunchedEffect(userMessage) {
        userMessage?.let { message ->
            Toast.makeText(context, message.message, Toast.LENGTH_SHORT).show()
        }
    }
}
