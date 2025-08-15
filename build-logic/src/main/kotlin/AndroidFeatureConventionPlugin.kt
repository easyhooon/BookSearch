
import com.easyhooon.booksearch.convention.api
import com.easyhooon.booksearch.convention.applyPlugins
import com.easyhooon.booksearch.convention.implementation
import com.easyhooon.booksearch.convention.ksp
import com.easyhooon.booksearch.convention.libs
import com.easyhooon.booksearch.convention.project
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins(
                "booksearch.android.library",
                "booksearch.android.library.compose",
                "booksearch.android.hilt",
            )

            dependencies {
                implementation(project(path = ":core:common"))
                implementation(project(path = ":core:designsystem"))
                implementation(project(path = ":core:domain"))
                implementation(project(path = ":core:navigation"))
                implementation(project(path = ":core:ui"))

                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.hilt.navigation.compose)

                implementation(libs.bundles.androidx.lifecycle)
            }
        }
    }
}
