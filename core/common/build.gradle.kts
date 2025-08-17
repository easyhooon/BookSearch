plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.library.compose)
    alias(libs.plugins.booksearch.android.hilt)
    alias(libs.plugins.booksearch.android.retrofit)
}

android {
    namespace = "com.easyhooon.booksearch.core.common"
}

dependencies {
    compileOnly(libs.compose.stable.marker)

    implementation(projects.core.domain)
    implementation(projects.core.network)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.logger)
}
