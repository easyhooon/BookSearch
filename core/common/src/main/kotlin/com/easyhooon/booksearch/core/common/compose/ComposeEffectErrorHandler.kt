package com.easyhooon.booksearch.core.common.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive

interface ComposeEffectErrorHandler {
    suspend fun emit(throwable: Throwable)
}

@Suppress("CompositionLocalAllowlist")
val LocalComposeEffectErrorHandler = staticCompositionLocalOf<ComposeEffectErrorHandler> {
    object : ComposeEffectErrorHandler {
        override suspend fun emit(throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}

@Suppress("TooGenericExceptionCaught")
@Composable
fun SafeLaunchedEffect(key: Any?, block: suspend CoroutineScope.() -> Unit) {
    val composeEffectErrorHandler = LocalComposeEffectErrorHandler.current
    LaunchedEffect(key) {
        try {
            block()
        } catch (e: Exception) {
            ensureActive()
            e.printStackTrace()
            composeEffectErrorHandler.emit(e)
        }
    }
}
