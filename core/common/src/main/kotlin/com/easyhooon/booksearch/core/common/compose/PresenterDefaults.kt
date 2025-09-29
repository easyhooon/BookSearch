package com.easyhooon.booksearch.core.common.compose

import androidx.compose.runtime.Composable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Composable
fun <UI_STATE> providePresenterDefaults(
    uiStateProducer: @Composable PresenterContext.() -> UI_STATE,
): UI_STATE {
    usePresenterContext {
        return uiStateProducer()
    }
}

/**
 * Context for presenter composable functions.
 */
interface PresenterContext

class DefaultPresenterContext : PresenterContext

@OptIn(ExperimentalContracts::class)
inline fun usePresenterContext(
    block: PresenterContext.() -> Unit,
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(DefaultPresenterContext())
}