import androidx.room.gradle.RoomExtension
import com.easyhooon.booksearch.convention.ApplicationConstants
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.configureAndroid
import com.easyhooon.booksearch.convention.implementation
import com.easyhooon.booksearch.convention.androidTestImplementation
import com.easyhooon.booksearch.convention.libs
import com.easyhooon.booksearch.convention.ksp
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(Plugins.ANDROIDX_ROOM, Plugins.KOTLINX_SERIALIZATION, Plugins.KSP)

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.ktx)
                ksp(libs.androidx.room.compiler)
                implementation(libs.kotlinx.serialization.json)

                androidTestImplementation(libs.androidx.room.testing)
            }
        }
    }
}
