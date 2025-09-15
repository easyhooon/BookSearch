package com.easyhooon.booksearch.core.network.di

import android.util.Log
import com.easyhooon.booksearch.core.network.BuildConfig
import com.easyhooon.booksearch.core.network.TokenInterceptor
import com.easyhooon.booksearch.core.network.client.BookSearchKtorClient
import com.easyhooon.booksearch.core.network.service.BookSearchService
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val MaxTimeoutMillis = 15_000L

private val jsonRule = Json {
    // 기본값도 JSON에 포함하여 직렬화
    encodeDefaults = true
    // JSON에 정의되지 않은 키는 무시 (역직렬화 시 에러 방지)
    ignoreUnknownKeys = true
    // JSON을 보기 좋게 들여쓰기하여 포맷팅
    prettyPrint = true
    // 엄격하지 않은 파싱 (따옴표 없는 키, 후행 쉼표 등 허용)
    isLenient = true
}

private val jsonConverterFactory = jsonRule.asConverterFactory("application/json".toMediaType())

private val FILTERED_HEADERS = setOf(
    "transfer-encoding",
    "connection",
    "x-content-type-options",
    "x-xss-protection",
    "cache-control",
    "pragma",
    "expires",
    "x-frame-options",
    "keep-alive",
    "server",
    "content-length",
)

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Singleton
    @Provides
    internal fun provideNetworkLogAdapter(): AndroidLogAdapter {
        val networkFormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // 스레드 정보 표시 여부
            .methodCount(0) // 메소드 스택 제거
            .methodOffset(0) // 메소드 오프셋 제거
            .tag("Network") // API 호출 전용 태그 설정
            .build()

        return AndroidLogAdapter(networkFormatStrategy)
    }

    @Singleton
    @Provides
    internal fun provideHttpLoggingInterceptor(
        networkLogAdapter: AndroidLogAdapter,
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            val shouldFilter = FILTERED_HEADERS.any { header ->
                message.lowercase().contains("$header:")
            }

            val isDuplicateContentType = message.lowercase().contains("content-type: application/json") &&
                !message.contains("charset")

            if (!shouldFilter && !isDuplicateContentType) {
                networkLogAdapter.log(Log.DEBUG, null, message)
            }
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Singleton
    @Provides
    internal fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
            .readTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
            .writeTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    internal fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(jsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    internal fun provideBookSearchService(
        retrofit: Retrofit,
    ): BookSearchService {
        return retrofit.create()
    }

    @Singleton
    @Provides
    internal fun provideKtorHttpClient(
        networkLogAdapter: AndroidLogAdapter,
    ): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    connectTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
                    readTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
                    writeTimeout(MaxTimeoutMillis, TimeUnit.MILLISECONDS)
                }
            }

            install(ContentNegotiation) {
                json(jsonRule)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        val shouldFilter = FILTERED_HEADERS.any { header ->
                            message.lowercase().contains("$header:")
                        }

                        val isDuplicateContentType = message.lowercase().contains("content-type: application/json") &&
                            !message.contains("charset")

                        if (!shouldFilter && !isDuplicateContentType) {
                            networkLogAdapter.log(Log.DEBUG, null, message)
                        }
                    }
                }
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }

            defaultRequest {
                url(BuildConfig.KAKAO_API_BASE_URL)
                header("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
            }
        }
    }

    @Singleton
    @Provides
    internal fun provideBookSearchKtorClient(
        httpClient: HttpClient,
    ): BookSearchKtorClient {
        return BookSearchKtorClient(httpClient)
    }
}
