import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.implementation
import com.easyhooon.booksearch.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class KotlinLibrarySerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                Plugins.KOTLINX_SERIALIZATION
            )

            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
