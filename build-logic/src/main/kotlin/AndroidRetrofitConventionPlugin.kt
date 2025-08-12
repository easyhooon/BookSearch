import com.android.build.gradle.LibraryExtension
import com.easyhooon.booksearch.convention.ApplicationConstants
import com.easyhooon.booksearch.convention.Plugins
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.configureAndroid
import com.easyhooon.booksearch.convention.implementation
import com.easyhooon.booksearch.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                "booksearch.kotlin.library.serialization",
            )

            dependencies {
                implementation(libs.retrofit)
                implementation(libs.retrofit.kotlinx.serialization.converter)
                implementation(libs.okhttp.logging.interceptor)
            }
        }
    }
}
