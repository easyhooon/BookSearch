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
            buildConfigField("String", "KAKAO_API_BASE_URL", getLocalProperty("KAKAO_API_BASE_URL"))
            buildConfigField("String", "KAKAO_REST_API_KEY", getLocalProperty("KAKAO_REST_API_KEY"))
        }
    }
}

dependencies {
    implementation(libs.logger)
}

fun getLocalProperty(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey) ?: "\"\""
}
