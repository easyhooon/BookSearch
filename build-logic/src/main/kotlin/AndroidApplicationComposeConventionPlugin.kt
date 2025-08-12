import com.android.build.api.dsl.ApplicationExtension
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                Plugins.ANDROID_APPLICATION,
                Plugins.KOTLIN_COMPOSE,
            )

            extensions.configure<ApplicationExtension> {
                configureCompose(this)
            }
        }
    }
}
