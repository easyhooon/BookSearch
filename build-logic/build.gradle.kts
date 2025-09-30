plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.room.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    val conventionPluginClasses = listOf(
        "android.application" to "AndroidApplicationConventionPlugin",
        "android.application.compose" to "AndroidApplicationComposeConventionPlugin",
        "android.library" to "AndroidLibraryConventionPlugin",
        "android.library.compose" to "AndroidLibraryComposeConventionPlugin",
        "android.feature" to "AndroidFeatureConventionPlugin",
        "android.hilt" to "AndroidHiltConventionPlugin",
        "android.retrofit" to "AndroidRetrofitConventionPlugin",
        "android.room" to "AndroidRoomConventionPlugin",
        "jvm.library" to "JvmLibraryConventionPlugin",
        "kotlin.library.serialization" to "KotlinLibrarySerializationConventionPlugin",
    )

    plugins {
        conventionPluginClasses.forEach { pluginClass ->
            pluginRegister(pluginClass)
        }
    }
}

// Pair<PluginName, ClassName>
fun NamedDomainObjectContainer<PluginDeclaration>.pluginRegister(data: Pair<String, String>) {
    val (pluginName, className) = data
    register(pluginName) {
        id = "booksearch.$pluginName"
        implementationClass = className
    }
}
