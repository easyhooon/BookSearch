import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.retrofit)
    alias(libs.plugins.booksearch.android.hilt)
}

android {
    namespace = "com.easyhooon.booksearch.core.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "KAKAO_API_BASE_URL", getServerBaseUrl("KAKAO_API_BASE_URL"))
        }
    }
}

dependencies {
    implementation(libs.logger)
}

fun getServerBaseUrl(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey) ?: "\"\""
}
