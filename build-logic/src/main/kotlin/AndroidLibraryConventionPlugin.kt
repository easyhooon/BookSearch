import com.android.build.gradle.LibraryExtension
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.easyhooon.booksearch.convention.ApplicationConstants

internal class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                Plugins.ANDROID_LIBRARY,
                Plugins.KOTLIN_ANDROID,
            )

            extensions.configure<LibraryExtension> {
                configureAndroid(this)

                defaultConfig.apply {
                    targetSdk = ApplicationConstants.TARGET_SDK
                }
            }
        }
    }
}
