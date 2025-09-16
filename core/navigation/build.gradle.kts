plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.library.compose)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

android {
    namespace = "com.easyhooon.booksearch.core.navigation"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.androidx.navigation.compose)
}
