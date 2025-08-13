plugins {
    alias(libs.plugins.booksearch.android.feature)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

android {
    namespace = "com.easyhooon.booksearch.feature.main"
}

dependencies {
    implementation(projects.feature.detail)
    implementation(projects.feature.favorites)
    implementation(projects.feature.search)

    implementation(libs.androidx.activity.compose)

    implementation(libs.logger)
}