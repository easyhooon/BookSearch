package com.easyhooon.booksearch.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureAndroid(extension: CommonExtension<*, *, *, *, *, *>) {
    extension.apply {
        compileSdk = ApplicationConstants.COMPILE_SDK

        defaultConfig {
            minSdk = ApplicationConstants.MIN_SDK
        }

        compileOptions {
            sourceCompatibility = ApplicationConstants.javaVersion
            targetCompatibility = ApplicationConstants.javaVersion
        }

        extensions.configure<KotlinProjectExtension> {
            jvmToolchain(ApplicationConstants.JAVA_VERSION_INT)
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.add("-Xcontext-parameters")
            }
        }

        dependencies {
            detektPlugins(libs.detekt.formatting)
        }
    }
}
