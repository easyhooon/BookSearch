import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    alias(libs.plugins.booksearch.android.library)
    alias(libs.plugins.booksearch.android.retrofit)
    alias(libs.plugins.booksearch.android.hilt)
    alias(libs.plugins.kotlin.serialization)
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
        release {
            buildConfigField("String", "KAKAO_API_BASE_URL", getLocalProperty("KAKAO_API_BASE_URL"))
            buildConfigField("String", "KAKAO_REST_API_KEY", getLocalProperty("KAKAO_REST_API_KEY"))
        }
    }
}

dependencies {
    implementation(libs.logger)

    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.kotlinx.serialization.json)

    implementation(libs.kotlinx.serialization.json)
}

fun getLocalProperty(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey) ?: "\"\""
}
