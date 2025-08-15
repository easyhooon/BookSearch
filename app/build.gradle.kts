import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.booksearch.android.application)
    alias(libs.plugins.booksearch.android.application.compose)
    alias(libs.plugins.booksearch.android.hilt)
}

android {
    namespace = "com.easyhooon.booksearch"

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".dev"
            manifestPlaceholders += mapOf(
                "appName" to "@string/app_name_dev",
            )
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders += mapOf(
                "appName" to "@string/app_name",
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    defaultConfig {
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", getApiKey("KAKAO_NATIVE_APP_KEY"))
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = getApiKey("KAKAO_NATIVE_APP_KEY").trim('"')
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.network)

    implementation(projects.feature.detail)
    implementation(projects.feature.search)
    implementation(projects.feature.favorites)
    implementation(projects.feature.main)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.startup)
    implementation(libs.coil.compose)
    implementation(libs.logger)
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey) ?: "\"\""
}
