package com.easyhooon.booksearch.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.easyhooon.booksearch.core.common.SwrClientFactory
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import soil.query.compose.SwrClientProvider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val swrClientFactory = application as SwrClientFactory
        setContent {
            SwrClientProvider(client = swrClientFactory.queryClient) {
                BookSearchTheme {
                    MainScreen()
                }
            }
        }
    }
}
