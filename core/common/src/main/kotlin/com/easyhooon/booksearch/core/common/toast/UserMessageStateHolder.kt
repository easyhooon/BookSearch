package com.easyhooon.booksearch.core.common.toast

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
interface UserMessageStateHolder {
    val userMessage: UserMessage?
    fun showMessage(message: String)
}

@Stable
class UserMessageStateHolderImpl : UserMessageStateHolder {
    override var userMessage: UserMessage? by mutableStateOf(null)
        private set

    override fun showMessage(message: String) {
        userMessage = UserMessage(message = message)
    }
}

data class UserMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
)