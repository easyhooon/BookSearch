plugins {
    alias(libs.plugins.booksearch.jvm.library)
    alias(libs.plugins.booksearch.kotlin.library.serialization)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.javax.inject)
}
