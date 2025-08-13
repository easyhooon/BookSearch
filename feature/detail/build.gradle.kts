plugins {
    alias(libs.plugins.booksearch.android.feature)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

android {
    namespace = "com.easyhooon.booksearch.feature.home"
}

dependencies {
    implementation(libs.logger)
}