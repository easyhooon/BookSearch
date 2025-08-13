plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.library.compose)
}

android {
    namespace = "com.easyhooon.booksearch.core.designsystem"
}

dependencies {
    implementation(projects.core.common)
    
    implementation(libs.androidx.splash)
    
    implementation(libs.coil.compose)
    implementation(libs.logger)
    
    implementation(libs.bundles.landscapist)
}
