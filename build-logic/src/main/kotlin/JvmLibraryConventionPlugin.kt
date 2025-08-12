import com.easyhooon.booksearch.convention.ApplicationConstants
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.detektPlugins
import com.easyhooon.booksearch.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                Plugins.JAVA_LIBRARY,
                Plugins.KOTLIN_JVM,
            )

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = ApplicationConstants.javaVersion
                targetCompatibility = ApplicationConstants.javaVersion
            }

            extensions.configure<KotlinProjectExtension> {
                jvmToolchain(ApplicationConstants.JAVA_VERSION_INT)
            }

            dependencies {
                detektPlugins(libs.detekt.formatting)
            }
        }
    }
}
