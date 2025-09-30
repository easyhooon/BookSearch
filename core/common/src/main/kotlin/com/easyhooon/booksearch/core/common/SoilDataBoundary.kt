package com.easyhooon.booksearch.core.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import soil.plant.compose.reacty.Await
import soil.plant.compose.reacty.ErrorBoundary
import soil.plant.compose.reacty.ErrorBoundaryContext
import soil.plant.compose.reacty.Suspense
import soil.query.compose.QueryObject
import soil.query.compose.SubscriptionObject
import soil.query.core.DataModel

@Composable
fun <T> SoilDataBoundary(
    state: DataModel<T>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = {
            coroutineScope.launch {
                state.performResetIfNeeded()
            }
        },
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(state = state, content = content)
        }
    }
}

@Composable
fun <T1, T2> SoilDataBoundary(
    state1: DataModel<T1>,
    state2: DataModel<T2>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T1, T2) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = {
            coroutineScope.launch {
                state1.performResetIfNeeded()
                state2.performResetIfNeeded()
            }
        },
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(
                state1 = state1,
                state2 = state2,
                content = content,
            )
        }
    }
}

@Composable
fun <T1, T2, T3> SoilDataBoundary(
    state1: DataModel<T1>,
    state2: DataModel<T2>,
    state3: DataModel<T3>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T1, T2, T3) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = {
            coroutineScope.launch {
                state1.performResetIfNeeded()
                state2.performResetIfNeeded()
                state3.performResetIfNeeded()
            }
        },
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(
                state1 = state1,
                state2 = state2,
                state3 = state3,
                content = content,
            )
        }
    }
}

// Context interfaces
interface SoilFallbackContext

interface SoilSuspenseContext : SoilFallbackContext

interface SoilErrorContext : SoilFallbackContext {
    val errorBoundaryContext: ErrorBoundaryContext
}

internal class DefaultSoilSuspenseContext : SoilSuspenseContext

internal class DefaultSoilErrorContext(
    override val errorBoundaryContext: ErrorBoundaryContext,
) : SoilErrorContext

// Fallback configurations
data class SoilFallback(
    val errorFallback: @Composable BoxScope.(SoilErrorContext) -> Unit,
    val suspenseFallback: @Composable BoxScope.(SoilSuspenseContext) -> Unit,
)

object SoilFallbackDefaults {
    fun default(): SoilFallback = SoilFallback(
        errorFallback = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                // Simple error fallback - you can customize this
            }
        },
        suspenseFallback = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    )
}

@Composable
private fun ErrorBoundary(
    modifier: Modifier = Modifier,
    fallback: @Composable BoxScope.(SoilErrorContext) -> Unit,
    onReset: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ErrorBoundary(
        fallback = {
            fallback(DefaultSoilErrorContext(it))
        },
        onReset = onReset,
        modifier = modifier,
        content = content,
    )
}

@Composable
private fun Suspense(
    fallback: @Composable BoxScope.(SoilSuspenseContext) -> Unit,
    content: @Composable () -> Unit,
) {
    Suspense(
        fallback = {
            fallback(DefaultSoilSuspenseContext())
        },
        content = content,
    )
}

private suspend fun <T> DataModel<T>.performResetIfNeeded() {
    when (this) {
        is QueryObject<T> -> this.error?.let { this.refresh() }
        is SubscriptionObject<T> -> this.error?.let { this.reset() }
    }
}
