package com.easyhooon.booksearch.core.domain.util

import kotlinx.coroutines.CancellationException

@Suppress("TooGenericExceptionCaught")
inline fun <T> cancellableRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}
