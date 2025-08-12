@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.hilt)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

android {
    namespace = "com.easyhooon.booksearch.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    
    implementation(libs.logger)
}