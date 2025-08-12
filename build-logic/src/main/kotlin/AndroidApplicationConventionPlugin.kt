import com.android.build.api.dsl.ApplicationExtension
import com.easyhooon.booksearch.convention.ApplicationConstants
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                Plugins.ANDROID_APPLICATION,
                Plugins.KOTLIN_ANDROID,
            )

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)

                defaultConfig {
                    targetSdk = ApplicationConstants.TARGET_SDK
                    versionName = ApplicationConstants.VERSION_NAME
                    versionCode = ApplicationConstants.VERSION_CODE
                }
            }
        }
    }
}
