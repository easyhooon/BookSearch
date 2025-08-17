package com.easyhooon.booksearch.core.common.util

import com.orhanobut.logger.Logger
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun handleException(exception: Throwable): String {
    return when {
        exception is HttpException -> {
            val message = getHttpErrorMessage(exception.code())
            Logger.e("HTTP ${exception.code()}: $message")
            message
        }

        exception.isNetworkError() -> {
            "네트워크 연결을 확인해주세요."
        }

        else -> {
            val errorMessage = exception.message ?: "알 수 없는 오류가 발생했습니다"
            Logger.e(errorMessage)
            errorMessage
        }
    }
}

private fun getHttpErrorMessage(statusCode: Int): String {
    return when (statusCode) {
        400 -> "요청이 올바르지 않습니다"
        403 -> "접근 권한이 없습니다"
        404 -> "존재하지 않는 데이터입니다"
        429 -> "요청이 너무 많습니다. 잠시 후 다시 시도해주세요"
        in 400..499 -> "요청 처리 중 오류가 발생했습니다"
        in 500..599 -> "서버 오류가 발생했습니다"
        else -> "알 수 없는 오류가 발생했습니다"
    }
}

fun Throwable.isNetworkError(): Boolean {
    return this is UnknownHostException ||
        this is ConnectException ||
        this is SocketTimeoutException ||
        this is IOException
}
