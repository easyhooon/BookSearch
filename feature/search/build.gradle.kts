plugins {
    alias(libs.plugins.booksearch.android.feature)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

android {
    namespace = "com.easyhooon.booksearch.feature.search"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.logger)

    implementation(libs.soil.query.core)
    implementation(libs.soil.query.compose)
    implementation(libs.soil.reacty)
    implementation(libs.soil.lazyload)

    implementation(libs.rin)
}
